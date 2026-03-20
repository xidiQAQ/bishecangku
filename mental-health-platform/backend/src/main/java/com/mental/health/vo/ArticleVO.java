package com.mental.health.vo;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文章VO
 */
@Data
public class ArticleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private String author;
    private Integer viewCount;
    private Integer collectCount;
    private LocalDateTime publishTime;
    private Boolean isCollected; // 当前用户是否已收藏
}
