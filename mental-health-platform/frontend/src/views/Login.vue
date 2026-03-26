<template>
  <div class="login-page">
    <div class="login-wrapper">
      <!-- 左侧品牌区 -->
      <div class="brand-side">
        <div class="brand-container">
          <!-- Logo和标题 -->
          <div class="brand-header">
            <div class="logo-circle">
              <el-icon><Sunny /></el-icon>
            </div>
            <h1 class="brand-name">心理健康平台</h1>
            <p class="brand-slogan">专业心理服务 · 守护心灵健康</p>
          </div>

          <!-- 特色列表 -->
          <div class="features-list">
            <div class="feature-card">
              <div class="feature-icon">
                <el-icon><ChatDotRound /></el-icon>
              </div>
              <div class="feature-text">
                <h3>专业心理咨询</h3>
                <p>50+认证咨询师在线服务</p>
              </div>
            </div>

            <div class="feature-card">
              <div class="feature-icon">
                <el-icon><DataAnalysis /></el-icon>
              </div>
              <div class="feature-text">
                <h3>科学心理测评</h3>
                <p>多维度专业量表评估</p>
              </div>
            </div>

            <div class="feature-card">
              <div class="feature-icon">
                <el-icon><Clock /></el-icon>
              </div>
              <div class="feature-text">
                <h3>24小时在线支持</h3>
                <p>随时随地获得帮助</p>
              </div>
            </div>
          </div>

          <!-- 底部装饰 -->
          <div class="brand-footer">
            <div class="stats-item">
              <div class="stats-number">1,234+</div>
              <div class="stats-label">成功案例</div>
            </div>
            <div class="stats-item">
              <div class="stats-number">4.9</div>
              <div class="stats-label">用户评分</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单区 -->
      <div class="form-side">
        <div class="form-container">
          <!-- 表单头部 -->
          <div class="form-header">
            <h2 class="form-title">{{ isLogin ? '欢迎回来' : '创建账号' }}</h2>
            <p class="form-desc">{{ isLogin ? '登录以继续使用服务' : '开始您的心理健康之旅' }}</p>
          </div>

          <!-- 表单内容 -->
          <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
            <el-form-item prop="username">
              <el-input 
                v-model="form.username" 
                placeholder="请输入用户名" 
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input 
                v-model="form.password" 
                type="password" 
                placeholder="请输入密码" 
                size="large"
                :prefix-icon="Lock"
                show-password
              />
            </el-form-item>

            <el-form-item v-if="!isLogin" prop="realName">
              <el-input 
                v-model="form.realName" 
                placeholder="请输入真实姓名" 
                size="large"
                :prefix-icon="User"
              />
            </el-form-item>

            <el-form-item v-if="!isLogin" prop="phone">
              <el-input 
                v-model="form.phone" 
                placeholder="请输入手机号" 
                size="large"
                :prefix-icon="Phone"
              />
            </el-form-item>

            <el-form-item v-if="!isLogin" prop="email">
              <el-input 
                v-model="form.email" 
                placeholder="请输入邮箱" 
                size="large"
                :prefix-icon="Message"
              />
            </el-form-item>

            <el-form-item v-if="!isLogin" prop="userType">
              <el-select 
                v-model="form.userType" 
                placeholder="请选择身份" 
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
              class="submit-button" 
              :loading="loading"
              @click="handleSubmit"
            >
              {{ isLogin ? '登 录' : '注 册' }}
            </el-button>
          </el-form>

          <!-- 表单底部 -->
          <div class="form-footer">
            <span class="footer-text">
              {{ isLogin ? '还没有账号？' : '已有账号？' }}
            </span>
            <a class="footer-link" @click="toggleMode">
              {{ isLogin ? '立即注册' : '立即登录' }}
            </a>
          </div>

          <!-- 快捷登录 -->
          <div v-if="isLogin" class="quick-login">
            <div class="quick-divider">
              <span>快速体验</span>
            </div>
            
            <div class="quick-buttons">
              <button class="quick-btn" @click="quickLogin('admin', 'admin123')">
                <div class="quick-icon admin">
                  <el-icon><Setting /></el-icon>
                </div>
                <span>管理员</span>
              </button>
              
              <button class="quick-btn" @click="quickLogin('counselor1', 'admin123')">
                <div class="quick-icon counselor">
                  <el-icon><User /></el-icon>
                </div>
                <span>咨询师</span>
              </button>
              
              <button class="quick-btn" @click="quickLogin('student1', 'admin123')">
                <div class="quick-icon student">
                  <el-icon><User /></el-icon>
                </div>
                <span>学生</span>
              </button>
            </div>
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
import { User, Lock, Phone, Message, Setting, Sunny, ChatDotRound, DataAnalysis, Clock } from '@element-plus/icons-vue'
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
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  userType: [{ required: true, message: '请选择身份', trigger: 'change' }]
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
      const res = await request.post('/auth/login', {
        username: form.username,
        password: form.password
      })

      if (res.code === 200) {
        userStore.login({
          token: res.data.token,
          refreshToken: res.data.refreshToken,
          userId: res.data.id,
          userInfo: res.data
        })
        ElMessage.success('登录成功')
        const userType = res.data.userType
        if (userType === 1) router.push('/student/home')
        else if (userType === 2) router.push('/counselor/dashboard')
        else if (userType === 3) router.push('/admin/dashboard')
      }
    } else {
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

const quickLogin = async (username, password) => {
  form.username = username
  form.password = password
  loading.value = true
  try {
    const res = await request.post('/auth/login', { username, password })
    if (res.code === 200) {
      userStore.login({
        token: res.data.token,
        userId: res.data.id,
        userInfo: res.data
      })
      ElMessage.success('登录成功')
      const userType = res.data.userType
      if (userType === 1) router.push('/student/home')
      else if (userType === 2) router.push('/counselor/dashboard')
      else if (userType === 3) router.push('/admin/dashboard')
    }
  } catch (error) {
    console.error('快捷登录失败:', error)
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
  background: $bg-color;
}

// 左侧装饰区
.left-section {
  flex: 1;
  background: $gradient-dark;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: $spacing-4xl;
  
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.05) 0%, transparent 70%);
    animation: pulse 8s ease-in-out infinite;
  }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); opacity: 0.5; }
  50% { transform: scale(1.1); opacity: 0.8; }
}

