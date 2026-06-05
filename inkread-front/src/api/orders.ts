import request from './request'
import type {
  CreateOrderPayload,
  CreateOrderResult,
  Order,
  OrderDetail,
  OrderQuery,
  PageResult,
  ShipOrderPayload
} from '@/types/api'

const asData = <T>(promise: Promise<unknown>) => promise as Promise<T>

function toOrderForm(payload: CreateOrderPayload) {
  const form = new URLSearchParams()
  payload.cartIds.forEach((id) => form.append('cartIds', String(id)))
  form.append('receiverName', payload.receiverName)
  form.append('receiverPhone', payload.receiverPhone)
  form.append('receiverAddress', payload.receiverAddress)
  return form
}

export const ordersApi = {
  create(payload: CreateOrderPayload) {
    return asData<CreateOrderResult>(
      request.post('/orders', toOrderForm(payload), {
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
      })
    )
  },
  list(params: OrderQuery) {
    return asData<PageResult<Order>>(request.get('/orders', { params }))
  },
  detail(id: number) {
    return asData<OrderDetail>(request.get(`/orders/${id}`))
  },
  cancel(id: number) {
    return asData<Order>(request.put(`/orders/${id}/cancel`))
  },
  confirm(id: number) {
    return asData<Order>(request.put(`/orders/${id}/confirm`))
  },
  adminList(params: OrderQuery) {
    return asData<PageResult<Order>>(request.get('/admin/orders', { params }))
  },
  ship(id: number, payload: ShipOrderPayload) {
    return asData<Order>(request.put(`/admin/orders/${id}/ship`, null, { params: payload }))
  }
}
