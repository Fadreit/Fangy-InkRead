<template>
  <section class="cart-page">
    <div class="section-heading cart-heading">
      <div>
        <p>Cart</p>
        <h1>购物车</h1>
      </div>
      <el-button :icon="Refresh" @click="loadCart">刷新</el-button>
    </div>

    <div v-loading="loading" class="cart-layout">
      <section class="cart-list surface-panel">
        <div v-if="cartItems.length" class="cart-list-head">
          <el-checkbox v-model="allSelected" :indeterminate="isIndeterminate" @change="toggleAll">
            全选 {{ selectedIds.length }}/{{ cartItems.length }}
          </el-checkbox>
          <el-button text type="danger" :disabled="!selectedIds.length" @click="removeSelected">删除所选</el-button>
        </div>

        <article v-for="item in cartItems" :key="item.id" class="cart-row">
          <el-checkbox v-model="selectedIds" :value="item.id" />
          <RouterLink class="cart-book-cover" :to="`/books/${item.bookId}`">
            <BookCover :src="item.coverUrl" :title="item.bookTitle" />
          </RouterLink>
          <div class="cart-row-main">
            <RouterLink :to="`/books/${item.bookId}`" class="cart-title">{{ item.bookTitle }}</RouterLink>
            <span class="cart-muted">{{ stockText(item) }}</span>
            <strong>¥{{ money(item.price) }}</strong>
          </div>
          <el-input-number
            :model-value="item.quantity"
            :min="1"
            :max="quantityMax(item)"
            controls-position="right"
            @change="(value: number | undefined) => updateQuantity(item, Number(value || 1))"
          />
          <div class="cart-row-total">¥{{ money(rowSubtotal(item)) }}</div>
          <el-button text type="danger" @click="removeItem(item)">移除</el-button>
        </article>

        <EmptyState
          v-if="!loading && cartItems.length === 0"
          glyph="Cart"
          title="购物车还是空的"
          description="从书架挑一本加入购物车，这里会显示数量、库存和小计。"
        >
          <RouterLink class="text-link" to="/">去逛书架</RouterLink>
        </EmptyState>
      </section>

      <aside class="cart-summary">
        <div class="summary-card">
          <span>已选图书</span>
          <strong>{{ selectedItems.length }}</strong>
          <small>合计 {{ selectedQuantity }} 件</small>
          <div class="summary-total">
            <span>应付金额</span>
            <b>¥{{ money(selectedTotal) }}</b>
          </div>
          <el-button type="primary" size="large" :disabled="!selectedItems.length" @click="checkoutPending">
            去结算
          </el-button>
        </div>
      </aside>
    </div>

    <el-drawer v-model="checkoutVisible" title="确认收货信息" size="420px">
      <div class="checkout-drawer">
        <div class="checkout-lines">
          <div v-for="item in selectedItems" :key="item.id">
            <span>{{ item.bookTitle }} x {{ item.quantity }}</span>
            <strong>¥{{ money(rowSubtotal(item)) }}</strong>
          </div>
        </div>

        <el-form :model="checkoutForm" label-position="top">
          <el-form-item label="收货人"><el-input v-model="checkoutForm.receiverName" /></el-form-item>
          <el-form-item label="手机号"><el-input v-model="checkoutForm.receiverPhone" /></el-form-item>
          <el-form-item label="收货地址"><el-input v-model="checkoutForm.receiverAddress" type="textarea" :rows="4" /></el-form-item>
        </el-form>

        <div class="drawer-total">
          <span>应付金额</span>
          <strong>¥{{ money(selectedTotal) }}</strong>
        </div>

        <el-button type="primary" size="large" :loading="creatingOrder" @click="createOrder">提交订单</el-button>
      </div>
    </el-drawer>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { bookApi } from '@/api/books'
import { cartApi } from '@/api/cart'
import { ordersApi } from '@/api/orders'
import BookCover from '@/components/BookCover.vue'
import EmptyState from '@/components/EmptyState.vue'
import type { CartItem } from '@/types/api'

const router = useRouter()
const loading = ref(false)
const creatingOrder = ref(false)
const checkoutVisible = ref(false)
const updatingIds = ref<number[]>([])
const cartItems = ref<CartItem[]>([])
const selectedIds = ref<number[]>([])
const checkoutForm = reactive({
  receiverName: '',
  receiverPhone: '',
  receiverAddress: ''
})

