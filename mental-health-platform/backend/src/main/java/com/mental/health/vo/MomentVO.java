package com.mental.health.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("树洞VO")
public class MomentVO {

    @ApiModelProperty("树洞ID")
    private Long id;

    @ApiModelProperty("匿名ID")
    private String anonymousId;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("图片URL列表")
    private List<String> imageUrls;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("评论数")
    private Integer commentCount;

    @ApiModelProperty("审核状态 0-待审核 1-已通过 2-已拒绝")
    private Integer auditStatus;

    @ApiModelProperty("审核意见")
    private String auditReason;

    @ApiModelProperty("发布时间（模糊到小时）")
    private String publishTime;

    @ApiModelProperty("是否已点赞")
    private Boolean liked = false;
}
