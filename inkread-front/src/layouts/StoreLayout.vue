<template>
  <div class="store-shell">
    <header class="topbar">
      <RouterLink class="brand" to="/">
        <BrandMark />
        <span>
          <strong>InkRead</strong>
          <small>墨香书阁</small>
        </span>
      </RouterLink>

      <nav class="store-nav">
        <RouterLink to="/">书架</RouterLink>
        <RouterLink to="/announcements">公告</RouterLink>
        <RouterLink to="/cart">购物车</RouterLink>
        <RouterLink to="/orders">订单</RouterLink>
        <RouterLink v-if="auth.isAdmin" to="/admin/books">后台</RouterLink>
      </nav>

      <div class="topbar-actions">
        <RouterLink v-if="!auth.isLoggedIn" class="text-link" to="/login">登录</RouterLink>
        <el-dropdown v-else trigger="click">
          <button class="avatar-button">
            {{ avatarText }}
          </button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/profile')">个人资料</el-dropdown-item>
              <el-dropdown-item v-if="auth.isAdmin" @click="$router.push('/admin/books')">管理后台</el-dropdown-item>
              <el-dropdown-item divided @click="auth.logout()">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <main>
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import BrandMark from '@/components/BrandMark.vue'

const auth = useAuthStore()
const avatarText = computed(() => (auth.user?.nickname || auth.user?.username || '读').slice(0, 1))
</script>
