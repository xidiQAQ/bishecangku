package com.mental.health.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.common.result.Result;
import com.mental.health.dto.ScheduleDTO;
import com.mental.health.entity.CounselorSchedule;
import com.mental.health.service.CounselorService;
import com.mental.health.vo.CounselorInfoVO;
import com.mental.health.vo.CounselorStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Api(tags = "咨询师端接口")
@RestController
@RequestMapping("/api/counselor")
@RequiredArgsConstructor
public class CounselorController {

    private final CounselorService counselorService;

    @ApiOperation("获取咨询师个人信息")
    @GetMapping("/profile")
    public Result<CounselorInfoVO> getProfile(@RequestHeader("userId") Long counselorId) {
        CounselorInfoVO vo = counselorService.getProfile(counselorId);
        return Result.success(vo);
    }

    @ApiOperation("更新咨询师个人信息")
    @PutMapping("/profile")
    public Result<Void> updateProfile(
            @RequestHeader("userId") Long counselorId,
            @RequestBody @Valid CounselorInfoVO vo) {
        counselorService.updateProfile(counselorId, vo);
        return Result.success();
    }

    @ApiOperation("获取工作统计数据")
    @GetMapping("/statistics")
    public Result<CounselorStatisticsVO> getStatistics(@RequestHeader("userId") Long counselorId) {
        CounselorStatisticsVO statistics = counselorService.getStatistics(counselorId);
        return Result.success(statistics);
    }

    @ApiOperation("获取时间表")
    @GetMapping("/schedule")
    public Result<List<CounselorSchedule>> getSchedule(
            @RequestHeader("userId") Long counselorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<CounselorSchedule> schedules = counselorService.getSchedule(counselorId, startDate, endDate);
        return Result.success(schedules);
    }

    @ApiOperation("批量设置时间表")
    @PostMapping("/schedule/batch")
    public Result<Void> batchSetSchedule(
            @RequestHeader("userId") Long counselorId,
            @RequestBody @Valid ScheduleDTO dto) {
        counselorService.batchSetSchedule(counselorId, dto);
        return Result.success();
    }

    @ApiOperation("更新单个时间段状态")
    @PutMapping("/schedule/{scheduleId}")
    public Result<Void> updateScheduleStatus(
            @RequestHeader("userId") Long counselorId,
            @PathVariable Long scheduleId,
            @RequestParam Integer status) {
        counselorService.updateScheduleStatus(counselorId, scheduleId, status);
        return Result.success();
    }

    @ApiOperation("删除时间段")
    @DeleteMapping("/schedule/{scheduleId}")
    public Result<Void> deleteSchedule(
            @RequestHeader("userId") Long counselorId,
            @PathVariable Long scheduleId) {
        counselorService.deleteSchedule(counselorId, scheduleId);
        return Result.success();
    }

    @ApiOperation("获取预约列表")
    @GetMapping("/appointments")
    public Result<Page> getAppointments(
            @RequestHeader("userId") Long counselorId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        Page page = counselorService.getAppointments(counselorId, status, current, size);
        return Result.success(page);
    }

    @ApiOperation("确认预约")
    @PutMapping("/appointments/{appointmentId}/confirm")
    public Result<Void> confirmAppointment(
            @RequestHeader("userId") Long counselorId,
            @PathVariable Long appointmentId) {
        counselorService.confirmAppointment(counselorId, appointmentId);
        return Result.success();
    }

    @ApiOperation("拒绝预约")
    @PutMapping("/appointments/{appointmentId}/reject")
    public Result<Void> rejectAppointment(
            @RequestHeader("userId") Long counselorId,
            @PathVariable Long appointmentId,
            @RequestParam String rejectReason) {
        counselorService.rejectAppointment(counselorId, appointmentId, rejectReason);
        return Result.success();
    }

    @ApiOperation("完成预约")
    @PutMapping("/appointments/{appointmentId}/complete")
    public Result<Void> completeAppointment(
            @RequestHeader("userId") Long counselorId,
            @PathVariable Long appointmentId) {
        counselorService.completeAppointment(counselorId, appointmentId);
        return Result.success();
    }
}
