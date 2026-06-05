<template>
  <main class="auth-page">
    <section class="auth-art">
      <RouterLink class="brand" to="/">
        <BrandMark />
        <span>
          <strong>InkRead</strong>
          <small>墨香书阁</small>
        </span>
      </RouterLink>
      <h1>登录后继续整理你的书阁。</h1>
      <p>把想读的书、未完的订单和熟悉的收货地址，都安静地放回你的书阁。</p>
    </section>

    <section class="auth-card">
      <el-tabs v-model="mode" stretch>
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" label-position="top" @submit.prevent>
            <el-form-item label="用户名"><el-input v-model="loginForm.username" /></el-form-item>
            <el-form-item label="密码"><el-input v-model="loginForm.password" type="password" show-password /></el-form-item>
            <el-button type="primary" size="large" :loading="loading" @click="submitLogin">登录</el-button>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" label-position="top" @submit.prevent>
            <el-form-item label="用户名"><el-input v-model="registerForm.username" /></el-form-item>
            <el-form-item label="昵称"><el-input v-model="registerForm.nickname" /></el-form-item>
            <el-form-item label="手机号"><el-input v-model="registerForm.phone" /></el-form-item>
            <el-form-item label="密码"><el-input v-model="registerForm.password" type="password" show-password /></el-form-item>
            <el-button type="primary" size="large" :loading="loading" @click="submitRegister">注册并登录</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </section>
  </main>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import BrandMark from '@/components/BrandMark.vue'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()
const mode = ref('login')
const loading = ref(false)

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', nickname: '', phone: '', password: '' })

const target = () => String(route.query.redirect || '/')

async function submitLogin() {
  loading.value = true
  try {
    await auth.login(loginForm)
    ElMessage.success('登录成功')
    router.push(target())
  } finally {
    loading.value = false
  }
}

async function submitRegister() {
  loading.value = true
  try {
    await auth.register(registerForm)
    ElMessage.success('注册成功')
    router.push(target())
  } finally {
    loading.value = false
  }
}
</script>
