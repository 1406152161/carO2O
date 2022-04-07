package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.config.SystemConfig;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.common.util.file.FileUploadUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipInputStream;

@Service
public class BpmnInfoServiceImpl implements IBpmnInfoService {
    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;
    @Autowired
    private RepositoryService repositoryService;
    @Override
    public TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<BpmnInfo>(bpmnInfoMapper.selectForList(qo));
    }


    @Override
    public void save(BpmnInfo bpmnInfo) {
        bpmnInfoMapper.insert(bpmnInfo);
    }

    @Override
    public BpmnInfo get(Long id) {
        return bpmnInfoMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(BpmnInfo bpmnInfo) {
        bpmnInfoMapper.updateByPrimaryKey(bpmnInfo);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            bpmnInfoMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<BpmnInfo> list() {
        return bpmnInfoMapper.selectAll();
    }

    @Override
    public String uploadBpmn(MultipartFile file) {
        if (file != null && file.getSize()>0) {
            //上传的文件名
            String filename = file.getOriginalFilename();
            //获取拓展名 .xxx
            String exp = FilenameUtils.getExtension(filename);
            if ("zip".equalsIgnoreCase(exp)||"bpmn".equalsIgnoreCase(exp)) {
                String upload=null;
                try {
                    upload = FileUploadUtils.upload(SystemConfig.getUploadPath(), file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return upload;
            }else {
                throw new BusinessException("流程定义文件仅支持bpmn和zip格式");
            }
        }else {
            throw new BusinessException("不允许上传空文件");
        }
    }

    @Override
    public void deploy(String bpmnPath, String bpmnType, String info){
        String lowStr = bpmnPath.toLowerCase();
        Deployment deployment = null;
        try {
            FileInputStream stream = new FileInputStream(new File(SystemConfig.getProfile(), bpmnPath));
            if (lowStr.endsWith("zip")) {
                deployment = repositoryService.createDeployment()
                        .addZipInputStream(new ZipInputStream(stream))
                        .deploy();
            }else if (lowStr.endsWith("bpmn")){
                deployment = repositoryService.createDeployment()
                        .addInputStream(bpmnPath, stream)
                        .deploy();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BpmnInfo bpmnInfo = new BpmnInfo();
        bpmnInfo.setBpmnType(bpmnType);
        bpmnInfo.setBpmnPath(bpmnPath);
        bpmnInfo.setDeploymentId(deployment.getId());
        bpmnInfo.setInfo(info);
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .latestVersion()
                .singleResult();
        bpmnInfo.setBpmnName(processDefinition.getName());
        bpmnInfo.setActProcessId(processDefinition.getId());
        bpmnInfo.setActProcessKey(processDefinition.getKey());
        bpmnInfo.setDeployTime(deployment.getDeploymentTime());

        bpmnInfoMapper.insert(bpmnInfo);
    }

    @Override
    public void updateInfo(Long id, String info) {
        bpmnInfoMapper.updateInfo(id,info);
    }

    @Override
    public void delete(Long id) {
        BpmnInfo bpmnInfo = bpmnInfoMapper.selectByPrimaryKey(id);
        repositoryService.deleteDeployment(bpmnInfo.getDeploymentId(),true);
        File file = new File(SystemConfig.getProfile(), bpmnInfo.getBpmnPath());
        if (file.exists()) {
            file.delete();
        }
        bpmnInfoMapper.deleteByPrimaryKey(id);
    }

    @Override
    public InputStream readResource(String deploymentId, String type) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .singleResult();

        //xml
        if ("xml".equalsIgnoreCase(type)) {
            return repositoryService.getResourceAsStream(deploymentId,processDefinition.getResourceName());
        }else if ("png".equalsIgnoreCase(type)){
            //png
            BpmnModel model = repositoryService.getBpmnModel(processDefinition.getId());
            ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
            //generateDiagram(流程模型,需要高亮的节点,需要高亮的线条,后面三个参数都表示是字体)
            return generator.generateDiagram(model, Collections.EMPTY_LIST, Collections.EMPTY_LIST,
                    "宋体","宋体","宋体");
        }
        return null;
    }

    @Override
    public BpmnInfo queryByType(String type) {
        return bpmnInfoMapper.selectByType(type);
    }
}
