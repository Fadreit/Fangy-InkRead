<template>
  <section class="admin-panel">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="订单号 / 收货人 / 手机号" clearable>
        <template #prefix><Search /></template>
      </el-input>
      <el-select v-model="query.status" placeholder="订单状态" clearable>
        <el-option v-for="status in statuses" :key="status" :label="status" :value="status" />
      </el-select>
      <el-button :icon="Search" @click="reload">查询</el-button>
      <el-button :icon="Refresh" @click="loadOrders">刷新</el-button>
    </div>

    <el-table v-loading="loading" :data="orders" class="ink-table">
      <el-table-column prop="orderNo" label="订单号" min-width="210" />
      <el-table-column label="收货信息" min-width="220">
        <template #default="{ row }">
          <strong>{{ row.receiverName }}</strong>
          <span class="table-subtext">{{ row.receiverPhone }}</span>
        </template>
      </el-table-column>
      <el-table-column label="金额" width="120">
        <template #default="{ row }">¥{{ money(row.totalAmount) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }"><el-tag :type="statusTag(row.status)">{{ row.status }}</el-tag></template>
      </el-table-column>
      <el-table-column label="物流" min-width="180">
        <template #default="{ row }">
          <span>{{ row.logisticsCompany || '未发货' }}</span>
          <span class="table-subtext">{{ row.logisticsNo || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="170" fixed="right">
        <template #default="{ row }">
          <el-button v-if="row.status === '待发货'" text type="primary" @click="openShip(row)">发货</el-button>
          <el-button text @click="openDetail(row)">详情</el-button>
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
        @current-change="loadOrders"
      />
    </footer>

    <el-dialog v-model="shipVisible" title="订单发货" width="460px">
      <el-form :model="shipForm" label-position="top">
        <el-form-item label="物流公司"><el-input v-model="shipForm.logisticsCompany" /></el-form-item>
        <el-form-item label="物流单号"><el-input v-model="shipForm.logisticsNo" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipVisible = false">取消</el-button>
        <el-button type="primary" :loading="shipping" @click="shipOrder">确认发货</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" title="订单详情" size="520px">
      <div v-if="detailOrder" class="admin-order-detail">
        <h2>{{ detailOrder.orderNo }}</h2>
        <el-tag :type="statusTag(detailOrder.status)">{{ detailOrder.status }}</el-tag>
        <dl>
          <div><dt>收货人</dt><dd>{{ detailOrder.receiverName }}</dd></div>
          <div><dt>手机号</dt><dd>{{ detailOrder.receiverPhone }}</dd></div>
          <div><dt>地址</dt><dd>{{ detailOrder.receiverAddress }}</dd></div>
          <div><dt>金额</dt><dd>¥{{ money(detailOrder.totalAmount) }}</dd></div>
          <div><dt>物流</dt><dd>{{ detailOrder.logisticsCompany || '未发货' }} {{ detailOrder.logisticsNo || '' }}</dd></div>
        </dl>
      </div>
    </el-drawer>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { ordersApi } from '@/api/orders'
import type { Order, OrderQuery, PageResult } from '@/types/api'

const statuses = ['待发货', '已发货', '已完成', '已取消']
const loading = ref(false)
const shipping = ref(false)
const shipVisible = ref(false)
const detailVisible = ref(false)
const orders = ref<Order[]>([])
const currentOrder = ref<Order | null>(null)
const detailOrder = ref<Order | null>(null)
const query = reactive<Required<Pick<OrderQuery, 'page' | 'size'>> & { status?: string; keyword?: string }>({ page: 1, size: 10 })
const page = reactive<PageResult<Order>>({ total: 0, page: 1, size: 10, pages: 0, records: [] })
const shipForm = reactive({ logisticsCompany: '', logisticsNo: '' })

const money = (value?: number) => Number(value || 0).toFixed(2)
const statusTag = (status: string) => {
  if (status === '待发货') return 'warning'
  if (status === '已发货') return 'primary'
  if (status === '已完成') return 'success'
  return 'info'
}

async function loadOrders() {
  loading.value = true
  try {
    const data = await ordersApi.adminList(query)
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

function openShip(order: Order) {
  currentOrder.value = order
  shipForm.logisticsCompany = order.logisticsCompany || ''
  shipForm.logisticsNo = order.logisticsNo || ''
  shipVisible.value = true
}

async function shipOrder() {
  if (!currentOrder.value) return
  if (!shipForm.logisticsCompany || !shipForm.logisticsNo) {
    ElMessage.warning('请填写物流信息')
    return
  }
  shipping.value = true
  try {
    await ordersApi.ship(currentOrder.value.id, shipForm)
    ElMessage.success('订单已发货')
    shipVisible.value = false
    await loadOrders()
  } finally {
    shipping.value = false
  }
}

function openDetail(order: Order) {
  detailOrder.value = order
  detailVisible.value = true
}

onMounted(loadOrders)
</script>
