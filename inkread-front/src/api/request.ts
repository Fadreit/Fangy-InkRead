import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { useAuthStore } from '@/stores/auth'
import type { Result } from '@/types/api'

const service = axios.create({
  baseURL: '/api',
  timeout: 12000
})

service.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('inkread_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
    config.headers['fy-inkread-token'] = token
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const result = response.data as Result
    if (!result || typeof result.code !== 'number') {
      return response.data
    }

    if (result.code === 200 || result.code === 201) {
      return result.data
    }

    if (result.code === 401) {
      const auth = useAuthStore()
      auth.logout(false)
      router.push({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
    }

    ElMessage.error(result.message || '请求失败')
    return Promise.reject(result)
  },
  (error: AxiosError<Result>) => {
    const message = error.response?.data?.message || error.message || '网络请求异常'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)

export default service
