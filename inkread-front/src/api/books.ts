import request from './request'
import type { Book, BookQuery, PageResult } from '@/types/api'

const asData = <T>(promise: Promise<unknown>) => promise as Promise<T>
interface UploadCoverResult {
  url: string
}

export const bookApi = {
  list(params: BookQuery) {
    return asData<PageResult<Book>>(request.get('/books', { params }))
  },
  detail(id: number) {
    return asData<Book>(request.get(`/books/${id}`))
  },
  adminList(params: BookQuery) {
    return asData<PageResult<Book>>(request.get('/admin/books', { params }))
  },
  create(payload: Book) {
    return asData<Book>(request.post('/admin/books', payload))
  },
  update(id: number, payload: Partial<Book>) {
    return asData<Book>(request.put(`/admin/books/${id}`, payload))
  },
  remove(id: number) {
    return asData<void>(request.delete(`/admin/books/${id}`))
  },
  shelf(id: number, status: number) {
    return asData<void>(request.put(`/admin/books/${id}/shelf`, null, { params: { status } }))
  },
  uploadCover(file: File) {
    const form = new FormData()
    form.append('file', file)
    return asData<UploadCoverResult>(request.post('/admin/books/upload', form, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }))
  }
}
