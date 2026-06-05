import request from './request'
import type { DashboardData, PageResult, User, UserQuery } from '@/types/api'

const asData = <T>(promise: Promise<unknown>) => promise as Promise<T>

export const adminApi = {
  dashboard() {
    return asData<DashboardData>(request.get('/admin/dashboard'))
  },
  users(params: UserQuery) {
    return asData<PageResult<User>>(request.get('/admin/users', { params }))
  },
  user(id: number) {
    return asData<User>(request.get(`/admin/uses/${id}`))
  },
  updateUserStatus(id: number, status: number) {
    return asData<void>(request.put(`/admin/users/${id}/status`, null, { params: { status } }))
  },
  updateUserRole(id: number, role: string) {
    return asData<void>(request.put(`/admin/users/${id}/role`, null, { params: { role } }))
  }
}
