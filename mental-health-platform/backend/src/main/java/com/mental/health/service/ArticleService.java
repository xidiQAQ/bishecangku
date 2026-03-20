package com.mental.health.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.entity.ArticleCategory;
import com.mental.health.vo.ArticleVO;

import java.util.List;

/**
 * 文章服务接口
 */
public interface ArticleService {

    /**
     * 分页查询文章列表
     */
    Page<ArticleVO> getArticlePage(Integer current, Integer size, Long categoryId, String keyword, Long userId);

    /**
     * 获取文章详情
     */
    ArticleVO getArticleDetail(Long id, Long userId);

    /**
     * 收藏文章
     */
    void collectArticle(Long articleId, Long userId);

    /**
     * 取消收藏
     */
    void uncollectArticle(Long articleId, Long userId);

    /**
     * 获取所有分类
     */
    List<ArticleCategory> getAllCategories();
}
