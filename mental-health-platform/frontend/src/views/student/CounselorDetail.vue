<template>
  <div class="counselor-detail-page" v-loading="loading">
    <div class="counselor-profile" v-if="counselor">
      <!-- 咨询师基本信息 -->
      <div class="profile-card">
        <el-avatar :size="120" :src="counselor.avatar">
          {{ counselor.realName?.charAt(0) }}
        </el-avatar>
        <h1 class="counselor-name">{{ counselor.realName }}</h1>
        <p class="counselor-title">{{ counselor.title }}</p>
        <div class="counselor-rating">
          <el-rate v-model="counselor.rating" disabled show-score />
          <span class="rating-count">({{ counselor.ratingCount || 0 }}次评价)</span>
        </div>
        <div class="counselor-tags">
          <el-tag
            v-for="(tag, index) in counselor.specialty"
            :key="index"
            type="primary"
          >
            {{ tag }}
          </el-tag>
        </div>
      </div>

      <!-- 咨询师详细介绍 -->
      <div class="profile-content">
        <div class="content-section">
          <h2 class="section-title">
            <el-icon><User /></el-icon>
            个人简介
          </h2>
          <p class="section-text">{{ counselor.introduction }}</p>
        </div>

        <div class="content-section">
          <h2 class="section-title">
            <el-icon><Medal /></el-icon>
            教育背景
          </h2>
          <p class="section-text">{{ counselor.education || '暂无信息' }}</p>
        </div>

        <div class="content-section">
          <h2 class="section-title">
            <el-icon><Star /></el-icon>
            工作经验
          </h2>
          <p class="section-text">{{ counselor.experience || '暂无信息' }}</p>
        </div>

        <div class="content-section">
          <h2 class="section-title">
            <el-icon><Star /></el-icon>
            擅长领域
          </h2>
          <ul class="specialty-list">
            <li v-for="(item, index) in counselor.specialty" :key="index">
              {{ item }}
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 预约时间选择 -->
    <div class="appointment-section" v-if="counselor">
      <h2 class="section-title">
        <el-icon><Calendar /></el-icon>
        选择预约时间
      </h2>

      <div class="schedule-container">
        <!-- 日期选择 -->
        <div class="date-selector">
          <h3>选择日期</h3>
          <div class="date-grid">
            <div
              v-for="date in availableDates"
              :key="date.value"
              class="date-card"
              :class="{ selected: selectedDate === date.value }"
              @click="selectDate(date.value)"
            >
              <div class="date-weekday">{{ date.weekday }}</div>
              <div class="date-day">{{ date.day }}</div>
              <div class="date-month">{{ date.month }}</div>
            </div>
          </div>
        </div>

        <!-- 时间段选择 -->
        <div class="time-slots" v-if="selectedDate && timeSlots.length > 0">
          <h3>可预约时段</h3>
          <div class="slots-grid">
            <div
              v-for="slot in timeSlots"
              :key="slot.id"
              class="time-slot"
              :class="{ selected: selectedSlot?.id === slot.id, disabled: !slot.isAvailable }"
              @click="selectSlot(slot)"
            >
              {{ slot.timeSlot }}
            </div>
          </div>
        </div>

        <el-empty v-else-if="selectedDate && timeSlots.length === 0" description="该日期暂无可预约时段" />
      </div>

      <div class="appointment-form" v-if="selectedSlot">
        <el-form :model="appointmentForm" label-width="100px">
          <el-form-item label="咨询方式">
            <el-radio-group v-model="appointmentForm.consultType">
              <el-radio label="线上咨询">线上咨询</el-radio>
              <el-radio label="线下咨询">线下咨询</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="咨询主题">
            <el-input
              v-model="appointmentForm.topic"
              placeholder="请简要描述您想咨询的问题"
              type="textarea"
              :rows="3"
            />
          </el-form-item>
        </el-form>

        <el-button
          type="primary"
          size="large"
          @click="submitAppointment"
          :loading="submitting"
          class="submit-btn"
        >
          确认预约
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { User, Medal, Star, Calendar } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import dayjs from 'dayjs'

const router = useRouter()
const route = useRoute()

const counselor = ref(null)
const loading = ref(false)
const selectedDate = ref(null)
const availableDates = ref([])
const timeSlots = ref([])
const selectedSlot = ref(null)
const submitting = ref(false)

const appointmentForm = ref({
  consultType: '线上咨询',
  topic: ''
})

// 生成未来7天的日期列表
const generateAvailableDates = () => {
  const dates = []
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  
  for (let i = 0; i < 7; i++) {
    const date = dayjs().add(i, 'day')
    dates.push({
      value: date.format('YYYY-MM-DD'),
      day: date.format('DD'),
      month: date.format('MM月'),
      weekday: weekdays[date.day()],
      isToday: i === 0
    })
  }
  
  availableDates.value = dates
  // 默认选择今天
  if (dates.length > 0) {
    selectedDate.value = dates[0].value
  }
}

