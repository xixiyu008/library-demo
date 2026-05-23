import request from '../utils/request'
import type { ApiResult, BorrowRecordVO, BorrowStatus, PageResult } from '../types'

export function borrowBook(data: { readerId: number; bookId: number }) {
  return request.post<ApiResult<{ borrowRecordId: number }>>('/borrows', data)
}

export function returnBook(id: number) {
  return request.put<ApiResult<void>>(`/borrows/${id}/return`)
}

export function getBorrows(params: { page: number; pageSize: number; readerId?: number; bookId?: number; status?: BorrowStatus }) {
  return request.get<ApiResult<PageResult<BorrowRecordVO>>>('/borrows', { params }).then((res) => res.data.data)
}

export function getMyBorrows(params: { page: number; pageSize: number }) {
  return request.get<ApiResult<PageResult<BorrowRecordVO>>>('/borrows/my', { params }).then((res) => res.data.data)
}
