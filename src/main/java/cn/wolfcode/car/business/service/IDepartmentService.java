package cn.wolfcode.car.business.service;

import cn.wolfcode.car.business.domain.Department;
import cn.wolfcode.car.business.query.DepartmentQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

public interface IDepartmentService {
    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<Department> query(DepartmentQuery qo);

    /**
     * 查单个
     * @param id
     * @return
     */
    Department get(Long id);


    /**
     * 保存
     * @param post
     */
    void save(Department post);


    /**
     * 更新
     * @param post
     */
    void update(Department post);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<Department> list();

}
