<template>
  <div class="appointments-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">我的预约</h1>
      <el-button type="primary" @click="router.push('/student/counselors')">
        <el-icon><Plus /></el-icon>
        新建预约
      </el-button>
    </div>

    <!-- 状态筛选 -->
    <div class="filter-tabs">
      <el-radio-group v-model="statusFilter" @change="fetchAppointments">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="待确认">待确认</el-radio-button>
        <el-radio-button label="已确认">已确认</el-radio-button>
        <el-radio-button label="已完成">已完成</el-radio-button>
        <el-radio-button label="已取消">已取消</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 预约列表 -->
    <div class="appointments-list" v-loading="loading">
      <div
        v-for="appointment in appointments"
        :key="appointment.id"
        class="appointment-card"
      >
        <div class="card-header">
          <div class="counselor-info">
            <el-avatar :size="50" :src="appointment.counselorAvatar">
              {{ appointment.counselorName?.charAt(0) }}
            </el-avatar>
            <div class="counselor-details">
              <h3 class="counselor-name">{{ appointment.counselorName }}</h3>
              <p class="counselor-title">{{ appointment.counselorTitle }}</p>
            </div>
          </div>
          <el-tag :type="getStatusType(appointment.status)" size="large">
            {{ appointment.statusText || appointment.status }}
          </el-tag>
        </div>

        <div class="card-content">
          <div class="info-row">
            <el-icon><Calendar /></el-icon>
            <span>{{ formatDateTime(appointment.appointmentDate, appointment.startTime) }}</span>
          </div>
          <div class="info-row">
            <el-icon><Clock /></el-icon>
            <span>{{ appointment.startTime }} - {{ appointment.endTime }}</span>
          </div>
          <div class="info-row">
            <el-icon><Location /></el-icon>
            <span>{{ appointment.consultType }}</span>
          </div>
          <div class="info-row">
            <el-icon><Document /></el-icon>
            <span>{{ appointment.topic }}</span>
          </div>
        </div>

        <div class="card-actions">
          <el-button
            v-if="appointment.status === 0 || appointment.statusText === '待确认'"
            type="danger"
            plain
            @click="cancelAppointment(appointment.id)"
          >
            取消预约
          </el-button>
          <el-button
            v-if="(appointment.status === 2 || appointment.statusText === '已完成') && !appointment.rating"
            type="primary"
            @click="showRatingDialog(appointment)"
          >
            评价咨询
          </el-button>
          <el-button
            v-if="appointment.rating"
            disabled
          >
            已评价
          </el-button>
        </div>
      </div>

      <el-empty v-if="!loading && appointments.length === 0" description="暂无预约记录" />
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > 0">
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchAppointments"
      />
    </div>

    <!-- 评价对话框 -->
    <el-dialog v-model="showRating" title="评价咨询" width="500px">
      <el-form :model="ratingForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="ratingForm.rating" show-text />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input
            v-model="ratingForm.comment"
            type="textarea"
            :rows="4"
            placeholder="分享您的咨询体验..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRating = false">取消</el-button>
        <el-button type="primary" @click="submitRating" :loading="submitting">
          提交评价
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Plus,
  Calendar,
  Clock,
  Location,
  Document
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

const router = useRouter()

const statusFilter = ref('')
const appointments = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showRating = ref(false)
const submitting = ref(false)
const currentAppointment = ref(null)
const ratingForm = ref({
  rating: 5,
  comment: ''
})

const fetchAppointments = async () => {
  try {
    loading.value = true
    const res = await request.get('/appointments/my', {
      params: {
        current: pageNum.value,
        size: pageSize.value,
        status: statusFilter.value || undefined
      }
    })
    appointments.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('获取预约失败:', error)
  } finally {
    loading.value = false
  }
}

const cancelAppointment = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消这个预约吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await request.put(`/appointments/${id}/cancel`)
    ElMessage.success('取消成功')
    fetchAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消失败:', error)
      ElMessage.error('取消失败')
    }
  }
}

const showRatingDialog = (appointment) => {
  currentAppointment.value = appointment
  ratingForm.value = {
    rating: 5,
    comment: ''
  }
  showRating.value = true
}

const submitRating = async () => {
  if (!ratingForm.value.comment.trim()) {
    ElMessage.warning('请填写评价内容')
    return
  }

  try {
    submitting.value = true
    await request.put(`/appointments/${currentAppointment.value.id}/rate`, {
      rating: ratingForm.value.rating,
      comment: ratingForm.value.comment
    })
    ElMessage.success('评价成功')
    showRating.value = false
    fetchAppointments()
  } catch (error) {
    console.error('评价失败:', error)
    ElMessage.error('评价失败')
  } finally {
    submitting.value = false
  }
}

const getStatusType = (status) => {
  // status可能是数字或文本
  if (typeof status === 'number') {
    const typeMap = { 0: 'warning', 1: 'success', 2: 'info', 3: 'danger', 4: 'danger' }
    return typeMap[status] || 'info'
  }
  const typeMap = {
    '待确认': 'warning',
    '已确认': 'success',
    '已完成': 'info',
    '已取消': 'danger',
    '已拒绝': 'danger'
  }
  return typeMap[status] || 'info'
}

const formatDateTime = (date, time) => {
  return `${dayjs(date).format('YYYY年MM月DD日')} ${time}`
}

onMounted(() => {
  fetchAppointments()
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.appointments-page {
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-xl;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: $text-primary;
}

.filter-tabs {
  margin-bottom: $spacing-xl;
}

.appointments-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
  margin-bottom: $spacing-xl;
}

.appointment-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-md;
  transition: $transition-base;

  &:hover {
    box-shadow: $shadow-lg;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-lg;
  padding-bottom: $spacing-lg;
  border-bottom: 1px solid $border-color;
}

.counselor-info {
  display: flex;
  gap: $spacing-md;
  align-items: center;
}

.counselor-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.counselor-name {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
}

.counselor-title {
  font-size: 14px;
  color: $text-secondary;
}

.card-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: $spacing-lg;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: $text-secondary;

  .el-icon {
    color: $primary-color;
    font-size: 18px;
  }
}

.card-actions {
  display: flex;
  gap: $spacing-md;
  justify-content: flex-end;
}

.pagination {
  display: flex;
  justify-content: center;
}
</style>