const selectedItems = computed(() => cartItems.value.filter((item) => selectedIds.value.includes(item.id)))
const selectedQuantity = computed(() => selectedItems.value.reduce((sum, item) => sum + item.quantity, 0))
const selectedTotal = computed(() => selectedItems.value.reduce((sum, item) => sum + rowSubtotal(item), 0))
const allSelected = computed({
  get: () => cartItems.value.length > 0 && selectedIds.value.length === cartItems.value.length,
  set: (checked: boolean) => toggleAll(checked)
})
const isIndeterminate = computed(() => selectedIds.value.length > 0 && selectedIds.value.length < cartItems.value.length)

const money = (value?: number) => Number(value || 0).toFixed(2)
const rowSubtotal = (item: CartItem) => Number(item.subtotal ?? Number(item.price || 0) * Number(item.quantity || 0))
const hasStock = (item: CartItem) => typeof item.stock === 'number' && Number.isFinite(item.stock)
const stockText = (item: CartItem) => (hasStock(item) ? `库存 ${item.stock}` : '库存以书籍详情为准')
const quantityMax = (item: CartItem) => (hasStock(item) ? Math.max(Number(item.stock), 1) : 999)

async function enrichCartItems(items: CartItem[]) {
  const missingStockItems = items.filter((item) => !hasStock(item) && item.bookId)
  if (!missingStockItems.length) return items

  const enriched = await Promise.all(
    missingStockItems.map((item) => bookApi.detail(item.bookId).then((book) => ({ id: item.id, stock: book.stock })).catch(() => null))
  )
  const stockMap = new Map(enriched.filter(Boolean).map((item) => [item!.id, item!.stock]))
  return items.map((item) => {
    const stock = stockMap.get(item.id)
    return typeof stock === 'number' ? { ...item, stock } : item
  })
}

async function loadCart() {
  loading.value = true
  try {
    const items = await cartApi.list()
    cartItems.value = await enrichCartItems(items)
    selectedIds.value = selectedIds.value.filter((id) => cartItems.value.some((item) => item.id === id))
  } finally {
    loading.value = false
  }
}

function toggleAll(checked: boolean | string | number) {
  selectedIds.value = checked ? cartItems.value.map((item) => item.id) : []
}

async function updateQuantity(item: CartItem, quantity: number) {
  if (quantity === item.quantity || updatingIds.value.includes(item.id)) return
  updatingIds.value.push(item.id)
  try {
    await cartApi.updateQuantity(item.id, quantity)
    item.quantity = quantity
    item.subtotal = Number(item.price || 0) * quantity
    ElMessage.success('数量已更新')
  } finally {
    updatingIds.value = updatingIds.value.filter((id) => id !== item.id)
  }
}

async function removeItem(item: CartItem) {
  await ElMessageBox.confirm(`确认移除《${item.bookTitle}》？`, '移除购物车项', { type: 'warning' })
  await cartApi.remove(item.id)
  ElMessage.success('已移除')
  await loadCart()
}

async function removeSelected() {
  if (!selectedIds.value.length) return
  await ElMessageBox.confirm(`确认移除已选的 ${selectedIds.value.length} 项？`, '批量移除', { type: 'warning' })
  await cartApi.removeMany(selectedIds.value)
  ElMessage.success('已移除所选图书')
  selectedIds.value = []
  await loadCart()
}

function checkoutPending() {
  if (!selectedItems.value.length) return
  checkoutVisible.value = true
}

async function createOrder() {
  if (!checkoutForm.receiverName || !checkoutForm.receiverPhone || !checkoutForm.receiverAddress) {
    ElMessage.warning('请填写完整收货信息')
    return
  }

  creatingOrder.value = true
  try {
    const order = await ordersApi.create({
      cartIds: selectedIds.value,
      ...checkoutForm
    })
    ElMessage.success('订单已提交')
    checkoutVisible.value = false
    selectedIds.value = []
    await loadCart()
    router.push(`/orders/${order.id}`)
  } finally {
    creatingOrder.value = false
  }
}

onMounted(loadCart)
</script>
