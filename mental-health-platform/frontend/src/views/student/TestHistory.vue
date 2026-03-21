<template>
  <div class="test-history-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">测试历史</h1>
      <p class="page-desc">查看您的历史测试记录和结果分析</p>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 空状态 -->
    <el-empty
      v-else-if="!loading && historyList.length === 0"
      description="暂无测试记录"
      :image-size="200"
    >
      <el-button type="primary" @click="router.push('/student/tests')">
        去做测试
      </el-button>
    </el-empty>

    <!-- 测试历史列表 -->
    <div v-else class="history-list">
      <div
        v-for="item in historyList"
        :key="item.id"
        class="history-card"
        @click="viewDetail(item)"
      >
        <div class="card-header">
          <div class="test-info">
            <h3 class="test-name">{{ item.testName }}</h3>
            <span class="test-type">{{ item.testType }}</span>
          </div>
          <div class="test-date">
            {{ formatDate(item.createdAt) }}
          </div>
        </div>

        <div class="card-body">
          <div class="score-section">
            <div class="score-label">测试得分</div>
            <div class="score-value" :class="getScoreClass(item.resultLevel)">
              {{ item.totalScore }}
            </div>
          </div>

          <div class="result-section">
            <div class="result-level" :class="getLevelClass(item.resultLevel)">
              {{ item.resultLevel }}
            </div>
            <div class="result-analysis">
              {{ item.resultAnalysis }}
            </div>
          </div>
        </div>

        <div class="card-footer">
          <el-button text type="primary">
            查看详情
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>
    </div>

    <!-- 测试结果详情对话框 -->
    <el-dialog
      v-model="showDetailDialog"
      title="测试结果详情"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="currentDetail" class="detail-content">
        <div class="detail-header">
          <h2 class="detail-title">{{ currentDetail.testName }}</h2>
          <span class="detail-type">{{ currentDetail.testType }}</span>
        </div>

        <div class="detail-score">
          <div class="score-item">
            <span class="label">测试得分</span>
            <span class="value" :class="getScoreClass(currentDetail.resultLevel)">
              {{ currentDetail.totalScore }}
            </span>
          </div>
          <div class="score-item">
            <span class="label">结果等级</span>
            <span class="value" :class="getLevelClass(currentDetail.resultLevel)">
              {{ currentDetail.resultLevel }}
            </span>
          </div>
          <div class="score-item">
            <span class="label">测试时长</span>
            <span class="value">{{ formatDuration(currentDetail.testDuration) }}</span>
          </div>
          <div class="score-item">
            <span class="label">测试时间</span>
            <span class="value">{{ formatDate(currentDetail.createdAt) }}</span>
          </div>
        </div>

        <div class="detail-section">
          <h3 class="section-title">结果分析</h3>
          <p class="section-content">{{ currentDetail.resultAnalysis }}</p>
        </div>

        <div class="detail-section">
          <h3 class="section-title">建议</h3>
          <p class="section-content">{{ currentDetail.suggestions }}</p>
        </div>

        <div class="detail-tips">
          <el-icon><InfoFilled /></el-icon>
          <span>如需进一步咨询，建议预约专业心理咨询师</span>
        </div>
      </div>

      <template #footer>
        <el-button @click="showDetailDialog = false">关闭</el-button>
        <el-button type="primary" @click="goToAppointment">
          预约咨询
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, InfoFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getTestHistory, getTestResult } from '@/api/test'
import dayjs from 'dayjs'

const router = useRouter()

