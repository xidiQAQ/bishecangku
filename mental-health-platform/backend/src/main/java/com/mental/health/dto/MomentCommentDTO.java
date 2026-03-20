package com.mental.health.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("树洞评论DTO")
public class MomentCommentDTO {

    @ApiModelProperty(value = "树洞ID", required = true)
    @NotNull(message = "树洞ID不能为空")
    private Long momentId;

    @ApiModelProperty("父评论ID（0表示一级评论）")
    private Long parentId = 0L;

    @ApiModelProperty(value = "评论内容", required = true)
    @NotBlank(message = "评论内容不能为空")
    private String content;
}
