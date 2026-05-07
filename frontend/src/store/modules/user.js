import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, getCurrentUser, updateProfile } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(null)
  
  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  
  // Actions
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }
  
  const clearToken = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }
  
  const loginAction = async (loginData) => {
    const res = await login(loginData)
    if (res.code === 1 || res.code === 200) {
      setToken(res.data.token)
      userInfo.value = res.data.user
      return res
    }
    throw new Error(res.msg || '登录失败')
  }
  
  const registerAction = async (registerData) => {
    const res = await register(registerData)
    if (res.code === 1 || res.code === 200) {
      setToken(res.data.token)
      userInfo.value = res.data.user
      return res
    }
    throw new Error(res.msg || '注册失败')
  }
  
  const fetchUserInfo = async () => {
    const res = await getCurrentUser()
    if (res.code === 1 || res.code === 200) {
      userInfo.value = res.data
      return res.data
    }
    throw new Error(res.msg || '获取用户信息失败')
  }
  
  const updateUserProfile = async (profileData) => {
    const res = await updateProfile(profileData)
    if (res.code === 1 || res.code === 200) {
      userInfo.value = res.data
      return res
    }
    throw new Error(res.msg || '更新资料失败')
  }
  
  const logout = () => {
    clearToken()
  }
  
  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    setToken,
    clearToken,
    loginAction,
    registerAction,
    fetchUserInfo,
    updateUserProfile,
    logout
  }
})
