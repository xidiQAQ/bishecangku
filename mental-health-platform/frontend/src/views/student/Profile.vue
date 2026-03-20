<template>
  <div class="profile-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">个人中心</h1>
    </div>

    <div class="profile-container">
      <!-- 个人信息卡片 -->
      <div class="info-card">
        <div class="avatar-section">
          <el-avatar :size="100" :src="userInfo.avatar">
            {{ userInfo.realName?.charAt(0) }}
          </el-avatar>
          <el-button text @click="showEditDialog = true">
            <el-icon><Edit /></el-icon>
            编辑资料
          </el-button>
        </div>

        <div class="info-section">
          <div class="info-item">
            <span class="label">姓名</span>
            <span class="value">{{ userInfo.realName }}</span>
          </div>
          <div class="info-item">
            <span class="label">手机号</span>
            <span class="value">{{ userInfo.phone }}</span>
          </div>
          <div class="info-item">
            <span class="label">学号</span>
            <span class="value">{{ userInfo.studentId || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">注册时间</span>
            <span class="value">{{ formatDate(userInfo.createdAt) }}</span>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
            <el-icon><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.appointmentCount }}</div>
            <div class="stat-label">预约次数</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.testCount }}</div>
            <div class="stat-label">测试次数</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.collectCount }}</div>
            <div class="stat-label">收藏文章</div>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.momentCount }}</div>
            <div class="stat-label">树洞发布</div>
          </div>
        </div>
      </div>

      <!-- 快捷操作 -->
      <div class="actions-card">
        <h3 class="card-title">快捷操作</h3>
        <div class="actions-list">
          <div class="action-item" @click="router.push('/student/appointments')">
            <el-icon><Calendar /></el-icon>
            <span>我的预约</span>
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </div>
          <div class="action-item" @click="router.push('/student/tests')">
            <el-icon><DataAnalysis /></el-icon>
            <span>测试历史</span>
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </div>
          <div class="action-item" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
            <el-icon class="arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑资料" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="editForm.realName" />
        </el-form-item>
        <el-form-item label="学号">
          <el-input v-model="editForm.studentId" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProfile" :loading="saving">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Edit,
  Calendar,
  Document,
  Star,
  ChatDotRound,
  DataAnalysis,
  SwitchButton,
  ArrowRight
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)
const showEditDialog = ref(false)
const saving = ref(false)

const editForm = ref({
  realName: '',
  studentId: ''
})

const stats = ref({
  appointmentCount: 0,
  testCount: 0,
  collectCount: 0,
  momentCount: 0
})

const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD')
}

const saveProfile = async () => {
  try {
    saving.value = true
    // TODO: 调用更新接口
    ElMessage.success('保存成功')
    showEditDialog.value = false
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  } catch (error) {
    // 用户取消
  }
}

onMounted(() => {
  editForm.value = {
    realName: userInfo.value.realName,
    studentId: userInfo.value.studentId
  }
  
  // TODO: 获取统计数据
  stats.value = {
    appointmentCount: 5,
    testCount: 3,
    collectCount: 12,
    momentCount: 8
  }
})
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.profile-page {
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: $spacing-xl;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: $text-primary;
}

.profile-container {
  display: flex;
  flex-direction: column;
  gap: $spacing-xl;
}

.info-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-md;
  display: flex;
  gap: $spacing-xl * 2;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-md;
}

.info-section {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $spacing-lg;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.label {
  font-size: 13px;
  color: $text-tertiary;
}

.value {
  font-size: 16px;
  color: $text-primary;
  font-weight: 500;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $spacing-lg;
}

.stat-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-lg;
  display: flex;
  gap: $spacing-md;
  box-shadow: $shadow-md;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: $radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  flex-shrink: 0;
}

.stat-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: $text-primary;
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: $text-tertiary;
}

.actions-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-xl;
  box-shadow: $shadow-md;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: $text-primary;
  margin-bottom: $spacing-lg;
}

.actions-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-sm;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: $spacing-md;
  border-radius: $radius-md;
  cursor: pointer;
  transition: $transition-base;

  &:hover {
    background: $bg-color;
  }

  .el-icon {
    font-size: 20px;
    color: $primary-color;
  }

  span {
    flex: 1;
    font-size: 15px;
    color: $text-primary;
  }

  .arrow {
    color: $text-tertiary;
    font-size: 16px;
  }
}

@media (max-width: 768px) {
  .info-card {
    flex-direction: column;
  }

  .info-section {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
