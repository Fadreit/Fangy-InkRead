import request from './request'
import type { AddCartPayload, CartItem } from '@/types/api'

const asData = <T>(promise: Promise<unknown>) => promise as Promise<T>

export const cartApi = {
  add(payload: AddCartPayload) {
    return asData<CartItem>(request.post('/cart', null, { params: payload }))
  },
  list() {
    return asData<CartItem[]>(request.get('/cart'))
  },
  updateQuantity(id: number, quantity: number) {
    return asData<CartItem>(request.put(`/cart/${id}`, null, { params: { quantity } }))
  },
  remove(id: number) {
    return asData<void>(request.delete(`/cart/${id}`))
  },
  async removeMany(ids: number[]) {
    await Promise.all(ids.map((id) => cartApi.remove(id)))
  }
}
