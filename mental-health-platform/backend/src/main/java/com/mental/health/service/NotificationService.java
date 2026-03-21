package com.mental.health.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.vo.NotificationVO;

/**
 * 通知服务接口
 */
public interface NotificationService {

    /**
     * 创建通知
     */
    void createNotification(Long userId, String title, String content, String type, Long relatedId);

    /**
     * 获取用户通知列表
     */
    Page<NotificationVO> getNotifications(Integer current, Integer size, Long userId);

    /**
     * 标记通知为已读
     */
    void markAsRead(Long notificationId, Long userId);

    /**
     * 标记所有通知为已读
     */
    void markAllAsRead(Long userId);

    /**
     * 获取未读通知数量
     */
    Long getUnreadCount(Long userId);

    /**
     * 删除通知
     */
    void deleteNotification(Long notificationId, Long userId);
}
