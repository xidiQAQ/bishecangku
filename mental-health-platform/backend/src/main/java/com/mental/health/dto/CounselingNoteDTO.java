package com.mental.health.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@ApiModel("咨询笔记DTO")
public class CounselingNoteDTO {

    @ApiModelProperty(value = "预约ID", required = true)
    @NotNull(message = "预约ID不能为空")
    private Long appointmentId;

    @ApiModelProperty(value = "学生ID", required = true)
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @ApiModelProperty(value = "笔记内容", required = true)
    @NotBlank(message = "笔记内容不能为空")
    private String noteContent;

    @ApiModelProperty("问题分析")
    private String problemAnalysis;

    @ApiModelProperty("咨询方案")
    private String counselingPlan;

    @ApiModelProperty("后续跟进计划")
    private String followUpPlan;

    @ApiModelProperty("下次预约建议日期")
    private LocalDate nextAppointment;
}