const loading = ref(false)
const historyList = ref([])
const showDetailDialog = ref(false)
const currentDetail = ref(null)

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds) return '未记录'
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}分${secs}秒`
}

// 获取分数样式类
const getScoreClass = (level) => {
  if (!level) return ''
  if (level.includes('正常')) return 'score-normal'
  if (level.includes('轻度')) return 'score-mild'
  if (level.includes('中度')) return 'score-moderate'
  if (level.includes('重度')) return 'score-severe'
  return ''
}

// 获取等级样式类
const getLevelClass = (level) => {
  if (!level) return ''
  if (level.includes('正常')) return 'level-normal'
  if (level.includes('轻度')) return 'level-mild'
  if (level.includes('中度')) return 'level-moderate'
  if (level.includes('重度')) return 'level-severe'
  return ''
}

// 加载测试历史
const loadHistory = async () => {
  try {
    loading.value = true
    const res = await getTestHistory()
    historyList.value = res.data || []
  } catch (error) {
    console.error('加载测试历史失败:', error)
    ElMessage.error('加载测试历史失败')
  } finally {
    loading.value = false
  }
}

// 查看详情
const viewDetail = async (item) => {
  try {
    const res = await getTestResult(item.id)
    currentDetail.value = res.data
    showDetailDialog.value = true
  } catch (error) {
    console.error('加载测试详情失败:', error)
    ElMessage.error('加载测试详情失败')
  }
}

// 去预约咨询
const goToAppointment = () => {
  showDetailDialog.value = false
  router.push('/student/counselors')
}

onMounted(() => {
  loadHistory()
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.test-history-page {
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: $spacing-xl;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 8px;
}

.page-desc {
  font-size: 15px;
  color: $text-tertiary;
}

.loading-container {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-md;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
}

.history-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-md;
  cursor: pointer;
  transition: $transition-base;

  &:hover {
    box-shadow: $shadow-lg;
    transform: translateY(-2px);
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: $spacing-lg;
  padding-bottom: $spacing-md;
  border-bottom: 1px solid $border-color;
}

.test-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.test-name {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  margin: 0;
}

.test-type {
  padding: 4px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: $radius-sm;
  font-size: 12px;
  font-weight: 500;
}

.test-date {
  font-size: 14px;
  color: $text-tertiary;
}

.card-body {
  display: flex;
  gap: $spacing-xl;
  margin-bottom: $spacing-lg;
}

.score-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $spacing-lg;
  background: $bg-color;
  border-radius: $radius-md;
  min-width: 120px;
}

.score-label {
  font-size: 13px;
  color: $text-tertiary;
  margin-bottom: 8px;
}

.score-value {
  font-size: 36px;
  font-weight: 700;
  line-height: 1;

  &.score-normal {
    color: #67c23a;
  }

  &.score-mild {
    color: #e6a23c;
  }

  &.score-moderate {
    color: #f56c6c;
  }

  &.score-severe {
    color: #c71585;
  }
}

.result-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.result-level {
  display: inline-block;
  padding: 6px 16px;
  border-radius: $radius-sm;
  font-size: 14px;
  font-weight: 600;
  width: fit-content;

  &.level-normal {
    background: #f0f9ff;
    color: #67c23a;
  }

  &.level-mild {
    background: #fef0e6;
    color: #e6a23c;
  }

  &.level-moderate {
    background: #fef0f0;
    color: #f56c6c;
  }

  &.level-severe {
    background: #fce4ec;
    color: #c71585;
  }
}

.result-analysis {
  font-size: 14px;
  color: $text-secondary;
  line-height: 1.6;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}

// 详情对话框样式
.detail-content {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-bottom: $spacing-md;
  border-bottom: 2px solid $border-color;
}

.detail-title {
  font-size: 20px;
  font-weight: 600;
  color: $text-primary;
  margin: 0;
}

.detail-type {
  padding: 4px 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: $radius-sm;
  font-size: 12px;
  font-weight: 500;
}

.detail-score {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-md;
  padding: $spacing-lg;
  background: $bg-color;
  border-radius: $radius-md;
}

.score-item {
  display: flex;
  flex-direction: column;
  gap: 8px;

  .label {
    font-size: 13px;
    color: $text-tertiary;
  }

  .value {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;

    &.score-normal,
    &.level-normal {
      color: #67c23a;
    }

    &.score-mild,
    &.level-mild {
      color: #e6a23c;
    }

    &.score-moderate,
    &.level-moderate {
      color: #f56c6c;
    }

    &.score-severe,
    &.level-severe {
      color: #c71585;
    }
  }
}

.detail-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: $text-primary;
  margin: 0;
}

.section-content {
  font-size: 14px;
  color: $text-secondary;
  line-height: 1.8;
  margin: 0;
}

.detail-tips {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: $spacing-md;
  background: #e6f7ff;
  border-radius: $radius-md;
  color: #1890ff;
  font-size: 13px;

  .el-icon {
    font-size: 16px;
  }
}

@media (max-width: 768px) {
  .card-body {
    flex-direction: column;
  }

  .score-section {
    width: 100%;
  }

  .detail-score {
    grid-template-columns: 1fr;
  }
}
</style>
