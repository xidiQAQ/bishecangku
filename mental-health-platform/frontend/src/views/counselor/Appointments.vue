<template>
  <div class="appointments-page">
    <el-card>
      <template #header>
        <div class="header">
          <span>预约管理</span>
          <el-radio-group v-model="activeTab" @change="getAppointments">
            <el-radio-button label="pending">待确认</el-radio-button>
            <el-radio-button label="confirmed">已确认</el-radio-button>
            <el-radio-button label="history">历史记录</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      
      <el-table :data="appointments" v-loading="loading">
        <el-table-column prop="appointmentNo" label="预约编号" width="180" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="appointmentDate" label="预约日期" width="120" />
        <el-table-column prop="timeSlot" label="时间段" width="150" />
        <el-table-column prop="consultType" label="咨询类型" width="120" />
        <el-table-column prop="problemDesc" label="问题描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" type="success" size="small" @click="confirmAppointment(row.id)">确认</el-button>
            <el-button v-if="row.status === 0" type="danger" size="small" @click="rejectAppointment(row)">拒绝</el-button>
            <el-button v-if="row.status === 1" type="warning" size="small" @click="completeAppointment(row.id)">完成</el-button>
            <el-button v-if="row.status === 2" type="info" size="small" @click="addNote(row)">添加笔记</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        @current-change="getAppointments"
        layout="total, prev, pager, next"
        style="margin-top: 20px; justify-content: center"
      />
    </el-card>
    
    <!-- 详情对话框 -->
    <el-dialog v-model="showDetailDialog" title="预约详情" width="600px">
      <el-descriptions :column="2" border v-if="currentAppointment">
        <el-descriptions-item label="预约编号">{{ currentAppointment.appointmentNo }}</el-descriptions-item>
        <el-descriptions-item label="学生姓名">{{ currentAppointment.studentName }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ currentAppointment.appointmentDate }}</el-descriptions-item>
        <el-descriptions-item label="时间段">{{ currentAppointment.timeSlot }}</el-descriptions-item>
        <el-descriptions-item label="咨询类型">{{ currentAppointment.consultType }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentAppointment.status)">{{ getStatusText(currentAppointment.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="2">{{ currentAppointment.problemDesc }}</el-descriptions-item>
        <el-descriptions-item label="学生备注" :span="2">{{ currentAppointment.studentRemark || '无' }}</el-descriptions-item>
        <el-descriptions-item label="咨询师备注" :span="2">{{ currentAppointment.counselorRemark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('pending')
const appointments = ref([])
const loading = ref(false)
const showDetailDialog = ref(false)
const currentAppointment = ref(null)
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})

const getAppointments = async () => {
  loading.value = true
  try {
    const statusMap = {
      pending: 0,
      confirmed: 1,
      history: null
    }
    
    const res = await request.get('/counselor/appointments', {
      params: {
        current: pagination.value.current,
        size: pagination.value.size,
        status: statusMap[activeTab.value]
      }
    })
    
    appointments.value = res.data.records
    pagination.value.total = res.data.total
  } catch (error) {
    ElMessage.error(error.message || '获取预约列表失败')
  } finally {
    loading.value = false
  }
}

const viewDetail = (row) => {
  currentAppointment.value = row
  showDetailDialog.value = true
}

const confirmAppointment = async (id) => {
  try {
    await request.put(`/counselor/appointments/${id}/confirm`)
    ElMessage.success('确认成功')
    getAppointments()
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
    getAppointments()
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
    getAppointments()
  } catch (error) {
    ElMessage.error(error.message || '操作失败')
  }
}

const addNote = (row) => {
  router.push({
    path: '/counselor/notes',
    query: { appointmentId: row.id }
  })
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
  getAppointments()
})
</script>

<style scoped lang="scss">
.appointments-page {
  .header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
