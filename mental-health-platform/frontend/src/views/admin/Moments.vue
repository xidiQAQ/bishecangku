<template>
  <div class="moments-container">
    <el-card class="stats-card">
      <div class="stats-row">
        <div class="stat-item">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">待审核</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.approved }}</div>
          <div class="stat-label">已通过</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.rejected }}</div>
          <div class="stat-label">已拒绝</div>
        </div>
      </div>
    </el-card>

    <el-card class="list-card">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待审核" name="pending" />
        <el-tab-pane label="已通过" name="approved" />
        <el-tab-pane label="已拒绝" name="rejected" />
      </el-tabs>

      <div v-loading="loading" class="moments-list">
        <div v-for="moment in moments" :key="moment.id" class="moment-card">
          <div class="moment-header">
            <span class="anonymous-id">{{ moment.anonymousId }}</span>
            <span class="publish-time">{{ moment.publishTime }}</span>
          </div>
          <div class="moment-content" v-html="highlightSensitiveWords(moment.content)"></div>
          <div v-if="moment.imageUrls && moment.imageUrls.length" class="moment-images">
            <el-image
              v-for="(url, index) in moment.imageUrls"
              :key="index"
              :src="url"
              :preview-src-list="moment.imageUrls"
              fit="cover"
              class="moment-image"
            />
          </div>
          <div class="moment-stats">
            <span><el-icon><ChatDotRound /></el-icon> {{ moment.commentCount }}</span>
            <span><el-icon><Star /></el-icon> {{ moment.likeCount }}</span>
          </div>
          <div v-if="activeTab === 'pending'" class="moment-actions">
            <el-button type="success" @click="handleApprove(moment)">通过</el-button>
            <el-button type="danger" @click="handleReject(moment)">拒绝</el-button>
          </div>
          <div v-else-if="moment.auditReason" class="audit-reason">
            <span class="reason-label">审核意见：</span>
            <span>{{ moment.auditReason }}</span>
          </div>
        </div>

        <el-empty v-if="!loading && moments.length === 0" description="暂无数据" />
      </div>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog v-model="rejectDialogVisible" title="拒绝审核" width="500px">
      <el-form :model="rejectForm" :rules="rejectRules" ref="rejectFormRef">
        <el-form-item label="拒绝原因" prop="reason">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Star } from '@element-plus/icons-vue'
import adminApi from '@/api/admin'

const loading = ref(false)
const moments = ref([])
const activeTab = ref('pending')
const rejectDialogVisible = ref(false)
const rejectFormRef = ref(null)
const currentMoment = ref(null)

const stats = reactive({
  pending: 0,
  approved: 0,
  rejected: 0
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const rejectForm = reactive({
  reason: ''
})

const rejectRules = {
  reason: [{ required: true, message: '请输入拒绝原因', trigger: 'blur' }]
}

// 敏感词列表（示例）
const sensitiveWords = ['自杀', '死亡', '抑郁', '焦虑']

onMounted(() => {
  loadStats()
  loadData()
})

const loadStats = async () => {
  try {
    const res = await adminApi.getStatistics()
    stats.pending = res.data.pendingMoments || 0
  } catch (error) {
    console.error('加载统计失败', error)
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      size: pagination.size,
      auditStatus: activeTab.value === 'pending' ? 0 : activeTab.value === 'approved' ? 1 : 2
    }
    
    const res = await adminApi.getPendingMoments(params)
    moments.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  pagination.current = 1
  loadData()
}

const highlightSensitiveWords = (content) => {
  if (!content) return ''
  let result = content
  sensitiveWords.forEach(word => {
    const regex = new RegExp(word, 'gi')
    result = result.replace(regex, `<span class="sensitive-word">${word}</span>`)
  })
  return result
}

const handleApprove = async (moment) => {
  try {
    await adminApi.auditMoment(moment.id, 1, '')
    ElMessage.success('审核通过')
    loadStats()
    loadData()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleReject = (moment) => {
  currentMoment.value = moment
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  try {
    await rejectFormRef.value.validate()
    await adminApi.auditMoment(currentMoment.value.id, 2, rejectForm.reason)
    ElMessage.success('已拒绝')
    rejectDialogVisible.value = false
    loadStats()
    loadData()
  } catch (error) {
    if (error !== false) {
      ElMessage.error('操作失败')
    }
  }
}
</script>

<style scoped lang="scss">
.moments-container {
  padding: 20px;
}

.stats-card {
  margin-bottom: 20px;
  
  .stats-row {
    display: flex;
    gap: 40px;
    
    .stat-item {
      text-align: center;
      
      .stat-value {
        font-size: 32px;
        font-weight: bold;
        color: var(--el-color-primary);
        margin-bottom: 8px;
      }
      
      .stat-label {
        font-size: 14px;
        color: #666;
      }
    }
  }
}

.list-card {
  .moments-list {
    min-height: 400px;
    
    .moment-card {
      padding: 20px;
      border: 1px solid #eee;
      border-radius: 8px;
      margin-bottom: 16px;
      
      .moment-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 12px;
        
        .anonymous-id {
          font-weight: 500;
          color: #333;
        }
        
        .publish-time {
          font-size: 12px;
          color: #999;
        }
      }
      
      .moment-content {
        line-height: 1.6;
        margin-bottom: 12px;
        
        :deep(.sensitive-word) {
          background-color: #ff4d4f;
          color: white;
          padding: 2px 4px;
          border-radius: 3px;
        }
      }
      
      .moment-images {
        display: flex;
        gap: 8px;
        margin-bottom: 12px;
        
        .moment-image {
          width: 100px;
          height: 100px;
          border-radius: 4px;
        }
      }
      
      .moment-stats {
        display: flex;
        gap: 20px;
        font-size: 14px;
        color: #666;
        margin-bottom: 12px;
        
        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
      
      .moment-actions {
        display: flex;
        gap: 12px;
      }
      
      .audit-reason {
        padding: 12px;
        background-color: #f5f5f5;
        border-radius: 4px;
        font-size: 14px;
        
        .reason-label {
          font-weight: 500;
          color: #666;
        }
      }
    }
  }
  
  :deep(.el-pagination) {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
