package com.mental.health.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@ApiModel("树洞评论VO")
public class MomentCommentVO {

    @ApiModelProperty("评论ID")
    private Long id;

    @ApiModelProperty("树洞ID")
    private Long momentId;

    @ApiModelProperty("匿名ID")
    private String anonymousId;

    @ApiModelProperty("父评论ID")
    private Long parentId;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("评论时间")
    private LocalDateTime createdAt;

    @ApiModelProperty("是否已点赞")
    private Boolean liked = false;
}
