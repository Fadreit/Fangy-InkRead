<template>
  <section v-loading="loading" class="message-detail-page">
    <RouterLink class="text-link" to="/messages">返回留言墙</RouterLink>

    <article v-if="message" class="message-detail-card">
      <div class="message-detail-head">
        <span class="message-avatar">{{ avatarText(message) }}</span>
        <div>
          <p class="eyebrow">Reader Note</p>
          <h1>{{ displayName(message) }}</h1>
          <time>{{ formatDate(message.createdAt) }}</time>
        </div>
      </div>

      <p class="message-detail-content">{{ message.content }}</p>

      <div class="message-detail-actions">
        <el-button :type="message.liked ? 'danger' : 'primary'" :icon="Pointer" @click="toggleLike">
          {{ message.liked ? '已点赞' : '点赞' }} · {{ message.likeCount || 0 }}
        </el-button>
        <el-button v-if="canDelete" type="danger" plain @click="removeMessage">删除留言</el-button>
      </div>
    </article>

    <EmptyState
      v-else-if="!loading"
      title="留言不见了"
      description="这条留言可能已经被删除，回到留言墙看看别的回声。"
    />
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Pointer } from '@element-plus/icons-vue'
import { messageApi } from '@/api/messages'
import EmptyState from '@/components/EmptyState.vue'
import { useAuthStore } from '@/stores/auth'
import type { MessageItem } from '@/types/api'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const loading = ref(false)
const message = ref<MessageItem | null>(null)

const displayName = (item: MessageItem) => item.nickname || item.username || '书阁读者'
const avatarText = (item: MessageItem) => displayName(item).slice(0, 1)
const formatDate = (value?: string) => value?.replace('T', ' ').slice(0, 16) || '-'
const canDelete = computed(() => Boolean(message.value && (auth.isAdmin || Number(auth.user?.id) === Number(message.value.userId))))

async function loadMessage() {
  const id = Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    message.value = await messageApi.detail(id)
  } finally {
    loading.value = false
  }
}

async function toggleLike() {
  if (!message.value) return
  if (!auth.isLoggedIn) {
    ElMessage.warning('登录后可以点赞')
    return
  }

  const result = message.value.liked ? await messageApi.unlike(message.value.id) : await messageApi.like(message.value.id)
  message.value.liked = !message.value.liked
  message.value.likeCount = result.likeCount
}

async function removeMessage() {
  if (!message.value) return
  await ElMessageBox.confirm('确认删除这条留言？', '删除留言', { type: 'warning' })
  await messageApi.remove(message.value.id)
  ElMessage.success('留言已删除')
  router.push('/messages')
}

onMounted(loadMessage)
</script>
