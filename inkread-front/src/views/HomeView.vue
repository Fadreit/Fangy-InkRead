<template>
  <section class="home-page">
    <div class="hero-band">
      <div class="hero-copy">
        <p class="eyebrow">Online Bookstore</p>
        <h1>在墨香书阁，遇见下一本想带回家的书。</h1>
        <p class="hero-subtitle">在墨香里挑一本想读的书，加入购物车、提交订单，让下一段阅读准时抵达。</p>
      </div>
      <div class="hero-panel">
        <span>今日书阁</span>
        <strong>{{ page.total }}</strong>
        <small>本在售图书</small>
      </div>
    </div>

    <RouterLink v-if="latestAnnouncement" class="home-announcement-strip" :to="`/announcements/${latestAnnouncement.id}`">
      <span>最新公告</span>
      <strong>{{ latestAnnouncement.title }}</strong>
      <small>{{ formatDate(latestAnnouncement.createdAt) }}</small>
    </RouterLink>

    <section class="filter-rail">
      <el-input v-model="query.keyword" size="large" placeholder="搜索书名或作者" clearable @keyup.enter="loadBooks">
        <template #prefix><Search /></template>
      </el-input>
      <el-select v-model="query.categoryId" size="large" placeholder="全部分类" clearable>
        <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
      </el-select>
      <el-segmented v-model="query.sort" :options="sortOptions" />
      <el-button size="large" type="primary" :icon="Search" @click="loadBooks">筛选</el-button>
    </section>

    <div v-loading="loading" class="book-grid">
      <RouterLink v-for="book in books" :key="book.id" class="book-card" :to="`/books/${book.id}`">
        <BookCover :src="book.coverUrl" :title="book.title" />
        <div class="book-card-body">
          <span class="book-author">{{ book.author || '佚名' }}</span>
          <h2>{{ book.title }}</h2>
          <p>{{ book.description || book.publisher || '这本书还没有简介，等一次翻开。' }}</p>
          <div class="book-meta">
            <strong>¥{{ money(book.price) }}</strong>
            <span>库存 {{ book.stock ?? 0 }}</span>
          </div>
        </div>
      </RouterLink>
    </div>

    <EmptyState
      v-if="!loading && books.length === 0"
      title="暂时没有匹配的图书"
      description="换个关键词或分类试试，也许下一本正藏在别的书架上。"
    />

    <footer v-if="page.total > (query.size || 12)" class="pager-row">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        background
        layout="prev, pager, next"
        :total="page.total"
        @current-change="loadBooks"
      />
    </footer>

    <el-dialog
      v-model="announcementDialogVisible"
      class="announcement-dialog"
      width="560px"
      :show-close="false"
      align-center
      @closed="markAnnouncementSeen"
    >
      <template #header>
        <div class="announcement-dialog-head">
          <span>书阁公告</span>
          <strong>{{ latestAnnouncement?.title }}</strong>
          <small>{{ formatDate(latestAnnouncement?.createdAt) }}</small>
        </div>
      </template>
      <p class="announcement-dialog-content">{{ latestAnnouncement?.content }}</p>
      <template #footer>
        <el-button @click="announcementDialogVisible = false">知道了</el-button>
        <el-button type="primary" @click="goAnnouncementDetail">查看公告</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { announcementApi } from '@/api/announcements'
import { bookApi } from '@/api/books'
import { categoryApi } from '@/api/categories'
import BookCover from '@/components/BookCover.vue'
import EmptyState from '@/components/EmptyState.vue'
import type { Announcement, Book, BookQuery, Category, PageResult } from '@/types/api'

const router = useRouter()
const loading = ref(false)
const books = ref<Book[]>([])
const categories = ref<Category[]>([])
const latestAnnouncement = ref<Announcement | null>(null)
const announcementDialogVisible = ref(false)
const page = reactive<PageResult<Book>>({ total: 0, page: 1, size: 12, pages: 0, records: [] })
const query = reactive<BookQuery>({ page: 1, size: 12, sort: 'latest' })

const sortOptions = [
  { label: '最新', value: 'latest' },
  { label: '价格低', value: 'price_asc' },
  { label: '价格高', value: 'price_desc' }
]

const money = (value?: number) => Number(value || 0).toFixed(2)
const formatDate = (value?: string) => value?.replace('T', ' ').slice(0, 16) || ''

async function loadBooks() {
  loading.value = true
  try {
    const data = await bookApi.list(query)
    Object.assign(page, data)
    books.value = data.records || []
  } finally {
    loading.value = false
  }
}

async function loadLatestAnnouncement() {
  const data = await announcementApi.list({ page: 1, size: 1 }).catch(() => null)
  const item = data?.records?.[0]
  if (!item?.id) return
  latestAnnouncement.value = item
  const seenKey = `inkread_seen_announcement_${item.id}`
  if (!sessionStorage.getItem(seenKey)) {
    announcementDialogVisible.value = true
  }
}

function markAnnouncementSeen() {
  if (latestAnnouncement.value?.id) {
    sessionStorage.setItem(`inkread_seen_announcement_${latestAnnouncement.value.id}`, '1')
  }
}

function goAnnouncementDetail() {
  markAnnouncementSeen()
  announcementDialogVisible.value = false
  if (latestAnnouncement.value?.id) {
    router.push(`/announcements/${latestAnnouncement.value.id}`)
  }
}

watch(() => [query.categoryId, query.sort], () => {
  query.page = 1
  loadBooks()
})

onMounted(async () => {
  categories.value = await categoryApi.list().catch(() => [])
  await loadLatestAnnouncement()
  await loadBooks()
})
</script>
