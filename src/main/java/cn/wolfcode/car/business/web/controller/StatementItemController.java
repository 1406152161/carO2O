package cn.wolfcode.car.business.web.controller;

import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.service.IStatementItemService;
import cn.wolfcode.car.business.service.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("business/statementItem")
public class StatementItemController {
    //模板前缀
    private static final String prefix = "business/statementItem/";
    @Autowired
    private IStatementItemService statementItemService;
    @Autowired
    private IStatementService statementService;
    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("business:statementItem:view")
    @RequestMapping("/itemDetail")
    public String listPage(Long statementId,Model model){
        Statement statement = statementService.get(statementId);
        model.addAttribute("statement",statement);
        if (Statement.STATUS_PAID.equals(statement.getStatus())) {
            return prefix + "showDetail";
        }else {
            return prefix + "editDetail";
        }

    }

    /*@RequiresPermissions("business:statementItem:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }*/
    @RequiresPermissions("business:statementItem:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("statementItem", statementItemService.get(id));
        return prefix + "editDetail";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("business:statementItem:showDetail")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<StatementItem> query(StatementItemQuery qo){
        return statementItemService.query(qo);
    }

    //新增
    /*@RequiresPermissions("business:statementItem:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(StatementItem statementItem){
        statementItemService.save(statementItem);
        return AjaxResult.success();
    }*/

    //编辑
    @RequiresPermissions("business:statementItem:editDetail")
    @RequestMapping("/editDetail")
    @ResponseBody
    public AjaxResult edit(StatementItem statementItem){
        statementItemService.update(statementItem);
        return AjaxResult.success();
    }
    
    //删除
    @RequiresPermissions("business:statementItem:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        statementItemService.deleteBatch(ids);
        return AjaxResult.success();
    }
    @RequiresPermissions("business:statementItem:saveItems")
    @RequestMapping("/saveItems")
    @ResponseBody
    public AjaxResult saveItems(@RequestParam List<StatementItem> items){
        statementItemService.saveItems(items);
        return AjaxResult.success();
    }
}
