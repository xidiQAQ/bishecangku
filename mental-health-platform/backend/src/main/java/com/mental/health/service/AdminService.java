package com.mental.health.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
}
