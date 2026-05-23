import { defineStore } from 'pinia'
import type { Role } from '../types'

interface AuthState {
  token: string
  username: string
  role: Role | ''
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token') || '',
    username: localStorage.getItem('username') || '',
    role: (localStorage.getItem('role') as Role) || ''
  }),
  actions: {
    setLogin(token: string, username: string, role: Role) {
      this.token = token
      this.username = username
      this.role = role
      localStorage.setItem('token', token)
      localStorage.setItem('username', username)
      localStorage.setItem('role', role)
    },
    logoutLocal() {
      this.token = ''
      this.username = ''
      this.role = ''
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
    }
  }
})
