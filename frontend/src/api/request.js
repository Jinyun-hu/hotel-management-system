import axios from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    // 如果是Blob类型响应（如文件下载），直接返回response
    if (response.config.responseType === 'blob') {
      return response
    }

    const res = response.data
    console.log('API Response:', res)

    // 如果响应码不是1或200，说明有错误
    if (res.code !== 1 && res.code !== 200) {
      ElMessage.error(res.msg || res.message || '请求失败')

      // 如果是401未授权，清除token并跳转到登录页
      if (res.code === 401) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }

      return Promise.reject(new Error(res.msg || res.message || '请求失败'))
    }

    return res
  },
  (error) => {
    console.error('API Error:', error)
    const { response } = error

    if (response) {
      console.error('Response data:', response.data)
      switch (response.status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          break
        case 403:
          ElMessage.error('没有权限执行此操作')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(response.data?.msg || response.data?.message || '网络错误')
      }
    } else {
      ElMessage.error('网络连接失败')
    }

    return Promise.reject(error)
  }
)

export default request
