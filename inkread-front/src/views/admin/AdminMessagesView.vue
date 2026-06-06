<template>
  <section class="admin-panel">
    <div class="toolbar user-toolbar">
      <el-input v-model="query.keyword" placeholder="搜索留言内容或用户" clearable @keyup.enter="loadMessages">
        <template #prefix><Search /></template>
      </el-input>
      <el-select v-model="deleteFilter" placeholder="删除状态">
        <el-option label="全部留言" value="all" />
        <el-option label="未删除" value="active" />
        <el-option label="已删除" value="deleted" />
      </el-select>
      <el-button :icon="Search" @click="loadMessages">查询</el-button>
      <el-button :icon="Refresh" @click="loadMessages">刷新</el-button>
    </div>

    <el-alert
      v-if="deletedCount"
      class="soft-delete-alert"
      type="warning"
      :closable="false"
      show-icon
      :title="`当前列表包含 ${deletedCount} 条已删除留言，已删除留言会灰显且不可继续删除。`"
    />

    <el-table v-loading="loading" :data="visibleMessages" class="ink-table" :row-class-name="messageRowClass">
      <el-table-column label="留言" min-width="360">
        <template #default="{ row }">
          <div class="table-message">
            <strong>
              {{ row.content }}
              <el-tag v-if="isDeleted(row)" class="inline-tag" type="danger" effect="plain">已删除</el-tag>
            </strong>
            <span>{{ displayName(row) }} · {{ formatDate(row.createdAt || row.updatedAt) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="点赞" width="90">
        <template #default="{ row }">{{ row.likeCount || 0 }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="isDeleted(row) ? 'danger' : 'success'">{{ isDeleted(row) ? '已删除' : '可见' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <span v-if="isDeleted(row)" class="deleted-hint">已软删除</span>
          <el-button v-else text type="danger" @click="removeMessage(row)">删除</el-button>
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
        @current-change="loadMessages"
      />
    </footer>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { messageApi } from '@/api/messages'
import type { MessageItem, MessageQuery, PageResult } from '@/types/api'

const loading = ref(false)
const messages = ref<MessageItem[]>([])
const deleteFilter = ref<'all' | 'active' | 'deleted'>('all')
const query = reactive<MessageQuery>({ page: 1, size: 10 })
const page = reactive<PageResult<MessageItem>>({ total: 0, page: 1, size: 10, pages: 0, records: [] })

const isDeleted = (message: MessageItem) => Number(message.isDeleted || 0) === 1
const deletedCount = computed(() => messages.value.filter(isDeleted).length)
const visibleMessages = computed(() => {
  if (deleteFilter.value === 'active') return messages.value.filter((message) => !isDeleted(message))
  if (deleteFilter.value === 'deleted') return messages.value.filter(isDeleted)
  return messages.value
})

const displayName = (message: MessageItem) => message.nickname || message.username || `用户 ${message.userId || '-'}`
const formatDate = (value?: string) => value?.replace('T', ' ').slice(0, 16) || '-'

function messageRowClass({ row }: { row: MessageItem }) {
  return isDeleted(row) ? 'soft-deleted-row' : ''
}

async function loadMessages() {
  loading.value = true
  try {
    const data = await messageApi.adminList(query)
    Object.assign(page, data)
    messages.value = data.records || []
  } finally {
    loading.value = false
  }
}

async function removeMessage(message: MessageItem) {
  if (isDeleted(message)) return
  await ElMessageBox.confirm('确认删除这条留言？', '删除留言', { type: 'warning' })
  await messageApi.remove(message.id)
  ElMessage.success('留言已删除')
  await loadMessages()
}

onMounted(loadMessages)
</script>
