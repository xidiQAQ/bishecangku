package com.mental.health.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
@ApiModel("测试题目VO")
public class TestQuestionVO {

    @ApiModelProperty("题目ID")
    private Long id;

    @ApiModelProperty("题号")
    private Integer questionNo;

    @ApiModelProperty("题目内容")
    private String questionText;

    @ApiModelProperty("选项列表")
    private List<Option> options;

    @ApiModelProperty("题型 1-单选 2-多选")
    private Integer questionType;

    @Data
    public static class Option {
        @ApiModelProperty("选项键")
        private String key;

        @ApiModelProperty("选项值")
        private String value;

        @ApiModelProperty("分数")
        private Integer score;
    }
}
