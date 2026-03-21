package com.mental.health.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mental.health.entity.Appointment;
import com.mental.health.entity.CounselorInfo;
import com.mental.health.mapper.AppointmentMapper;
import com.mental.health.mapper.CounselorInfoMapper;
import com.mental.health.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class AppointmentReminderTask {

    @Autowired
    private AppointmentMapper appointmentMapper;

    @Autowired
    private CounselorInfoMapper counselorInfoMapper;

    @Autowired
    private NotificationService notificationService;

    /**
     * 每小时执行一次，检查即将到来的预约
     * 测试时可以改为每分钟: @Scheduled(cron = "0 * * * * ?")
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void checkUpcomingAppointments() {
        log.info("开始检查即将到来的预约...");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalDate tomorrow = today.plusDays(1);
        
        // 查询已确认的预约
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Appointment::getStatus, 1); // 已确认状态
        wrapper.in(Appointment::getAppointmentDate, today, tomorrow);
        
        List<Appointment> appointments = appointmentMapper.selectList(wrapper);
        
        for (Appointment appointment : appointments) {
            try {
                processAppointmentReminder(appointment, now);
            } catch (Exception e) {
                log.error("处理预约提醒失败: appointmentId={}", appointment.getId(), e);
            }
        }
        
        log.info("预约提醒检查完成，处理了 {} 条预约记录", appointments.size());
    }

    private void processAppointmentReminder(Appointment appointment, LocalDateTime now) {
        LocalDateTime appointmentTime = getAppointmentDateTime(appointment);
        if (appointmentTime == null) {
            return;
        }
        
        long hoursUntil = java.time.Duration.between(now, appointmentTime).toHours();
        
        // 24小时提醒（提前一天）
        if (hoursUntil >= 23 && hoursUntil <= 25) {
            sendReminder(appointment, appointmentTime, "24小时");
        }
        // 1小时提醒
        else if (hoursUntil >= 0 && hoursUntil <= 1) {
            sendReminder(appointment, appointmentTime, "1小时");
        }
    }

    private void sendReminder(Appointment appointment, LocalDateTime appointmentTime, String timeRange) {
        String dateStr = appointmentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        
        // 给学生发送提醒
        String studentTitle = "预约提醒";
        String studentContent = String.format(
            "您有一个咨询预约将在%s后开始。\n预约时间：%s\n咨询类型：%s",
            timeRange, dateStr, appointment.getConsultType()
        );
        notificationService.createNotification(
            appointment.getStudentId(),
            studentTitle,
            studentContent,
            "appointment",
            appointment.getId()
        );
        
        // 给咨询师发送提醒
        CounselorInfo counselorInfo = counselorInfoMapper.selectById(appointment.getCounselorId());
        if (counselorInfo != null) {
            String counselorTitle = "预约提醒";
            String counselorContent = String.format(
                "您有一个咨询预约将在%s后开始。\n预约时间：%s\n咨询类型：%s",
                timeRange, dateStr, appointment.getConsultType()
            );
            notificationService.createNotification(
                counselorInfo.getUserId(),
                counselorTitle,
                counselorContent,
                "appointment",
                appointment.getId()
            );
        }
        
        log.info("已发送{}提醒: appointmentId={}", timeRange, appointment.getId());
    }

    private LocalDateTime getAppointmentDateTime(Appointment appointment) {
        try {
            LocalDate date = appointment.getAppointmentDate();
            String timeSlot = appointment.getTimeSlot();
            
            if (timeSlot == null || !timeSlot.contains("-")) {
                return null;
            }
            
            // 解析时间段，取开始时间 (格式: "09:00-10:00")
            String startTimeStr = timeSlot.split("-")[0];
            LocalTime startTime = LocalTime.parse(startTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
            
            return LocalDateTime.of(date, startTime);
        } catch (Exception e) {
            log.error("解析预约时间失败: appointmentId={}", appointment.getId(), e);
            return null;
        }
    }
}
