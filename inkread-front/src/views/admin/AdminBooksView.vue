<template>
  <section class="admin-panel">
    <div class="toolbar">
      <el-input v-model="query.keyword" placeholder="搜索书名或作者" clearable>
        <template #prefix><Search /></template>
      </el-input>
      <el-select v-model="query.categoryId" placeholder="全部分类" clearable>
        <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
      </el-select>
      <el-select v-model="query.status" placeholder="上下架" clearable>
        <el-option label="上架" :value="1" />
        <el-option label="下架" :value="0" />
      </el-select>
      <el-select v-model="deleteFilter" placeholder="删除状态">
        <el-option label="全部图书" value="all" />
        <el-option label="未删除" value="active" />
        <el-option label="已删除" value="deleted" />
      </el-select>
      <el-button :icon="Search" @click="loadBooks">查询</el-button>
      <el-button type="primary" :icon="Plus" @click="openEditor()">新增图书</el-button>
    </div>

    <el-alert
      v-if="deletedCount"
      class="soft-delete-alert"
      type="warning"
      :closable="false"
      show-icon
      :title="`当前列表包含 ${deletedCount} 本已删除图书，已删除图书会灰显且不可继续编辑、上下架或删除。`"
    />

    <el-table v-loading="loading" :data="visibleBooks" class="ink-table" :row-class-name="bookRowClass">
      <el-table-column label="图书" min-width="260">
        <template #default="{ row }">
          <div class="table-book">
            <BookCover :src="row.coverUrl" :title="row.title" />
            <div>
              <strong>
                {{ row.title }}
                <el-tag v-if="isDeleted(row)" class="inline-tag" type="danger" effect="plain">已删除</el-tag>
              </strong>
              <span>{{ row.author }} · ¥{{ money(row.price) }}</span>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="90" />
      <el-table-column label="分类" width="130">
        <template #default="{ row }">{{ categoryName(row.categoryId) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="isDeleted(row)" type="danger">已删除</el-tag>
          <el-tag v-else :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <template v-if="isDeleted(row)">
            <span class="deleted-hint">已软删除</span>
          </template>
          <template v-else>
            <el-button text type="primary" @click="openEditor(row)">编辑</el-button>
            <el-button text @click="toggleShelf(row)">{{ row.status === 1 ? '下架' : '上架' }}</el-button>
            <el-button text type="danger" @click="removeBook(row)">删除</el-button>
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
        @current-change="loadBooks"
      />
    </footer>

    <el-dialog v-model="editorVisible" :title="editingBook?.id ? '编辑图书' : '新增图书'" width="720px">
      <el-form :model="form" label-position="top" class="dialog-grid">
        <el-form-item label="书名"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="作者"><el-input v-model="form.author" /></el-form-item>
        <el-form-item label="ISBN"><el-input v-model="form.isbn" /></el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类">
            <el-option v-for="category in categories" :key="category.id" :label="category.name" :value="category.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="出版社"><el-input v-model="form.publisher" /></el-form-item>
        <el-form-item label="出版日期"><el-date-picker v-model="form.publishDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
        <el-form-item label="图书封面" class="dialog-span">
          <div class="cover-upload-field">
            <BookCover :src="form.coverUrl" :title="form.title || '封面'" />
            <div class="cover-upload-main">
              <el-upload
                :show-file-list="false"
                :auto-upload="false"
                accept=".jpg,.jpeg,.png,.webp,image/jpeg,image/png,image/webp"
                :on-change="handleCoverPicked"
              >
                <el-button type="primary" plain :loading="uploadingCover">选择并上传封面</el-button>
              </el-upload>
              <p>支持 JPG / PNG / WEBP，文件不超过 2MB。上传成功后会自动写入 OSS 图片地址。</p>
              <el-input v-model="form.coverUrl" readonly placeholder="上传后自动生成封面地址" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="简介" class="dialog-span"><el-input v-model="form.description" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveBook">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadFile } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { bookApi } from '@/api/books'
