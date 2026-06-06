import request from './request'
import type { Announcement, AnnouncementQuery, PageResult } from '@/types/api'

const asData = <T>(promise: Promise<unknown>) => promise as Promise<T>

export const announcementApi = {
  list(params: AnnouncementQuery = {}) {
    return asData<PageResult<Announcement>>(request.get('/announcements', { params }))
  },
  detail(id: number) {
    return asData<Announcement>(request.get(`/announcements/${id}`))
  },
  adminList(params: AnnouncementQuery = {}) {
    return asData<PageResult<Announcement>>(request.get('/admin/announcements', { params }))
  },
  create(payload: Announcement) {
    return asData<Announcement>(request.post('/admin/announcements', payload))
  },
  update(id: number, payload: Partial<Announcement>) {
    return asData<Announcement>(request.put(`/admin/announcements/${id}`, payload))
  },
  remove(id: number) {
    return asData<void>(request.delete(`/admin/announcements/${id}`))
  },
  status(id: number, status: number) {
    return asData<Announcement>(request.put(`/admin/announcements/${id}/status`, null, { params: { status } }))
  }
}
