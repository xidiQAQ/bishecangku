package com.mental.health.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.common.result.Result;
import com.mental.health.dto.MomentCommentDTO;
import com.mental.health.dto.MomentDTO;
import com.mental.health.service.MomentService;
import com.mental.health.vo.MomentCommentVO;
import com.mental.health.vo.MomentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "树洞接口")
@RestController
@RequestMapping("/api/moments")
@RequiredArgsConstructor
public class MomentController {

    private final MomentService momentService;

    @ApiOperation("发布树洞")
    @PostMapping
    public Result<MomentVO> publishMoment(
            @RequestHeader("userId") Long userId,
            @Validated @RequestBody MomentDTO momentDTO) {
        MomentVO moment = momentService.publishMoment(userId, momentDTO);
        return Result.success(moment);
    }

    @ApiOperation("获取树洞列表")
    @GetMapping
    public Result<Page<MomentVO>> getMomentList(
            @RequestHeader("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<MomentVO> page = momentService.getMomentList(userId, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("获取树洞详情")
    @GetMapping("/{momentId}")
    public Result<MomentVO> getMomentDetail(
            @PathVariable Long momentId,
            @RequestHeader("userId") Long userId) {
        MomentVO moment = momentService.getMomentDetail(momentId, userId);
        return Result.success(moment);
    }

    @ApiOperation("点赞树洞")
    @PostMapping("/{momentId}/like")
    public Result<Void> likeMoment(
            @PathVariable Long momentId,
            @RequestHeader("userId") Long userId) {
        momentService.likeMoment(momentId, userId);
        return Result.success();
    }

    @ApiOperation("评论树洞")
    @PostMapping("/comments")
    public Result<MomentCommentVO> commentMoment(
            @RequestHeader("userId") Long userId,
            @Validated @RequestBody MomentCommentDTO commentDTO) {
        MomentCommentVO comment = momentService.commentMoment(userId, commentDTO);
        return Result.success(comment);
    }

    @ApiOperation("获取树洞评论列表")
    @GetMapping("/{momentId}/comments")
    public Result<List<MomentCommentVO>> getCommentList(
            @PathVariable Long momentId,
            @RequestHeader("userId") Long userId) {
        List<MomentCommentVO> comments = momentService.getCommentList(momentId, userId);
        return Result.success(comments);
    }

    @ApiOperation("点赞评论")
    @PostMapping("/comments/{commentId}/like")
    public Result<Void> likeComment(
            @PathVariable Long commentId,
            @RequestHeader("userId") Long userId) {
        momentService.likeComment(commentId, userId);
        return Result.success();
    }

    @ApiOperation("删除树洞")
    @DeleteMapping("/{momentId}")
    public Result<Void> deleteMoment(
            @PathVariable Long momentId,
            @RequestHeader("userId") Long userId) {
        momentService.deleteMoment(momentId, userId);
        return Result.success();
    }

    @ApiOperation("举报树洞")
    @PostMapping("/{momentId}/report")
    public Result<Void> reportMoment(
            @PathVariable Long momentId,
            @RequestHeader("userId") Long userId,
            @RequestParam String reason) {
        momentService.reportMoment(momentId, userId, reason);
        return Result.success();
    }
}
