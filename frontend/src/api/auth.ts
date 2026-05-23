import request from '../utils/request'
import type { ApiResult, Role } from '../types'

export interface LoginResponse {
  token: string
  username: string
  role: Role
}

export function login(data: { username: string; password: string; captchaKey?: string; captchaCode?: string }) {
  return request.post<ApiResult<LoginResponse>>('/auth/login', data).then((res) => res.data.data)
}

export function logout() {
  return request.post<ApiResult<void>>('/auth/logout')
}

export function getCaptcha() {
  return request.get<ApiResult<{ captchaKey: string; captchaCode: string }>>('/auth/captcha').then((res) => res.data.data)
}
