<template>
  <div class="moments-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">心灵树洞</h1>
      <p class="page-subtitle">匿名分享你的心情，倾听他人的故事</p>
    </div>

    <!-- 发布树洞 -->
    <div class="publish-card">
      <el-input
        v-model="newMoment"
        type="textarea"
        :rows="4"
        placeholder="说说你的心情... (匿名发布，请放心表达)"
        maxlength="500"
        show-word-limit
      />
      <div class="publish-actions">
        <el-checkbox v-model="isAnonymous">匿名发布</el-checkbox>
        <el-button
          type="primary"
          @click="publishMoment"
          :loading="publishing"
          :disabled="!newMoment.trim()"
        >
          <el-icon><Promotion /></el-icon>
          发布
        </el-button>
      </div>
    </div>

    <!-- 树洞列表 -->
    <div class="moments-list" v-loading="loading">
      <div
        v-for="moment in moments"
        :key="moment.id"
        class="moment-card"
      >
        <div class="moment-header">
          <div class="user-info">
            <el-avatar :size="40">{{ moment.anonymousId }}</el-avatar>
            <div class="user-details">
              <span class="username">匿名用户 {{ moment.anonymousId }}</span>
              <span class="time">{{ formatTime(moment.createdAt) }}</span>
            </div>
          </div>
          <el-dropdown v-if="moment.isMine">
            <el-icon class="more-icon"><MoreFilled /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="deleteMoment(moment.id)">
                  <el-icon><Delete /></el-icon>
                  删除
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <div class="moment-content">
          <p>{{ moment.content }}</p>
        </div>

        <div class="moment-actions">
          <div class="action-btn" @click="toggleLike(moment)">
            <el-icon :class="{ liked: moment.isLiked }">
              <component :is="moment.isLiked ? StarFilled : Star" />
            </el-icon>
            <span>{{ moment.likeCount }}</span>
          </div>
          <div class="action-btn" @click="toggleComments(moment.id)">
            <el-icon><ChatDotRound /></el-icon>
            <span>{{ moment.commentCount }}</span>
          </div>
          <div class="action-btn report-btn" @click="reportMoment(moment.id)">
            <el-icon><Warning /></el-icon>
            <span>举报</span>
          </div>
        </div>

        <!-- 评论区 -->
        <div class="comments-section" v-if="expandedMoments.includes(moment.id)">
          <div class="comment-input">
            <el-input
              v-model="commentInputs[moment.id]"
              placeholder="写下你的评论..."
              @keyup.enter="submitComment(moment.id)"
            >
              <template #append>
                <el-button @click="submitComment(moment.id)">发送</el-button>
              </template>
            </el-input>
          </div>

          <div class="comments-list" v-loading="loadingComments[moment.id]">
            <div
              v-for="comment in comments[moment.id]"
              :key="comment.id"
              class="comment-item"
            >
              <el-avatar :size="32">{{ comment.anonymousId }}</el-avatar>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="comment-user">匿名 {{ comment.anonymousId }}</span>
                  <span class="comment-time">{{ formatTime(comment.createdAt) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && moments.length === 0" description="还没有人分享哦" />
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchMoments"
      />
    </div>

    <!-- 举报对话框 -->
    <el-dialog v-model="reportDialogVisible" title="举报树洞" width="500px">
      <el-form :model="reportForm" :rules="reportRules" ref="reportFormRef">
        <el-form-item label="举报原因" prop="reason">
          <el-input
            v-model="reportForm.reason"
            type="textarea"
            :rows="4"
            placeholder="请详细描述举报原因"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reportDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReport">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import {
  Promotion,
  Star,
  StarFilled,
  ChatDotRound,
  MoreFilled,
  Delete,
  Warning
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const newMoment = ref('')
const isAnonymous = ref(true)
const publishing = ref(false)
const moments = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const expandedMoments = ref([])
const comments = reactive({})
const commentInputs = reactive({})
const loadingComments = reactive({})

const reportDialogVisible = ref(false)
const reportFormRef = ref(null)
const reportForm = reactive({
  momentId: null,
  reason: ''
})
const reportRules = {
  reason: [
    { required: true, message: '请输入举报原因', trigger: 'blur' },
    { min: 10, message: '请详细描述举报原因（至少10个字）', trigger: 'blur' }
  ]
}

const fetchMoments = async () => {
  try {
    loading.value = true
    const res = await request.get('/moments', {
      params: {
        current: pageNum.value,
        size: pageSize.value
      }
    })
    moments.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取树洞失败:', error)
  } finally {
    loading.value = false
  }
}

const publishMoment = async () => {
  if (!newMoment.value.trim()) {
    ElMessage.warning('请输入内容')
    return
  }

  try {
    publishing.value = true
    await request.post('/moments', {
      content: newMoment.value,
      isAnonymous: isAnonymous.value
    })
    ElMessage.success('发布成功')
    newMoment.value = ''
    fetchMoments()
  } catch (error) {
    console.error('发布失败:', error)
    ElMessage.error(error.message || '发布失败')
  } finally {
    publishing.value = false
  }
}

const toggleLike = async (moment) => {
  try {
    await request.post(`/moments/${moment.id}/like`)
    moment.isLiked = !moment.isLiked
    moment.likeCount += moment.isLiked ? 1 : -1
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const toggleComments = async (momentId) => {
  const index = expandedMoments.value.indexOf(momentId)
  if (index > -1) {
    expandedMoments.value.splice(index, 1)
  } else {
    expandedMoments.value.push(momentId)
    if (!comments[momentId]) {
      await fetchComments(momentId)
    }
  }
}

const fetchComments = async (momentId) => {
  try {
    loadingComments[momentId] = true
    const res = await request.get(`/moments/${momentId}/comments`)
    comments[momentId] = res.data || []
  } catch (error) {
    console.error('获取评论失败:', error)
  } finally {
    loadingComments[momentId] = false
  }
}

const submitComment = async (momentId) => {
  const content = commentInputs[momentId]
  if (!content?.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }

  try {
    await request.post('/moments/comments', {
      momentId,
      content
    })
    ElMessage.success('评论成功')
    commentInputs[momentId] = ''
    fetchComments(momentId)
    
    // 更新评论数
    const moment = moments.value.find(m => m.id === momentId)
    if (moment) moment.commentCount++
  } catch (error) {
    console.error('评论失败:', error)
    ElMessage.error(error.message || '评论失败')
  }
}

const deleteMoment = async (momentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条树洞吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.delete(`/moments/${momentId}`)
    ElMessage.success('删除成功')
    fetchMoments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const reportMoment = (momentId) => {
  reportForm.momentId = momentId
  reportForm.reason = ''
  reportDialogVisible.value = true
}

const submitReport = async () => {
  try {
    await reportFormRef.value.validate()
    
    await request.post(`/moments/${reportForm.momentId}/report`, null, {
      params: {
        reason: reportForm.reason
      }
    })
    
    ElMessage.success('举报成功，我们会尽快处理')
    reportDialogVisible.value = false
  } catch (error) {
    if (error !== false) {
      console.error('举报失败:', error)
      ElMessage.error(error.message || '举报失败')
    }
  }
}

const formatTime = (time) => {
  return dayjs(time).fromNow()
}

onMounted(() => {
  fetchMoments()
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.moments-page {
  max-width: 800px;
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
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 16px;
  color: $text-secondary;
}

.publish-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  margin-bottom: $spacing-xl;
  box-shadow: $shadow-md;
}

.publish-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: $spacing-md;
}

.moments-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
  margin-bottom: $spacing-xl;
}

.moment-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-md;
  transition: $transition-base;

  &:hover {
    box-shadow: $shadow-lg;
  }
}

.moment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-md;
}

.user-info {
  display: flex;
  gap: 12px;
  align-items: center;
}

.user-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.username {
  font-size: 15px;
  font-weight: 600;
  color: $text-primary;
}

.time {
  font-size: 13px;
  color: $text-tertiary;
}

.more-icon {
  font-size: 20px;
  color: $text-tertiary;
  cursor: pointer;

  &:hover {
    color: $text-primary;
  }
}

.moment-content {
  margin-bottom: $spacing-md;

  p {
    font-size: 15px;
    color: $text-primary;
    line-height: 1.8;
    white-space: pre-wrap;
  }
}

.moment-actions {
  display: flex;
  gap: $spacing-lg;
  padding-top: $spacing-md;
  border-top: 1px solid $border-color;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: $text-tertiary;
  transition: $transition-fast;
  font-size: 14px;

  &:hover {
    color: $primary-color;
  }

  &.report-btn:hover {
    color: #f56c6c;
  }

  .el-icon {
    font-size: 18px;

    &.liked {
      color: #f59e0b;
    }
  }
}

.comments-section {
  margin-top: $spacing-lg;
  padding-top: $spacing-lg;
  border-top: 1px solid $border-color;
}

.comment-input {
  margin-bottom: $spacing-md;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.comment-user {
  font-size: 14px;
  font-weight: 600;
  color: $text-primary;
}

.comment-time {
  font-size: 12px;
  color: $text-tertiary;
}

.comment-text {
  font-size: 14px;
  color: $text-secondary;
  line-height: 1.6;
}

.pagination {
  display: flex;
  justify-content: center;
}
</style>
