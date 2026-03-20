package com.mental.health.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.dto.MomentCommentDTO;
import com.mental.health.dto.MomentDTO;
import com.mental.health.vo.MomentCommentVO;
import com.mental.health.vo.MomentVO;

import java.util.List;

public interface MomentService {

    /**
     * 发布树洞
     */
    MomentVO publishMoment(Long userId, MomentDTO momentDTO);

    /**
     * 获取树洞列表（分页）
     */
    Page<MomentVO> getMomentList(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 获取树洞详情
     */
    MomentVO getMomentDetail(Long momentId, Long userId);

    /**
     * 点赞/取消点赞树洞
     */
    void likeMoment(Long momentId, Long userId);

    /**
     * 评论树洞
     */
    MomentCommentVO commentMoment(Long userId, MomentCommentDTO commentDTO);

    /**
     * 获取树洞评论列表
     */
    List<MomentCommentVO> getCommentList(Long momentId, Long userId);

    /**
     * 点赞/取消点赞评论
     */
    void likeComment(Long commentId, Long userId);

    /**
     * 删除树洞（仅本人）
     */
    void deleteMoment(Long momentId, Long userId);

    /**
     * 初始化敏感词库
     */
    void initSensitiveWords();
}
