<template>
  <div class="home-page">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="banner-text">
          <h1 class="banner-title">
            你好，{{ userInfo.realName || '同学' }} 👋
          </h1>
          <p class="banner-subtitle">
            每一个情绪都值得被看见，每一份困扰都值得被倾听
          </p>
          <div class="banner-actions">
            <el-button type="primary" size="large" round @click="goToCounselors">
              <el-icon><Calendar /></el-icon>
              立即预约咨询
            </el-button>
            <el-button size="large" round @click="goToTests">
              <el-icon><DataAnalysis /></el-icon>
              心理测评
            </el-button>
          </div>
        </div>
        <div class="banner-illustration">
          <img src="https://illustrations.popsy.co/amber/mental-health.svg" alt="心理健康" />
        </div>
      </div>
    </div>

    <!-- 快捷入口 -->
    <div class="quick-access">
      <div
        v-for="item in quickItems"
        :key="item.title"
        class="quick-item"
        @click="router.push(item.path)"
      >
        <div class="quick-icon" :style="{ background: item.color }">
          <el-icon><component :is="item.icon" /></el-icon>
        </div>
        <h3 class="quick-title">{{ item.title }}</h3>
        <p class="quick-desc">{{ item.desc }}</p>
      </div>
    </div>

    <!-- 推荐文章 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><Document /></el-icon>
          心理科普推荐
        </h2>
        <el-button text @click="router.push('/student/articles')">
          查看更多
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <div class="article-grid" v-loading="articlesLoading">
        <div
          v-for="article in articles"
          :key="article.id"
          class="article-card"
          @click="goToArticle(article.id)"
        >
          <div class="article-cover">
            <img :src="article.coverImage || getDefaultCover()" alt="" />
            <div class="article-category">{{ article.categoryName }}</div>
          </div>
          <div class="article-content">
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-summary">{{ article.summary }}</p>
            <div class="article-meta">
              <span>
                <el-icon><View /></el-icon>
                {{ article.viewCount }}
              </span>
              <span>
                <el-icon><Star /></el-icon>
                {{ article.collectCount }}
              </span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 咨询师推荐 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><User /></el-icon>
          优秀咨询师
        </h2>
        <el-button text @click="router.push('/student/counselors')">
          查看更多
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>

      <div class="counselor-grid" v-loading="counselorsLoading">
        <div
          v-for="counselor in counselors"
          :key="counselor.id"
          class="counselor-card"
          @click="goToCounselor(counselor.id)"
        >
          <el-avatar :size="80" :src="counselor.avatar">
            {{ counselor.realName?.charAt(0) }}
          </el-avatar>
          <h3 class="counselor-name">{{ counselor.realName }}</h3>
          <p class="counselor-title">{{ counselor.title }}</p>
          <div class="counselor-rating">
            <el-rate v-model="counselor.rating" disabled show-score />
          </div>
          <div class="counselor-tags">
            <el-tag
              v-for="(tag, index) in counselor.specialty?.slice(0, 2)"
              :key="index"
              size="small"
              type="info"
            >
              {{ tag }}
            </el-tag>
          </div>
        </div>
      </div>
    </section>

    <!-- 温馨提示 -->
    <div class="tips-card">
      <div class="tips-icon">💡</div>
      <div class="tips-content">
        <h3>温馨提示</h3>
        <p>如果您正在经历严重的心理困扰，请及时寻求专业帮助。我们的咨询师随时为您提供支持。</p>
        <p class="tips-hotline">24小时心理援助热线：<strong>400-161-9995</strong></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Calendar,
  DataAnalysis,
  Document,
  ChatDotRound,
  User,
  View,
  Star,
  ArrowRight
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)

const quickItems = [
  {
    title: '心理科普',
    desc: '了解心理健康知识',
    icon: Document,
    color: 'linear-gradient(135deg, #06b6d4 0%, #3b82f6 100%)',
    path: '/student/articles'
  },
  {
    title: '咨询预约',
    desc: '预约专业心理咨询',
    icon: Calendar,
    color: 'linear-gradient(135deg, #f59e0b 0%, #ef4444 100%)',
    path: '/student/counselors'
  },
  {
    title: '心理测试',
    desc: '了解自己的心理状态',
    icon: DataAnalysis,
    color: 'linear-gradient(135deg, #06b6d4 0%, #10b981 100%)',
    path: '/student/tests'
  },
  {
    title: '心灵树洞',
    desc: '匿名分享你的心情',
    icon: ChatDotRound,
    color: 'linear-gradient(135deg, #10b981 0%, #06b6d4 100%)',
    path: '/student/moments'
  }
]

const articles = ref([])
const articlesLoading = ref(false)

const counselors = ref([])
const counselorsLoading = ref(false)

