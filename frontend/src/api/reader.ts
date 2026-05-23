import request from '../utils/request'
import type { ApiResult, PageResult, ReaderStatus, ReaderVO } from '../types'

export function getReaders(params: { page: number; pageSize: number; keyword?: string }) {
  return request.get<ApiResult<PageResult<ReaderVO>>>('/readers', { params }).then((res) => res.data.data)
}

export function createReader(data: { studentNo: string; name: string; college?: string; phone?: string; userId?: number }) {
  return request.post<ApiResult<ReaderVO>>('/readers', data)
}

export function updateReader(id: number, data: { studentNo: string; name: string; college?: string; phone?: string; userId?: number; status: ReaderStatus }) {
  return request.put<ApiResult<ReaderVO>>(`/readers/${id}`, data)
}

export function deleteReader(id: number) {
  return request.delete<ApiResult<void>>(`/readers/${id}`)
}
