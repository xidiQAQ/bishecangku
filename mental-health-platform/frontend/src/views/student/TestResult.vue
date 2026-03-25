<template>
  <div class="test-result-page">
    <div v-loading="loading" class="result-container">
      <div class="result-header">
        <div class="result-icon" :class="getResultClass(result.resultLevel)">
          <el-icon><CircleCheck v-if="isNormal" /><Warning v-else /></el-icon>
        </div>
        <h1 class="result-title">测试完成</h1>
        <h2 class="test-name">{{ result.testName }}</h2>
      </div>

      <div class="score-card">
        <div class="score-display">
          <span class="score-label">总分</span>
          <span class="score-value">{{ result.totalScore }}</span>
        </div>
        <el-tag :type="getTagType(result.resultLevel)" size="large" class="result-tag">
          {{ result.resultLevel }}
        </el-tag>
      </div>

      <div class="result-content">
        <div class="content-section">
          <h3 class="section-title">
            <el-icon><Document /></el-icon>
            结果分析
          </h3>
          <p class="section-text">{{ result.resultAnalysis }}</p>
        </div>

        <div class="content-section">
          <h3 class="section-title">
            <el-icon><InfoFilled /></el-icon>
            建议
          </h3>
          <p class="section-text">{{ result.suggestions }}</p>
        </div>

        <div class="test-meta">
          <div class="meta-item">
            <el-icon><Clock /></el-icon>
            <span>测试时长：{{ formatDuration(result.testDuration) }}</span>
          </div>
          <div class="meta-item">
            <el-icon><Calendar /></el-icon>
            <span>完成时间：{{ formatDate(result.createdAt) }}</span>
          </div>
        </div>
      </div>

      <div class="action-buttons">
        <el-button size="large" @click="goToHistory">
          <el-icon><Clock /></el-icon>
          查看历史记录
        </el-button>
        <el-button 
          v-if="needConsultation" 
          type="primary" 
          size="large" 
          @click="goToAppointment"
        >
          <el-icon><Calendar /></el-icon>
          预约心理咨询
        </el-button>
        <el-button size="large" @click="goToTests">
          <el-icon><DataAnalysis /></el-icon>
          返回测试列表
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  CircleCheck,
  Warning,
  Document,
  InfoFilled,
  Clock,
  Calendar,
  DataAnalysis
} from '@element-plus/icons-vue'
import { getTestResult } from '@/api/test'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()

const resultId = ref(route.params.resultId)
const result = ref({})
const loading = ref(false)

const isNormal = computed(() => result.value.resultLevel === '正常')
const needConsultation = computed(() => {
  const level = result.value.resultLevel
  return level && (level.includes('中度') || level.includes('重度'))
})

onMounted(() => {
  loadResult()
})

const loadResult = async () => {
  try {
    loading.value = true
    const res = await getTestResult(resultId.value)
    result.value = res.data
  } catch (error) {
    ElMessage.error('加载测试结果失败')
  } finally {
    loading.value = false
  }
}

const getResultClass = (level) => {
  if (!level) return ''
  if (level === '正常') return 'normal'
  if (level.includes('轻度')) return 'mild'
  if (level.includes('中度')) return 'moderate'
  if (level.includes('重度')) return 'severe'
  return ''
}

const getTagType = (level) => {
  if (!level) return 'info'
  if (level === '正常') return 'success'
  if (level.includes('轻度')) return 'warning'
  if (level.includes('中度')) return 'danger'
  if (level.includes('重度')) return 'danger'
  return 'info'
}

const formatDuration = (seconds) => {
  if (!seconds) return '0分钟'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return secs > 0 ? `${mins}分${secs}秒` : `${mins}分钟`
}

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

const goToHistory = () => {
  router.push('/student/test-history')
}

const goToAppointment = () => {
  router.push('/student/counselors')
}

const goToTests = () => {
  router.push('/student/tests')
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.test-result-page {
  max-width: 800px;
  margin: 0 auto;
  padding: $spacing-xl 0;
}

.result-container {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl * 2;
  box-shadow: $shadow-xl;
}

.result-header {
  text-align: center;
  margin-bottom: 40px;
}

.result-icon {
  width: 100px;
  height: 100px;
  margin: 0 auto 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 50px;
  color: white;

  &.normal {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  }

  &.mild {
    background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
  }

  &.moderate {
    background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
  }

  &.severe {
    background: linear-gradient(135deg, #dc2626 0%, #991b1b 100%);
  }
}

.result-title {
  font-size: 28px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 8px;
}

.test-name {
  font-size: 20px;
  font-weight: 600;
  color: $text-secondary;
}

.score-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: $radius-lg;
  margin-bottom: 32px;
  color: white;
}

.score-display {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.score-label {
  font-size: 16px;
  opacity: 0.9;
}

.score-value {
  font-size: 48px;
  font-weight: 700;
}

.result-tag {
  font-size: 18px;
  padding: 12px 24px;
  border: none;
}

.result-content {
  margin-bottom: 32px;
}

.content-section {
  margin-bottom: 32px;

  &:last-child {
    margin-bottom: 0;
  }
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;

  .el-icon {
    color: $primary-color;
    font-size: 24px;
  }
}

.section-text {
  font-size: 16px;
  color: $text-secondary;
  line-height: 1.8;
  padding: 20px;
  background: $bg-color;
  border-radius: $radius-md;
  border-left: 4px solid $primary-color;
}

.test-meta {
  display: flex;
  justify-content: center;
  gap: 32px;
  padding: 24px;
  background: $bg-color;
  border-radius: $radius-md;
  margin-top: 24px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: $text-secondary;

  .el-icon {
    color: $primary-color;
    font-size: 18px;
  }
}

.action-buttons {
  display: flex;
  gap: 16px;
  justify-content: center;

  .el-button {
    flex: 1;
    max-width: 200px;
  }
}

@media (max-width: 768px) {
  .result-container {
    padding: $spacing-xl;
  }

  .score-card {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .test-meta {
    flex-direction: column;
    gap: 12px;
  }

  .action-buttons {
    flex-direction: column;

    .el-button {
      max-width: 100%;
    }
  }
}
</style>
