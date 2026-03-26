<template>
  <div class="counselor-layout">
    <el-container>
      <el-aside width="200px">
        <div class="logo">咨询师工作台</div>
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#2c3e50"
          text-color="#fff"
          active-text-color="#06b6d4"
        >
          <el-menu-item index="/counselor/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>工作台</span>
          </el-menu-item>
          <el-menu-item index="/counselor/schedule">
            <el-icon><Calendar /></el-icon>
            <span>时间管理</span>
          </el-menu-item>
          <el-menu-item index="/counselor/appointments">
            <el-icon><Document /></el-icon>
            <span>预约管理</span>
          </el-menu-item>
          <el-menu-item index="/counselor/notes">
            <el-icon><Notebook /></el-icon>
            <span>咨询笔记</span>
          </el-menu-item>
          <el-menu-item index="/counselor/profile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header>
          <div class="header-content">
            <span class="title">心理健康关怀平台</span>
            <div class="user-info">
              <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
                <el-button circle @click="goToNotifications">
                  <el-icon :size="20"><Bell /></el-icon>
                </el-button>
              </el-badge>
              <span>{{ userStore.userInfo?.realName }}</span>
              <el-button type="danger" size="small" @click="logout">退出</el-button>
            </div>
          </div>
        </el-header>
        
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { DataBoard, Calendar, Document, User, Bell } from '@element-plus/icons-vue'
import { getUnreadCount } from '@/api/notification'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const unreadCount = computed(() => userStore.unreadCount)
let pollingTimer = null

onMounted(() => {
  loadUnreadCount()
  // 每30秒轮询一次未读数量
  pollingTimer = setInterval(loadUnreadCount, 30000)
})

onUnmounted(() => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
  }
})

const loadUnreadCount = async () => {
  try {
    const res = await getUnreadCount()
    userStore.setUnreadCount(res.data || 0)
  } catch (error) {
    console.error('加载未读通知数量失败', error)
    // 如果接口不存在，设置为0，避免持续报错
    userStore.setUnreadCount(0)
  }
}

const goToNotifications = () => {
  router.push('/counselor/notifications')
}

const logout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.counselor-layout {
  height: 100vh;
  
  .el-container {
    height: 100%;
  }
  
  .el-aside {
    background-color: #2c3e50;
    
    .logo {
      height: 60px;
      line-height: 60px;
      text-align: center;
      color: #fff;
      font-size: 18px;
      font-weight: bold;
      border-bottom: 1px solid #34495e;
    }
    
    .el-menu {
      border-right: none;
    }
  }
  
  .el-header {
    background-color: #fff;
    border-bottom: 1px solid #e4e7ed;
    display: flex;
    align-items: center;
    padding: 0 20px;
    
    .header-content {
      width: 100%;
      display: flex;
      justify-content: space-between;
      align-items: center;
      
      .title {
        font-size: 20px;
        font-weight: bold;
        color: #2c3e50;
      }
      
      .user-info {
        display: flex;
        align-items: center;
        gap: 15px;

        .notification-badge {
          :deep(.el-badge__content) {
            background-color: #f56c6c;
          }
        }
      }
    }
  }
  
  .el-main {
    background-color: #f5f7fa;
    padding: 20px;
  }
}
</style>
