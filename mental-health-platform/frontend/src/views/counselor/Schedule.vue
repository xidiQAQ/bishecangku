<template>
  <div class="schedule-page">
    <el-card>
      <template #header>
        <div class="header">
          <span>时间表管理</span>
          <el-button type="primary" @click="showBatchDialog = true">批量设置</el-button>
        </div>
      </template>
      
      <el-calendar v-model="currentDate">
        <template #date-cell="{ data }">
          <div class="calendar-day" @click="handleDateClick(data.day)">
            <div class="day-number">{{ data.day.split('-').slice(-1)[0] }}</div>
            <div class="time-slots">
              <el-tag
                v-for="slot in getDateSlots(data.day)"
                :key="slot.id"
                :type="getSlotType(slot.status)"
                size="small"
                class="slot-tag"
              >
                {{ slot.timeSlot }}
              </el-tag>
            </div>
          </div>
        </template>
      </el-calendar>
    </el-card>
    
    <!-- 批量设置对话框 -->
    <el-dialog v-model="showBatchDialog" title="批量设置时间表" width="600px">
      <el-form :model="batchForm" label-width="100px">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        
        <el-form-item label="星期">
          <el-checkbox-group v-model="batchForm.weekdays">
            <el-checkbox :label="1">周一</el-checkbox>
            <el-checkbox :label="2">周二</el-checkbox>
            <el-checkbox :label="3">周三</el-checkbox>
            <el-checkbox :label="4">周四</el-checkbox>
            <el-checkbox :label="5">周五</el-checkbox>
            <el-checkbox :label="6">周六</el-checkbox>
            <el-checkbox :label="7">周日</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        
        <el-form-item label="时间段">
          <el-checkbox-group v-model="batchForm.timeSlots">
            <el-checkbox label="09:00-10:00">09:00-10:00</el-checkbox>
            <el-checkbox label="10:00-11:00">10:00-11:00</el-checkbox>
            <el-checkbox label="14:00-15:00">14:00-15:00</el-checkbox>
            <el-checkbox label="15:00-16:00">15:00-16:00</el-checkbox>
            <el-checkbox label="16:00-17:00">16:00-17:00</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showBatchDialog = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSet">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 单日时间段管理对话框 -->
    <el-dialog v-model="showDayDialog" :title="`${selectedDate} 时间段管理`" width="500px">
      <el-table :data="daySlots">
        <el-table-column prop="timeSlot" label="时间段" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getSlotType(row.status)">{{ getSlotText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button v-if="row.status !== 2" type="primary" size="small" @click="updateSlotStatus(row, row.status === 0 ? 1 : 0)">
              {{ row.status === 0 ? '启用' : '休息' }}
            </el-button>
            <el-button v-if="row.status !== 2" type="danger" size="small" @click="deleteSlot(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const currentDate = ref(new Date())
const schedules = ref([])
const showBatchDialog = ref(false)
const showDayDialog = ref(false)
const selectedDate = ref('')
const daySlots = ref([])
const dateRange = ref([])
const batchForm = ref({
  weekdays: [],
  timeSlots: []
})

const getSchedules = async () => {
  try {
    const startDate = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth(), 1)
    const endDate = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 0)
    
    const res = await request.get('/counselor/schedule', {
      params: {
        startDate: startDate.toISOString().split('T')[0],
        endDate: endDate.toISOString().split('T')[0]
      }
    })
    schedules.value = res.data
  } catch (error) {
    ElMessage.error(error.message || '获取时间表失败')
  }
}

const getDateSlots = (date) => {
  return schedules.value.filter(s => s.scheduleDate === date)
}

const handleDateClick = (date) => {
  selectedDate.value = date
  daySlots.value = getDateSlots(date)
  if (daySlots.value.length > 0) {
    showDayDialog.value = true
  }
}

const handleBatchSet = async () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    ElMessage.warning('请选择日期范围')
    return
  }
  if (batchForm.value.timeSlots.length === 0) {
    ElMessage.warning('请选择时间段')
    return
  }
  
  try {
    await request.post('/counselor/schedule/batch', {
      startDate: dateRange.value[0],
      endDate: dateRange.value[1],
      weekdays: batchForm.value.weekdays,
      timeSlots: batchForm.value.timeSlots
    })
    ElMessage.success('设置成功')
    showBatchDialog.value = false
    getSchedules()
  } catch (error) {
    ElMessage.error(error.message || '设置失败')
  }
}

const updateSlotStatus = async (row, status) => {
  try {
    await request.put(`/counselor/schedule/${row.id}`, null, {
      params: { status }
    })
    ElMessage.success('更新成功')
    getSchedules()
    handleDateClick(selectedDate.value)
  } catch (error) {
    ElMessage.error(error.message || '更新失败')
  }
}

const deleteSlot = async (row) => {
  try {
    await request.delete(`/counselor/schedule/${row.id}`)
    ElMessage.success('删除成功')
    getSchedules()
    handleDateClick(selectedDate.value)
  } catch (error) {
    ElMessage.error(error.message || '删除失败')
  }
}

const getSlotType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

const getSlotText = (status) => {
  const texts = { 0: '休息', 1: '可预约', 2: '已预约' }
  return texts[status] || '未知'
}

onMounted(() => {
  getSchedules()
})
</script>

<style scoped lang="scss">
.schedule-page {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .calendar-day {
    height: 100%;
    cursor: pointer;
    padding: 5px;
    
    &:hover {
      background-color: #f5f7fa;
    }
    
    .day-number {
      font-weight: bold;
      margin-bottom: 5px;
    }
    
    .time-slots {
      display: flex;
      flex-direction: column;
      gap: 2px;
      
      .slot-tag {
        font-size: 10px;
      }
    }
  }
}
</style>