const fetchCounselor = async () => {
  try {
    loading.value = true
    const res = await request.get(`/appointments/counselors/${route.params.id}`)
    // 处理specialty字段，将字符串转为数组
    counselor.value = {
      ...res.data,
      specialty: res.data.specialty ? res.data.specialty.split(',') : [],
      rating: res.data.rating ? Number(res.data.rating) : 0
    }
  } catch (error) {
    console.error('获取咨询师失败:', error)
    ElMessage.error('咨询师不存在')
    router.back()
  } finally {
    loading.value = false
  }
}

const fetchSchedule = async (date) => {
  try {
    const res = await request.get(`/appointments/counselors/${route.params.id}/schedule`, {
      params: { date: date }
    })
    // 转换后端返回的数据格式
    timeSlots.value = (res.data || []).map(schedule => ({
      id: schedule.id,
      timeSlot: schedule.timeSlot,
      isAvailable: schedule.status === 1 // 1表示可预约
    }))
  } catch (error) {
    console.error('获取时间表失败:', error)
    timeSlots.value = []
  }
}

const selectDate = (date) => {
  selectedDate.value = date
  selectedSlot.value = null
  fetchSchedule(date)
}

const selectSlot = (slot) => {
  if (slot.isAvailable) {
    selectedSlot.value = slot
  }
}

const submitAppointment = async () => {
  if (!appointmentForm.value.topic) {
    ElMessage.warning('请填写咨询主题')
    return
  }

  try {
    submitting.value = true
    await request.post('/appointments', {
      counselorId: counselor.value.id,
      appointmentDate: selectedDate.value,
      timeSlot: selectedSlot.value.timeSlot,
      consultType: appointmentForm.value.consultType,
      problemDesc: appointmentForm.value.topic
    })
    ElMessage.success('预约成功！咨询师将尽快确认')
    router.push('/student/appointments')
  } catch (error) {
    console.error('预约失败:', error)
    ElMessage.error(error.message || '预约失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  generateAvailableDates()
  fetchCounselor()
  // 获取今天的时间表
  if (availableDates.value.length > 0) {
    fetchSchedule(availableDates.value[0].value)
  }
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.counselor-detail-page {
  max-width: 1200px;
  margin: 0 auto;
}

.counselor-profile {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: $spacing-xl;
  margin-bottom: $spacing-xl;
}

.profile-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  text-align: center;
  box-shadow: $shadow-md;
  height: fit-content;
}

.counselor-name {
  font-size: 24px;
  font-weight: 700;
  color: $text-primary;
  margin: 16px 0 8px;
}

.counselor-title {
  font-size: 15px;
  color: $text-secondary;
  margin-bottom: 16px;
}

.counselor-rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.rating-count {
  font-size: 13px;
  color: $text-tertiary;
}

.counselor-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.profile-content {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-md;
}

.content-section {
  margin-bottom: $spacing-xl;

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
  }
}

.section-text {
  font-size: 15px;
  color: $text-secondary;
  line-height: 1.8;
}

.specialty-list {
  list-style: none;
  padding: 0;

  li {
    font-size: 15px;
    color: $text-secondary;
    padding: 8px 0;
    padding-left: 20px;
    position: relative;

    &::before {
      content: '✓';
      position: absolute;
      left: 0;
      color: $primary-color;
      font-weight: bold;
    }
  }
}

.appointment-section {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-md;
}

.schedule-container {
  margin: $spacing-lg 0;
}

.date-selector {
  margin-bottom: $spacing-xl;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 16px;
  }
}

.date-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: $spacing-md;
}

.date-card {
  background: white;
  border: 2px solid $border-color;
  border-radius: $radius-md;
  padding: 12px;
  text-align: center;
  cursor: pointer;
  transition: $transition-fast;

  &:hover {
    border-color: $primary-color;
    background: rgba($primary-color, 0.05);
  }

  &.selected {
    border-color: $primary-color;
    background: $primary-color;
    color: white;

    .date-weekday,
    .date-day,
    .date-month {
      color: white;
    }
  }
}

.date-weekday {
  font-size: 12px;
  color: $text-tertiary;
  margin-bottom: 4px;
}

.date-day {
  font-size: 24px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 4px;
}

.date-month {
  font-size: 12px;
  color: $text-secondary;
}

.calendar-day {
  padding: 8px;
  text-align: center;
  cursor: pointer;
  border-radius: $radius-sm;
  transition: $transition-fast;

  &.available:hover {
    background: $primary-light;
    color: white;
  }
}

.time-slots {
  margin-top: $spacing-lg;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: $text-primary;
    margin-bottom: 16px;
  }
}

.slots-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: $spacing-md;
}

.time-slot {
  padding: 12px;
  text-align: center;
  border: 2px solid $border-color;
  border-radius: $radius-md;
  cursor: pointer;
  transition: $transition-fast;
  font-size: 14px;

  &:hover:not(.disabled) {
    border-color: $primary-color;
    background: rgba($primary-color, 0.05);
  }

  &.selected {
    border-color: $primary-color;
    background: $primary-color;
    color: white;
  }

  &.disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}

.appointment-form {
  margin-top: $spacing-xl;
  padding-top: $spacing-xl;
  border-top: 1px solid $border-color;
}

.submit-btn {
  width: 100%;
  margin-top: $spacing-lg;
}

@media (max-width: 768px) {
  .counselor-profile {
    grid-template-columns: 1fr;
  }

  .date-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .slots-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
