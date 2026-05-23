import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResult } from '../types'
import { useAuthStore } from '../stores/auth'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

request.interceptors.request.use((config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
})

request.interceptors.response.use(
  (response) => {
    const result = response.data as ApiResult<unknown>
    if (result.code !== 0) {
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(new Error(result.message))
    }
    return response
  },
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore().logoutLocal()
      location.href = '/login'
      return Promise.reject(error)
    }
    if (error.response?.status === 403) {
      ElMessage.error('当前账号没有权限执行该操作')
      return Promise.reject(error)
    }
    if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
      return Promise.reject(error)
    }
    ElMessage.error(error.response?.data?.message || '网络异常，请检查服务是否正常')
    return Promise.reject(error)
  }
)

export default request
