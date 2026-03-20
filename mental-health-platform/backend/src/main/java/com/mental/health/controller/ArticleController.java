package com.mental.health.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mental.health.common.result.Result;
import com.mental.health.entity.ArticleCategory;
import com.mental.health.service.ArticleService;
import com.mental.health.vo.ArticleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文章控制器
 */
@Api(tags = "文章接口")
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @ApiOperation("获取文章分类列表")
    @GetMapping("/categories")
    public Result<List<ArticleCategory>> getCategories() {
        try {
            List<ArticleCategory> categories = articleService.getAllCategories();
            return Result.success(categories);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("分页查询文章列表")
    @GetMapping("/page")
    public Result<Page<ArticleVO>> getArticlePage(
            @ApiParam("当前页") @RequestParam(defaultValue = "1") Integer current,
            @ApiParam("每页大小") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam("分类ID") @RequestParam(required = false) Long categoryId,
            @ApiParam("搜索关键词") @RequestParam(required = false) String keyword,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId) {
        try {
            Page<ArticleVO> page = articleService.getArticlePage(current, size, categoryId, keyword, userId);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("获取文章详情")
    @GetMapping("/{id}")
    public Result<ArticleVO> getArticleDetail(
            @ApiParam("文章ID") @PathVariable Long id,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId) {
        try {
            ArticleVO article = articleService.getArticleDetail(id, userId);
            return Result.success(article);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("收藏文章")
    @PostMapping("/{id}/collect")
    public Result<String> collectArticle(
            @ApiParam("文章ID") @PathVariable Long id,
            @ApiParam("用户ID") @RequestParam Long userId) {
        try {
            articleService.collectArticle(id, userId);
            return Result.success("收藏成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @ApiOperation("取消收藏")
    @DeleteMapping("/{id}/collect")
    public Result<String> uncollectArticle(
            @ApiParam("文章ID") @PathVariable Long id,
            @ApiParam("用户ID") @RequestParam Long userId) {
        try {
            articleService.uncollectArticle(id, userId);
            return Result.success("取消收藏成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
