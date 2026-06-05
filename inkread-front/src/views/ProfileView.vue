<template>
  <section class="profile-page">
    <div class="section-heading">
      <p>Account</p>
      <h1>个人资料</h1>
    </div>

    <div class="profile-grid">
      <el-form class="surface-panel" :model="profileForm" label-position="top">
        <el-form-item label="用户名"><el-input v-model="profileForm.username" disabled /></el-form-item>
        <el-form-item label="昵称"><el-input v-model="profileForm.nickname" /></el-form-item>
        <el-form-item label="手机号"><el-input v-model="profileForm.phone" /></el-form-item>
        <el-button type="primary" :loading="savingProfile" @click="saveProfile">保存资料</el-button>
      </el-form>

      <el-form class="surface-panel" :model="passwordForm" label-position="top">
        <el-form-item label="原密码"><el-input v-model="passwordForm.oldPassword" type="password" show-password /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="passwordForm.newPassword" type="password" show-password /></el-form-item>
        <el-form-item label="确认新密码"><el-input v-model="passwordForm.confirmPassword" type="password" show-password /></el-form-item>
        <el-button type="primary" plain :loading="savingPassword" @click="savePassword">修改密码</el-button>
      </el-form>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const savingProfile = ref(false)
const savingPassword = ref(false)
const profileForm = reactive({ username: '', nickname: '', phone: '' })
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

onMounted(async () => {
  await auth.fetchProfile()
  Object.assign(profileForm, auth.user)
})

async function saveProfile() {
  savingProfile.value = true
  try {
    auth.user = await authApi.updateProfile(profileForm)
    auth.persist()
    ElMessage.success('资料已更新')
  } finally {
    savingProfile.value = false
  }
}

async function savePassword() {
  savingPassword.value = true
  try {
    await authApi.updatePassword(passwordForm)
    ElMessage.success('密码已修改，请重新登录')
    auth.logout()
  } finally {
    savingPassword.value = false
  }
}
</script>
