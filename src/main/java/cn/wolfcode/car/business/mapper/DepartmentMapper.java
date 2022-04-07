package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Department;
import cn.wolfcode.car.business.query.DepartmentQuery;

import java.util.List;

public interface DepartmentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Department record);

    Department selectByPrimaryKey(Long id);

    List<Department> selectAll();

    int updateByPrimaryKey(Department record);

    List<Department> selectForList(DepartmentQuery qo);
}