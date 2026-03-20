package com.mental.health.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.dto.CounselingNoteDTO;
import com.mental.health.vo.CounselingNoteVO;

public interface CounselingNoteService {

    /**
     * 创建咨询笔记
     */
    CounselingNoteVO createNote(Long counselorId, CounselingNoteDTO noteDTO);

    /**
     * 获取咨询笔记列表（分页）
     */
    Page<CounselingNoteVO> getNoteList(Long counselorId, Long studentId, Integer pageNum, Integer pageSize);

    /**
     * 获取咨询笔记详情
     */
    CounselingNoteVO getNoteDetail(Long noteId, Long counselorId);

    /**
     * 更新咨询笔记
     */
    CounselingNoteVO updateNote(Long noteId, Long counselorId, CounselingNoteDTO noteDTO);

    /**
     * 删除咨询笔记
     */
    void deleteNote(Long noteId, Long counselorId);
}