const fetchArticles = async () => {
  try {
    articlesLoading.value = true
    const res = await request.get('/articles/page', {
      params: { current: 1, size: 4 }
    })
    articles.value = res.data.records || []
  } catch (error) {
    console.error('获取文章失败:', error)
  } finally {
    articlesLoading.value = false
  }
}

const fetchCounselors = async () => {
  try {
    counselorsLoading.value = true
    const res = await request.get('/appointments/counselors', {
      params: { current: 1, size: 4 }
    })
    counselors.value = res.data.records || []
  } catch (error) {
    console.error('获取咨询师失败:', error)
  } finally {
    counselorsLoading.value = false
  }
}

const goToCounselors = () => {
  router.push('/student/counselors')
}

const goToTests = () => {
  router.push('/student/tests')
}

const goToArticle = (id) => {
  router.push(`/student/articles/${id}`)
}

const goToCounselor = (id) => {
  router.push(`/student/counselors/${id}`)
}

const getDefaultCover = () => {
  const colors = ['667eea', 'f093fb', '4facfe', '43e97b']
  const color = colors[Math.floor(Math.random() * colors.length)]
  return `https://via.placeholder.com/400x250/${color}/ffffff?text=心理健康`
}

onMounted(() => {
  fetchArticles()
  fetchCounselors()
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.home-page {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: $radius-xl;
  padding: 60px;
  margin-bottom: $spacing-xl;
  color: white;
  box-shadow: $shadow-xl;
}

.banner-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 60px;
}

.banner-text {
  flex: 1;
}

.banner-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
  line-height: 1.2;
}

.banner-subtitle {
  font-size: 18px;
  opacity: 0.95;
  margin-bottom: 32px;
  line-height: 1.6;
}

.banner-actions {
  display: flex;
  gap: 16px;

  .el-button {
    font-size: 16px;
    padding: 16px 32px;
    height: auto;
  }
}

.banner-illustration {
  flex-shrink: 0;
  width: 300px;

  img {
    width: 100%;
    height: auto;
  }
}

.quick-access {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-lg;
  margin-bottom: $spacing-xl;
}

.quick-item {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  text-align: center;
  cursor: pointer;
  transition: $transition-base;
  box-shadow: $shadow-md;

  &:hover {
    transform: translateY(-8px);
    box-shadow: $shadow-xl;
  }
}

.quick-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
}

.quick-title {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 8px;
}

.quick-desc {
  font-size: 14px;
  color: $text-secondary;
}

.section {
  margin-bottom: $spacing-xl;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $spacing-lg;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: $text-primary;
  display: flex;
  align-items: center;
  gap: 8px;

  .el-icon {
    font-size: 28px;
    color: $primary-color;
  }
}

.article-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-lg;
}

.article-card {
  background: white;
  border-radius: $radius-lg;
  overflow: hidden;
  cursor: pointer;
  transition: $transition-base;
  box-shadow: $shadow-md;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-xl;
  }
}

.article-cover {
  position: relative;
  height: 180px;
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

.article-category {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(255, 255, 255, 0.95);
  padding: 4px 12px;
  border-radius: $radius-full;
  font-size: 12px;
  font-weight: 600;
  color: $primary-color;
}

.article-content {
  padding: $spacing-lg;
}

.article-title {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-summary {
  font-size: 14px;
  color: $text-secondary;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: $text-tertiary;

  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.counselor-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-lg;
}

.counselor-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  text-align: center;
  cursor: pointer;
  transition: $transition-base;
  box-shadow: $shadow-md;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-xl;
  }
}

.counselor-name {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  margin: 12px 0 4px;
}

.counselor-title {
  font-size: 14px;
  color: $text-secondary;
  margin-bottom: 12px;
}

.counselor-rating {
  margin-bottom: 12px;
}

.counselor-tags {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}

.tips-card {
  background: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);
  border-radius: $radius-lg;
  padding: $spacing-xl;
  display: flex;
  gap: $spacing-lg;
  box-shadow: $shadow-md;
}

.tips-icon {
  font-size: 48px;
  flex-shrink: 0;
}

.tips-content {
  h3 {
    font-size: 20px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 8px;
  }

  p {
    font-size: 15px;
    color: $text-secondary;
    line-height: 1.6;
    margin-bottom: 8px;
  }

  .tips-hotline {
    font-size: 16px;
    color: $text-primary;

    strong {
      color: #d63031;
      font-size: 18px;
    }
  }
}

@media (max-width: 1024px) {
  .quick-access {
    grid-template-columns: repeat(2, 1fr);
  }

  .counselor-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .welcome-banner {
    padding: 40px 30px;
  }

  .banner-content {
    flex-direction: column;
    gap: 30px;
  }

  .banner-illustration {
    width: 200px;
  }

  .banner-title {
    font-size: 32px;
  }

  .article-grid {
    grid-template-columns: 1fr;
  }

  .quick-access,
  .counselor-grid {
    grid-template-columns: 1fr;
  }
}
</style>
