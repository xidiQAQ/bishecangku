import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useUserStore } from '@/stores/user'

// 创建axios实例
const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000
})

// 是否正在刷新token
let isRefreshing = false
// 重试队列
let requests = []

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 添加token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    
    // 添加userId
    const userId = localStorage.getItem('userId')
    if (userId) {
      config.headers['userId'] = userId
    }
    
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    
    // 如果返回的状态码不是200，说明接口有问题
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 401: 未登录或token过期
      if (res.code === 401) {
        return handleTokenExpired(response.config)
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return res
  },
  error => {
    console.error('响应错误:', error)
    
    if (error.response) {
      switch (error.response.status) {
        case 401:
          return handleTokenExpired(error.config)
        case 403:
          ElMessage.error('没有权限访问')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(error.response.data.message || '请求失败')
      }
    } else {
      ElMessage.error('网络连接失败')
    }
    
    return Promise.reject(error)
  }
)

// 处理Token过期
async function handleTokenExpired(config) {
  const refreshToken = localStorage.getItem('refreshToken')
  
  // 如果没有refreshToken，直接跳转登录
  if (!refreshToken) {
    clearAuthAndRedirect()
    return Promise.reject(new Error('未登录'))
  }
  
  // 如果正在刷新token，将请求加入队列
  if (isRefreshing) {
    return new Promise((resolve) => {
      requests.push((token) => {
        config.headers['Authorization'] = `Bearer ${token}`
        resolve(request(config))
      })
    })
  }
  
  isRefreshing = true
  
  try {
    // 调用刷新token接口
    const response = await axios.post(
      `${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'}/auth/refresh`,
      {},
      {
        headers: {
          'Refresh-Token': refreshToken
        }
      }
    )
    
    if (response.data.code === 200) {
      const { token: newToken, refreshToken: newRefreshToken } = response.data.data
      
      // 更新token
      const userStore = useUserStore()
      userStore.setToken(newToken)
      userStore.setRefreshToken(newRefreshToken)
      
      // 重试队列中的请求
      requests.forEach(cb => cb(newToken))
      requests = []
      
      // 重试当前请求
      config.headers['Authorization'] = `Bearer ${newToken}`
      return request(config)
    } else {
      clearAuthAndRedirect()
      return Promise.reject(new Error('Token刷新失败'))
    }
  } catch (error) {
    console.error('Token刷新失败:', error)
    clearAuthAndRedirect()
    return Promise.reject(error)
  } finally {
    isRefreshing = false
  }
}

// 清除认证信息并跳转登录
function clearAuthAndRedirect() {
  ElMessage.error('登录已过期，请重新登录')
  localStorage.removeItem('token')
  localStorage.removeItem('refreshToken')
  localStorage.removeItem('userId')
  localStorage.removeItem('userInfo')
  router.push('/login')
}

export default request
