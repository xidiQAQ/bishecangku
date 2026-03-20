package com.mental.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 咨询师时间表实体类
 */
@Data
@TableName("counselor_schedule")
public class CounselorSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long counselorId;
    private LocalDate scheduleDate;
    private String timeSlot;
    private Integer status; // 0-休息 1-可预约 2-已预约 3-已完成

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
