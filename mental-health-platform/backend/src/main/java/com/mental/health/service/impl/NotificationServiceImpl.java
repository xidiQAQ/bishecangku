package com.mental.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.entity.Notification;
import com.mental.health.mapper.NotificationMapper;
import com.mental.health.service.NotificationService;
import com.mental.health.vo.NotificationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public void createNotification(Long userId, String title, String content, String type, Long relatedId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setIsRead(0);
        
        notificationMapper.insert(notification);
        log.info("创建通知成功: userId={}, title={}", userId, title);
    }

    @Override
    public Page<NotificationVO> getNotifications(Integer current, Integer size, Long userId) {
        Page<Notification> page = new Page<>(current, size);
        
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .orderByDesc(Notification::getCreatedAt);
        
        Page<Notification> notificationPage = notificationMapper.selectPage(page, wrapper);
        
        Page<NotificationVO> voPage = new Page<>(current, size);
        voPage.setTotal(notificationPage.getTotal());
        
        List<NotificationVO> voList = notificationPage.getRecords().stream()
                .map(notification -> {
                    NotificationVO vo = new NotificationVO();
                    BeanUtils.copyProperties(notification, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new RuntimeException("通知不存在");
        }
        
        notification.setIsRead(1);
        notificationMapper.updateById(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        
        List<Notification> notifications = notificationMapper.selectList(wrapper);
        notifications.forEach(notification -> {
            notification.setIsRead(1);
            notificationMapper.updateById(notification);
        });
    }

    @Override
    public Long getUnreadCount(Long userId) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getUserId, userId)
                .eq(Notification::getIsRead, 0);
        
        return notificationMapper.selectCount(wrapper);
    }

    @Override
    @Transactional
    public void deleteNotification(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new RuntimeException("通知不存在");
        }
        
        notificationMapper.deleteById(notificationId);
    }
}
