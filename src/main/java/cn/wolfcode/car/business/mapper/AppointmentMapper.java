package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.query.AppointmentQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface AppointmentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Appointment record);

    Appointment selectByPrimaryKey(Long id);

    List<Appointment> selectAll();

    int updateByPrimaryKey(Appointment record);

    List<Appointment> selectForList(AppointmentQuery qo);

    void updateByAppoint(Appointment appointment);

    void updateByAppointStatus(@Param("id") Long id, @Param("status") Integer statusArrival, @Param("date") Date date);

    void updateAppointStatus(@Param("id") Long id, @Param("cancel") Integer statusCancel);
}