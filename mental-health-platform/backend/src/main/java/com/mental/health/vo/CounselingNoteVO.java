package com.mental.health.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ApiModel("咨询笔记VO")
public class CounselingNoteVO {

    @ApiModelProperty("笔记ID")
    private Long id;

    @ApiModelProperty("预约ID")
    private Long appointmentId;

    @ApiModelProperty("学生ID")
    private Long studentId;

    @ApiModelProperty("学生姓名")
    private String studentName;

    @ApiModelProperty("笔记内容")
    private String noteContent;

    @ApiModelProperty("问题分析")
    private String problemAnalysis;

    @ApiModelProperty("咨询方案")
    private String counselingPlan;

    @ApiModelProperty("后续跟进计划")
    private String followUpPlan;

    @ApiModelProperty("下次预约建议日期")
    private LocalDate nextAppointment;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedAt;
}
