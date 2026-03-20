package com.mental.health.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.common.exception.BusinessException;
import com.mental.health.entity.Article;
import com.mental.health.entity.ArticleCategory;
import com.mental.health.entity.ArticleCollect;
import com.mental.health.mapper.ArticleCategoryMapper;
import com.mental.health.mapper.ArticleCollectMapper;
import com.mental.health.mapper.ArticleMapper;
import com.mental.health.service.ArticleService;
import com.mental.health.vo.ArticleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 文章服务实现类
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleCategoryMapper categoryMapper;

    @Autowired
    private ArticleCollectMapper collectMapper;

    @Override
    public Page<ArticleVO> getArticlePage(Integer current, Integer size, Long categoryId, String keyword, Long userId) {
        // 构建查询条件
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, 1); // 只查询已发布的文章
        
        if (categoryId != null) {
            wrapper.eq(Article::getCategoryId, categoryId);
        }
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Article::getTitle, keyword)
                    .or()
                    .like(Article::getSummary, keyword));
        }
        
        wrapper.orderByDesc(Article::getPublishTime);

        // 分页查询
        Page<Article> page = new Page<>(current, size);
        articleMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<ArticleVO> voPage = new Page<>(current, size, page.getTotal());
        List<ArticleVO> voList = page.getRecords().stream().map(article -> {
            ArticleVO vo = new ArticleVO();
            BeanUtils.copyProperties(article, vo);
            
            // 查询分类名称
            ArticleCategory category = categoryMapper.selectById(article.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
            
            // 查询是否已收藏
            if (userId != null) {
                LambdaQueryWrapper<ArticleCollect> collectWrapper = new LambdaQueryWrapper<>();
                collectWrapper.eq(ArticleCollect::getUserId, userId)
                        .eq(ArticleCollect::getArticleId, article.getId());
                Long count = collectMapper.selectCount(collectWrapper);
                vo.setIsCollected(count > 0);
            }
            
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    @Transactional
    public ArticleVO getArticleDetail(Long id, Long userId) {
        Article article = articleMapper.selectById(id);
        if (article == null || article.getStatus() != 1) {
            throw new RuntimeException("文章不存在或已下架");
        }

        // 增加浏览次数
        article.setViewCount(article.getViewCount() + 1);
        articleMapper.updateById(article);

        // 转换为VO
        ArticleVO vo = new ArticleVO();
        BeanUtils.copyProperties(article, vo);

        // 查询分类名称
        ArticleCategory category = categoryMapper.selectById(article.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        // 查询是否已收藏
        if (userId != null) {
            LambdaQueryWrapper<ArticleCollect> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ArticleCollect::getUserId, userId)
                    .eq(ArticleCollect::getArticleId, id);
            Long count = collectMapper.selectCount(wrapper);
            vo.setIsCollected(count > 0);
        }

        return vo;
    }

    @Override
    @Transactional
    public void collectArticle(Long articleId, Long userId) {
        // 检查文章是否存在
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 检查是否已收藏
        LambdaQueryWrapper<ArticleCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleCollect::getUserId, userId)
                .eq(ArticleCollect::getArticleId, articleId);
        Long count = collectMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("已收藏该文章");
        }

        // 添加收藏记录
        ArticleCollect collect = new ArticleCollect();
        collect.setUserId(userId);
        collect.setArticleId(articleId);
        collectMapper.insert(collect);

        // 更新文章收藏数
        article.setCollectCount(article.getCollectCount() + 1);
        articleMapper.updateById(article);
    }

    @Override
    @Transactional
    public void uncollectArticle(Long articleId, Long userId) {
        // 删除收藏记录
        LambdaQueryWrapper<ArticleCollect> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleCollect::getUserId, userId)
                .eq(ArticleCollect::getArticleId, articleId);
        collectMapper.delete(wrapper);

        // 更新文章收藏数
        Article article = articleMapper.selectById(articleId);
        if (article != null && article.getCollectCount() > 0) {
            article.setCollectCount(article.getCollectCount() - 1);
            articleMapper.updateById(article);
        }
    }

    @Override
    public List<ArticleCategory> getAllCategories() {
        LambdaQueryWrapper<ArticleCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ArticleCategory::getStatus, 1)
                .orderByAsc(ArticleCategory::getSortOrder);
        return categoryMapper.selectList(wrapper);
    }
}
