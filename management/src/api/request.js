// src/api/request.js
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { clearAdminSession, loadAdminToken } from '@/utils/adminSession'

const service = axios.create({
  baseURL: '/api',
  timeout: 5000
})

// 【修改这里】：请求拦截器注入 Token
service.interceptors.request.use(
  config => {
    const token = loadAdminToken()
    if (token && !config.skipAuth) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || 'Error')
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res.data
    }
  },
  error => {
    if (error.response && error.response.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      clearAdminSession()
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    } else {
      ElMessage.error('Network Error')
    }
    return Promise.reject(error)
  }
)

export default service