.decoration-content {
  position: relative;
  z-index: 1;
}

.floating-card {
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: $radius-xl;
  padding: $spacing-lg $spacing-xl;
  display: flex;
  align-items: center;
  gap: $spacing-md;
  margin-bottom: $spacing-lg;
  animation: float 6s ease-in-out infinite;
  
  &.card-1 {
    animation-delay: 0s;
    margin-left: 0;
  }
  
  &.card-2 {
    animation-delay: 2s;
    margin-left: $spacing-3xl;
  }
  
  &.card-3 {
    animation-delay: 4s;
    margin-left: $spacing-2xl;
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.card-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.15);
  border-radius: $radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.card-info {
  color: white;
}

.card-number {
  font-size: $font-size-2xl;
  font-weight: $font-weight-bold;
  line-height: 1;
  margin-bottom: 4px;
}

.card-label {
  font-size: $font-size-sm;
  opacity: 0.8;
}

.brand-area {
  position: relative;
  z-index: 1;
  color: white;
}

.logo-box {
  display: flex;
  align-items: center;
  gap: $spacing-md;
  margin-bottom: $spacing-md;
}

.logo-icon {
  width: 56px;
  height: 56px;
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.brand-title {
  font-size: $font-size-3xl;
  font-weight: $font-weight-bold;
  letter-spacing: -0.02em;
}

.brand-desc {
  font-size: $font-size-md;
  opacity: 0.8;
  margin-left: 70px;
}

// 右侧表单区
.right-section {
  width: 520px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-3xl;
}

.form-box {
  width: 100%;
  max-width: 420px;
}

// Tab切换
.tab-switcher {
  position: relative;
  display: flex;
  background: $bg-soft;
  border-radius: $radius-lg;
  padding: 4px;
  margin-bottom: $spacing-3xl;
}

.tab-btn {
  flex: 1;
  text-align: center;
  padding: $spacing-md;
  font-size: $font-size-md;
  font-weight: $font-weight-medium;
  color: $text-secondary;
  cursor: pointer;
  transition: $transition-fast;
  position: relative;
  z-index: 2;
  
  &.active {
    color: $text-primary;
  }
}

.tab-slider {
  position: absolute;
  top: 4px;
  left: 4px;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background: white;
  border-radius: $radius-md;
  box-shadow: $shadow-sm;
  transition: $transition-base;
  z-index: 1;
  
  &.right {
    transform: translateX(100%);
  }
}

// 表单标题
.form-title-area {
  margin-bottom: $spacing-xl;
}

.form-title {
  font-size: $font-size-4xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $spacing-sm;
  letter-spacing: -0.02em;
}

.form-subtitle {
  font-size: $font-size-md;
  color: $text-secondary;
}

// 表单样式
.login-form {
  .el-form-item {
    margin-bottom: $spacing-lg;
  }
}

.custom-input {
  position: relative;
  
  .input-prefix {
    position: absolute;
    left: 16px;
    top: 50%;
    transform: translateY(-50%);
    font-size: 18px;
    color: $text-tertiary;
    z-index: 1;
  }
  
  :deep(.el-input__wrapper) {
    padding-left: 48px;
    border-radius: $radius-lg;
    border: 2px solid $border-light;
    box-shadow: none;
    transition: $transition-base;
    
    &:hover {
      border-color: $border-color;
    }
    
    &.is-focus {
      border-color: $primary-color;
      box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
    }
  }
  
  :deep(.el-input__inner) {
    font-size: $font-size-md;
  }
}

:deep(.el-select .el-input__wrapper) {
  padding-left: 16px;
}

.submit-btn {
  width: 100%;
  height: 52px;
  border-radius: $radius-lg;
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  background: $gradient-cool;
  border: none;
  margin-top: $spacing-md;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: $spacing-sm;
  transition: $transition-base;
  
  .btn-icon {
    transition: $transition-fast;
  }
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-hover;
    
    .btn-icon {
      transform: translateX(4px);
    }
  }
}

// 快捷登录
.quick-login-area {
  margin-top: $spacing-3xl;
}

.divider-line {
  position: relative;
  text-align: center;
  margin-bottom: $spacing-xl;
  
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 1px;
    background: $border-color;
  }
  
  span {
    position: relative;
    background: white;
    padding: 0 $spacing-md;
    color: $text-tertiary;
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
  }
}

