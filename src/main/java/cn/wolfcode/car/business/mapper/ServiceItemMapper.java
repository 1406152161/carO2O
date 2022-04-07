package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.common.base.query.QueryObject;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ServiceItemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ServiceItem record);

    ServiceItem selectByPrimaryKey(Long id);

    List<ServiceItem> selectAll();

    int updateByPrimaryKey(ServiceItem record);

    List<ServiceItem> selectForList(ServiceItemQuery qo);

    void changeSaleStatus(@Param("id") Long id, @Param("salestatusOff") Integer salestatusOff);

    void changeAuditStatus(@Param("id") Long id, @Param("audit") Integer audit);
}