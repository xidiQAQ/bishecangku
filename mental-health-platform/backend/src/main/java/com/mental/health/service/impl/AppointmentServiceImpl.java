package com.mental.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.dto.AppointmentDTO;
import com.mental.health.entity.*;
import com.mental.health.mapper.*;
import com.mental.health.service.AppointmentService;
import com.mental.health.vo.AppointmentVO;
import com.mental.health.vo.CounselorVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private CounselorInfoMapper counselorInfoMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private CounselorScheduleMapper scheduleMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Override
    public Page<CounselorVO> getCounselorPage(Integer current, Integer size) {
        LambdaQueryWrapper<CounselorInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CounselorInfo::getStatus, 1);
        wrapper.orderByDesc(CounselorInfo::getRating);

        Page<CounselorInfo> page = new Page<>(current, size);
        counselorInfoMapper.selectPage(page, wrapper);

        Page<CounselorVO> voPage = new Page<>(current, size, page.getTotal());
        List<CounselorVO> voList = page.getRecords().stream().map(info -> {
            CounselorVO vo = new CounselorVO();
            BeanUtils.copyProperties(info, vo);
            
            SysUser user = userMapper.selectById(info.getUserId());
            if (user != null) {
                vo.setRealName(user.getRealName());
                vo.setAvatar(user.getAvatar());
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public CounselorVO getCounselorDetail(Long counselorId) {
        CounselorInfo info = counselorInfoMapper.selectById(counselorId);
        if (info == null) {
            throw new RuntimeException("咨询师不存在");
        }

        CounselorVO vo = new CounselorVO();
        BeanUtils.copyProperties(info, vo);

        SysUser user = userMapper.selectById(info.getUserId());
        if (user != null) {
            vo.setRealName(user.getRealName());
            vo.setAvatar(user.getAvatar());
        }

        return vo;
    }

    @Override
    public List<CounselorSchedule> getCounselorSchedule(Long counselorId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<CounselorSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CounselorSchedule::getCounselorId, counselorId);
        wrapper.between(CounselorSchedule::getScheduleDate, startDate, endDate);
        wrapper.orderByAsc(CounselorSchedule::getScheduleDate, CounselorSchedule::getTimeSlot);
        return scheduleMapper.selectList(wrapper);
    }

    @Override
    @Transactional
    public void createAppointment(AppointmentDTO dto, Long studentId) {
        // 查询时间段
        LambdaQueryWrapper<CounselorSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CounselorSchedule::getCounselorId, dto.getCounselorId());
        wrapper.eq(CounselorSchedule::getScheduleDate, dto.getAppointmentDate());
        wrapper.eq(CounselorSchedule::getTimeSlot, dto.getTimeSlot());
        CounselorSchedule schedule = scheduleMapper.selectOne(wrapper);

        if (schedule == null) {
            throw new RuntimeException("该时间段不存在");
        }
        if (schedule.getStatus() != 1) {
            throw new RuntimeException("该时间段不可预约");
        }

        // 创建预约
        Appointment appointment = new Appointment();
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setStudentId(studentId);
        appointment.setCounselorId(dto.getCounselorId());
        appointment.setScheduleId(schedule.getId());
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setTimeSlot(dto.getTimeSlot());
        appointment.setConsultType(dto.getConsultType());
        appointment.setProblemDesc(dto.getProblemDesc());
        appointment.setStudentRemark(dto.getStudentRemark());
        appointment.setStatus(0);

        appointmentMapper.insert(appointment);

        // 更新时间表状态
        schedule.setStatus(2);
        scheduleMapper.updateById(schedule);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long appointmentId, Long userId, String reason) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }

        if (!appointment.getStudentId().equals(userId)) {
            throw new RuntimeException("无权操作");
        }

        if (appointment.getStatus() == 3) {
            throw new RuntimeException("预约已取消");
        }

        // 更新预约状态
        appointment.setStatus(3);
        appointment.setCancelReason(reason);
        appointmentMapper.updateById(appointment);

        // 恢复时间表状态
        if (appointment.getScheduleId() != null) {
            CounselorSchedule schedule = scheduleMapper.selectById(appointment.getScheduleId());
            if (schedule != null) {
                schedule.setStatus(1);
                scheduleMapper.updateById(schedule);
            }
        }
    }

    @Override
    public Page<AppointmentVO> getMyAppointments(Integer current, Integer size, Long userId, Integer status) {
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getStudentId, userId);
        if (status != null) {
            wrapper.eq(Appointment::getStatus, status);
        }
        wrapper.orderByDesc(Appointment::getCreatedAt);

        Page<Appointment> page = new Page<>(current, size);
        appointmentMapper.selectPage(page, wrapper);

        Page<AppointmentVO> voPage = new Page<>(current, size, page.getTotal());
        List<AppointmentVO> voList = page.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public AppointmentVO getAppointmentDetail(Long appointmentId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        return convertToVO(appointment);
    }

    @Override
    @Transactional
    public void confirmAppointment(Long appointmentId, Long counselorId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null || !appointment.getCounselorId().equals(counselorId)) {
            throw new RuntimeException("预约不存在或无权操作");
        }
        appointment.setStatus(1);
        appointmentMapper.updateById(appointment);
    }

    @Override
    @Transactional
    public void rejectAppointment(Long appointmentId, Long counselorId, String reason) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null || !appointment.getCounselorId().equals(counselorId)) {
            throw new RuntimeException("预约不存在或无权操作");
        }
        appointment.setStatus(4);
        appointment.setCancelReason(reason);
        appointmentMapper.updateById(appointment);

        // 恢复时间表
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
    public void completeAppointment(Long appointmentId, Long counselorId) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null || !appointment.getCounselorId().equals(counselorId)) {
            throw new RuntimeException("预约不存在或无权操作");
        }
        appointment.setStatus(2);
        appointmentMapper.updateById(appointment);
    }

    @Override
    @Transactional
    public void rateAppointment(Long appointmentId, Long studentId, Integer rating, String comment) {
        Appointment appointment = appointmentMapper.selectById(appointmentId);
        if (appointment == null || !appointment.getStudentId().equals(studentId)) {
            throw new RuntimeException("预约不存在或无权操作");
        }
        if (appointment.getStatus() != 2) {
            throw new RuntimeException("只能评价已完成的预约");
        }

        appointment.setRating(rating);
        appointment.setComment(comment);
        appointmentMapper.updateById(appointment);

        // 更新咨询师评分
        updateCounselorRating(appointment.getCounselorId());
    }

    private AppointmentVO convertToVO(Appointment appointment) {
        AppointmentVO vo = new AppointmentVO();
        BeanUtils.copyProperties(appointment, vo);

        SysUser student = userMapper.selectById(appointment.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getRealName());
        }

        SysUser counselor = userMapper.selectById(appointment.getCounselorId());
        if (counselor != null) {
            vo.setCounselorName(counselor.getRealName());
        }

        vo.setStatusText(getStatusText(appointment.getStatus()));
        return vo;
    }

    private String getStatusText(Integer status) {
        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(0, "待确认");
        statusMap.put(1, "已确认");
        statusMap.put(2, "已完成");
        statusMap.put(3, "已取消");
        statusMap.put(4, "已拒绝");
        return statusMap.getOrDefault(status, "未知");
    }

    private String generateAppointmentNo() {
        return "APT" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private void updateCounselorRating(Long counselorId) {
        // 查询所有已评价的预约
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getCounselorId, counselorId);
        wrapper.eq(Appointment::getStatus, 2);
        wrapper.isNotNull(Appointment::getRating);
        List<Appointment> appointments = appointmentMapper.selectList(wrapper);

        if (!appointments.isEmpty()) {
            double avgRating = appointments.stream()
                    .mapToInt(Appointment::getRating)
                    .average()
                    .orElse(0.0);

            CounselorInfo info = counselorInfoMapper.selectOne(
                    new LambdaQueryWrapper<CounselorInfo>()
                            .eq(CounselorInfo::getUserId, counselorId)
            );
            if (info != null) {
                info.setRating(java.math.BigDecimal.valueOf(avgRating));
                info.setRatingCount(appointments.size());
                counselorInfoMapper.updateById(info);
            }
        }
    }
}
