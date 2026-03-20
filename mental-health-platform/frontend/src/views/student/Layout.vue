<template>
  <div class="student-layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-content">
        <div class="logo">
          <el-icon class="logo-icon"><Sunny /></el-icon>
          <span class="logo-text">心理健康平台</span>
        </div>

        <nav class="nav-menu">
          <router-link
            v-for="item in menuItems"
            :key="item.path"
            :to="item.path"
            class="nav-item"
            active-class="active"
          >
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </router-link>
        </nav>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="36" :src="userInfo.avatar">
                {{ userInfo.realName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userInfo.realName || '用户' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="main-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 底部 -->
    <footer class="footer">
      <div class="footer-content">
        <p>&copy; 2024 大学生心理健康关怀平台. All rights reserved.</p>
        <p class="footer-links">
          <a href="#">关于我们</a>
          <span>|</span>
          <a href="#">隐私政策</a>
          <span>|</span>
          <a href="#">联系我们</a>
        </p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Sunny,
  HomeFilled,
  Document,
  Calendar,
  ChatDotRound,
  DataAnalysis,
  User,
  Setting,
  SwitchButton,
  ArrowDown
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo)

const menuItems = [
  { path: '/student/home', label: '首页', icon: HomeFilled },
  { path: '/student/articles', label: '心理科普', icon: Document },
  { path: '/student/counselors', label: '咨询预约', icon: Calendar },
  { path: '/student/tests', label: '心理测试', icon: DataAnalysis },
  { path: '/student/moments', label: '心灵树洞', icon: ChatDotRound }
]

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/student/profile')
      break
    case 'settings':
      router.push('/student/settings')
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  })
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.student-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: $bg-color;
}

.header {
  background: white;
  box-shadow: $shadow-md;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 $spacing-lg;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 700;
  color: $primary-color;
  cursor: pointer;

  .logo-icon {
    font-size: 32px;
    background: $gradient-primary;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.nav-menu {
  display: flex;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  border-radius: $radius-lg;
  color: $text-secondary;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: $transition-base;

  &:hover {
    background: rgba(99, 102, 241, 0.1);
    color: $primary-color;
  }

  &.active {
    background: $gradient-primary;
    color: white;
  }

  .el-icon {
    font-size: 18px;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: $radius-lg;
  cursor: pointer;
  transition: $transition-base;

  &:hover {
    background: $bg-color;
  }

  .user-name {
    font-size: 14px;
    font-weight: 500;
    color: $text-primary;
  }
}

.main-content {
  flex: 1;
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  padding: $spacing-xl $spacing-lg;
}

.footer {
  background: white;
  border-top: 1px solid $border-color;
  padding: $spacing-lg 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 $spacing-lg;
  text-align: center;
  color: $text-secondary;
  font-size: $font-size-sm;

  p {
    margin: 8px 0;
  }
}

.footer-links {
  display: flex;
  justify-content: center;
  gap: 12px;

  a {
    color: $text-secondary;
    text-decoration: none;
    transition: $transition-fast;

    &:hover {
      color: $primary-color;
    }
  }
}

@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }

  .header-content {
    padding: 0 $spacing-md;
  }

  .main-content {
    padding: $spacing-lg $spacing-md;
  }
}
</style>
