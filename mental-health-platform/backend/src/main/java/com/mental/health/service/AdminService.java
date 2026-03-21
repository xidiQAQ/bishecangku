package com.mental.health.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.entity.Article;
import com.mental.health.entity.SysUser;
import com.mental.health.vo.AdminStatisticsVO;
import com.mental.health.vo.MomentVO;

public interface AdminService {

    /**
     * 获取统计数据
     */
    AdminStatisticsVO getStatistics();

    /**
     * 获取用户列表
     */
    Page<SysUser> getUserList(Integer userType, Integer pageNum, Integer pageSize);

    /**
     * 更新用户状态
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 获取待审核树洞列表
     */
    Page<MomentVO> getPendingMoments(Integer pageNum, Integer pageSize);

    /**
     * 审核树洞
     */
    void auditMoment(Long momentId, Long adminId, Integer auditStatus, String auditReason);

    /**
     * 获取文章列表
     */
    Page getArticles(String title, Long categoryId, Integer current, Integer size);

    /**
     * 创建文章
     */
    void createArticle(Article article);

    /**
     * 更新文章
     */
    void updateArticle(Article article);

    /**
     * 删除文章
     */
    void deleteArticle(Long id);

    /**
     * 获取咨询师列表
     */
    Page getCounselors(Integer current, Integer size);

    /**
     * 添加咨询师
     */
    void addCounselor(com.mental.health.dto.CounselorDTO dto);

    /**
     * 更新咨询师
     */
    void updateCounselor(Long id, com.mental.health.dto.CounselorDTO dto);

    /**
     * 获取所有预约列表（管理员）
     */
    Page getAllAppointments(Integer current, Integer size, Integer status);
}
