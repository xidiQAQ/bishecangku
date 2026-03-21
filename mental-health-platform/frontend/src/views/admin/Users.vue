<template>
  <div class="users-container">
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="用户类型">
          <el-select v-model="searchForm.userType" placeholder="请选择用户类型" clearable>
            <el-option label="全部" :value="null" />
            <el-option label="学生" :value="1" />
            <el-option label="咨询师" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="searchForm.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="用户类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.userType === 1 ? 'primary' : 'success'">
              {{ row.userType === 1 ? '学生' : row.userType === 2 ? '咨询师' : '管理员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" width="180" />
        <el-table-column label="活动统计" width="200">
          <template #default="{ row }">
            <div class="activity-stats">
              <span>预约: {{ row.appointmentCount || 0 }}</span>
              <span>树洞: {{ row.momentCount || 0 }}</span>
              <span>测试: {{ row.testCount || 0 }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              :type="row.status === 1 ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="primary" @click="handleViewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <el-dialog v-model="detailDialogVisible" title="用户详情" width="600px">
      <el-descriptions :column="2" border v-if="currentUser">
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ currentUser.realName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="用户类型">
          <el-tag :type="currentUser.userType === 1 ? 'primary' : 'success'">
            {{ currentUser.userType === 1 ? '学生' : '咨询师' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">
          {{ currentUser.createdAt }}
        </el-descriptions-item>
        <el-descriptions-item label="预约次数">
          {{ currentUser.appointmentCount || 0 }}
        </el-descriptions-item>
        <el-descriptions-item label="发布树洞">
          {{ currentUser.momentCount || 0 }}
        </el-descriptions-item>
        <el-descriptions-item label="测试次数" :span="2">
          {{ currentUser.testCount || 0 }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import adminApi from '@/api/admin'

const loading = ref(false)
const tableData = ref([])
const detailDialogVisible = ref(false)
const currentUser = ref(null)

const searchForm = reactive({
  userType: null,
  username: '',
  phone: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

onMounted(() => {
  loadData()
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await adminApi.getUserList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.data.records || []
    pagination.total = res.data.total || 0
  } catch (error) {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.userType = null
  searchForm.username = ''
  searchForm.phone = ''
  handleSearch()
}

const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  const newStatus = row.status === 1 ? 0 : 1
  
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户"${row.username}"吗？${row.status === 1 ? '禁用后该用户将无法登录系统。' : ''}`,
      '提示',
      { type: 'warning' }
    )
    
    await adminApi.updateUserStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

const handleViewDetail = (row) => {
  currentUser.value = row
  detailDialogVisible.value = true
}
</script>

<style scoped lang="scss">
.users-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  .activity-stats {
    display: flex;
    flex-direction: column;
    gap: 4px;
    font-size: 12px;
    color: #666;
  }
  
  :deep(.el-pagination) {
    margin-top: 20px;
    justify-content: flex-end;
  }
}
</style>
