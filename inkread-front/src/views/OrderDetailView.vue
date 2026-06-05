<template>
  <section v-loading="loading" class="order-detail-page">
    <template v-if="detail">
      <RouterLink class="text-link" to="/orders">返回订单列表</RouterLink>
      <div class="order-detail-hero">
        <div>
          <p class="eyebrow">Order Detail</p>
          <h1>{{ detail.order.orderNo }}</h1>
          <el-tag :type="statusTag(detail.order.status)">{{ detail.order.status }}</el-tag>
        </div>
        <strong>¥{{ money(detail.order.totalAmount) }}</strong>
      </div>

      <div class="order-detail-grid">
        <section class="surface-panel">
          <h2>图书清单</h2>
          <article v-for="item in detail.items" :key="item.id" class="order-item-row">
            <BookCover :src="item.bookCover" :title="item.bookTitle" />
            <div>
              <RouterLink :to="`/books/${item.bookId}`">{{ item.bookTitle }}</RouterLink>
              <span>x {{ item.quantity }}</span>
            </div>
            <strong>¥{{ money(item.subtotal) }}</strong>
          </article>
        </section>

        <aside class="surface-panel order-address-card">
          <h2>收货与物流</h2>
          <dl>
            <div><dt>收货人</dt><dd>{{ detail.order.receiverName }}</dd></div>
            <div><dt>手机号</dt><dd>{{ detail.order.receiverPhone }}</dd></div>
            <div><dt>地址</dt><dd>{{ detail.order.receiverAddress }}</dd></div>
            <div><dt>物流公司</dt><dd>{{ detail.order.logisticsCompany || '暂未发货' }}</dd></div>
            <div><dt>物流单号</dt><dd>{{ detail.order.logisticsNo || '暂未生成' }}</dd></div>
          </dl>
          <el-button v-if="detail.order.status === '待发货'" type="danger" plain @click="cancelOrder">取消订单</el-button>
          <el-button v-if="detail.order.status === '已发货'" type="primary" @click="confirmOrder">确认收货</el-button>
        </aside>
      </div>
    </template>
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ordersApi } from '@/api/orders'
import BookCover from '@/components/BookCover.vue'
import type { OrderDetail } from '@/types/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref<OrderDetail | null>(null)

const money = (value?: number) => Number(value || 0).toFixed(2)
const statusTag = (status: string) => {
  if (status === '待发货') return 'warning'
  if (status === '已发货') return 'primary'
  if (status === '已完成') return 'success'
  return 'info'
}

async function loadDetail() {
  loading.value = true
  try {
    detail.value = await ordersApi.detail(Number(route.params.id))
  } finally {
    loading.value = false
  }
}

async function cancelOrder() {
  if (!detail.value) return
  await ElMessageBox.confirm(`确认取消订单 ${detail.value.order.orderNo}？`, '取消订单', { type: 'warning' })
  await ordersApi.cancel(detail.value.order.id)
  ElMessage.success('订单已取消')
  await loadDetail()
}

async function confirmOrder() {
  if (!detail.value) return
  await ordersApi.confirm(detail.value.order.id)
  ElMessage.success('已确认收货')
  await loadDetail()
}

onMounted(async () => {
  await loadDetail().catch(() => router.push('/orders'))
})
</script>
