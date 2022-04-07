package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.Employee;
import cn.wolfcode.car.business.query.EmployeeQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

public interface IEmployeeService {
    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<Employee> query(EmployeeQuery qo);

    /**
     * 查单个
     * @param id
     * @return
     */
    Employee get(Long id);


    /**
     * 保存
     * @param post
     */
    void save(Employee post);


    /**
     * 更新
     * @param post
     */
    void update(Employee post);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<Employee> list();

}
