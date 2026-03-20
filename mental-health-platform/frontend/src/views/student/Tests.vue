<template>
  <div class="tests-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">心理测评</h1>
      <p class="page-subtitle">了解自己的心理状态，及时关注心理健康</p>
    </div>

    <!-- 测试列表 -->
    <div class="tests-grid" v-loading="loading">
      <div
        v-for="test in tests"
        :key="test.id"
        class="test-card"
        @click="startTest(test.id)"
      >
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
        <el-button type="primary" class="start-btn">
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
            <el-tag :type="getResultType(record.result)">
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
  margin-bottom: $spacing-xl;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 12px;
  background: $gradient-cool;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: $text-secondary;
}

.tests-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-lg;
  margin-bottom: $spacing-xl * 2;
}

.test-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  cursor: pointer;
  transition: $transition-base;
  box-shadow: $shadow-md;
  text-align: center;

  &:hover {
    transform: translateY(-8px);
    box-shadow: $shadow-xl;
  }
}

.test-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: $gradient-cool;
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 40px;
}

.test-name {
  font-size: 22px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 12px;
}

.test-desc {
  font-size: 15px;
  color: $text-secondary;
  line-height: 1.6;
  margin-bottom: 20px;
  min-height: 48px;
}

.test-meta {
  display: flex;
  justify-content: center;
  gap: 24px;
  margin-bottom: 20px;
  font-size: 14px;
  color: $text-tertiary;

  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }
}

.start-btn {
  width: 100%;
}

.history-section {
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
  display: flex;
  align-items: center;
  gap: 8px;

  .el-icon {
    color: $primary-color;
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
  padding: $spacing-lg;
  border-radius: $radius-md;
  cursor: pointer;
  transition: $transition-base;
  border: 1px solid $border-color;

  &:hover {
    background: $bg-color;
    border-color: $primary-color;
  }
}

.history-info {
  flex: 1;
}

.history-test-name {
  font-size: 16px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 4px;
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
}

.score-label {
  font-size: 12px;
  color: $text-tertiary;
}

.score-value {
  font-size: 24px;
  font-weight: 700;
  color: $primary-color;
}

.history-result {
  min-width: 80px;
  text-align: center;
}

@media (max-width: 768px) {
  .tests-grid {
    grid-template-columns: 1fr;
  }

  .history-item {
    flex-wrap: wrap;
  }
}
</style>
