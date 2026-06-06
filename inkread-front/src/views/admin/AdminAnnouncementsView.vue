<template>
  <section class="admin-panel">
    <div class="toolbar announcement-toolbar">
      <el-input v-model="query.keyword" placeholder="搜索公告标题或内容" clearable @keyup.enter="loadAnnouncements">
        <template #prefix><Search /></template>
      </el-input>
      <el-select v-model="query.status" placeholder="发布状态" clearable>
        <el-option label="已发布" :value="1" />
        <el-option label="草稿" :value="0" />
      </el-select>
      <el-select v-model="deleteFilter" placeholder="删除状态">
        <el-option label="全部公告" value="all" />
        <el-option label="未删除" value="active" />
        <el-option label="已删除" value="deleted" />
      </el-select>
      <el-button :icon="Search" @click="loadAnnouncements">查询</el-button>
      <el-button type="primary" :icon="Plus" @click="openEditor()">新增公告</el-button>
    </div>

    <el-alert
      v-if="deletedCount"
      class="soft-delete-alert"
      type="warning"
      :closable="false"
      show-icon
      :title="`当前列表包含 ${deletedCount} 条已删除公告，已删除公告会灰显且不可继续编辑、发布或删除。`"
    />

    <el-table v-loading="loading" :data="visibleAnnouncements" class="ink-table" :row-class-name="announcementRowClass">
      <el-table-column label="公告" min-width="320">
        <template #default="{ row }">
          <div class="table-announcement">
            <strong>
              {{ row.title }}
              <el-tag v-if="isDeleted(row)" class="inline-tag" type="danger" effect="plain">已删除</el-tag>
            </strong>
            <span>{{ preview(row.content) }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template #default="{ row }">
          <el-tag v-if="isDeleted(row)" type="danger">已删除</el-tag>
          <el-tag v-else :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" width="180">
        <template #default="{ row }">{{ formatDate(row.updatedAt || row.createdAt) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <template v-if="isDeleted(row)">
            <span class="deleted-hint">已软删除</span>
          </template>
          <template v-else>
            <el-button text type="primary" @click="openEditor(row)">编辑</el-button>
            <el-button text @click="toggleStatus(row)">{{ row.status === 1 ? '设为草稿' : '发布' }}</el-button>
            <el-button text type="danger" @click="removeAnnouncement(row)">删除</el-button>
          </template>
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
        @current-change="loadAnnouncements"
      />
    </footer>

    <el-dialog v-model="editorVisible" :title="editingAnnouncement?.id ? '编辑公告' : '新增公告'" width="640px">
      <el-form :model="form" label-position="top">
        <el-form-item label="公告标题">
          <el-input v-model="form.title" maxlength="80" show-word-limit />
        </el-form-item>
        <el-form-item label="公告内容">
          <el-input v-model="form.content" type="textarea" :rows="8" maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item label="发布状态">
          <el-radio-group v-model="form.status">
            <el-radio-button :value="1">发布</el-radio-button>
            <el-radio-button :value="0">草稿</el-radio-button>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveAnnouncement">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { announcementApi } from '@/api/announcements'
import type { Announcement, AnnouncementQuery, PageResult } from '@/types/api'

const loading = ref(false)
const saving = ref(false)
const editorVisible = ref(false)
const editingAnnouncement = ref<Announcement | null>(null)
const announcements = ref<Announcement[]>([])
const deleteFilter = ref<'all' | 'active' | 'deleted'>('all')
const query = reactive<AnnouncementQuery>({ page: 1, size: 10 })
const page = reactive<PageResult<Announcement>>({ total: 0, page: 1, size: 10, pages: 0, records: [] })
const form = reactive<Announcement>({ title: '', content: '', status: 1 })

const isDeleted = (announcement: Announcement) => Number(announcement.isDeleted || 0) === 1
const deletedCount = computed(() => announcements.value.filter(isDeleted).length)
const visibleAnnouncements = computed(() => {
  if (deleteFilter.value === 'active') return announcements.value.filter((announcement) => !isDeleted(announcement))
  if (deleteFilter.value === 'deleted') return announcements.value.filter(isDeleted)
  return announcements.value
})

const formatDate = (value?: string) => value?.replace('T', ' ').slice(0, 16) || '-'
const preview = (value?: string) => {
  const text = value || '暂无内容'
  return text.length > 72 ? `${text.slice(0, 72)}...` : text
}

function announcementRowClass({ row }: { row: Announcement }) {
  return isDeleted(row) ? 'soft-deleted-row' : ''
}

async function loadAnnouncements() {
  loading.value = true
  try {
    const data = await announcementApi.adminList(query)
    Object.assign(page, data)
    announcements.value = data.records || []
  } finally {
    loading.value = false
  }
}

function openEditor(announcement?: Announcement) {
  if (announcement && isDeleted(announcement)) return
  editingAnnouncement.value = announcement || null
  Object.assign(form, {
    title: announcement?.title || '',
    content: announcement?.content || '',
    status: announcement?.status ?? 1
  })
  editorVisible.value = true
}

async function saveAnnouncement() {
  if (!form.title.trim() || !form.content.trim()) {
    ElMessage.warning('标题和内容不能为空')
    return
  }

  saving.value = true
  try {
    const payload = {
      title: form.title.trim(),
      content: form.content.trim(),
      status: form.status ?? 0
    }
    if (editingAnnouncement.value?.id) {
      await announcementApi.update(editingAnnouncement.value.id, payload)
      ElMessage.success('公告已更新')
    } else {
      await announcementApi.create(payload)
      ElMessage.success('公告已新增')
    }
    editorVisible.value = false
    await loadAnnouncements()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(announcement: Announcement) {
  if (!announcement.id || isDeleted(announcement)) return
  await announcementApi.status(announcement.id, announcement.status === 1 ? 0 : 1)
  ElMessage.success('状态已更新')
  await loadAnnouncements()
}

async function removeAnnouncement(announcement: Announcement) {
  if (!announcement.id || isDeleted(announcement)) return
  await ElMessageBox.confirm(`确认删除公告「${announcement.title}」？`, '删除公告', { type: 'warning' })
  await announcementApi.remove(announcement.id)
  ElMessage.success('公告已删除')
  await loadAnnouncements()
}

onMounted(loadAnnouncements)
</script>
