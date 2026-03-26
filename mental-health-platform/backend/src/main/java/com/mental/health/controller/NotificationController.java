package com.mental.health.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.common.result.Result;
import com.mental.health.service.NotificationService;
import com.mental.health.vo.NotificationVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "通知管理")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/list")
    @Operation(summary = "获取通知列表")
    public Result<Page<NotificationVO>> getNotifications(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户未登录");
        }
        Page<NotificationVO> page = notificationService.getNotifications(current, size, userId);
        return Result.success(page);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "获取未读通知数量")
    public Result<Long> getUnreadCount(@RequestHeader(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户未登录");
        }
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "标记通知为已读")
    public Result<Void> markAsRead(@PathVariable Long id, @RequestHeader(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户未登录");
        }
        notificationService.markAsRead(id, userId);
        return Result.success();
    }

    @PutMapping("/read-all")
    @Operation(summary = "标记所有通知为已读")
    public Result<Void> markAllAsRead(@RequestHeader(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户未登录");
        }
        notificationService.markAllAsRead(userId);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知")
    public Result<Void> deleteNotification(@PathVariable Long id, @RequestHeader(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return Result.error("用户未登录");
        }
        notificationService.deleteNotification(id, userId);
        return Result.success();
    }
}