import { categoryApi } from '@/api/categories'
import BookCover from '@/components/BookCover.vue'
import type { Book, BookQuery, Category, PageResult } from '@/types/api'

const loading = ref(false)
const saving = ref(false)
const uploadingCover = ref(false)
const editorVisible = ref(false)
const editingBook = ref<Book | null>(null)
const books = ref<Book[]>([])
const categories = ref<Category[]>([])
const deleteFilter = ref<'all' | 'active' | 'deleted'>('all')
const query = reactive<BookQuery>({ page: 1, size: 10 })
const page = reactive<PageResult<Book>>({ total: 0, page: 1, size: 10, pages: 0, records: [] })
const form = reactive<Book>({
  title: '',
  author: '',
  isbn: '',
  categoryId: undefined,
  price: 0,
  stock: 0,
  publisher: '',
  publishDate: '',
  description: '',
  coverUrl: '',
  status: 1
})

const money = (value?: number) => Number(value || 0).toFixed(2)
const categoryName = (id?: number) => categories.value.find((item) => item.id === id)?.name || '未分类'
const isDeleted = (book: Book) => Number(book.isDeleted || 0) === 1
const deletedCount = computed(() => books.value.filter(isDeleted).length)
const visibleBooks = computed(() => {
  if (deleteFilter.value === 'active') return books.value.filter((book) => !isDeleted(book))
  if (deleteFilter.value === 'deleted') return books.value.filter(isDeleted)
  return books.value
})

function bookRowClass({ row }: { row: Book }) {
  return isDeleted(row) ? 'soft-deleted-row' : ''
}

async function loadBooks() {
  loading.value = true
  try {
    const data = await bookApi.adminList(query)
    Object.assign(page, data)
    books.value = data.records || []
  } finally {
    loading.value = false
  }
}

function openEditor(book?: Book) {
  if (book && isDeleted(book)) return
  editingBook.value = book || null
  Object.assign(form, {
    title: book?.title || '',
    author: book?.author || '',
    isbn: book?.isbn || '',
    categoryId: book?.categoryId,
    price: Number(book?.price || 0),
    stock: Number(book?.stock || 0),
    publisher: book?.publisher || '',
    publishDate: book?.publishDate || '',
    description: book?.description || '',
    coverUrl: book?.coverUrl || '',
    status: book?.status ?? 1
  })
  editorVisible.value = true
}

async function saveBook() {
  saving.value = true
  try {
    if (editingBook.value?.id) {
      await bookApi.update(editingBook.value.id, form)
      ElMessage.success('图书已更新')
    } else {
      await bookApi.create(form)
      ElMessage.success('图书已新增')
    }
    editorVisible.value = false
    await loadBooks()
  } finally {
    saving.value = false
  }
}

async function handleCoverPicked(uploadFile: UploadFile) {
  const file = uploadFile.raw
  if (!file) return

  const allowedTypes = ['image/jpeg', 'image/png', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.warning('封面仅支持 JPG、PNG 或 WEBP 格式')
    return
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.warning('封面图片不能超过 2MB')
    return
  }

  uploadingCover.value = true
  try {
    const data = await bookApi.uploadCover(file)
    form.coverUrl = data.url
    ElMessage.success('封面已上传')
  } finally {
    uploadingCover.value = false
  }
}

async function toggleShelf(book: Book) {
  if (!book.id || isDeleted(book)) return
  await bookApi.shelf(book.id, book.status === 1 ? 0 : 1)
  ElMessage.success('状态已更新')
  await loadBooks()
}

async function removeBook(book: Book) {
  if (!book.id || isDeleted(book)) return
  await ElMessageBox.confirm(`确认删除《${book.title}》？`, '删除图书', { type: 'warning' })
  await bookApi.remove(book.id)
  ElMessage.success('图书已删除')
  await loadBooks()
}

onMounted(async () => {
  categories.value = await categoryApi.adminList().catch(() => [])
  await loadBooks()
})
</script>
