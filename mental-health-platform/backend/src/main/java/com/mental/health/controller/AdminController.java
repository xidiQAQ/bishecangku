package com.mental.health.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.common.result.Result;
import com.mental.health.entity.SysUser;
import com.mental.health.service.AdminService;
import com.mental.health.vo.AdminStatisticsVO;
import com.mental.health.vo.MomentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "管理后台接口")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @ApiOperation("获取统计数据")
    @GetMapping("/statistics")
    public Result<AdminStatisticsVO> getStatistics() {
        AdminStatisticsVO statistics = adminService.getStatistics();
        return Result.success(statistics);
    }

    @ApiOperation("获取用户列表")
    @GetMapping("/users")
    public Result<Page<SysUser>> getUserList(
            @RequestParam(required = false) Integer userType,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysUser> page = adminService.getUserList(userType, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("更新用户状态")
    @PutMapping("/users/{userId}/status")
    public Result<Void> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status) {
        adminService.updateUserStatus(userId, status);
        return Result.success();
    }

    @ApiOperation("获取待审核树洞列表")
    @GetMapping("/moments/pending")
    public Result<Page<MomentVO>> getPendingMoments(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<MomentVO> page = adminService.getPendingMoments(pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("审核树洞")
    @PutMapping("/moments/{momentId}/audit")
    public Result<Void> auditMoment(
            @PathVariable Long momentId,
            @RequestHeader("userId") Long adminId,
            @RequestParam Integer auditStatus,
            @RequestParam(required = false) String auditReason) {
        adminService.auditMoment(momentId, adminId, auditStatus, auditReason);
        return Result.success();
    }
}
