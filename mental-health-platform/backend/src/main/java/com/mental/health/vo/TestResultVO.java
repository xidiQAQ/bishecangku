package com.mental.health.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@ApiModel("测试结果VO")
public class TestResultVO {

    @ApiModelProperty("结果ID")
    private Long id;

    @ApiModelProperty("测试ID")
    private Long testId;

    @ApiModelProperty("测试名称")
    private String testName;

    @ApiModelProperty("测试类型")
    private String testType;

    @ApiModelProperty("总分")
    private Integer totalScore;

    @ApiModelProperty("结果等级")
    private String resultLevel;

    @ApiModelProperty("结果分析")
    private String resultAnalysis;

    @ApiModelProperty("建议")
    private String suggestions;

    @ApiModelProperty("测试用时（秒）")
    private Integer testDuration;

    @ApiModelProperty("测试时间")
    private LocalDateTime createdAt;
}
