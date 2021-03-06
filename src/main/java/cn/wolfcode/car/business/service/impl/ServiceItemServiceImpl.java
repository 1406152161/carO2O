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
            throw new BusinessException("????????????????????????,?????????????????????");
        }
        if (item.getAuditStatus().equals(ServiceItem.AUDITSTATUS_AUDITING)) {
            throw new BusinessException("???????????????????????????");
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
            throw new BusinessException("????????????");
        }
        serviceItemMapper.changeSaleStatus(id,ServiceItem.SALESTATUS_OFF);
    }

    @Override
    public void saleOn(Long id) {
        //?????????????????????????????????
        ServiceItem serviceItem = serviceItemMapper.selectByPrimaryKey(id);
        if (serviceItem==null||ServiceItem.SALESTATUS_ON.equals(serviceItem.getSaleStatus())) {
            throw new BusinessException("????????????");
        }
        //??????????????????---????????????
        if (ServiceItem.CARPACKAGE_NO.equals(serviceItem.getCarPackage())) {
            serviceItemMapper.changeSaleStatus(id,ServiceItem.SALESTATUS_ON);
            return;
        }else {
            //???????????????---???????????????????????????????????????
            if (ServiceItem.AUDITSTATUS_APPROVED.equals(serviceItem.getAuditStatus())) {
                serviceItemMapper.changeSaleStatus(id,ServiceItem.SALESTATUS_ON);
                return;
            }else {
                throw new BusinessException("????????????????????????????????????????????????????????????");
            }
        }
    }

    @Override
    public void startAudit(Long id, Long bpmnInfoId, Long director, Long finance, String info) {
        ServiceItem serviceItem = serviceItemMapper.selectByPrimaryKey(id);
        if (serviceItem==null||ServiceItem.CARPACKAGE_NO.equals(serviceItem.getCarPackage())
                || !ServiceItem.AUDITSTATUS_INIT.equals(serviceItem.getAuditStatus())){
            throw new BusinessException("????????????");
        }

        CarPackageAudit audit = new CarPackageAudit();
        if (director==null) {
            throw new BusinessException("???????????????????????????????????????");
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
        //acit7??????????????????bigdicimal????????????????????????????????????long??????
        map.put("discountPrice",serviceItem.getDiscountPrice().longValue());
        String businessKey = audit.getId().toString();
        BpmnInfo bpmnInfo = bpmnInfoService.get(bpmnInfoId);
        //??????1???????????????key?????????2 ??????key?????????3???????????????
        ProcessInstance instance = runtimeService.startProcessInstanceByKey(bpmnInfo.getActProcessKey(), businessKey, map);
        instance.getId();
        carPackageAuditService.updateInstanceId(audit.getId(),instance.getId());
    }

    @Override
    public void changeAuditStatus(Long id, Integer auditStatus) {
        serviceItemMapper.changeAuditStatus(id, auditStatus);
    }
}
