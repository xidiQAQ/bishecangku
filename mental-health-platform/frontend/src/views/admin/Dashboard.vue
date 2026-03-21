<template>
  <div class="admin-dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalUsers || 0 }}</div>
              <div class="stat-label">总用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #06b6d4 0%, #0891b2 100%)">
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
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
              <el-icon><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.completedAppointments || 0 }}</div>
              <div class="stat-label">已完成预约</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fad0c4 0%, #ffd1ff 100%)">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.pendingMoments || 0 }}</div>
              <div class="stat-label">待审核树洞</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>最近预约</span>
          </template>
          <el-table :data="recentAppointments" max-height="400">
            <el-table-column prop="studentName" label="学生" width="100" />
            <el-table-column prop="counselorName" label="咨询师" width="100" />
            <el-table-column prop="appointmentDate" label="日期" width="120" />
            <el-table-column prop="timeSlot" label="时间段" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>待审核树洞</span>
          </template>
          <el-table :data="pendingMoments" max-height="400">
            <el-table-column prop="anonymousId" label="匿名ID" width="150" />
            <el-table-column prop="content" label="内容" show-overflow-tooltip />
            <el-table-column prop="createdAt" label="发布时间" width="180" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button type="success" size="small" @click="auditMoment(row.id, 1)">通过</el-button>
                <el-button type="danger" size="small" @click="auditMoment(row.id, 2)">拒绝</el-button>
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
const recentAppointments = ref([])
const pendingMoments = ref([])

const getStatistics = async () => {
  try {
    const res = await request.get('/admin/statistics')
    statistics.value = res.data
  } catch (error) {
    ElMessage.error(error.message || '获取统计数据失败')
  }
}

const getRecentAppointments = async () => {
  try {
    const res = await request.get('/admin/appointments', {
      params: {
        current: 1,
        size: 10
      }
    })
    recentAppointments.value = res.data.records
  } catch (error) {
    ElMessage.error(error.message || '获取预约列表失败')
  }
}

const getPendingMoments = async () => {
  try {
    const res = await request.get('/admin/moments/pending', {
      params: {
        pageNum: 1,
        pageSize: 10
      }
    })
    pendingMoments.value = res.data.records
  } catch (error) {
    ElMessage.error(error.message || '获取待审核列表失败')
  }
}

const auditMoment = async (id, status) => {
  try {
    if (status === 2) {
      const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝审核', {
        confirmButtonText: '确定',
        cancelButtonText: '取消'
      })
      
      await request.put(`/admin/moments/${id}/audit`, null, {
        params: {
          auditStatus: status,
          auditReason: value
        }
      })
    } else {
      await request.put(`/admin/moments/${id}/audit`, null, {
        params: { auditStatus: status }
      })
    }
    
    ElMessage.success('审核成功')
    getPendingMoments()
    getStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '审核失败')
    }
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
  getRecentAppointments()
  getPendingMoments()
})
</script>

<style scoped lang="scss">
.admin-dashboard {
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
