<template>
  <section v-loading="loading" class="admin-dashboard">
    <div class="metric-grid">
      <article v-for="metric in metrics" :key="metric.label" class="metric-card">
        <span>{{ metric.label }}</span>
        <strong>{{ metric.value }}</strong>
        <small>{{ metric.hint }}</small>
      </article>
    </div>

    <section class="admin-panel dashboard-orders">
      <div class="panel-title-row">
        <div>
          <p>Recent Orders</p>
          <h2>最近订单</h2>
        </div>
        <RouterLink class="text-link" to="/admin/orders">查看全部</RouterLink>
      </div>

      <el-table :data="dashboard?.recentOrders || []" class="ink-table">
        <el-table-column prop="orderNo" label="订单号" min-width="210" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column label="金额" width="130">
          <template #default="{ row }">¥{{ money(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }"><el-tag :type="statusTag(row.status)">{{ row.status }}</el-tag></template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
      </el-table>
    </section>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { adminApi } from '@/api/admin'
import type { DashboardData } from '@/types/api'

const loading = ref(false)
const dashboard = ref<DashboardData | null>(null)

const metrics = computed(() => [
  { label: '图书', value: dashboard.value?.bookCount ?? 0, hint: '当前书库规模' },
  { label: '分类', value: dashboard.value?.categoryCount ?? 0, hint: '可浏览分类' },
  { label: '待发货', value: dashboard.value?.pendingOrderCount ?? 0, hint: '需要处理的订单' },
  { label: '用户', value: dashboard.value?.userCount ?? 0, hint: '注册账户总数' }
])

const money = (value?: number) => Number(value || 0).toFixed(2)
const formatDate = (value?: string) => value?.replace('T', ' ').slice(0, 19) || '-'
const statusTag = (status: string) => {
  if (status === '待发货') return 'warning'
  if (status === '已发货') return 'primary'
  if (status === '已完成') return 'success'
  return 'info'
}

onMounted(async () => {
  loading.value = true
  try {
    dashboard.value = await adminApi.dashboard()
  } finally {
    loading.value = false
  }
})
</script>
