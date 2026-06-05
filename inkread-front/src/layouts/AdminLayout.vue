<template>
  <div class="admin-shell">
    <aside class="admin-sidebar">
      <RouterLink class="admin-brand" to="/">
        <BrandMark />
        <span>
          <strong>InkRead</strong>
          <small>Admin Console</small>
        </span>
      </RouterLink>

      <nav class="admin-menu">
        <RouterLink to="/admin"><DataAnalysis /> 仪表盘</RouterLink>
        <RouterLink to="/admin/books"><Collection /> 图书管理</RouterLink>
        <RouterLink to="/admin/categories"><Menu /> 分类管理</RouterLink>
        <RouterLink to="/admin/orders"><Tickets /> 订单管理</RouterLink>
        <RouterLink to="/admin/users"><User /> 用户管理</RouterLink>
      </nav>
    </aside>

    <section class="admin-main">
      <header class="admin-header">
        <div>
          <p>运营后台</p>
          <h1>{{ routeTitle }}</h1>
        </div>
        <div class="admin-header-actions">
          <RouterLink class="text-link" to="/">回到书阁</RouterLink>
          <button class="avatar-button" @click="auth.logout()">{{ avatarText }}</button>
        </div>
      </header>
      <RouterView />
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Collection, DataAnalysis, Menu, Tickets, User } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'
import BrandMark from '@/components/BrandMark.vue'

const route = useRoute()
const auth = useAuthStore()

const titles: Record<string, string> = {
  'admin-dashboard': '仪表盘',
  'admin-books': '图书管理',
  'admin-categories': '分类管理',
  'admin-orders': '订单管理',
  'admin-users': '用户管理'
}

const routeTitle = computed(() => titles[String(route.name)] || '控制台')
const avatarText = computed(() => (auth.user?.nickname || auth.user?.username || '管').slice(0, 1))
</script>
