package com.mental.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mental.health.entity.*;
import com.mental.health.mapper.*;
import com.mental.health.service.AdminService;
import com.mental.health.vo.AdminStatisticsVO;
import com.mental.health.vo.MomentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SysUserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final AppointmentMapper appointmentMapper;
    private final TestResultMapper testResultMapper;
    private final MomentMapper momentMapper;
    private final ObjectMapper objectMapper;

    @Override
    public AdminStatisticsVO getStatistics() {
        AdminStatisticsVO vo = new AdminStatisticsVO();
        
        // 用户统计
        vo.setTotalUsers(userMapper.selectCount(null));
        
        LambdaQueryWrapper<SysUser> studentWrapper = new LambdaQueryWrapper<>();
        studentWrapper.eq(SysUser::getUserType, 1);
        vo.setTotalStudents(userMapper.selectCount(studentWrapper));
        
        LambdaQueryWrapper<SysUser> counselorWrapper = new LambdaQueryWrapper<>();
        counselorWrapper.eq(SysUser::getUserType, 2);
        vo.setTotalCounselors(userMapper.selectCount(counselorWrapper));
        
        // 文章统计
        vo.setTotalArticles(articleMapper.selectCount(null));
        
        // 预约统计
        vo.setTotalAppointments(appointmentMapper.selectCount(null));
        
        LambdaQueryWrapper<Appointment> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(Appointment::getStatus, 2);
        vo.setCompletedAppointments(appointmentMapper.selectCount(completedWrapper));
        
        // 测试统计
        vo.setTotalTests(testResultMapper.selectCount(null));
        
        // 树洞统计
        vo.setTotalMoments(momentMapper.selectCount(null));
        
        LambdaQueryWrapper<Moment> pendingWrapper = new LambdaQueryWrapper<>();
        pendingWrapper.eq(Moment::getAuditStatus, 0);
        vo.setPendingMoments(momentMapper.selectCount(pendingWrapper));
        
        // 今日统计
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        LambdaQueryWrapper<SysUser> todayUserWrapper = new LambdaQueryWrapper<>();
        todayUserWrapper.between(SysUser::getCreatedAt, todayStart, todayEnd);
        vo.setTodayNewUsers(userMapper.selectCount(todayUserWrapper));
        
        LambdaQueryWrapper<Appointment> todayAppointmentWrapper = new LambdaQueryWrapper<>();
        todayAppointmentWrapper.between(Appointment::getCreatedAt, todayStart, todayEnd);
        vo.setTodayNewAppointments(appointmentMapper.selectCount(todayAppointmentWrapper));
        
        LambdaQueryWrapper<Moment> todayMomentWrapper = new LambdaQueryWrapper<>();
        todayMomentWrapper.between(Moment::getCreatedAt, todayStart, todayEnd);
        vo.setTodayNewMoments(momentMapper.selectCount(todayMomentWrapper));
        
        return vo;
    }

    @Override
    public Page<SysUser> getUserList(Integer userType, Integer pageNum, Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (userType != null) {
            wrapper.eq(SysUser::getUserType, userType);
        }
        wrapper.orderByDesc(SysUser::getCreatedAt);
        
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public Page<MomentVO> getPendingMoments(Integer pageNum, Integer pageSize) {
        Page<Moment> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Moment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Moment::getAuditStatus, 0)
                .orderByAsc(Moment::getCreatedAt);
        
        Page<Moment> momentPage = momentMapper.selectPage(page, wrapper);
        
        Page<MomentVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setTotal(momentPage.getTotal());
        
        List<MomentVO> voList = momentPage.getRecords().stream().map(moment -> {
            MomentVO vo = new MomentVO();
            vo.setId(moment.getId());
            vo.setAnonymousId(moment.getAnonymousId());
            vo.setContent(moment.getContent());
            
            if (moment.getImageUrls() != null && !moment.getImageUrls().isEmpty()) {
                try {
                    List<String> urls = objectMapper.readValue(
                        moment.getImageUrls(), 
                        new TypeReference<List<String>>() {}
                    );
                    vo.setImageUrls(urls);
                } catch (Exception e) {
                    log.error("解析图片URL失败", e);
                }
            }
            
            vo.setLikeCount(moment.getLikeCount());
            vo.setCommentCount(moment.getCommentCount());
            vo.setAuditStatus(moment.getAuditStatus());
            vo.setPublishTime(moment.getCreatedAt().toString());
            
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public void auditMoment(Long momentId, Long adminId, Integer auditStatus, String auditReason) {
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null) {
            throw new RuntimeException("树洞不存在");
        }
        
        moment.setAuditStatus(auditStatus);
        moment.setAuditReason(auditReason);
        moment.setAuditorId(adminId);
        moment.setAuditTime(LocalDateTime.now());
        
        momentMapper.updateById(moment);
    }
}
