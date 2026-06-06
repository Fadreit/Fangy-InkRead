<template>
  <section v-loading="loading" class="announcement-detail-page">
    <RouterLink class="text-link" to="/announcements">返回公告</RouterLink>

    <article v-if="announcement" class="announcement-article">
      <p class="eyebrow">Notice</p>
      <h1>{{ announcement.title }}</h1>
      <time>{{ formatDate(announcement.createdAt) }}</time>
      <div class="announcement-content">{{ announcement.content }}</div>
    </article>

    <EmptyState
      v-else-if="!loading"
      title="公告不见了"
      description="这条公告可能已经撤下，请回到公告栏查看最新消息。"
    />
  </section>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { announcementApi } from '@/api/announcements'
import EmptyState from '@/components/EmptyState.vue'
import type { Announcement } from '@/types/api'

const route = useRoute()
const loading = ref(false)
const announcement = ref<Announcement | null>(null)

const formatDate = (value?: string) => value?.replace('T', ' ').slice(0, 16) || ''

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    announcement.value = await announcementApi.detail(id)
  } finally {
    loading.value = false
  }
})
</script>
