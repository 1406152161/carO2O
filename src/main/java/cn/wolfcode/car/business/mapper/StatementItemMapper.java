package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.StatementItemQuery;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface StatementItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StatementItem record);

    StatementItem selectByPrimaryKey(Long id);

    List<StatementItem> selectAll();

    int updateByPrimaryKey(StatementItem record);

    List<StatementItem> selectForList(StatementItemQuery qo);

}