.quick-btns {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-md;
}

.quick-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-sm;
  padding: $spacing-lg;
  background: $bg-soft;
  border-radius: $radius-lg;
  cursor: pointer;
  transition: $transition-base;
  border: 2px solid transparent;
  
  &:hover {
    background: white;
    border-color: $primary-color;
    transform: translateY(-4px);
    box-shadow: $shadow-lg;
  }
  
  span {
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    color: $text-primary;
  }
}

.quick-avatar {
  width: 48px;
  height: 48px;
  border-radius: $radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  
  &.admin-bg {
    background: linear-gradient(135deg, #64748B 0%, #475569 100%);
  }
  
  &.counselor-bg {
    background: linear-gradient(135deg, #0EA5E9 0%, #2563EB 100%);
  }
  
  &.student-bg {
    background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  }
}

// 响应式
@media (max-width: 968px) {
  .login-container {
    flex-direction: column;
  }
  
  .left-section {
    min-height: 300px;
    padding: $spacing-xl;
  }
  
  .floating-card {
    &.card-2, &.card-3 {
      margin-left: 0;
    }
  }
  
  .right-section {
    width: 100%;
  }
}
</style>


<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.login-page {
  min-height: 100vh;
  background: $bg-green-light;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.login-wrapper {
  display: flex;
  width: 100%;
  max-width: 1100px;
  min-height: 650px;
  background: white;
  border-radius: $radius-3xl;
  box-shadow: $shadow-2xl;
  overflow: hidden;
}

// 左侧品牌区
.brand-side {
  flex: 1;
  background: $gradient-green;
  padding: $spacing-4xl;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: -100px;
    right: -100px;
    width: 300px;
    height: 300px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
  }
  
  &::after {
    content: '';
    position: absolute;
    bottom: -150px;
    left: -150px;
    width: 400px;
    height: 400px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 50%;
  }
}

.brand-container {
  position: relative;
  z-index: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: white;
}

.brand-header {
  text-align: center;
}

.logo-circle {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: $radius-2xl;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  margin: 0 auto $spacing-lg;
  animation: logoFloat 3s ease-in-out infinite;
}

@keyframes logoFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.brand-name {
  font-size: $font-size-4xl;
  font-weight: $font-weight-bold;
  margin-bottom: $spacing-sm;
  letter-spacing: -0.01em;
}

.brand-slogan {
  font-size: $font-size-md;
  opacity: 0.9;
}

.features-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;
  margin: $spacing-3xl 0;
}

.feature-card {
  display: flex;
  align-items: flex-start;
  gap: $spacing-md;
  padding: $spacing-lg;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  border-radius: $radius-lg;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: $transition-base;
  
  &:hover {
    background: rgba(255, 255, 255, 0.15);
    transform: translateX(8px);
  }
}

.feature-icon {
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: $radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  flex-shrink: 0;
}

.feature-text {
  h3 {
    font-size: $font-size-lg;
    font-weight: $font-weight-semibold;
    margin-bottom: 4px;
  }
  
  p {
    font-size: $font-size-sm;
    opacity: 0.9;
  }
}

.brand-footer {
  display: flex;
  gap: $spacing-3xl;
  padding-top: $spacing-xl;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
}

.stats-item {
  flex: 1;
}

.stats-number {
  font-size: $font-size-3xl;
  font-weight: $font-weight-bold;
  margin-bottom: 4px;
}

.stats-label {
  font-size: $font-size-sm;
  opacity: 0.9;
}

// 右侧表单区
.form-side {
  width: 480px;
  padding: $spacing-4xl;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-container {
  width: 100%;
  max-width: 400px;
}

.form-header {
  margin-bottom: $spacing-3xl;
  text-align: center;
}

.form-title {
  font-size: $font-size-4xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $spacing-sm;
  letter-spacing: -0.02em;
}

.form-desc {
  font-size: $font-size-md;
  color: $text-secondary;
}

.login-form {
  .el-form-item {
    margin-bottom: $spacing-lg;
  }
  
  :deep(.el-input__wrapper) {
    border-radius: $radius-lg;
    padding: 14px 16px;
    border: 2px solid $border-light;
    box-shadow: none;
    transition: $transition-base;
    
    &:hover {
      border-color: $border-color;
    }
    
    &.is-focus {
      border-color: $primary-color;
      box-shadow: 0 0 0 4px rgba(16, 185, 129, 0.1);
    }
  }
  
  :deep(.el-input__inner) {
    font-size: $font-size-md;
  }
  
  :deep(.el-select .el-input__wrapper) {
    padding: 14px 16px;
  }
}

.submit-button {
  width: 100%;
  height: 52px;
  border-radius: $radius-lg;
  font-size: $font-size-lg;
  font-weight: $font-weight-semibold;
  background: $gradient-green;
  border: none;
  margin-top: $spacing-md;
  letter-spacing: 0.1em;
  transition: $transition-base;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-hover;
  }
  
  &:active {
    transform: translateY(0);
  }
}

.form-footer {
  margin-top: $spacing-xl;
  text-align: center;
  font-size: $font-size-sm;
}

.footer-text {
  color: $text-secondary;
}

.footer-link {
  color: $primary-color;
  font-weight: $font-weight-semibold;
  cursor: pointer;
  margin-left: 4px;
  transition: $transition-fast;
  
  &:hover {
    color: $primary-dark;
    text-decoration: underline;
  }
}

// 快捷登录
.quick-login {
  margin-top: $spacing-3xl;
}

.quick-divider {
  position: relative;
  text-align: center;
  margin-bottom: $spacing-xl;
  
  &::before {
    content: '';
    position: absolute;
    top: 50%;
    left: 0;
    right: 0;
    height: 1px;
    background: $border-color;
  }
  
  span {
    position: relative;
    background: white;
    padding: 0 $spacing-md;
    color: $text-tertiary;
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
  }
}

.quick-buttons {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-md;
}

.quick-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-sm;
  padding: $spacing-lg $spacing-sm;
  background: $bg-soft;
  border-radius: $radius-lg;
  border: 2px solid transparent;
  cursor: pointer;
  transition: $transition-base;
  
  &:hover {
    background: white;
    border-color: $primary-color;
    transform: translateY(-4px);
    box-shadow: $shadow-lg;
  }
  
  span {
    font-size: $font-size-sm;
    font-weight: $font-weight-medium;
    color: $text-primary;
  }
}

.quick-icon {
  width: 48px;
  height: 48px;
  border-radius: $radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  
  &.admin {
    background: linear-gradient(135deg, #64748B 0%, #475569 100%);
  }
  
  &.counselor {
    background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%);
  }
  
  &.student {
    background: linear-gradient(135deg, #10B981 0%, #059669 100%);
  }
}

// 响应式
@media (max-width: 968px) {
  .login-wrapper {
    flex-direction: column;
    max-width: 500px;
  }
  
  .brand-side {
    padding: $spacing-3xl $spacing-xl;
  }
  
  .features-list {
    margin: $spacing-xl 0;
  }
  
  .form-side {
    width: 100%;
    padding: $spacing-3xl $spacing-xl;
  }
  
  .brand-name {
    font-size: $font-size-3xl;
  }
  
  .form-title {
    font-size: $font-size-3xl;
  }
}
</style>
