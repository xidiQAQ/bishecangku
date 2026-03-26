<template>
  <div class="tests-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-decoration"></div>
      <h1 class="page-title">心理测评</h1>
      <p class="page-subtitle">了解自己的心理状态，及时关注心理健康</p>
    </div>

    <!-- 测试列表 -->
    <div class="tests-grid" v-loading="loading">
      <div
        v-for="(test, index) in tests"
        :key="test.id"
        class="test-card"
        :class="`test-card-${index % 4}`"
        @click="startTest(test.id)"
      >
        <div class="test-card-bg"></div>
        <div class="test-icon">
          <el-icon><DataAnalysis /></el-icon>
        </div>
        <h3 class="test-name">{{ test.name }}</h3>
        <p class="test-desc">{{ test.description }}</p>
        <div class="test-meta">
          <span>
            <el-icon><Document /></el-icon>
            {{ test.questionCount }} 题
          </span>
          <span>
            <el-icon><Clock /></el-icon>
            约 {{ test.estimatedTime }} 分钟
          </span>
        </div>
        <el-button type="primary" class="start-btn" round>
          开始测试
          <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
    </div>

    <!-- 测试历史 -->
    <div class="history-section" v-if="history.length > 0">
      <h2 class="section-title">
        <el-icon><Clock /></el-icon>
        测试历史
      </h2>
      <div class="history-list">
        <div
          v-for="record in history"
          :key="record.id"
          class="history-item"
          @click="viewResult(record.id)"
        >
          <div class="history-info">
            <h4 class="history-test-name">{{ record.testName }}</h4>
            <p class="history-date">{{ formatDate(record.createdAt) }}</p>
          </div>
          <div class="history-score">
            <span class="score-label">得分</span>
            <span class="score-value">{{ record.score }}</span>
          </div>
          <div class="history-result">
            <el-tag :type="getResultType(record.result)" size="large" round>
              {{ record.result }}
            </el-tag>
          </div>
          <el-button text type="primary">
            查看详情
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { DataAnalysis, Document, Clock, ArrowRight } from '@element-plus/icons-vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

const router = useRouter()

const tests = ref([])
const history = ref([])
const loading = ref(false)

const fetchTests = async () => {
  try {
    loading.value = true
    const res = await request.get('/tests')
    tests.value = res.data || []
  } catch (error) {
    console.error('获取测试列表失败:', error)
  } finally {
    loading.value = false
  }
}

const fetchHistory = async () => {
  try {
    const res = await request.get('/tests/history')
    history.value = res.data || []
  } catch (error) {
    console.error('获取测试历史失败:', error)
  }
}

const startTest = (testId) => {
  router.push(`/student/tests/${testId}`)
}

