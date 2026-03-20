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
          <span class="rating-count">({{ counselor.consultCount }}次咨询)</span>
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
            专业资质
          </h2>
          <p class="section-text">{{ counselor.qualification }}</p>
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
        <el-calendar v-model="selectedDate">
          <template #date-cell="{ data }">
            <div
              class="calendar-day"
              :class="{ available: isDateAvailable(data.day) }"
              @click="selectDate(data.day)"
            >
              {{ data.day.split('-').slice(2).join('-') }}
            </div>
          </template>
        </el-calendar>

        <div class="time-slots" v-if="timeSlots.length > 0">
          <h3>可预约时段</h3>
          <div class="slots-grid">
            <div
              v-for="slot in timeSlots"
              :key="slot.id"
              class="time-slot"
              :class="{ selected: selectedSlot?.id === slot.id, disabled: !slot.isAvailable }"
              @click="selectSlot(slot)"
            >
              {{ slot.startTime }} - {{ slot.endTime }}
            </div>
          </div>
        </div>

        <el-empty v-else description="该日期暂无可预约时段" />
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
const selectedDate = ref(new Date())
const timeSlots = ref([])
const selectedSlot = ref(null)
const submitting = ref(false)

const appointmentForm = ref({
  consultType: '线上咨询',
  topic: ''
})

const fetchCounselor = async () => {
  try {
    loading.value = true
    const res = await request.get(`/appointments/counselors/${route.params.id}`)
    counselor.value = res.data
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
      params: { date: dayjs(date).format('YYYY-MM-DD') }
    })
    timeSlots.value = res.data || []
  } catch (error) {
    console.error('获取时间表失败:', error)
    timeSlots.value = []
  }
}

const isDateAvailable = (date) => {
  const today = dayjs().startOf('day')
  const checkDate = dayjs(date)
  return checkDate.isAfter(today) || checkDate.isSame(today)
}

const selectDate = (date) => {
  if (isDateAvailable(date)) {
    selectedDate.value = new Date(date)
    selectedSlot.value = null
    fetchSchedule(date)
  }
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
      scheduleId: selectedSlot.value.id,
      consultType: appointmentForm.value.consultType,
      topic: appointmentForm.value.topic
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
  fetchCounselor()
  fetchSchedule(selectedDate.value)
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
  grid-template-columns: repeat(4, 1fr);
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

  .slots-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
