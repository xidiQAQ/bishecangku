package com.mental.health.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@ApiModel("心理测试VO")
public class TestVO {

    @ApiModelProperty("测试ID")
    private Long id;

    @ApiModelProperty("测试名称")
    private String name;

    @ApiModelProperty("测试说明")
    private String description;

    @ApiModelProperty("测试类型")
    private String testType;

    @ApiModelProperty("题目总数")
    private Integer totalQuestions;

    @ApiModelProperty("时间限制（分钟）")
    private Integer timeLimit;

    @ApiModelProperty("封面图")
    private String coverImage;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdAt;
}
