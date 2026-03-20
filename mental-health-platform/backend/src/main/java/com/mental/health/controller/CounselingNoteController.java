package com.mental.health.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.common.result.Result;
import com.mental.health.dto.CounselingNoteDTO;
import com.mental.health.service.CounselingNoteService;
import com.mental.health.vo.CounselingNoteVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "咨询笔记接口")
@RestController
@RequestMapping("/api/counseling-notes")
@RequiredArgsConstructor
public class CounselingNoteController {

    private final CounselingNoteService noteService;

    @ApiOperation("创建咨询笔记")
    @PostMapping
    public Result<CounselingNoteVO> createNote(
            @RequestHeader("userId") Long counselorId,
            @Validated @RequestBody CounselingNoteDTO noteDTO) {
        CounselingNoteVO note = noteService.createNote(counselorId, noteDTO);
        return Result.success(note);
    }

    @ApiOperation("获取咨询笔记列表")
    @GetMapping
    public Result<Page<CounselingNoteVO>> getNoteList(
            @RequestHeader("userId") Long counselorId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<CounselingNoteVO> page = noteService.getNoteList(counselorId, studentId, pageNum, pageSize);
        return Result.success(page);
    }

    @ApiOperation("获取咨询笔记详情")
    @GetMapping("/{noteId}")
    public Result<CounselingNoteVO> getNoteDetail(
            @PathVariable Long noteId,
            @RequestHeader("userId") Long counselorId) {
        CounselingNoteVO note = noteService.getNoteDetail(noteId, counselorId);
        return Result.success(note);
    }

    @ApiOperation("更新咨询笔记")
    @PutMapping("/{noteId}")
    public Result<CounselingNoteVO> updateNote(
            @PathVariable Long noteId,
            @RequestHeader("userId") Long counselorId,
            @Validated @RequestBody CounselingNoteDTO noteDTO) {
        CounselingNoteVO note = noteService.updateNote(noteId, counselorId, noteDTO);
        return Result.success(note);
    }

    @ApiOperation("删除咨询笔记")
    @DeleteMapping("/{noteId}")
    public Result<Void> deleteNote(
            @PathVariable Long noteId,
            @RequestHeader("userId") Long counselorId) {
        noteService.deleteNote(noteId, counselorId);
        return Result.success();
    }
}
