<template>
  <section v-loading="loading" class="detail-page">
    <template v-if="book">
      <BookCover :src="book.coverUrl" :title="book.title" />
      <article class="detail-copy">
        <RouterLink class="text-link" to="/">返回书架</RouterLink>
        <p class="eyebrow">{{ categoryName }}</p>
        <h1>{{ book.title }}</h1>
        <dl class="detail-facts">
          <div><dt>作者</dt><dd>{{ book.author || '佚名' }}</dd></div>
          <div><dt>出版社</dt><dd>{{ book.publisher || '未填写' }}</dd></div>
          <div><dt>出版日期</dt><dd>{{ book.publishDate || '未填写' }}</dd></div>
          <div><dt>ISBN</dt><dd>{{ book.isbn || '未填写' }}</dd></div>
        </dl>
        <p class="detail-description">{{ book.description || '这本书还没有简介。' }}</p>
        <div class="detail-purchase">
          <strong>¥{{ money(book.price) }}</strong>
          <span>库存 {{ book.stock ?? 0 }}</span>
          <el-input-number v-model="quantity" :min="1" :max="book.stock || 1" controls-position="right" />
          <el-button
            type="primary"
            size="large"
            :icon="ShoppingCart"
            :loading="adding"
            :disabled="!book.stock"
            @click="addToCart"
          >
            加入购物车
          </el-button>
        </div>
      </article>
    </template>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'
import { bookApi } from '@/api/books'
import { cartApi } from '@/api/cart'
import { categoryApi } from '@/api/categories'
import BookCover from '@/components/BookCover.vue'
import { useAuthStore } from '@/stores/auth'
import type { Book, Category } from '@/types/api'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const adding = ref(false)
const quantity = ref(1)
const book = ref<Book | null>(null)
const categories = ref<Category[]>([])

const categoryName = computed(() => categories.value.find((item) => item.id === book.value?.categoryId)?.name || '图书详情')
const money = (value?: number) => Number(value || 0).toFixed(2)

async function addToCart() {
  if (!book.value?.id) return
  if (!auth.isLoggedIn) {
    router.push({ name: 'login', query: { redirect: route.fullPath } })
    return
  }

  adding.value = true
  try {
    await cartApi.add({ bookId: book.value.id, quantity: quantity.value })
    ElMessage.success('已加入购物车')
    router.push('/cart')
  } finally {
    adding.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    const [bookData, categoryData] = await Promise.all([
      bookApi.detail(id),
      categoryApi.list().catch(() => [])
    ])
    book.value = bookData
    categories.value = categoryData
  } finally {
    loading.value = false
  }
})
</script>
