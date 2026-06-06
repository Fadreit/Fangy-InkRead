<template>
  <section class="announcements-page">
    <header class="section-heading announcement-hero">
      <p>Notice Board</p>
      <h1>书阁公告</h1>
      <span>新书上架、服务调整与阅读活动都会在这里留下一纸告示。</span>
    </header>

    <div v-loading="loading" class="announcement-list">
      <RouterLink
        v-for="announcement in announcements"
        :key="announcement.id"
        class="announcement-card"
        :to="`/announcements/${announcement.id}`"
      >
        <div class="announcement-card-date">
          <span>{{ monthDay(announcement.createdAt) }}</span>
          <small>{{ yearText(announcement.createdAt) }}</small>
        </div>
        <div class="announcement-card-main">
          <h2>{{ announcement.title }}</h2>
          <p>{{ announcement.content }}</p>
        </div>
      </RouterLink>
    </div>

    <EmptyState
      v-if="!loading && announcements.length === 0"
      title="暂时没有公告"
      description="书阁还很安静，有新的消息会第一时间贴在这里。"
    />

    <footer v-if="page.total > (query.size || 8)" class="pager-row">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        background
        layout="prev, pager, next"
        :total="page.total"
        @current-change="loadAnnouncements"
      />
    </footer>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { announcementApi } from '@/api/announcements'
import EmptyState from '@/components/EmptyState.vue'
import type { Announcement, AnnouncementQuery, PageResult } from '@/types/api'

const loading = ref(false)
const announcements = ref<Announcement[]>([])
const query = reactive<AnnouncementQuery>({ page: 1, size: 8 })
const page = reactive<PageResult<Announcement>>({ total: 0, page: 1, size: 8, pages: 0, records: [] })

const asDate = (value?: string) => (value ? new Date(value.replace(' ', 'T')) : null)
const monthDay = (value?: string) => {
  const date = asDate(value)
  if (!date || Number.isNaN(date.getTime())) return '--'
  return `${date.getMonth() + 1}.${String(date.getDate()).padStart(2, '0')}`
}
const yearText = (value?: string) => String(asDate(value)?.getFullYear() || '')

async function loadAnnouncements() {
  loading.value = true
  try {
    const data = await announcementApi.list(query)
    Object.assign(page, data)
    announcements.value = data.records || []
  } finally {
    loading.value = false
  }
}

onMounted(loadAnnouncements)
</script>
