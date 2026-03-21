package com.mental.health.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 预约VO
 */
@Data
public class AppointmentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String appointmentNo;
    private Long studentId;
    private String studentName;
    private Long counselorId;
    private String counselorName;
    private String counselorTitle;
    private String counselorAvatar;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String startTime;
    private String endTime;
    private String topic;
    private String consultType;
    private String problemDesc;
    private Integer status;
    private String statusText;
    private String cancelReason;
    private String studentRemark;
    private String counselorRemark;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
