<template>
  <section class="orders-page">
    <div class="section-heading cart-heading">
      <div>
        <p>Orders</p>
        <h1>我的订单</h1>
      </div>
      <el-segmented v-model="query.status" :options="statusOptions" @change="reload" />
    </div>

    <section v-loading="loading" class="order-stack">
      <article v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-card-head">
          <div>
            <span>订单号</span>
            <strong>{{ order.orderNo }}</strong>
          </div>
          <el-tag :type="statusTag(order.status)">{{ order.status }}</el-tag>
        </div>
        <div class="order-card-body">
          <div>
            <small>收货人</small>
            <span>{{ order.receiverName }} · {{ order.receiverPhone }}</span>
          </div>
          <div>
            <small>金额</small>
            <span class="price-text">¥{{ money(order.totalAmount) }}</span>
          </div>
          <div>
            <small>创建时间</small>
            <span>{{ formatDate(order.createdAt) }}</span>
          </div>
        </div>
        <div class="order-card-actions">
          <RouterLink class="text-link" :to="`/orders/${order.id}`">查看详情</RouterLink>
          <el-button v-if="order.status === '待发货'" text type="danger" @click="cancelOrder(order)">取消订单</el-button>
          <el-button v-if="order.status === '已发货'" text type="primary" @click="confirmOrder(order)">确认收货</el-button>
        </div>
      </article>

      <EmptyState
        v-if="!loading && orders.length === 0"
        glyph="Order"
        title="还没有订单"
        description="从购物车提交一次订单，这里会出现物流和状态流转。"
      >
        <RouterLink class="text-link" to="/">去逛书架</RouterLink>
      </EmptyState>
    </section>

    <footer v-if="page.total > query.size" class="pager-row">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        background
        layout="prev, pager, next"
        :total="page.total"
        @current-change="loadOrders"
      />
    </footer>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ordersApi } from '@/api/orders'
import EmptyState from '@/components/EmptyState.vue'
import type { Order, OrderQuery, PageResult } from '@/types/api'

const loading = ref(false)
const orders = ref<Order[]>([])
const query = reactive<Required<Pick<OrderQuery, 'page' | 'size'>> & { status: string }>({ page: 1, size: 8, status: '' })
const page = reactive<PageResult<Order>>({ total: 0, page: 1, size: 8, pages: 0, records: [] })
const statusOptions = [
  { label: '全部', value: '' },
  { label: '待发货', value: '待发货' },
  { label: '已发货', value: '已发货' },
  { label: '已完成', value: '已完成' },
  { label: '已取消', value: '已取消' }
]

const money = (value?: number) => Number(value || 0).toFixed(2)
const formatDate = (value?: string) => value?.replace('T', ' ').slice(0, 19) || '-'
const statusTag = (status: string) => {
  if (status === '待发货') return 'warning'
  if (status === '已发货') return 'primary'
  if (status === '已完成') return 'success'
  return 'info'
}

async function loadOrders() {
  loading.value = true
  try {
    const data = await ordersApi.list({ ...query, status: query.status || undefined })
    Object.assign(page, data)
    orders.value = data.records || []
  } finally {
    loading.value = false
  }
}

function reload() {
  query.page = 1
  loadOrders()
}

async function cancelOrder(order: Order) {
  await ElMessageBox.confirm(`确认取消订单 ${order.orderNo}？`, '取消订单', { type: 'warning' })
  await ordersApi.cancel(order.id)
  ElMessage.success('订单已取消')
  await loadOrders()
}

async function confirmOrder(order: Order) {
  await ordersApi.confirm(order.id)
  ElMessage.success('已确认收货')
  await loadOrders()
}

onMounted(loadOrders)
</script>
