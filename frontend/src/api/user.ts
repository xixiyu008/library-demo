import request from '../utils/request'
import type { ApiResult, PageResult, Role, UserVO } from '../types'

export function getUsers(params: { page: number; pageSize: number; keyword?: string }) {
  return request.get<ApiResult<PageResult<UserVO>>>('/users', { params }).then((res) => res.data.data)
}

export function createUser(data: { username: string; password: string; role: Role; enabled: boolean }) {
  return request.post<ApiResult<UserVO>>('/users', data)
}

export function updateUser(id: number, data: { password?: string; role: Role; enabled: boolean }) {
  return request.put<ApiResult<UserVO>>(`/users/${id}`, data)
}

export function deleteUser(id: number) {
  return request.delete<ApiResult<void>>(`/users/${id}`)
}
