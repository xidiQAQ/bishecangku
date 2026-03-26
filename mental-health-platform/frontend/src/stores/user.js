import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
  const unreadCount = ref(0) // 未读通知数量

  // 设置用户信息
  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 设置token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 设置refreshToken
  const setRefreshToken = (newRefreshToken) => {
    refreshToken.value = newRefreshToken
    localStorage.setItem('refreshToken', newRefreshToken)
  }

  // 设置userId
  const setUserId = (id) => {
    userId.value = id
    localStorage.setItem('userId', id)
  }

  // 设置未读通知数量
  const setUnreadCount = (count) => {
    unreadCount.value = count
  }

  // 登录
  const login = (loginData) => {
    setToken(loginData.token)
    setRefreshToken(loginData.refreshToken)
    setUserId(loginData.userId)
    setUserInfo(loginData.userInfo)
  }

  // 登出
  const logout = () => {
    token.value = ''
    refreshToken.value = ''
    userId.value = ''
    userInfo.value = {}
    unreadCount.value = 0
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userId')
    localStorage.removeItem('userInfo')
  }

  // 是否登录
  const isLogin = () => {
    return !!token.value
  }

  // 获取用户类型
  const getUserType = () => {
    return userInfo.value.userType || 0
  }

  return {
    token,
    refreshToken,
    userId,
    userInfo,
    unreadCount,
    setUserInfo,
    setToken,
    setRefreshToken,
    setUserId,
    setUnreadCount,
    login,
    logout,
    isLogin,
    getUserType
  }
})
