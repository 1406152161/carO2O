package cn.wolfcode.car.business.service.impl;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.query.AppointmentQuery;
import cn.wolfcode.car.business.service.IAppointmentService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements IAppointmentService {
    @Autowired
    private AppointmentMapper appointmentMapper;
    
    public TablePageInfo<Appointment> query(AppointmentQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<Appointment>(appointmentMapper.selectForList(qo));
    }
    
    public void save(Appointment appointment) {
        appointment.setStatus(Appointment.STATUS_APPOINTMENT);
        appointment.setCreateTime(new Date());
        appointmentMapper.insert(appointment);
    }
    
    public Appointment get(Long id) {
        return appointmentMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(Appointment appointment) {
        //需要额外检查是否可能导致数据丢失问题
        Appointment appointment1 = appointmentMapper.selectByPrimaryKey(appointment.getId());
        if (!appointment1.getStatus().equals(Appointment.STATUS_APPOINTMENT)) {
            throw new BusinessException("服务预约单只有在预约中状态才允许修改");
        }
        //必须是预约中才可以进行编辑
        appointmentMapper.updateByAppoint(appointment);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            appointmentMapper.deleteByPrimaryKey(dictId);
        }
    }
    @Override
    public List<Appointment> list() {
        return appointmentMapper.selectAll();
    }

    @Override
    public void arrival(Long id) {
        Appointment appointment = appointmentMapper.selectByPrimaryKey(id);
        if (!appointment.getStatus().equals(Appointment.STATUS_APPOINTMENT)) {
            throw new BusinessException("服务预约单只有在预约中状态才允许修改到店");
        }
        appointmentMapper.updateByAppointStatus(id,Appointment.STATUS_ARRIVAL,new Date());
    }
    @Override
    public void cancel(Long id) {
        Appointment appointment = appointmentMapper.selectByPrimaryKey(id);
        if (!appointment.getStatus().equals(Appointment.STATUS_APPOINTMENT)) {
            throw new BusinessException("服务预约单只有在预约中状态才允许取消");
        }
        appointmentMapper.updateAppointStatus(id,Appointment.STATUS_CANCEL);
    }
}
