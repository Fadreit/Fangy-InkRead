import { defineStore } from 'pinia'
import { authApi } from '@/api/auth'
import type { LoginPayload, RegisterPayload, User } from '@/types/api'

interface AuthState {
  token: string
  user: User | null
}

export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('inkread_token') || '',
    user: JSON.parse(localStorage.getItem('inkread_user') || 'null') as User | null
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token),
    isAdmin: (state) => state.user?.role === 'admin'
  },
  actions: {
    persist() {
      localStorage.setItem('inkread_token', this.token)
      localStorage.setItem('inkread_user', JSON.stringify(this.user))
    },
    async login(payload: LoginPayload) {
      const data = await authApi.login(payload)
      this.token = data.token
      this.user = data.user
      this.persist()
    },
    async register(payload: RegisterPayload) {
      await authApi.register(payload)
      await this.login({ username: payload.username, password: payload.password })
    },
    async fetchProfile() {
      if (!this.token) return
      this.user = await authApi.profile()
      this.persist()
    },
    logout(redirect = true) {
      this.token = ''
      this.user = null
      localStorage.removeItem('inkread_token')
      localStorage.removeItem('inkread_user')
      if (redirect) {
        window.location.href = '/login'
      }
    }
  }
})
