import request from '../utils/request'
import type { ApiResult, BookStatus, BookVO, PageResult } from '../types'

export function getBooks(params: { page: number; pageSize: number; keyword?: string }) {
  return request.get<ApiResult<PageResult<BookVO>>>('/books', { params }).then((res) => res.data.data)
}

export function createBook(data: Omit<BookVO, 'id' | 'status'>) {
  return request.post<ApiResult<BookVO>>('/books', data)
}

export function updateBook(id: number, data: Omit<BookVO, 'id'> & { status: BookStatus }) {
  return request.put<ApiResult<BookVO>>(`/books/${id}`, data)
}

export function deleteBook(id: number) {
  return request.delete<ApiResult<void>>(`/books/${id}`)
}
