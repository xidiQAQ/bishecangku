package com.mental.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.common.exception.BusinessException;
import com.mental.health.dto.ScheduleDTO;
import com.mental.health.entity.Appointment;
import com.mental.health.entity.CounselorInfo;
import com.mental.health.entity.CounselorSchedule;
import com.mental.health.entity.SysUser;
import com.mental.health.mapper.AppointmentMapper;
import com.mental.health.mapper.CounselorInfoMapper;
import com.mental.health.mapper.CounselorScheduleMapper;
import com.mental.health.mapper.SysUserMapper;
import com.mental.health.service.CounselorService;
import com.mental.health.vo.CounselorInfoVO;
import com.mental.health.vo.CounselorStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CounselorServiceImpl implements CounselorService {

    private final CounselorInfoMapper counselorInfoMapper;
    private final CounselorScheduleMapper scheduleMapper;
    private final AppointmentMapper appointmentMapper;
    private final SysUserMapper userMapper;

    @Override
    public CounselorInfoVO getProfile(Long counselorId) {
        SysUser user = userMapper.selectById(counselorId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        CounselorInfo info = counselorInfoMapper.selectOne(
            new LambdaQueryWrapper<CounselorInfo>()
                .eq(CounselorInfo::getUserId, counselorId)
        );
        
        if (info == null) {
            throw new BusinessException("咨询师信息不存在");
        }
        
        CounselorInfoVO vo = new CounselorInfoVO();
        BeanUtils.copyProperties(info, vo);
        vo.setRealName(user.getRealName());
        return vo;
    }

    @Override
    @Transactional
    public void updateProfile(Long counselorId, CounselorInfoVO vo) {
        CounselorInfo info = counselorInfoMapper.selectOne(
            new LambdaQueryWrapper<CounselorInfo>()
                .eq(CounselorInfo::getUserId, counselorId)
        );
        
        if (info == null) {
            throw new BusinessException("咨询师信息不存在");
        }
        
        info.setTitle(vo.getTitle());
        info.setSpecialty(vo.getSpecialty());
        info.setIntroduction(vo.getIntroduction());
        info.setEducation(vo.getEducation());
        info.setExperience(vo.getExperience());
        info.setCertificate(vo.getCertificate());
        
        counselorInfoMapper.updateById(info);
    }

    @Override
    public CounselorStatisticsVO getStatistics(Long userId) {
        CounselorInfo info = counselorInfoMapper.selectOne(
            new LambdaQueryWrapper<CounselorInfo>()
                .eq(CounselorInfo::getUserId, userId)
        );
        
        if (info == null) {
            throw new BusinessException("咨询师信息不存在");
        }
        
        // 获取本月预约数
        LocalDateTime monthStart = YearMonth.now().atDay(1).atStartOfDay();
        Long monthCount = appointmentMapper.selectCount(
            new LambdaQueryWrapper<Appointment>()
                .eq(Appointment::getCounselorId, info.getId())
                .ge(Appointment::getCreatedAt, monthStart)
        );
        
        CounselorStatisticsVO vo = new CounselorStatisticsVO();
        vo.setTotalAppointments(info.getAppointmentCount());
        vo.setCompletedAppointments(info.getCompletedCount());
        vo.setMonthAppointments(monthCount.intValue());
        vo.setAverageRating(info.getRating().doubleValue());
        vo.setRatingCount(info.getRatingCount());
        vo.setRatingTrend(new ArrayList<>());
        
        return vo;
    }

    @Override
    public List<CounselorSchedule> getSchedule(Long userId, LocalDate startDate, LocalDate endDate) {
        // 先查询咨询师信息获取counselor_info.id
        CounselorInfo info = counselorInfoMapper.selectOne(
            new LambdaQueryWrapper<CounselorInfo>()
                .eq(CounselorInfo::getUserId, userId)
        );
        
        if (info == null) {
            throw new BusinessException("咨询师信息不存在");
        }
        
        return scheduleMapper.selectList(
            new LambdaQueryWrapper<CounselorSchedule>()
                .eq(CounselorSchedule::getCounselorId, info.getId())
                .between(CounselorSchedule::getScheduleDate, startDate, endDate)
                .orderByAsc(CounselorSchedule::getScheduleDate)
                .orderByAsc(CounselorSchedule::getTimeSlot)
        );
    }

    @Override
    @Transactional
    public void batchSetSchedule(Long userId, ScheduleDTO dto) {
        // 先查询咨询师信息获取counselor_info.id
        CounselorInfo info = counselorInfoMapper.selectOne(
            new LambdaQueryWrapper<CounselorInfo>()
                .eq(CounselorInfo::getUserId, userId)
        );
        
        if (info == null) {
            throw new BusinessException("咨询师信息不存在");
        }
        
        LocalDate currentDate = dto.getStartDate();
        
        while (!currentDate.isAfter(dto.getEndDate())) {
            // 如果指定了星期几,检查当前日期是否匹配
            if (dto.getWeekdays() != null && !dto.getWeekdays().isEmpty()) {
                int dayOfWeek = currentDate.getDayOfWeek().getValue();
                if (!dto.getWeekdays().contains(dayOfWeek)) {
                    currentDate = currentDate.plusDays(1);
                    continue;
                }
            }
            
            for (String timeSlot : dto.getTimeSlots()) {
                // 检查是否已存在
                Long count = scheduleMapper.selectCount(
                    new LambdaQueryWrapper<CounselorSchedule>()
                        .eq(CounselorSchedule::getCounselorId, info.getId())
                        .eq(CounselorSchedule::getScheduleDate, currentDate)
                        .eq(CounselorSchedule::getTimeSlot, timeSlot)
                );
                
                if (count == 0) {
                    CounselorSchedule schedule = new CounselorSchedule();
                    schedule.setCounselorId(info.getId());
                    schedule.setScheduleDate(currentDate);
                    schedule.setTimeSlot(timeSlot);
                    schedule.setStatus(1); // 可预约
                    scheduleMapper.insert(schedule);
                }
            }
            
            currentDate = currentDate.plusDays(1);
        }
    }

    @Override
    @Transactional
    public void updateScheduleStatus(Long userId, Long scheduleId, Integer status) {
        // 先查询咨询师信息获取counselor_info.id
        CounselorInfo info = counselorInfoMapper.selectOne(
            new LambdaQueryWrapper<CounselorInfo>()
                .eq(CounselorInfo::getUserId, userId)
        );
        
        if (info == null) {
            throw new BusinessException("咨询师信息不存在");
        }
        
        CounselorSchedule schedule = scheduleMapper.selectById(scheduleId);
        
        if (schedule == null) {
            throw new BusinessException("时间段不存在");
        }
        
        if (!schedule.getCounselorId().equals(info.getId())) {
            throw new BusinessException("无权操作");
        }
        
        // 如果已被预约,不允许修改
        if (schedule.getStatus() == 2) {
            throw new BusinessException("该时间段已被预约,无法修改");
        }
        
        schedule.setStatus(status);
        scheduleMapper.updateById(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(Long userId, Long scheduleId) {
        // 先查询咨询师信息获取counselor_info.id
        CounselorInfo info = counselorInfoMapper.selectOne(
            new LambdaQueryWrapper<CounselorInfo>()
                .eq(CounselorInfo::getUserId, userId)
        );
        
        if (info == null) {
            throw new BusinessException("咨询师信息不存在");
        }
        
        CounselorSchedule schedule = scheduleMapper.selectById(scheduleId);
        
        if (schedule == null) {
            throw new BusinessException("时间段不存在");
        }
        
        if (!schedule.getCounselorId().equals(info.getId())) {
            throw new BusinessException("无权操作");
        }
        
        // 如果已被预约,不允许删除
        if (schedule.getStatus() == 2) {
            throw new BusinessException("该时间段已被预约,无法删除");
        }
        
        scheduleMapper.deleteById(scheduleId);
    }

    @Override
    public Page getAppointments(Long userId, Integer status, Integer current, Integer size) {
        // 先查询咨询师信息获取counselor_info.id
        CounselorInfo info = counselorInfoMapper.selectOne(
            new LambdaQueryWrapper<CounselorInfo>()
                .eq(CounselorInfo::getUserId, userId)
        );
        
        if (info == null) {
            throw new BusinessException("咨询师信息不存在");
        }
        
        Page<Appointment> page = new Page<>(current, size);
        
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getCounselorId, info.getId()); // 使用counselor_info.id
        if (status != null) {
            wrapper.eq(Appointment::getStatus, status);
        }
        wrapper.orderByDesc(Appointment::getCreatedAt);
        
        Page<Appointment> appointmentPage = appointmentMapper.selectPage(page, wrapper);
        
        // 转换成VO
        Page voPage = new Page<>(current, size);
        voPage.setTotal(appointmentPage.getTotal());
        
        List voList = appointmentPage.getRecords().stream().map(appointment -> {
            com.mental.health.vo.AppointmentVO vo = new com.mental.health.vo.AppointmentVO();
            BeanUtils.copyProperties(appointment, vo);
            
            // 查询学生信息
            SysUser student = userMapper.selectById(appointment.getStudentId());
            if (student != null) {
                vo.setStudentName(student.getRealName());
            }
            
            // 查询咨询师信息
            CounselorInfo counselorInfo = counselorInfoMapper.selectById(appointment.getCounselorId());
            if (counselorInfo != null) {
                SysUser counselor = userMapper.selectById(counselorInfo.getUserId());
                if (counselor != null) {
                    vo.setCounselorName(counselor.getRealName());
                    vo.setCounselorAvatar(counselor.getAvatar());
                }
                vo.setCounselorTitle(counselorInfo.getTitle());
            }
            
            // 解析时间段
            if (appointment.getTimeSlot() != null && appointment.getTimeSlot().contains("-")) {
                String[] times = appointment.getTimeSlot().split("-");
                vo.setStartTime(times[0]);
                vo.setEndTime(times[1]);
            }
            
            // 设置主题
            if (appointment.getProblemDesc() != null) {
                vo.setTopic(appointment.getProblemDesc().length() > 20 
                    ? appointment.getProblemDesc().substring(0, 20) + "..." 
                    : appointment.getProblemDesc());
            }
            
            // 设置状态文本
            Map<Integer, String> statusMap = new HashMap<>();
            statusMap.put(0, "待确认");
            statusMap.put(1, "已确认");
            statusMap.put(2, "已完成");
            statusMap.put(3, "已取消");
            statusMap.put(4, "已拒绝");
            vo.setStatusText(statusMap.getOrDefault(appointment.getStatus(), "未知"));
            
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void confirmAppointment(Long userId, Long appointmentId) {
        // 先查询咨询师信息获取counselor_info.id
        CounselorInfo info = counselorInfoMapper.selectOne(
            new LambdaQueryWrapper<CounselorInfo>()
                .eq(CounselorInfo::getUserId, userId)
        );
        
        if (info == null) {
            throw new BusinessException("咨询师信息不存在");
        }
        
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null || !appointment.getCounselorId().equals(info.getId())) {
            throw new RuntimeException("预约不存在或无权操作");
        }
        if (appointment.getStatus() != 0) {
            throw new RuntimeException("预约状态不正确");
        }
        
        appointment.setStatus(1);
        appointmentMapper.updateById(appointment);
    }

    @Override
    @Transactional
    public void rejectAppointment(Long userId, Long appointmentId, String rejectReason) {
        // 先查询咨询师信息获取counselor_info.id
        CounselorInfo info = counselorInfoMapper.selectOne(
            new LambdaQueryWrapper<CounselorInfo>()
                .eq(CounselorInfo::getUserId, userId)
        );
        
        if (info == null) {
            throw new BusinessException("咨询师信息不存在");
        }
        
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null || !appointment.getCounselorId().equals(info.getId())) {
            throw new RuntimeException("预约不存在或无权操作");
        }
        if (appointment.getStatus() != 0) {
            throw new RuntimeException("预约状态不正确");
        }
        
        appointment.setStatus(4);
        appointment.setCancelReason(rejectReason);
        appointmentMapper.updateById(appointment);
        
        // 释放时间段
        if (appointment.getScheduleId() != null) {
            CounselorSchedule schedule = scheduleMapper.selectById(appointment.getScheduleId());
            if (schedule != null) {
                schedule.setStatus(1);
                scheduleMapper.updateById(schedule);
            }
        }
    }

    @Override
    @Transactional
    public void completeAppointment(Long userId, Long appointmentId) {
        // 先查询咨询师信息获取counselor_info.id
        CounselorInfo info = counselorInfoMapper.selectOne(
            new LambdaQueryWrapper<CounselorInfo>()
                .eq(CounselorInfo::getUserId, userId)
        );
        
        if (info == null) {
            throw new BusinessException("咨询师信息不存在");
        }
        
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null || !appointment.getCounselorId().equals(info.getId())) {
            throw new RuntimeException("预约不存在或无权操作");
        }
        if (appointment.getStatus() != 1) {
            throw new RuntimeException("预约状态不正确");
        }
        
        appointment.setStatus(2);
        appointmentMapper.updateById(appointment);
        
        // 更新时间段状态
        if (appointment.getScheduleId() != null) {
            CounselorSchedule schedule = scheduleMapper.selectById(appointment.getScheduleId());
            if (schedule != null) {
                schedule.setStatus(3);
                scheduleMapper.updateById(schedule);
            }
        }
    }
}
