import request from './request'
import type { MessageItem, MessageLikeResult, MessagePayload, MessageQuery, PageResult } from '@/types/api'

const asData = <T>(promise: Promise<unknown>) => promise as Promise<T>

export const messageApi = {
  create(payload: MessagePayload) {
    return asData<MessageItem>(request.post('/messages', payload))
  },
  wall(sort: 'latest' | 'hot' = 'latest') {
    return asData<MessageItem[]>(request.get('/messages/wall', { params: { sort } }))
  },
  list(params: MessageQuery = {}) {
    return asData<PageResult<MessageItem>>(request.get('/messages', { params }))
  },
  detail(id: number) {
    return asData<MessageItem>(request.get(`/messages/${id}`))
  },
  like(id: number) {
    return asData<MessageLikeResult>(request.post(`/messages/${id}/like`))
  },
  unlike(id: number) {
    return asData<MessageLikeResult>(request.delete(`/messages/${id}/like`))
  },
  remove(id: number) {
    return asData<void>(request.delete(`/messages/${id}`, { params: { id } }))
  },
  adminList(params: MessageQuery = {}) {
    return asData<PageResult<MessageItem>>(request.get('/admin/messages', { params }))
  }
}
