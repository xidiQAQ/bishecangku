package com.mental.health.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel("测试答题DTO")
public class TestAnswerDTO {

    @ApiModelProperty(value = "测试ID", required = true)
    @NotNull(message = "测试ID不能为空")
    private Long testId;

    @ApiModelProperty(value = "答案列表", required = true)
    @NotNull(message = "答案不能为空")
    private List<Answer> answers;

    @ApiModelProperty("测试用时（秒）")
    private Integer testDuration;

    @Data
    public static class Answer {
        @ApiModelProperty("题目ID")
        private Long questionId;

        @ApiModelProperty("题号")
        private Integer questionNo;

        @ApiModelProperty("答案")
        private String answer;

        @ApiModelProperty("得分")
        private Integer score;
    }
}
