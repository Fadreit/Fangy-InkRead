export interface Result<T = unknown> {
  code: number
  message: string
  data: T
}

export interface PageResult<T> {
  total: number
  page: number
  size: number
  pages: number
  records: T[]
}

export interface User {
  id?: number
  username: string
  password?: string
  nickname?: string
  phone?: string
  role?: 'user' | 'admin' | string
  status?: number
  createdAt?: string
  updatedAt?: string
  isDeleted?: number
}

export interface LoginPayload {
  username: string
  password: string
}

export interface RegisterPayload extends LoginPayload {
  nickname?: string
  phone?: string
}

export interface LoginResponse {
  token: string
  user: User
}

export interface Category {
  id?: number
  name: string
  description?: string
  sortOrder?: number
  bookCount?: number
  status?: number
  isDeleted?: number
  createdAt?: string
  updatedAt?: string
}

export interface Announcement {
  id?: number
  title: string
  content: string
  status?: number
  createdAt?: string
  updatedAt?: string
  isDeleted?: number
}

export interface AnnouncementQuery {
  page?: number
  size?: number
  keyword?: string
  status?: number
}

export interface Book {
  id?: number
  title: string
  author: string
  isbn?: string
  categoryId?: number
  categoryName?: string
  price: number
  stock: number
  publisher?: string
  publishDate?: string
  description?: string
  coverUrl?: string
  status?: number
  isDeleted?: number
  createdAt?: string
  updatedAt?: string
}

export interface CartItem {
  id: number
  bookId: number
  bookTitle: string
  coverUrl?: string
  price: number
  stock?: number
  quantity: number
  subtotal: number
  selected?: string
  createdAt?: string
}

export interface AddCartPayload {
  bookId: number
  quantity: number
}

export type OrderStatus = '待发货' | '已发货' | '已完成' | '已取消' | string

export interface Order {
  id: number
  orderNo: string
  userId?: number
  totalAmount: number
  status: OrderStatus
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  logisticsCompany?: string
  logisticsNo?: string
  createdAt?: string
  updatedAt?: string
}

export interface OrderItem {
  id: number
  orderId: number
  bookId: number
  bookTitle: string
  bookCover?: string
  price: number
  quantity: number
  subtotal: number
}

export interface OrderDetail {
  order: Order
  username: string
  items: OrderItem[]
}

export interface CreateOrderPayload {
  cartIds: number[]
  receiverName: string
  receiverPhone: string
  receiverAddress: string
}

export interface CreateOrderResult {
  id: number
  orderNo: string
  totalAmount: number
  status: OrderStatus
  itemCount: number
  createdAt?: string
}

export interface OrderQuery {
  page?: number
  size?: number
  status?: OrderStatus
  keyword?: string
}

export interface ShipOrderPayload {
  logisticsCompany: string
  logisticsNo: string
}

export interface DashboardOrder {
  id: number
  orderNo: string
  username: string
  totalAmount: number
  status: OrderStatus
  createdAt?: string
}

export interface DashboardData {
  bookCount: number
  categoryCount: number
  pendingOrderCount: number
  userCount: number
  recentOrders: DashboardOrder[]
}

export interface UserQuery {
  page?: number
  size?: number
  keyword?: string
}

export interface BookQuery {
  page?: number
  size?: number
  keyword?: string
  categoryId?: number
  status?: number
  isDeleted?: number
  sort?: 'latest' | 'price_asc' | 'price_desc' | 'sales'
}

export interface PasswordPayload {
  oldPassword: string
  newPassword: string
  confirmPassword: string
}
