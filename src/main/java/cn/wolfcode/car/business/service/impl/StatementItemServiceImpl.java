package cn.wolfcode.car.business.service.impl;


import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.mapper.StatementItemMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.service.IStatementItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StatementItemServiceImpl implements IStatementItemService {
    @Autowired
    private StatementItemMapper statementItemMapper;
    @Autowired
    private StatementMapper statementMapper;
    
    @Override
    public TablePageInfo<StatementItem> query(StatementItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<StatementItem>(statementItemMapper.selectForList(qo));
    }


    @Override
    public void save(StatementItem statementItem) {
        statementItemMapper.insert(statementItem);
    }

    @Override
    public StatementItem get(Long id) {
        return statementItemMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(StatementItem statementItem) {
        statementItemMapper.updateByPrimaryKey(statementItem);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            statementItemMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<StatementItem> list() {
        return statementItemMapper.selectAll();
    }

    @Override
    public void saveItems(List<StatementItem> items) {
        if (items!=null) {
            StatementItem si = items.remove(items.size() - 1);
            statementItemMapper.deleteByPrimaryKey(si.getStatementId());
            BigDecimal totalCount = new BigDecimal("0");
            BigDecimal totalAmount = new BigDecimal("0.00");
            if (items.size()>0) {
                for (StatementItem item : items) {
                    totalCount = totalCount.add(new BigDecimal(item.getItemQuantity()));
                    totalAmount = totalAmount.add(item.getItemPrice().multiply(new BigDecimal(item.getItemQuantity())));
                    statementItemMapper.insert(item);
                }
                statementMapper.updateAmount(si.getStatementId(),si.getItemPrice(),totalCount,totalAmount);
            }else {
                statementMapper.updateAmount(si.getStatementId(),new BigDecimal("0.00"),totalCount,totalAmount);
            }
        }
    }
}
