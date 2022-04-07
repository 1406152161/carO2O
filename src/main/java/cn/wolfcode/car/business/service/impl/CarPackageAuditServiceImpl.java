package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.mapper.CarPackageAuditMapper;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.business.service.ICarPackageAuditService;
import cn.wolfcode.car.business.service.IServiceItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class CarPackageAuditServiceImpl implements ICarPackageAuditService {
    @Autowired
    private CarPackageAuditMapper carPackageAuditMapper;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IBpmnInfoService bpmnInfoService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IServiceItemService serviceItemService;

    @Override
    public TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<CarPackageAudit>(carPackageAuditMapper.selectForList(qo));
    }


    @Override
    public void save(CarPackageAudit carPackageAudit) {
        carPackageAuditMapper.insert(carPackageAudit);
    }

    @Override
    public CarPackageAudit get(Long id) {
        return carPackageAuditMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(CarPackageAudit carPackageAudit) {
        carPackageAuditMapper.updateByPrimaryKey(carPackageAudit);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            carPackageAuditMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<CarPackageAudit> list() {
        return carPackageAuditMapper.selectAll();
    }

    @Override
    public InputStream processImg(Long id) {
        CarPackageAudit audit = carPackageAuditMapper.selectByPrimaryKey(id);
        BpmnInfo bpmnInfo = bpmnInfoService.get(audit.getBpmnInfoId());
        BpmnModel model = repositoryService.getBpmnModel(bpmnInfo.getActProcessId());
        DefaultProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        if (CarPackageAudit.STATUS_IN_ROGRESS.equals(audit.getStatus())) {
            Task task = taskService.createTaskQuery()
                    .processInstanceId(audit.getInstanceId()).singleResult();
            List<String> activeActivityIds =
                    runtimeService.getActiveActivityIds(task.getExecutionId());

            return generator.generateDiagram(model, activeActivityIds,
                    Collections.EMPTY_LIST,
                    "宋体", "宋体", "宋体");
        } else {
        }
        //generateDiagram(流程模型,需要高亮的节点,需要高亮的线条,后面三个参数都表示是字体)

        return generator.generateDiagram(model, Collections.EMPTY_LIST, Collections.EMPTY_LIST,
                "宋体", "宋体", "宋体");
    }

    @Override
    public void updateInstanceId(Long id, String instanceId) {
        carPackageAuditMapper.updateInstanceId(id, instanceId);
    }

    @Override
    public void cancelApply(Long id) {
        CarPackageAudit audit = carPackageAuditMapper.selectByPrimaryKey(id);
        if (audit == null || !CarPackageAudit.STATUS_IN_ROGRESS.equals(audit.getStatus())) {
            throw new BusinessException("参数异常");
        }
        audit.setStatus(CarPackageAudit.STATUS_CANCEL);
        carPackageAuditMapper.updateByPrimaryKey(audit);
        serviceItemService.changeAuditStatus(audit.getServiceItemId(), ServiceItem.AUDITSTATUS_INIT);
        runtimeService.deleteProcessInstance(audit.getInstanceId(), "流程被撤销了");
    }

    @Override
    public void audit(Long id, int auditStatus, String auditRecord) {
        CarPackageAudit audit = carPackageAuditMapper.selectByPrimaryKey(id);
        if (audit == null || !CarPackageAudit.STATUS_IN_ROGRESS.equals(audit.getStatus())
                || !audit.getAuditorId().equals(ShiroUtils.getUserId())) {
            throw new BusinessException("参数异常");
        }
        //流程审核记录表记录当前审核时间
        audit.setAuditTime(new Date());
        String userName = ShiroUtils.getUser().getUserName();
        //约定：2 为同意
        if (auditStatus == 2) {
            auditRecord += "【" + userName + "同意】";
        } else {
            auditRecord += "【" + userName + "拒绝】";
        }
        audit.setAuditRecord(audit.getInfo() + ";" + auditRecord);

        //执行当前节点
        Task task = taskService.createTaskQuery().processInstanceId(audit.getInstanceId())
                .singleResult();
        //设置审核操作的同意与否参数
        taskService.setVariable(task.getId(), "auditStatus", auditStatus);
        //设置审批备注
        taskService.addComment(task.getId(), audit.getInstanceId(), auditRecord);
        //执行节点审批
        taskService.complete(task.getId());

        //获取下一个节点
        Task nextTask = taskService.createTaskQuery().processInstanceId(audit.getInstanceId())
                .singleResult();

        //审核逻辑
        //审核通过---
        if (auditStatus == CarPackageAudit.STATUS_PASS) {
            //1>如果还有下一个节点
            if (nextTask != null) {
                //流程审核记录表当前审核人-(财务)
                audit.setAuditorId(Long.parseLong(nextTask.getAssignee()));

            } else {
                //2>如果没有下一个节点- 流程结束
                //套餐服务单项状态：通过
                serviceItemService.changeAuditStatus(audit.getServiceItemId(), ServiceItem.AUDITSTATUS_APPROVED);
                //流程审核记录状态：通过
                audit.setStatus(CarPackageAudit.STATUS_PASS);
            }
        } else {
            //审核拒绝----
            //套餐服务单项状态：初始化  --- 建议加一个状态：审核拒绝，改动逻辑， 发起审核申请有2个状态可以
            serviceItemService.changeAuditStatus(audit.getServiceItemId(), ServiceItem.AUDITSTATUS_INIT);
            //流程审核记录状态：审核拒绝
            audit.setStatus(CarPackageAudit.STATUS_REJECT);
        }
        carPackageAuditMapper.updateByPrimaryKey(audit);
    }
}
