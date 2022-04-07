package cn.wolfcode.car.business.web.controller;

import cn.wolfcode.car.business.domain.Department;
import cn.wolfcode.car.business.domain.Employee;
import cn.wolfcode.car.business.query.EmployeeQuery;
import cn.wolfcode.car.business.service.IDepartmentService;
import cn.wolfcode.car.business.service.IEmployeeService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("business/employee")
public class EmployeeController {
    //模板前缀
    private static final String prefix = "business/employee/";
    @Autowired
    private IEmployeeService employeeService;
    @Autowired
    private IDepartmentService departmentService;
    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("business:employee:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("business:employee:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("business:employee:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("employee", employeeService.get(id));
        return prefix + "edit";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("business:employee:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<Employee> query(EmployeeQuery qo,Model model){
        List<Department> departments = departmentService.list();
        model.addAttribute("departments",departments);
        return employeeService.query(qo);
    }

    //新增
    @RequiresPermissions("business:employee:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Employee employee){
        employeeService.save(employee);
        return AjaxResult.success();
    }

    //编辑
    @RequiresPermissions("business:employee:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Employee employee){
        employeeService.update(employee);
        return AjaxResult.success();
    }

    //删除
    @RequiresPermissions("business:employee:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        employeeService.deleteBatch(ids);
        return AjaxResult.success();
    }

    @RequestMapping("/deptList")
    public String deptList(Long id ,Model model){
        Department department = departmentService.get(id);
        model.addAttribute("dept",department);
        return prefix + "dept";

    }
}
