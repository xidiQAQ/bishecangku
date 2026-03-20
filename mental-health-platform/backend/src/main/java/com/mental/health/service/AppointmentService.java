package com.mental.health.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.dto.AppointmentDTO;
import com.mental.health.entity.CounselorSchedule;
import com.mental.health.vo.AppointmentVO;
import com.mental.health.vo.CounselorVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 预约服务接口
 */
public interface AppointmentService {

    /**
     * 获取咨询师列表
     */
    Page<CounselorVO> getCounselorPage(Integer current, Integer size);

    /**
     * 获取咨询师详情
     */
    CounselorVO getCounselorDetail(Long counselorId);

    /**
     * 获取咨询师时间表
     */
    List<CounselorSchedule> getCounselorSchedule(Long counselorId, LocalDate startDate, LocalDate endDate);

    /**
     * 创建预约
     */
    void createAppointment(AppointmentDTO dto, Long studentId);

    /**
     * 取消预约
     */
    void cancelAppointment(Long appointmentId, Long userId, String reason);

    /**
     * 获取我的预约列表
     */
    Page<AppointmentVO> getMyAppointments(Integer current, Integer size, Long userId, Integer status);

    /**
     * 获取预约详情
     */
    AppointmentVO getAppointmentDetail(Long appointmentId);

    /**
     * 咨询师确认预约
     */
    void confirmAppointment(Long appointmentId, Long counselorId);

    /**
     * 咨询师拒绝预约
     */
    void rejectAppointment(Long appointmentId, Long counselorId, String reason);

    /**
     * 完成预约
     */
    void completeAppointment(Long appointmentId, Long counselorId);

    /**
     * 学生评价
     */
    void rateAppointment(Long appointmentId, Long studentId, Integer rating, String comment);
}
