package cn.wolfcode.car.business.web.controller;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.business.service.IBpmnInfoService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.poi.util.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("business/bpmnInfo")
public class BpmnInfoController {
    //模板前缀
    private static final String prefix = "business/bpmnInfo/";

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("business:bpmnInfo:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("business:bpmnInfo:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("business:bpmnInfo:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("bpmnInfo", bpmnInfoService.get(id));
        return prefix + "edit";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("business:bpmnInfo:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo){
        return bpmnInfoService.query(qo);
    }

    //编辑
    @RequiresPermissions("business:bpmnInfo:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(BpmnInfo bpmnInfo){
        bpmnInfoService.updateInfo(bpmnInfo.getId(),bpmnInfo.getInfo());
        return AjaxResult.success();
    }

    //删除
    @RequiresPermissions("business:bpmnInfo:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        bpmnInfoService.deleteBatch(ids);
        return AjaxResult.success();
    }

    @RequiresPermissions("business:bpmnInfo:delete")
    @RequestMapping("/delete")
    @ResponseBody
    public AjaxResult delete(Long id){
        bpmnInfoService.delete(id);
        return AjaxResult.success();
    }

    @RequiresPermissions("business:bpmnInfo:deploy")
    @RequestMapping("/deployPage")
    public String deployPage(){
        return prefix + "deploy";
    }

    @RequiresPermissions("business:bpmnInfo:upload")
    @RequestMapping("/upload")
    @ResponseBody
    public AjaxResult upload(MultipartFile file){
        String path = bpmnInfoService.uploadBpmn(file);
        return AjaxResult.success("上传成功",path);
    }


    @RequestMapping("/deploy")
    @ResponseBody
    public AjaxResult deploy(String bpmnPath, String bpmnType, String info) {
        bpmnInfoService.deploy(bpmnPath, bpmnType, info);
        return AjaxResult.success();
    }


    @RequestMapping("/readResource")
    @ResponseBody
    public void readResource(String deploymentId, String type, HttpServletResponse response) throws IOException {
        InputStream stream = bpmnInfoService.readResource(deploymentId, type);
        IOUtils.copy(stream,response.getOutputStream());
    }
}
