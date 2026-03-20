<template>
  <div class="article-detail-page" v-loading="loading">
    <div class="article-container" v-if="article">
      <!-- 文章头部 -->
      <div class="article-header">
        <el-tag type="primary" size="large">{{ article.categoryName }}</el-tag>
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span>
            <el-icon><Clock /></el-icon>
            {{ formatDate(article.createdAt) }}
          </span>
          <span>
            <el-icon><View /></el-icon>
            {{ article.viewCount }} 阅读
          </span>
          <span>
            <el-icon><Star /></el-icon>
            {{ article.collectCount }} 收藏
          </span>
        </div>
      </div>

      <!-- 文章封面 -->
      <div class="article-cover" v-if="article.coverImage">
        <img :src="article.coverImage" alt="" />
      </div>

      <!-- 文章内容 -->
      <div class="article-content" v-html="article.content"></div>

      <!-- 文章底部操作 -->
      <div class="article-actions">
        <el-button
          :type="isCollected ? 'primary' : 'default'"
          :icon="isCollected ? Star : StarFilled"
          @click="toggleCollect"
          size="large"
        >
          {{ isCollected ? '已收藏' : '收藏文章' }}
        </el-button>
        <el-button @click="router.back()" size="large">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>
      </div>
    </div>

    <!-- 推荐文章 -->
    <div class="recommend-section" v-if="recommendArticles.length > 0">
      <h2 class="section-title">相关推荐</h2>
      <div class="recommend-list">
        <div
          v-for="item in recommendArticles"
          :key="item.id"
          class="recommend-item"
          @click="goToArticle(item.id)"
        >
          <div class="recommend-cover">
            <img :src="item.coverImage || getDefaultCover()" alt="" />
          </div>
          <div class="recommend-info">
            <h3 class="recommend-title">{{ item.title }}</h3>
            <p class="recommend-summary">{{ item.summary }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Clock, View, Star, StarFilled, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

const article = ref(null)
const loading = ref(false)
const isCollected = ref(false)
const recommendArticles = ref([])

const fetchArticle = async () => {
  try {
    loading.value = true
    const res = await request.get(`/articles/${route.params.id}`)
    article.value = res.data
    isCollected.value = res.data.isCollected || false
  } catch (error) {
    console.error('获取文章失败:', error)
    ElMessage.error('文章不存在或已删除')
    router.back()
  } finally {
    loading.value = false
  }
}

const fetchRecommendArticles = async () => {
  try {
    const res = await request.get('/articles/page', {
      params: {
        current: 1,
        size: 3,
        categoryId: article.value?.categoryId
      }
    })
    recommendArticles.value = (res.data.records || []).filter(
      (item) => item.id !== article.value?.id
    )
  } catch (error) {
    console.error('获取推荐文章失败:', error)
  }
}

const toggleCollect = async () => {
  try {
    if (isCollected.value) {
      await request.delete(`/articles/${route.params.id}/collect`)
      ElMessage.success('取消收藏成功')
      isCollected.value = false
      article.value.collectCount--
    } else {
      await request.post(`/articles/${route.params.id}/collect`)
      ElMessage.success('收藏成功')
      isCollected.value = true
      article.value.collectCount++
    }
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error(error.message || '操作失败')
  }
}

const goToArticle = (id) => {
  router.push(`/student/articles/${id}`)
  fetchArticle()
  fetchRecommendArticles()
  window.scrollTo(0, 0)
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY年MM月DD日')
}

const getDefaultCover = () => {
  return 'https://via.placeholder.com/200x150/667eea/ffffff?text=心理健康'
}

onMounted(() => {
  fetchArticle()
  setTimeout(() => {
    if (article.value) {
      fetchRecommendArticles()
    }
  }, 500)
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.article-detail-page {
  max-width: 900px;
  margin: 0 auto;
}

.article-container {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl * 2;
  margin-bottom: $spacing-xl;
  box-shadow: $shadow-md;
}

.article-header {
  text-align: center;
  margin-bottom: $spacing-xl;
}

.article-title {
  font-size: 32px;
  font-weight: 700;
  color: $text-primary;
  margin: 20px 0;
  line-height: 1.4;
}

.article-meta {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  font-size: 14px;
  color: $text-tertiary;

  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.article-cover {
  margin-bottom: $spacing-xl;
  border-radius: $radius-lg;
  overflow: hidden;

  img {
    width: 100%;
    height: auto;
    display: block;
  }
}

.article-content {
  font-size: 16px;
  line-height: 1.8;
  color: $text-primary;
  margin-bottom: $spacing-xl;

  :deep(p) {
    margin-bottom: 16px;
  }

  :deep(h2) {
    font-size: 24px;
    font-weight: 600;
    margin: 32px 0 16px;
    color: $text-primary;
  }

  :deep(h3) {
    font-size: 20px;
    font-weight: 600;
    margin: 24px 0 12px;
    color: $text-primary;
  }

  :deep(ul),
  :deep(ol) {
    margin: 16px 0;
    padding-left: 24px;
  }

  :deep(li) {
    margin-bottom: 8px;
  }

  :deep(blockquote) {
    border-left: 4px solid $primary-color;
    padding-left: 16px;
    margin: 16px 0;
    color: $text-secondary;
    font-style: italic;
  }

  :deep(img) {
    max-width: 100%;
    border-radius: $radius-md;
    margin: 16px 0;
  }
}

.article-actions {
  display: flex;
  gap: $spacing-md;
  justify-content: center;
  padding-top: $spacing-xl;
  border-top: 1px solid $border-color;
}

.recommend-section {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-md;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: $spacing-lg;
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.recommend-item {
  display: flex;
  gap: $spacing-md;
  padding: $spacing-md;
  border-radius: $radius-md;
  cursor: pointer;
  transition: $transition-base;

  &:hover {
    background: $bg-color;
  }
}

.recommend-cover {
  width: 120px;
  height: 90px;
  flex-shrink: 0;
  border-radius: $radius-md;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.recommend-info {
  flex: 1;
}

.recommend-title {
  font-size: 16px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.recommend-summary {
  font-size: 14px;
  color: $text-secondary;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

@media (max-width: 768px) {
  .article-container {
    padding: $spacing-lg;
  }

  .article-title {
    font-size: 24px;
  }

  .article-content {
    font-size: 15px;
  }

  .article-actions {
    flex-direction: column;
  }
}
</style>
