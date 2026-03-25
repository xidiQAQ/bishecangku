package com.mental.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mental.health.common.utils.AnonymousIdUtil;
import com.mental.health.common.utils.SensitiveWordUtil;
import com.mental.health.dto.MomentCommentDTO;
import com.mental.health.dto.MomentDTO;
import com.mental.health.entity.*;
import com.mental.health.mapper.MomentCommentMapper;
import com.mental.health.mapper.MomentMapper;
import com.mental.health.mapper.MomentLikeMapper;
import com.mental.health.mapper.MomentReportMapper;
import com.mental.health.mapper.MomentCommentLikeMapper;
import com.mental.health.mapper.SensitiveWordMapper;
import com.mental.health.service.MomentService;
import com.mental.health.vo.MomentCommentVO;
import com.mental.health.vo.MomentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentServiceImpl implements MomentService {

    private final MomentMapper momentMapper;
    private final MomentCommentMapper commentMapper;
    private final SensitiveWordMapper sensitiveWordMapper;
    private final MomentLikeMapper momentLikeMapper;
    private final MomentReportMapper momentReportMapper;
    private final MomentCommentLikeMapper commentLikeMapper;
    private final ObjectMapper objectMapper;

    /**
     * 应用启动时初始化敏感词库
     */
    @PostConstruct
    @Override
    public void initSensitiveWords() {
        LambdaQueryWrapper<SensitiveWord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SensitiveWord::getStatus, 1);
        
        List<SensitiveWord> words = sensitiveWordMapper.selectList(wrapper);
        Set<String> wordSet = words.stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toSet());
        
        SensitiveWordUtil.initSensitiveWordMap(wordSet);
        log.info("敏感词库初始化完成");
    }

    @Override
    @Transactional
    public MomentVO publishMoment(Long userId, MomentDTO momentDTO) {
        String content = momentDTO.getContent();
        
        // 敏感词检测
        Set<String> sensitiveWords = SensitiveWordUtil.getSensitiveWords(content);
        
        // 检查是否包含拦截级敏感词（level=2）
        boolean hasBlockWords = false;
        boolean hasWarningWords = false;
        
        if (!sensitiveWords.isEmpty()) {
            for (String word : sensitiveWords) {
                LambdaQueryWrapper<SensitiveWord> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SensitiveWord::getWord, word)
                        .eq(SensitiveWord::getStatus, 1);
                SensitiveWord sw = sensitiveWordMapper.selectOne(wrapper);
                
                if (sw != null && sw.getLevel() == 2) {
                    hasBlockWords = true;
                    break;
                } else if (sw != null && sw.getLevel() == 1) {
                    hasWarningWords = true;
                }
            }
        }
        
        // 如果包含拦截级敏感词，直接拒绝
        if (hasBlockWords) {
            throw new RuntimeException("内容包含违规词汇，发布失败");
        }
        
        // 生成匿名ID
        String anonymousId = AnonymousIdUtil.generateAnonymousId(userId);
        
        // 创建树洞记录
        Moment moment = new Moment();
        moment.setUserId(userId);
        moment.setAnonymousId(anonymousId);
        moment.setContent(content);
        moment.setImageUrls(momentDTO.getImageUrls());
        moment.setLikeCount(0);
        moment.setCommentCount(0);
        moment.setStatus(1);
        
        // 设置审核状态
        if (hasWarningWords) {
            // 包含警告级敏感词，需要人工审核
            moment.setAuditStatus(0);
        } else {
            // 无敏感词，自动通过
            moment.setAuditStatus(1);
        }
        
        momentMapper.insert(moment);
        
        // 返回结果
        MomentVO vo = new MomentVO();
        vo.setId(moment.getId());
        vo.setAnonymousId(anonymousId);
        vo.setContent(content);
        
        if (momentDTO.getImageUrls() != null && !momentDTO.getImageUrls().isEmpty()) {
            try {
                List<String> urls = objectMapper.readValue(
                    momentDTO.getImageUrls(), 
                    new TypeReference<List<String>>() {}
                );
                vo.setImageUrls(urls);
            } catch (Exception e) {
                log.error("解析图片URL失败", e);
            }
        }
        
        vo.setLikeCount(0);
        vo.setCommentCount(0);
        vo.setAuditStatus(moment.getAuditStatus());
        vo.setPublishTime(formatPublishTime(moment.getCreatedAt().toString()));
        
        return vo;
    }

    @Override
    public Page<MomentVO> getMomentList(Long userId, Integer pageNum, Integer pageSize) {
        Page<Moment> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<Moment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Moment::getAuditStatus, 1)
                .eq(Moment::getStatus, 1)
                .orderByDesc(Moment::getCreatedAt);
        
        Page<Moment> momentPage = momentMapper.selectPage(page, wrapper);
        
        Page<MomentVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setTotal(momentPage.getTotal());
        
        List<MomentVO> voList = momentPage.getRecords().stream().map(moment -> {
            MomentVO vo = convertToVO(moment, userId);
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public MomentVO getMomentDetail(Long momentId, Long userId) {
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null) {
            throw new RuntimeException("树洞不存在");
        }
        
        return convertToVO(moment, userId);
    }

    @Override
    @Transactional
    public void likeMoment(Long momentId, Long userId) {
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null) {
            throw new RuntimeException("树洞不存在");
        }
        
        // 检查是否已点赞
        LambdaQueryWrapper<MomentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentLike::getMomentId, momentId)
                .eq(MomentLike::getUserId, userId);
        MomentLike existingLike = momentLikeMapper.selectOne(wrapper);
        
        if (existingLike != null) {
            // 已点赞，取消点赞
            momentLikeMapper.deleteById(existingLike.getId());
            moment.setLikeCount(Math.max(0, moment.getLikeCount() - 1));
        } else {
            // 未点赞，添加点赞
            MomentLike like = new MomentLike();
            like.setMomentId(momentId);
            like.setUserId(userId);
            momentLikeMapper.insert(like);
            moment.setLikeCount(moment.getLikeCount() + 1);
        }
        
        momentMapper.updateById(moment);
    }

    @Override
    @Transactional
    public MomentCommentVO commentMoment(Long userId, MomentCommentDTO commentDTO) {
        // 检查树洞是否存在
        Moment moment = momentMapper.selectById(commentDTO.getMomentId());
        if (moment == null) {
            throw new RuntimeException("树洞不存在");
        }
        
        // 敏感词检测
        if (SensitiveWordUtil.containsSensitiveWord(commentDTO.getContent())) {
            throw new RuntimeException("评论包含敏感词");
        }
        
        // 生成匿名ID
        String anonymousId = AnonymousIdUtil.generateAnonymousId(userId);
        
        // 创建评论
        MomentComment comment = new MomentComment();
        comment.setMomentId(commentDTO.getMomentId());
        comment.setUserId(userId);
        comment.setAnonymousId(anonymousId);
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setLikeCount(0);
        comment.setStatus(1);
        
        commentMapper.insert(comment);
        
        // 更新树洞评论数
        moment.setCommentCount(moment.getCommentCount() + 1);
        momentMapper.updateById(moment);
        
        // 返回结果
        MomentCommentVO vo = new MomentCommentVO();
        BeanUtils.copyProperties(comment, vo);
        return vo;
    }

    @Override
    public List<MomentCommentVO> getCommentList(Long momentId, Long userId) {
        LambdaQueryWrapper<MomentComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentComment::getMomentId, momentId)
                .eq(MomentComment::getStatus, 1)
                .orderByAsc(MomentComment::getCreatedAt);
        
        List<MomentComment> comments = commentMapper.selectList(wrapper);
        return comments.stream().map(comment -> {
            MomentCommentVO vo = new MomentCommentVO();
            BeanUtils.copyProperties(comment, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {
        MomentComment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 检查是否已点赞
        LambdaQueryWrapper<MomentCommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentCommentLike::getCommentId, commentId)
                .eq(MomentCommentLike::getUserId, userId);
        MomentCommentLike existingLike = commentLikeMapper.selectOne(wrapper);
        
        if (existingLike != null) {
            // 已点赞，取消点赞
            commentLikeMapper.deleteById(existingLike.getId());
            comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
        } else {
            // 未点赞，添加点赞
            MomentCommentLike like = new MomentCommentLike();
            like.setCommentId(commentId);
            like.setUserId(userId);
            commentLikeMapper.insert(like);
            comment.setLikeCount(comment.getLikeCount() + 1);
        }
        
        commentMapper.updateById(comment);
    }

    @Override
    @Transactional
    public void deleteMoment(Long momentId, Long userId) {
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null) {
            throw new RuntimeException("树洞不存在");
        }
        
        if (!moment.getUserId().equals(userId)) {
            throw new RuntimeException("只能删除自己的树洞");
        }
        
        momentMapper.deleteById(momentId);
    }

    @Override
    @Transactional
    public void reportMoment(Long momentId, Long userId, String reason) {
        // 检查树洞是否存在
        Moment moment = momentMapper.selectById(momentId);
        if (moment == null) {
            throw new RuntimeException("树洞不存在");
        }
        
        // 检查是否已举报过
        LambdaQueryWrapper<MomentReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MomentReport::getMomentId, momentId)
                .eq(MomentReport::getReporterId, userId)
                .eq(MomentReport::getStatus, 0);
        long count = momentReportMapper.selectCount(wrapper);
        
        if (count > 0) {
            throw new RuntimeException("您已举报过该树洞，请等待处理");
        }
        
        // 创建举报记录
        MomentReport report = new MomentReport();
        report.setMomentId(momentId);
        report.setReporterId(userId);
        report.setReason(reason);
        report.setStatus(0);
        
        momentReportMapper.insert(report);
    }

    /**
     * 转换Moment实体为VO，包含用户相关状态
     */
    private MomentVO convertToVO(Moment moment, Long userId) {
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
        vo.setPublishTime(formatPublishTime(moment.getCreatedAt().toString()));
        
        // 检查是否是当前用户的树洞
        vo.setIsMine(moment.getUserId().equals(userId));
        
        // 检查当前用户是否已点赞
        if (userId != null) {
            LambdaQueryWrapper<MomentLike> likeWrapper = new LambdaQueryWrapper<>();
            likeWrapper.eq(MomentLike::getMomentId, moment.getId())
                    .eq(MomentLike::getUserId, userId);
            long likeCount = momentLikeMapper.selectCount(likeWrapper);
            vo.setIsLiked(likeCount > 0);
        }
        
        return vo;
    }

    /**
     * 格式化发布时间（模糊到小时，保护隐私）
     */
    private String formatPublishTime(String dateTime) {
        try {
            // 只显示到小时
            return dateTime.substring(0, 13) + ":00";
        } catch (Exception e) {
            return dateTime;
        }
    }
}
