package com.mental.health.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约记录实体类
 */
@Data
@TableName("appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String appointmentNo;
    private Long studentId;
    private Long counselorId;
    private Long scheduleId;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String consultType;
    private String problemDesc;
    private Integer status; // 0-待确认 1-已确认 2-已完成 3-已取消 4-已拒绝
    private String cancelReason;
    private String studentRemark;
    private String counselorRemark;
    private Integer rating;
    private String comment;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;
}
