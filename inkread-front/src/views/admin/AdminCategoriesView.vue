<template>
  <section class="admin-panel">
    <div class="toolbar">
      <el-button type="primary" :icon="Plus" @click="openEditor()">新增分类</el-button>
      <el-button :icon="Refresh" @click="loadCategories">刷新</el-button>
    </div>

    <el-table v-loading="loading" :data="categories" class="ink-table">
      <el-table-column prop="name" label="分类名" min-width="180" />
      <el-table-column prop="description" label="描述" min-width="260" show-overflow-tooltip />
      <el-table-column prop="sortOrder" label="排序" width="90" />
      <el-table-column prop="bookCount" label="图书数" width="100" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button text type="primary" @click="openEditor(row)">编辑</el-button>
          <el-button text @click="toggleStatus(row)">{{ row.status === 1 ? '禁用' : '启用' }}</el-button>
          <el-button text type="danger" @click="removeCategory(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="editorVisible" :title="editingCategory?.id ? '编辑分类' : '新增分类'" width="520px">
      <el-form :model="form" label-position="top">
        <el-form-item label="分类名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { categoryApi } from '@/api/categories'
import type { Category } from '@/types/api'

const loading = ref(false)
const saving = ref(false)
const editorVisible = ref(false)
const editingCategory = ref<Category | null>(null)
const categories = ref<Category[]>([])
const form = reactive<Category>({ name: '', description: '', sortOrder: 0, status: 1 })

async function loadCategories() {
  loading.value = true
  try {
    categories.value = await categoryApi.adminList()
  } finally {
    loading.value = false
  }
}

function openEditor(category?: Category) {
  editingCategory.value = category || null
  Object.assign(form, {
    name: category?.name || '',
    description: category?.description || '',
    sortOrder: category?.sortOrder || 0,
    status: category?.status ?? 1
  })
  editorVisible.value = true
}

async function saveCategory() {
  saving.value = true
  try {
    if (editingCategory.value?.id) {
      await categoryApi.update(editingCategory.value.id, form)
      ElMessage.success('分类已更新')
    } else {
      await categoryApi.create(form)
      ElMessage.success('分类已新增')
    }
    editorVisible.value = false
    await loadCategories()
  } finally {
    saving.value = false
  }
}

async function toggleStatus(category: Category) {
  if (!category.id) return
  await categoryApi.status(category.id, category.status === 1 ? 0 : 1)
  ElMessage.success('状态已更新')
  await loadCategories()
}

async function removeCategory(category: Category) {
  if (!category.id) return
  await ElMessageBox.confirm(`确认删除分类「${category.name}」？`, '删除分类', { type: 'warning' })
  await categoryApi.remove(category.id)
  ElMessage.success('分类已删除')
  await loadCategories()
}

onMounted(loadCategories)
</script>
