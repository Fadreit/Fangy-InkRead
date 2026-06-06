import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/layouts/StoreLayout.vue'),
      children: [
        { path: '', name: 'home', component: () => import('@/views/HomeView.vue') },
        { path: 'books/:id', name: 'book-detail', component: () => import('@/views/BookDetailView.vue') },
        { path: 'announcements', name: 'announcements', component: () => import('@/views/AnnouncementsView.vue') },
        { path: 'announcements/:id', name: 'announcement-detail', component: () => import('@/views/AnnouncementDetailView.vue') },
        { path: 'messages', name: 'messages', component: () => import('@/views/MessagesView.vue') },
        { path: 'messages/:id', name: 'message-detail', component: () => import('@/views/MessageDetailView.vue') },
        { path: 'cart', name: 'cart', component: () => import('@/views/CartView.vue'), meta: { requiresAuth: true } },
        { path: 'orders', name: 'orders', component: () => import('@/views/OrdersView.vue'), meta: { requiresAuth: true } },
        { path: 'orders/:id', name: 'order-detail', component: () => import('@/views/OrderDetailView.vue'), meta: { requiresAuth: true } },
        { path: 'profile', name: 'profile', component: () => import('@/views/ProfileView.vue'), meta: { requiresAuth: true } }
      ]
    },
    { path: '/login', name: 'login', component: () => import('@/views/LoginView.vue') },
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
      children: [
        { path: '', name: 'admin-dashboard', component: () => import('@/views/admin/AdminDashboardView.vue') },
        { path: 'books', name: 'admin-books', component: () => import('@/views/admin/AdminBooksView.vue') },
        { path: 'categories', name: 'admin-categories', component: () => import('@/views/admin/AdminCategoriesView.vue') },
        { path: 'announcements', name: 'admin-announcements', component: () => import('@/views/admin/AdminAnnouncementsView.vue') },
        { path: 'messages', name: 'admin-messages', component: () => import('@/views/admin/AdminMessagesView.vue') },
        { path: 'orders', name: 'admin-orders', component: () => import('@/views/admin/AdminOrdersView.vue') },
        { path: 'users', name: 'admin-users', component: () => import('@/views/admin/AdminUsersView.vue') }
      ]
    }
  ],
  scrollBehavior: () => ({ top: 0 })
})

router.beforeEach(async (to) => {
  const auth = useAuthStore()
  if (auth.token && !auth.user) {
    await auth.fetchProfile().catch(() => auth.logout(false))
  }

  if (to.meta.requiresAuth && !auth.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (to.meta.requiresAdmin && !auth.isAdmin) {
    return { name: 'home' }
  }

  return true
})

export default router
