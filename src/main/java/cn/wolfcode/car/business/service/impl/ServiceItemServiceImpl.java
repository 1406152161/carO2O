package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.mapper.ServiceItemMapper;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.business.service.ICarPackageAuditService;
import cn.wolfcode.car.business.service.IServiceItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceItemServiceImpl implements IServiceItemService {
    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Autowired
    private ICarPackageAuditService carPackageAuditService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IBpmnInfoService bpmnInfoService;
    @Override
    public TablePageInfo<ServiceItem> query(ServiceItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<ServiceItem>(serviceItemMapper.selectForList(qo));
    }


    @Override
    public void save(ServiceItem serviceItem) {
        serviceItem.setCreateTime(new Date());
        serviceItemMapper.insert(serviceItem);
    }

    @Override
    public ServiceItem get(Long id) {
        return serviceItemMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(ServiceItem serviceItem) {
        ServiceItem item = serviceItemMapper.selectByPrimaryKey(serviceItem.getId());
        if (item.getSaleStatus().equals(ServiceItem.SALESTATUS_ON)) {
            throw new BusinessException("上架状态不可修改,请下架后再修改");
        }
        if (item.getAuditStatus().equals(ServiceItem.AUDITSTATUS_AUDITING)) {
            throw new BusinessException("项目审核中不可修改");
        }
        serviceItem.setCreateTime(new Date());
        serviceItemMapper.updateByPrimaryKey(serviceItem);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            serviceItemMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<ServiceItem> list() {
        return serviceItemMapper.selectAll();
    }

    @Override
    public void saleOff(Long id) {
        ServiceItem serviceItem = serviceItemMapper.selectByPrimaryKey(id);
        if (serviceItem==null|| ServiceItem.SALESTATUS_OFF.equals(serviceItem.getSaleStatus())) {
            throw new BusinessException("参数异常");
        }
        serviceItemMapper.changeSaleStatus(id,ServiceItem.SALESTATUS_OFF);
    }

    @Override
    public void saleOn(Long id) {
        //满足什么条件才允许上架
        ServiceItem serviceItem = serviceItemMapper.selectByPrimaryKey(id);
        if (serviceItem==null||ServiceItem.SALESTATUS_ON.equals(serviceItem.getSaleStatus())) {
            throw new BusinessException("参数异常");
        }
        //普通服务单项---直接上架
        if (ServiceItem.CARPACKAGE_NO.equals(serviceItem.getCarPackage())) {
            serviceItemMapper.changeSaleStatus(id,ServiceItem.SALESTATUS_ON);
            return;
        }else {
            //如果是套餐---需要审核通过状态才可以上架
            if (ServiceItem.AUDITSTATUS_APPROVED.equals(serviceItem.getAuditStatus())) {
                serviceItemMapper.changeSaleStatus(id,ServiceItem.SALESTATUS_ON);
                return;
            }else {
                throw new BusinessException("套餐服务项只有在审核通过情况下才允许上架");
            }
        }
    }

    @Override
    public void startAudit(Long id, Long bpmnInfoId, Long director, Long finance, String info) {
        ServiceItem serviceItem = serviceItemMapper.selectByPrimaryKey(id);
        if (serviceItem==null||ServiceItem.CARPACKAGE_NO.equals(serviceItem.getCarPackage())
                || !ServiceItem.AUDITSTATUS_INIT.equals(serviceItem.getAuditStatus())){
            throw new BusinessException("参数异常");
        }

        CarPackageAudit audit = new CarPackageAudit();
        if (director==null) {
            throw new BusinessException("必须指定第一审核人（店长）");
        }
        audit.setAuditorId(director);
        audit.setBpmnInfoId(bpmnInfoId);
        audit.setCreateTime(new Date());
        audit.setCreator(ShiroUtils.getLoginName());
        audit.setInfo(info);


        audit.setServiceItemId(id);
        audit.setServiceItemInfo(serviceItem.getInfo());
        audit.setServiceItemPrice(serviceItem.getDiscountPrice());
        audit.setStatus(CarPackageAudit.STATUS_IN_ROGRESS);
        carPackageAuditService.save(audit);

        serviceItemMapper.changeAuditStatus(id,ServiceItem.AUDITSTATUS_AUDITING);

        Map<String,Object> map = new HashMap<>();


        map.put("director",director);
        if (finance != null) {
            map.put("finance",finance);
        }
        //acit7中是不能使用bigdicimal进行大小判断，在此处使用long类型
        map.put("discountPrice",serviceItem.getDiscountPrice().longValue());
        String businessKey = audit.getId().toString();
        BpmnInfo bpmnInfo = bpmnInfoService.get(bpmnInfoId);
        //参数1：流程定义key，参数2 业务key，参数3，流程参数
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(bpmnInfo.getActProcessKey(), businessKey, map);
        instance.getId();
        carPackageAuditService.updateInstanceId(audit.getId(),instance.getId());
    }

    @Override
    public void changeAuditStatus(Long id, Integer auditStatus) {
        serviceItemMapper.changeAuditStatus(id, auditStatus);
    }
}
