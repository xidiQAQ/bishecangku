import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

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

  // 设置userId
  const setUserId = (id) => {
    userId.value = id
    localStorage.setItem('userId', id)
  }

  // 登录
  const login = (loginData) => {
    setToken(loginData.token)
    setUserId(loginData.userId)
    setUserInfo(loginData.userInfo)
  }

  // 登出
  const logout = () => {
    token.value = ''
    userId.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
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
    userId,
    userInfo,
    setUserInfo,
    setToken,
    setUserId,
    login,
    logout,
    isLogin,
    getUserType
  }
})
