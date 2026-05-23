export type Role = 'ADMIN' | 'LIBRARIAN' | 'STUDENT'
export type BookStatus = 'AVAILABLE' | 'DISABLED'
export type ReaderStatus = 'ACTIVE' | 'DISABLED'
export type BorrowStatus = 'BORROWED' | 'RETURNED' | 'OVERDUE'

export interface ApiResult<T> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  total: number
  records: T[]
}

export interface UserVO {
  id: number
  username: string
  role: Role
  enabled: boolean
}

export interface BookVO {
  id: number
  isbn?: string
  title: string
  author?: string
  publisher?: string
  category?: string
  totalCount: number
  availableCount: number
  status: BookStatus
}

export interface ReaderVO {
  id: number
  studentNo: string
  name: string
  college?: string
  phone?: string
  status: ReaderStatus
  userId?: number
}

export interface BorrowRecordVO {
  id: number
  readerId: number
  bookId: number
  readerName?: string
  bookTitle?: string
  borrowTime: string
  dueTime: string
  returnTime?: string
  status: BorrowStatus
}
