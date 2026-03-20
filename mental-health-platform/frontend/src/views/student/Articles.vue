<template>
  <div class="articles-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">心理科普</h1>
      <p class="page-subtitle">探索心理健康知识，关爱自己的内心世界</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-bar">
      <el-input
        v-model="searchQuery"
        placeholder="搜索文章标题或内容..."
        clearable
        @change="handleSearch"
        class="search-input"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-select
        v-model="selectedCategory"
        placeholder="选择分类"
        clearable
        @change="handleCategoryChange"
        class="category-select"
      >
        <el-option
          v-for="category in categories"
          :key="category.id"
          :label="category.name"
          :value="category.id"
        />
      </el-select>
    </div>

    <!-- 文章列表 -->
    <div class="articles-list" v-loading="loading">
      <div
        v-for="article in articles"
        :key="article.id"
        class="article-item"
        @click="goToDetail(article.id)"
      >
        <div class="article-cover">
          <img :src="article.coverImage || getDefaultCover()" alt="" />
        </div>
        <div class="article-info">
          <div class="article-header">
            <el-tag size="small" type="primary">{{ article.categoryName }}</el-tag>
            <span class="article-date">{{ formatDate(article.createdAt) }}</span>
          </div>
          <h2 class="article-title">{{ article.title }}</h2>
          <p class="article-summary">{{ article.summary }}</p>
          <div class="article-footer">
            <div class="article-stats">
              <span>
                <el-icon><View /></el-icon>
                {{ article.viewCount }}
              </span>
              <span>
                <el-icon><Star /></el-icon>
                {{ article.collectCount }}
              </span>
            </div>
            <el-button text type="primary">
              阅读全文
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && articles.length === 0" description="暂无文章" />
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="fetchArticles"
        @size-change="fetchArticles"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View, Star, ArrowRight } from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

const router = useRouter()

const searchQuery = ref('')
const selectedCategory = ref(null)
const categories = ref([])
const articles = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchCategories = async () => {
  try {
    const res = await request.get('/articles/categories')
    categories.value = res.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchArticles = async () => {
  try {
    loading.value = true
    const res = await request.get('/articles/page', {
      params: {
        current: pageNum.value,
        size: pageSize.value,
        keyword: searchQuery.value || undefined,
        categoryId: selectedCategory.value || undefined
      }
    })
    articles.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取文章失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pageNum.value = 1
  fetchArticles()
}

const handleCategoryChange = () => {
  pageNum.value = 1
  fetchArticles()
}

const goToDetail = (id) => {
  router.push(`/student/articles/${id}`)
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

const getDefaultCover = () => {
  const colors = ['667eea', 'f093fb', '4facfe', '43e97b']
  const color = colors[Math.floor(Math.random() * colors.length)]
  return `https://via.placeholder.com/300x200/${color}/ffffff?text=心理健康`
}

onMounted(() => {
  fetchCategories()
  fetchArticles()
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.articles-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: $spacing-xl;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 12px;
  background: $gradient-primary;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: $text-secondary;
}

.filter-bar {
  display: flex;
  gap: $spacing-md;
  margin-bottom: $spacing-xl;
}

.search-input {
  flex: 1;
}

.category-select {
  width: 200px;
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
  margin-bottom: $spacing-xl;
}

.article-item {
  background: white;
  border-radius: $radius-lg;
  overflow: hidden;
  display: flex;
  cursor: pointer;
  transition: $transition-base;
  box-shadow: $shadow-md;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-xl;
  }
}

.article-cover {
  width: 300px;
  height: 200px;
  flex-shrink: 0;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: $transition-base;
  }

  &:hover img {
    transform: scale(1.05);
  }
}

.article-info {
  flex: 1;
  padding: $spacing-lg;
  display: flex;
  flex-direction: column;
}

.article-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.article-date {
  font-size: 13px;
  color: $text-tertiary;
}

.article-title {
  font-size: 22px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-summary {
  font-size: 15px;
  color: $text-secondary;
  line-height: 1.6;
  margin-bottom: auto;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid $border-color;
}

.article-stats {
  display: flex;
  gap: 16px;
  font-size: 14px;
  color: $text-tertiary;

  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.pagination {
  display: flex;
  justify-content: center;
}

@media (max-width: 768px) {
  .article-item {
    flex-direction: column;
  }

  .article-cover {
    width: 100%;
    height: 180px;
  }

  .filter-bar {
    flex-direction: column;
  }

  .category-select {
    width: 100%;
  }
}
</style>
