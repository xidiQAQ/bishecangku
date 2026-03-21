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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final CounselorInfoMapper counselorInfoMapper;
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

    @Override
    public Page getArticles(String title, Long categoryId, Integer current, Integer size) {
        Page<Article> page = new Page<>(current, size);
        
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like(Article::getTitle, title);
        }
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        wrapper.orderByDesc(Article::getCreatedAt);
        
        return articleMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void createArticle(Article article) {
        articleMapper.insert(article);
    }

    @Override
    @Transactional
    public void updateArticle(Article article) {
        articleMapper.updateById(article);
    }

    @Override
    @Transactional
    public void deleteArticle(Long id) {
        articleMapper.deleteById(id);
    }

    @Override
    public Page getCounselors(Integer current, Integer size) {
        Page<SysUser> page = new Page<>(current, size);
        
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserType, 2)
                .orderByDesc(SysUser::getCreatedAt);
        
        return userMapper.selectPage(page, wrapper);
    }

    @Override
    @Transactional
    public void addCounselor(com.mental.health.dto.CounselorDTO dto) {
        // 创建用户账号
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword()); // 实际应该加密
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setUserType(2); // 咨询师
        user.setStatus(1);
        userMapper.insert(user);
        
        // 创建咨询师信息
        CounselorInfo info = new CounselorInfo();
        info.setUserId(user.getId());
        info.setTitle(dto.getTitle());
        info.setSpecialty(dto.getSpecialty());
        info.setIntroduction(dto.getIntroduction());
        info.setEducation(dto.getEducation());
        info.setCertificate(dto.getCertificate());
        info.setRating(java.math.BigDecimal.ZERO);
        info.setRatingCount(0);
        counselorInfoMapper.insert(info);
    }

    @Override
    @Transactional
    public void updateCounselor(Long id, com.mental.health.dto.CounselorDTO dto) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        userMapper.updateById(user);
        
        LambdaQueryWrapper<CounselorInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CounselorInfo::getUserId, id);
        CounselorInfo info = counselorInfoMapper.selectOne(wrapper);
        
        if (info != null) {
            info.setTitle(dto.getTitle());
            info.setSpecialty(dto.getSpecialty());
            info.setIntroduction(dto.getIntroduction());
            info.setEducation(dto.getEducation());
            info.setCertificate(dto.getCertificate());
            counselorInfoMapper.updateById(info);
        }
    }

    @Override
    public Page getAllAppointments(Integer current, Integer size, Integer status) {
        Page<Appointment> page = new Page<>(current, size);
        
        LambdaQueryWrapper<Appointment> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Appointment::getStatus, status);
        }
        wrapper.orderByDesc(Appointment::getCreatedAt);
        
        Page<Appointment> appointmentPage = appointmentMapper.selectPage(page, wrapper);
        
        Page<com.mental.health.vo.AppointmentVO> voPage = new Page<>(current, size);
        voPage.setTotal(appointmentPage.getTotal());
        
        List<com.mental.health.vo.AppointmentVO> voList = appointmentPage.getRecords().stream().map(appointment -> {
            com.mental.health.vo.AppointmentVO vo = new com.mental.health.vo.AppointmentVO();
            org.springframework.beans.BeanUtils.copyProperties(appointment, vo);
            
            // 查询学生信息
            SysUser student = userMapper.selectById(appointment.getStudentId());
            if (student != null) {
                vo.setStudentName(student.getRealName());
            }
            
            // 通过counselor_info.id查询咨询师信息
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
}
