<template>
  <section class="admin-panel">
    <div class="toolbar user-toolbar">
      <el-input v-model="query.keyword" placeholder="搜索用户名" clearable>
        <template #prefix><Search /></template>
      </el-input>
      <el-button :icon="Search" @click="reload">查询</el-button>
      <el-button :icon="Refresh" @click="loadUsers">刷新</el-button>
    </div>

    <el-table v-loading="loading" :data="users" class="ink-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="用户" min-width="220">
        <template #default="{ row }">
          <strong>{{ row.username }}</strong>
          <span class="table-subtext">{{ row.nickname || '未设置昵称' }} · {{ row.phone || '未设置手机' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="角色" width="130">
        <template #default="{ row }">
          <el-select :model-value="row.role" size="small" @change="(role: string) => updateRole(row, role)">
            <el-option label="普通用户" value="user" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" width="180">
        <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="130" fixed="right">
        <template #default="{ row }">
          <el-button text :type="row.status === 1 ? 'danger' : 'primary'" @click="toggleStatus(row)">
            {{ row.status === 1 ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <footer class="pager-row">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        background
        layout="prev, pager, next"
        :total="page.total"
        @current-change="loadUsers"
      />
    </footer>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { adminApi } from '@/api/admin'
import type { PageResult, User, UserQuery } from '@/types/api'

const loading = ref(false)
const users = ref<User[]>([])
const query = reactive<Required<Pick<UserQuery, 'page' | 'size'>> & { keyword?: string }>({ page: 1, size: 10 })
const page = reactive<PageResult<User>>({ total: 0, page: 1, size: 10, pages: 0, records: [] })

const formatDate = (value?: string) => value?.replace('T', ' ').slice(0, 19) || '-'

async function loadUsers() {
  loading.value = true
  try {
    const data = await adminApi.users(query)
    Object.assign(page, data)
    users.value = data.records || []
  } finally {
    loading.value = false
  }
}

function reload() {
  query.page = 1
  loadUsers()
}

async function toggleStatus(user: User) {
  if (!user.id) return
  const nextStatus = user.status === 1 ? 0 : 1
  await ElMessageBox.confirm(`确认${nextStatus === 1 ? '启用' : '禁用'}用户 ${user.username}？`, '用户状态', { type: 'warning' })
  await adminApi.updateUserStatus(user.id, nextStatus)
  ElMessage.success('用户状态已更新')
  await loadUsers()
}

async function updateRole(user: User, role: string) {
  if (!user.id || user.role === role) return
  await adminApi.updateUserRole(user.id, role)
  ElMessage.success('用户角色已更新')
  await loadUsers()
}

onMounted(loadUsers)
</script>
