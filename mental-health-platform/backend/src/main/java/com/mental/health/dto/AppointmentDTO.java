package com.mental.health.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 预约请求DTO
 */
@Data
public class AppointmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "咨询师ID不能为空")
    private Long counselorId;

    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;

    @NotBlank(message = "时间段不能为空")
    private String timeSlot;

    @NotBlank(message = "咨询类型不能为空")
    private String consultType;

    private String problemDesc;
    private String studentRemark;
    
    // 评价相关字段
    private Integer rating;
    private String comment;
}
