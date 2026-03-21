<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalAppointments || 0 }}</div>
              <div class="stat-label">总预约数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%)">
              <el-icon><Check /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.completedAppointments || 0 }}</div>
              <div class="stat-label">已完成</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.monthAppointments || 0 }}</div>
              <div class="stat-label">本月预约</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fad0c4 0%, #ffd1ff 100%)">
              <el-icon><Star /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.averageRating?.toFixed(1) || '0.0' }}</div>
              <div class="stat-label">平均评分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>今日预约</span>
          </template>
          <el-table :data="todayAppointments" v-loading="loading">
            <el-table-column prop="appointmentNo" label="预约编号" width="180" />
            <el-table-column prop="studentName" label="学生姓名" width="120" />
            <el-table-column prop="timeSlot" label="时间段" width="150" />
            <el-table-column prop="consultType" label="咨询类型" width="120" />
            <el-table-column prop="problemDesc" label="问题描述" show-overflow-tooltip />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="{ row }">
                <el-button v-if="row.status === 0" type="primary" size="small" @click="confirmAppointment(row.id)">确认</el-button>
                <el-button v-if="row.status === 0" type="danger" size="small" @click="rejectAppointment(row)">拒绝</el-button>
                <el-button v-if="row.status === 1" type="success" size="small" @click="completeAppointment(row.id)">完成</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const statistics = ref({})
const todayAppointments = ref([])
const loading = ref(false)

const getStatistics = async () => {
  try {
    const res = await request.get('/counselor/statistics')
    statistics.value = res.data
  } catch (error) {
    ElMessage.error(error.message || '获取统计数据失败')
  }
}

const getTodayAppointments = async () => {
  loading.value = true
  try {
    const res = await request.get('/counselor/appointments', {
      params: {
        current: 1,
        size: 100
      }
    })
    const today = new Date().toISOString().split('T')[0]
    todayAppointments.value = res.data.records.filter(item => 
      item.appointmentDate === today && item.status !== 3 && item.status !== 4
    )
  } catch (error) {
    ElMessage.error(error.message || '获取预约列表失败')
  } finally {
    loading.value = false
  }
}

const confirmAppointment = async (id) => {
  try {
    await request.put(`/counselor/appointments/${id}/confirm`)
    ElMessage.success('确认成功')
    getTodayAppointments()
  } catch (error) {
    ElMessage.error(error.message || '确认失败')
  }
}

const rejectAppointment = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝预约', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputPattern: /.+/,
      inputErrorMessage: '请输入拒绝原因'
    })
    
    await request.put(`/counselor/appointments/${row.id}/reject`, null, {
      params: { rejectReason: value }
    })
    ElMessage.success('已拒绝')
    getTodayAppointments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '操作失败')
    }
  }
}

const completeAppointment = async (id) => {
  try {
    await request.put(`/counselor/appointments/${id}/complete`)
    ElMessage.success('已完成')
    getTodayAppointments()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'success', 2: 'info', 3: 'info', 4: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待确认', 1: '已确认', 2: '已完成', 3: '已取消', 4: '已拒绝' }
  return texts[status] || '未知'
}

onMounted(() => {
  getStatistics()
  getTodayAppointments()
})
</script>

<style scoped lang="scss">
.dashboard {
  .stat-card {
    .stat-content {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .stat-icon {
        width: 60px;
        height: 60px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 28px;
      }
      
      .stat-info {
        flex: 1;
        
        .stat-value {
          font-size: 28px;
          font-weight: bold;
          color: #2c3e50;
          margin-bottom: 5px;
        }
        
        .stat-label {
          font-size: 14px;
          color: #909399;
        }
      }
    }
  }
}
</style>
