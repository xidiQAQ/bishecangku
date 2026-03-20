<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="login-left">
        <div class="welcome-content">
          <h1 class="welcome-title">
            <span class="gradient-text">心理健康</span>
            <br />关怀平台
          </h1>
          <p class="welcome-subtitle">
            为每一个需要倾听的心灵<br />提供温暖的港湾
          </p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon class="feature-icon"><ChatDotRound /></el-icon>
              <span>专业心理咨询</span>
            </div>
            <div class="feature-item">
              <el-icon class="feature-icon"><Calendar /></el-icon>
              <span>便捷在线预约</span>
            </div>
            <div class="feature-item">
              <el-icon class="feature-icon"><Document /></el-icon>
              <span>心理健康测评</span>
            </div>
          </div>
        </div>
      </div>

      <div class="login-right">
        <div class="login-form-wrapper">
          <h2 class="form-title">{{ isLogin ? '欢迎回来' : '创建账号' }}</h2>
          <p class="form-subtitle">{{ isLogin ? '登录您的账号' : '开始您的心理健康之旅' }}</p>

          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            class="login-form"
            @submit.prevent="handleSubmit"
          >
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="用户名"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="密码"
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item v-if="!isLogin" prop="realName">
              <el-input
                v-model="form.realName"
                placeholder="真实姓名"
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item v-if="!isLogin" prop="phone">
              <el-input
                v-model="form.phone"
                placeholder="手机号"
                size="large"
                :prefix-icon="Phone"
              />
            </el-form-item>

            <el-form-item v-if="!isLogin" prop="email">
              <el-input
                v-model="form.email"
                placeholder="邮箱"
                size="large"
                :prefix-icon="Message"
              />
            </el-form-item>

            <el-form-item v-if="!isLogin" prop="userType">
              <el-select
                v-model="form.userType"
                placeholder="选择身份"
                size="large"
                style="width: 100%"
              >
                <el-option label="学生" :value="1" />
                <el-option label="咨询师" :value="2" />
              </el-select>
            </el-form-item>

            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="loading"
              @click="handleSubmit"
            >
              {{ isLogin ? '登录' : '注册' }}
            </el-button>
          </el-form>

          <div class="form-footer">
            <span class="toggle-text">
              {{ isLogin ? '还没有账号？' : '已有账号？' }}
              <a @click="toggleMode" class="toggle-link">
                {{ isLogin ? '立即注册' : '立即登录' }}
              </a>
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Phone, Message, ChatDotRound, Calendar, Document } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import request from '@/utils/request'

const router = useRouter()
const userStore = useUserStore()

const isLogin = ref(true)
const loading = ref(false)
const formRef = ref(null)

const form = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  userType: 1
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入真实姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  userType: [
    { required: true, message: '请选择身份', trigger: 'change' }
  ]
}

const toggleMode = () => {
  isLogin.value = !isLogin.value
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    loading.value = true

    if (isLogin.value) {
      // 登录
      const res = await request.post('/auth/login', {
        username: form.username,
        password: form.password
      })

      if (res.code === 200) {
        userStore.login({
          token: res.data.token,
          userId: res.data.userId,
          userInfo: res.data
        })

        ElMessage.success('登录成功')

        // 根据用户类型跳转
        const userType = res.data.userType
        if (userType === 1) {
          router.push('/student')
        } else if (userType === 2) {
          router.push('/counselor')
        } else if (userType === 3) {
          router.push('/admin')
        }
      }
    } else {
      // 注册
      const res = await request.post('/auth/register', {
        username: form.username,
        password: form.password,
        realName: form.realName,
        phone: form.phone,
        email: form.email,
        userType: form.userType
      })

      if (res.code === 200) {
        ElMessage.success('注册成功，请登录')
        isLogin.value = true
        form.password = ''
      }
    }
  } catch (error) {
    console.error('提交失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #06b6d4 0%, #3b82f6 100%);
  position: relative;
  overflow: hidden;
  padding: 20px;
}

.bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  overflow: hidden;

  .circle {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    animation: float 20s infinite ease-in-out;

    &.circle-1 {
      width: 300px;
      height: 300px;
      top: -100px;
      left: -100px;
      animation-delay: 0s;
    }

    &.circle-2 {
      width: 200px;
      height: 200px;
      bottom: -50px;
      right: 10%;
      animation-delay: 5s;
    }

    &.circle-3 {
      width: 150px;
      height: 150px;
      top: 50%;
      right: -50px;
      animation-delay: 10s;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

.login-card {
  position: relative;
  z-index: 1;
  display: flex;
  width: 100%;
  max-width: 1000px;
  min-height: 600px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, #06b6d4 0%, #3b82f6 100%);
  padding: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.welcome-content {
  max-width: 400px;
}

.welcome-title {
  font-size: 42px;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 20px;

  .gradient-text {
    background: linear-gradient(135deg, #fff 0%, #f0f0f0 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
}

.welcome-subtitle {
  font-size: 18px;
  line-height: 1.6;
  opacity: 0.9;
  margin-bottom: 40px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;

  .feature-icon {
    font-size: 24px;
    background: rgba(255, 255, 255, 0.2);
    padding: 8px;
    border-radius: 8px;
  }
}

.login-right {
  flex: 1;
  padding: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-form-wrapper {
  width: 100%;
  max-width: 400px;
}

.form-title {
  font-size: 32px;
  font-weight: 700;
  color: $text-primary;
  margin-bottom: 8px;
}

.form-subtitle {
  font-size: 16px;
  color: $text-secondary;
  margin-bottom: 32px;
}

.login-form {
  .el-form-item {
    margin-bottom: 20px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 12px;
    padding: 12px 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  :deep(.el-select .el-input__wrapper) {
    padding: 12px 16px;
  }
}

.submit-btn {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #06b6d4 0%, #3b82f6 100%);
  border: none;
  margin-top: 12px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(6, 182, 212, 0.4);
  }
}

.form-footer {
  margin-top: 24px;
  text-align: center;
}

.toggle-text {
  color: $text-secondary;
  font-size: 14px;
}

.toggle-link {
  color: $primary-color;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s ease;

  &:hover {
    color: $primary-dark;
    text-decoration: underline;
  }
}

@media (max-width: 768px) {
  .login-card {
    flex-direction: column;
    max-width: 500px;
  }

  .login-left {
    padding: 40px 30px;
  }

  .login-right {
    padding: 40px 30px;
  }

  .welcome-title {
    font-size: 32px;
  }

  .form-title {
    font-size: 28px;
  }
}
</style>
