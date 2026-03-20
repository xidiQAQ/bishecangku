package com.mental.health.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel("树洞发布DTO")
public class MomentDTO {

    @ApiModelProperty(value = "内容", required = true)
    @NotBlank(message = "内容不能为空")
    private String content;

    @ApiModelProperty("图片URL列表（JSON数组字符串）")
    private String imageUrls;
}