const viewResult = (resultId) => {
  router.push(`/student/tests/result/${resultId}`)
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const getResultType = (result) => {
  if (result.includes('正常')) return 'success'
  if (result.includes('轻度')) return 'warning'
  if (result.includes('中度')) return 'danger'
  if (result.includes('重度')) return 'danger'
  return 'info'
}

onMounted(() => {
  fetchTests()
  fetchHistory()
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.tests-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  text-align: center;
  margin-bottom: $spacing-xl * 2;
  position: relative;
  padding: $spacing-xl 0;
}

.header-decoration {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 120px;
  height: 4px;
  background: $gradient-primary;
  border-radius: $radius-full;
  margin-bottom: $spacing-lg;
}

.page-title {
  font-size: 42px;
  font-weight: 700;
  color: $text-primary;
  margin: $spacing-lg 0 $spacing-md;
  position: relative;
  
  &::after {
    content: '✨';
    margin-left: 12px;
    font-size: 32px;
  }
}

.page-subtitle {
  font-size: 17px;
  color: $text-secondary;
  line-height: 1.6;
}

.tests-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-xl;
  margin-bottom: $spacing-xl * 2;
}

.test-card {
  background: white;
  border-radius: $radius-xl;
  padding: $spacing-xl * 1.5;
  cursor: pointer;
  transition: $transition-base;
  box-shadow: $shadow-md;
  text-align: center;
  position: relative;
  overflow: hidden;
  border: 2px solid transparent;

  &:hover {
    transform: translateY(-12px);
    box-shadow: $shadow-hover;
    border-color: $primary-color;
    
    .test-card-bg {
      opacity: 1;
      transform: scale(1.1);
    }
    
    .test-icon {
      transform: scale(1.1) rotate(5deg);
    }
  }
}

.test-card-bg {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  opacity: 0;
  transition: $transition-slow;
  pointer-events: none;
}

.test-card-0 {
  .test-icon {
    background: $gradient-cool;
  }
  .test-card-bg {
    background: radial-gradient(circle, rgba(37, 99, 235, 0.1) 0%, transparent 70%);
  }
}

.test-card-1 {
  .test-icon {
    background: linear-gradient(135deg, #64748B 0%, #475569 100%);
  }
  .test-card-bg {
    background: radial-gradient(circle, rgba(100, 116, 139, 0.1) 0%, transparent 70%);
  }
}

.test-card-2 {
  .test-icon {
    background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  }
  .test-card-bg {
    background: radial-gradient(circle, rgba(16, 185, 129, 0.1) 0%, transparent 70%);
  }
}

.test-card-3 {
  .test-icon {
    background: linear-gradient(135deg, #0EA5E9 0%, #06B6D4 100%);
  }
  .test-card-bg {
    background: radial-gradient(circle, rgba(14, 165, 233, 0.1) 0%, transparent 70%);
  }
}

.test-icon {
  width: 90px;
  height: 90px;
  margin: 0 auto $spacing-lg;
  border-radius: $radius-xl;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 44px;
  transition: $transition-base;
  box-shadow: $shadow-lg;
}

.test-name {
  font-size: 24px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: $spacing-md;
  position: relative;
  z-index: 1;
}

.test-desc {
  font-size: 15px;
  color: $text-secondary;
  line-height: 1.8;
  margin-bottom: $spacing-lg;
  min-height: 54px;
  position: relative;
  z-index: 1;
}

.test-meta {
  display: flex;
  justify-content: center;
  gap: $spacing-xl;
  margin-bottom: $spacing-lg;
  font-size: 14px;
  color: $text-tertiary;
  position: relative;
  z-index: 1;

  span {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 8px 16px;
    background: $bg-soft;
    border-radius: $radius-full;
    font-weight: 500;
  }
}

.start-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  position: relative;
  z-index: 1;
}

.history-section {
  background: white;
  border-radius: $radius-xl;
  padding: $spacing-xl * 1.5;
  box-shadow: $shadow-md;
}

.section-title {
  font-size: 26px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: $spacing-xl;
  display: flex;
  align-items: center;
  gap: 10px;

  .el-icon {
    color: $primary-color;
    font-size: 30px;
  }
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.history-item {
  display: flex;
  align-items: center;
  gap: $spacing-lg;
  padding: $spacing-lg * 1.25;
  border-radius: $radius-lg;
  cursor: pointer;
  transition: $transition-base;
  border: 2px solid $border-color;
  background: $bg-white;

  &:hover {
    background: $bg-soft;
    border-color: $primary-color;
    transform: translateX(8px);
    box-shadow: $shadow-md;
  }
}

.history-info {
  flex: 1;
}

.history-test-name {
  font-size: 17px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 6px;
}

.history-date {
  font-size: 13px;
  color: $text-tertiary;
}

.history-score {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: $spacing-md;
  background: $bg-soft;
  border-radius: $radius-md;
  min-width: 80px;
}

.score-label {
  font-size: 12px;
  color: $text-tertiary;
  font-weight: 500;
}

.score-value {
  font-size: 28px;
  font-weight: 700;
  background: $gradient-primary;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.history-result {
  min-width: 100px;
  text-align: center;
}

@media (max-width: 768px) {
  .page-title {
    font-size: 32px;
  }
  
  .tests-grid {
    grid-template-columns: 1fr;
    gap: $spacing-lg;
  }

  .history-item {
    flex-wrap: wrap;
    gap: $spacing-md;
  }
  
  .history-score {
    order: -1;
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
  }
}
</style>
