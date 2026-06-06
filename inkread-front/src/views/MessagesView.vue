<template>
  <section class="messages-page">
    <div class="message-hero">
      <div>
        <p class="eyebrow">Reader Notes</p>
        <h1>留言墙</h1>
        <p>把想说的话留在书阁里，和后来翻页的人擦肩。</p>
      </div>
      <div class="message-hero-panel">
        <span>墙上留言</span>
        <strong>{{ page.total }}</strong>
        <small>条读者回声</small>
      </div>
    </div>

    <section class="message-composer surface-panel">
      <div>
        <h2>写一张便笺</h2>
        <p>{{ auth.isLoggedIn ? '分享读书片刻、找书愿望，或给书阁留一句话。' : '登录后可以发布留言和点赞。' }}</p>
      </div>
      <el-input
        v-model="form.content"
        type="textarea"
        :rows="4"
        maxlength="500"
        show-word-limit
        placeholder="写下你的留言..."
        :disabled="!auth.isLoggedIn"
      />
      <div class="message-composer-actions">
        <RouterLink v-if="!auth.isLoggedIn" class="text-link" to="/login">去登录</RouterLink>
        <el-button type="primary" :loading="posting" :disabled="!auth.isLoggedIn" @click="postMessage">发布留言</el-button>
      </div>
    </section>

    <section class="message-wall-section">
      <div class="panel-title-row">
        <div>
          <p>Featured Wall</p>
          <h2>墙上精选</h2>
        </div>
        <el-segmented v-model="wallSort" :options="wallSortOptions" @change="loadWall" />
      </div>

      <div v-loading="wallLoading" class="message-wall-grid">
        <RouterLink v-for="message in wallMessages" :key="message.id" class="message-note" :to="`/messages/${message.id}`">
          <p>{{ message.content }}</p>
          <div>
            <span>{{ displayName(message) }}</span>
            <strong>{{ message.likeCount || 0 }} 赞</strong>
          </div>
        </RouterLink>
      </div>
    </section>

    <section class="message-list-section">
      <div class="panel-title-row">
        <div>
          <p>All Notes</p>
          <h2>留言区</h2>
        </div>
        <el-button :icon="Refresh" @click="loadMessages">刷新</el-button>
      </div>

      <div v-loading="loading" class="message-list">
        <article v-for="message in messages" :key="message.id" class="message-row surface-panel">
          <RouterLink class="message-avatar" :to="`/messages/${message.id}`">{{ avatarText(message) }}</RouterLink>
          <div class="message-row-main">
            <div class="message-row-head">
              <RouterLink :to="`/messages/${message.id}`">
                <strong>{{ displayName(message) }}</strong>
                <span>{{ formatDate(message.createdAt) }}</span>
              </RouterLink>
              <el-button text :type="message.liked ? 'danger' : 'primary'" :icon="Pointer" @click="toggleLike(message)">
                {{ message.likeCount || 0 }}
              </el-button>
            </div>
            <p>{{ message.content }}</p>
            <div class="message-row-actions">
              <RouterLink class="text-link" :to="`/messages/${message.id}`">查看</RouterLink>
              <el-button v-if="canDelete(message)" text type="danger" @click="removeMessage(message)">删除</el-button>
            </div>
          </div>
        </article>
      </div>

      <EmptyState
        v-if="!loading && messages.length === 0"
        title="还没有留言"
        description="第一张便笺还空着，等一句刚好落下的话。"
      />

      <footer v-if="page.total > (query.size || 8)" class="pager-row">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          background
          layout="prev, pager, next"
          :total="page.total"
          @current-change="loadMessages"
        />
      </footer>
    </section>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Pointer, Refresh } from '@element-plus/icons-vue'
import { messageApi } from '@/api/messages'
import EmptyState from '@/components/EmptyState.vue'
import { useAuthStore } from '@/stores/auth'
import type { MessageItem, MessageQuery, PageResult } from '@/types/api'

const auth = useAuthStore()
const loading = ref(false)
const wallLoading = ref(false)
const posting = ref(false)
const messages = ref<MessageItem[]>([])
const wallMessages = ref<MessageItem[]>([])
const wallSort = ref<'latest' | 'hot'>('latest')
const query = reactive<MessageQuery>({ page: 1, size: 8 })
const page = reactive<PageResult<MessageItem>>({ total: 0, page: 1, size: 8, pages: 0, records: [] })
const form = reactive({ content: '' })

const wallSortOptions = [
  { label: '最新', value: 'latest' },
  { label: '热门', value: 'hot' }
]

const displayName = (message: MessageItem) => message.nickname || message.username || '书阁读者'
const avatarText = (message: MessageItem) => displayName(message).slice(0, 1)
const formatDate = (value?: string) => value?.replace('T', ' ').slice(0, 16) || '-'
const canDelete = (message: MessageItem) => auth.isAdmin || (auth.user?.id && Number(auth.user.id) === Number(message.userId))

async function loadWall() {
  wallLoading.value = true
  try {
    wallMessages.value = await messageApi.wall(wallSort.value)
  } finally {
    wallLoading.value = false
  }
}

async function loadMessages() {
  loading.value = true
  try {
    const data = await messageApi.list(query)
    Object.assign(page, data)
    messages.value = data.records || []
  } finally {
    loading.value = false
  }
}

async function postMessage() {
  const content = form.content.trim()
  if (!content) {
    ElMessage.warning('请先写下留言内容')
    return
  }

  posting.value = true
  try {
    await messageApi.create({ content })
    ElMessage.success('留言已发布')
    form.content = ''
    query.page = 1
    await Promise.all([loadWall(), loadMessages()])
  } finally {
    posting.value = false
  }
}

async function toggleLike(message: MessageItem) {
  if (!auth.isLoggedIn) {
    ElMessage.warning('登录后可以点赞')
    return
  }

  const result = message.liked ? await messageApi.unlike(message.id) : await messageApi.like(message.id)
  message.liked = !message.liked
  message.likeCount = result.likeCount
  const wallItem = wallMessages.value.find((item) => item.id === message.id)
  if (wallItem) {
    wallItem.liked = message.liked
    wallItem.likeCount = result.likeCount
  }
}

async function removeMessage(message: MessageItem) {
  await ElMessageBox.confirm('确认删除这条留言？', '删除留言', { type: 'warning' })
  await messageApi.remove(message.id)
  ElMessage.success('留言已删除')
  await Promise.all([loadWall(), loadMessages()])
}

onMounted(async () => {
  await Promise.all([loadWall(), loadMessages()])
})
</script>
