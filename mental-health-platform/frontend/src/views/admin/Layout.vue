<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="200px">
        <div class="logo">管理后台</div>
        <el-menu
          :default-active="activeMenu"
          router
          background-color="#2c3e50"
          text-color="#fff"
          active-text-color="#06b6d4"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据看板</span>
          </el-menu-item>
          <el-menu-item index="/admin/counselors">
            <el-icon><User /></el-icon>
            <span>咨询师管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/articles">
            <el-icon><Document /></el-icon>
            <span>文章管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/moments">
            <el-icon><ChatDotRound /></el-icon>
            <span>树洞审核</span>
          </el-menu-item>
          <el-menu-item index="/admin/users">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/statistics">
            <el-icon><TrendCharts /></el-icon>
            <span>数据统计</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header>
          <div class="header-content">
            <span class="title">心理健康关怀平台 - 管理后台</span>
            <div class="user-info">
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
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { DataAnalysis, User, Document, ChatDotRound, UserFilled, TrendCharts } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const logout = () => {
  userStore.logout()
  ElMessage.success('退出成功')
  router.push('/login')
}
</script>

<style scoped lang="scss">
.admin-layout {
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
      }
    }
  }
  
  .el-main {
    background-color: #f5f7fa;
    padding: 20px;
  }
}
</style>
