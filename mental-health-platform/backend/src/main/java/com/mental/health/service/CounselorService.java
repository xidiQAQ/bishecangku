package com.mental.health.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.dto.ScheduleDTO;
import com.mental.health.entity.CounselorSchedule;
import com.mental.health.vo.CounselorInfoVO;
import com.mental.health.vo.CounselorStatisticsVO;

import java.time.LocalDate;
import java.util.List;

public interface CounselorService {
    
    CounselorInfoVO getProfile(Long counselorId);
    
    void updateProfile(Long counselorId, CounselorInfoVO vo);
    
    CounselorStatisticsVO getStatistics(Long counselorId);
    
    List<CounselorSchedule> getSchedule(Long counselorId, LocalDate startDate, LocalDate endDate);
    
    void batchSetSchedule(Long counselorId, ScheduleDTO dto);
    
    void updateScheduleStatus(Long counselorId, Long scheduleId, Integer status);
    
    void deleteSchedule(Long counselorId, Long scheduleId);
    
    Page getAppointments(Long counselorId, Integer status, Integer current, Integer size);
    
    void confirmAppointment(Long counselorId, Long appointmentId);
    
    void rejectAppointment(Long counselorId, Long appointmentId, String rejectReason);
    
    void completeAppointment(Long counselorId, Long appointmentId);
}
