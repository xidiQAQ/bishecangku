package com.mental.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.common.utils.AesUtil;
import com.mental.health.dto.CounselingNoteDTO;
import com.mental.health.entity.Appointment;
import com.mental.health.entity.CounselingNote;
import com.mental.health.entity.SysUser;
import com.mental.health.mapper.AppointmentMapper;
import com.mental.health.mapper.CounselingNoteMapper;
import com.mental.health.mapper.SysUserMapper;
import com.mental.health.service.CounselingNoteService;
import com.mental.health.vo.CounselingNoteVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CounselingNoteServiceImpl implements CounselingNoteService {

    private final CounselingNoteMapper noteMapper;
    private final AppointmentMapper appointmentMapper;
    private final SysUserMapper userMapper;

    @Override
    @Transactional
    public CounselingNoteVO createNote(Long counselorId, CounselingNoteDTO noteDTO) {
        // 验证预约是否存在且属于该咨询师
        Appointment appointment = appointmentMapper.selectById(noteDTO.getAppointmentId());
        if (appointment == null) {
            throw new RuntimeException("预约不存在");
        }
        if (!appointment.getCounselorId().equals(counselorId)) {
            throw new RuntimeException("无权操作此预约的笔记");
        }

        // 创建笔记（加密存储）
        CounselingNote note = new CounselingNote();
        note.setAppointmentId(noteDTO.getAppointmentId());
        note.setCounselorId(counselorId);
        note.setStudentId(noteDTO.getStudentId());
        
        // 加密敏感字段
        note.setNoteContent(AesUtil.encrypt(noteDTO.getNoteContent()));
        note.setProblemAnalysis(AesUtil.encrypt(noteDTO.getProblemAnalysis()));
        note.setCounselingPlan(AesUtil.encrypt(noteDTO.getCounselingPlan()));
        note.setFollowUpPlan(AesUtil.encrypt(noteDTO.getFollowUpPlan()));
        note.setNextAppointment(noteDTO.getNextAppointment());
        
        noteMapper.insert(note);
        
        // 返回解密后的数据
        return buildNoteVO(note);
    }

    @Override
    public Page<CounselingNoteVO> getNoteList(Long counselorId, Long studentId, Integer pageNum, Integer pageSize) {
        Page<CounselingNote> page = new Page<>(pageNum, pageSize);
        
        LambdaQueryWrapper<CounselingNote> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CounselingNote::getCounselorId, counselorId);
        
        if (studentId != null) {
            wrapper.eq(CounselingNote::getStudentId, studentId);
        }
        
        wrapper.orderByDesc(CounselingNote::getCreatedAt);
        
        Page<CounselingNote> notePage = noteMapper.selectPage(page, wrapper);
        
        Page<CounselingNoteVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setTotal(notePage.getTotal());
        
        List<CounselingNoteVO> voList = notePage.getRecords().stream()
                .map(this::buildNoteVO)
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public CounselingNoteVO getNoteDetail(Long noteId, Long counselorId) {
        CounselingNote note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }
        
        // 权限检查：仅咨询师本人可查看
        if (!note.getCounselorId().equals(counselorId)) {
            throw new RuntimeException("无权查看此笔记");
        }
        
        return buildNoteVO(note);
    }

    @Override
    @Transactional
    public CounselingNoteVO updateNote(Long noteId, Long counselorId, CounselingNoteDTO noteDTO) {
        CounselingNote note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }
        
        // 权限检查
        if (!note.getCounselorId().equals(counselorId)) {
            throw new RuntimeException("无权修改此笔记");
        }
        
        // 更新加密字段
        note.setNoteContent(AesUtil.encrypt(noteDTO.getNoteContent()));
        note.setProblemAnalysis(AesUtil.encrypt(noteDTO.getProblemAnalysis()));
        note.setCounselingPlan(AesUtil.encrypt(noteDTO.getCounselingPlan()));
        note.setFollowUpPlan(AesUtil.encrypt(noteDTO.getFollowUpPlan()));
        note.setNextAppointment(noteDTO.getNextAppointment());
        
        noteMapper.updateById(note);
        
        return buildNoteVO(note);
    }

    @Override
    @Transactional
    public void deleteNote(Long noteId, Long counselorId) {
        CounselingNote note = noteMapper.selectById(noteId);
        if (note == null) {
            throw new RuntimeException("笔记不存在");
        }
        
        // 权限检查
        if (!note.getCounselorId().equals(counselorId)) {
            throw new RuntimeException("无权删除此笔记");
        }
        
        noteMapper.deleteById(noteId);
    }

    /**
     * 构建VO对象（解密数据）
     */
    private CounselingNoteVO buildNoteVO(CounselingNote note) {
        CounselingNoteVO vo = new CounselingNoteVO();
        vo.setId(note.getId());
        vo.setAppointmentId(note.getAppointmentId());
        vo.setStudentId(note.getStudentId());
        
        // 获取学生姓名
        SysUser student = userMapper.selectById(note.getStudentId());
        if (student != null) {
            vo.setStudentName(student.getRealName());
        }
        
        // 解密敏感字段
        vo.setNoteContent(AesUtil.decrypt(note.getNoteContent()));
        vo.setProblemAnalysis(AesUtil.decrypt(note.getProblemAnalysis()));
        vo.setCounselingPlan(AesUtil.decrypt(note.getCounselingPlan()));
        vo.setFollowUpPlan(AesUtil.decrypt(note.getFollowUpPlan()));
        vo.setNextAppointment(note.getNextAppointment());
        vo.setCreatedAt(note.getCreatedAt());
        vo.setUpdatedAt(note.getUpdatedAt());
        
        return vo;
    }
}
