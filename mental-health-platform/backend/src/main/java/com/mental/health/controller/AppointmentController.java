package com.mental.health.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.common.result.Result;
import com.mental.health.dto.AppointmentDTO;
import com.mental.health.entity.CounselorSchedule;
import com.mental.health.service.AppointmentService;
import com.mental.health.vo.AppointmentVO;
import com.mental.health.vo.CounselorVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * 预约控制器
 */
@Api(tags = "预约接口")
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @ApiOperation("获取咨询师列表")
    @GetMapping("/counselors")
    public Result<Page<CounselorVO>> getCounselors(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<CounselorVO> page = appointmentService.getCounselorPage(current, size);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取咨询师详情")
    @GetMapping("/counselors/{id}")
    public Result<CounselorVO> getCounselorDetail(@PathVariable Long id) {
        try {
            CounselorVO vo = appointmentService.getCounselorDetail(id);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取咨询师时间表")
    @GetMapping("/counselors/{id}/schedule")
    public Result<List<CounselorSchedule>> getCounselorSchedule(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<CounselorSchedule> schedules = appointmentService.getCounselorSchedule(id, startDate, endDate);
            return Result.success(schedules);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("创建预约")
    @PostMapping
    public Result<String> createAppointment(
            @Valid @RequestBody AppointmentDTO dto,
            @RequestParam Long studentId) {
        try {
            appointmentService.createAppointment(dto, studentId);
            return Result.success("预约成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("取消预约")
    @PutMapping("/{id}/cancel")
    public Result<String> cancelAppointment(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam(required = false) String reason) {
        try {
            appointmentService.cancelAppointment(id, userId, reason);
            return Result.success("取消成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取我的预约列表")
    @GetMapping("/my")
    public Result<Page<AppointmentVO>> getMyAppointments(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status) {
        try {
            Page<AppointmentVO> page = appointmentService.getMyAppointments(current, size, userId, status);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取预约详情")
    @GetMapping("/{id}")
    public Result<AppointmentVO> getAppointmentDetail(@PathVariable Long id) {
        try {
            AppointmentVO vo = appointmentService.getAppointmentDetail(id);
            return Result.success(vo);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("咨询师确认预约")
    @PutMapping("/{id}/confirm")
    public Result<String> confirmAppointment(
            @PathVariable Long id,
            @RequestParam Long counselorId) {
        try {
            appointmentService.confirmAppointment(id, counselorId);
            return Result.success("确认成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("咨询师拒绝预约")
    @PutMapping("/{id}/reject")
    public Result<String> rejectAppointment(
            @PathVariable Long id,
            @RequestParam Long counselorId,
            @RequestParam String reason) {
        try {
            appointmentService.rejectAppointment(id, counselorId, reason);
            return Result.success("已拒绝");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("完成预约")
    @PutMapping("/{id}/complete")
    public Result<String> completeAppointment(
            @PathVariable Long id,
            @RequestParam Long counselorId) {
        try {
            appointmentService.completeAppointment(id, counselorId);
            return Result.success("已完成");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("学生评价")
    @PutMapping("/{id}/rate")
    public Result<String> rateAppointment(
            @PathVariable Long id,
            @RequestParam Long studentId,
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment) {
        try {
            appointmentService.rateAppointment(id, studentId, rating, comment);
            return Result.success("评价成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
