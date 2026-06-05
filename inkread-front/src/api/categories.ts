import request from './request'
import type { Category } from '@/types/api'

const asData = <T>(promise: Promise<unknown>) => promise as Promise<T>

export const categoryApi = {
  list() {
    return asData<Category[]>(request.get('/categories'))
  },
  adminList() {
    return asData<Category[]>(request.get('/admin/categories'))
  },
  create(payload: Category) {
    return asData<Category>(request.post('/admin/categories', payload))
  },
  update(id: number, payload: Partial<Category>) {
    return asData<Category>(request.put(`/admin/categories/${id}`, payload))
  },
  remove(id: number) {
    return asData<void>(request.delete(`/admin/categories/${id}`))
  },
  status(id: number, status: number) {
    return asData<void>(request.put(`/admin/categories/${id}/status`, null, { params: { status } }))
  }
}
