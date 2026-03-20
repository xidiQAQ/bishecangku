package com.mental.health.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mental.health.entity.ArticleCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章分类Mapper
 */
@Mapper
public interface ArticleCategoryMapper extends BaseMapper<ArticleCategory> {
}
