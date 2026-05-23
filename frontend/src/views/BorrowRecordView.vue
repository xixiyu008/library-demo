<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { Check, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBooks } from '../api/book'
import { borrowBook, getBorrows, returnBook } from '../api/borrow'
import { getReaders } from '../api/reader'
import type { BookVO, BorrowRecordVO, BorrowStatus, ReaderVO } from '../types'

const rows = ref<BorrowRecordVO[]>([])
const total = ref(0)
const loading = ref(false)
const borrowing = ref(false)
const query = reactive<{ page: number; pageSize: number; readerId?: number; bookId?: number; status?: BorrowStatus }>({ page: 1, pageSize: 10 })
const borrowForm = reactive({ readerId: undefined as number | undefined, bookId: undefined as number | undefined })
const readerOptions = ref<ReaderVO[]>([])
const bookOptions = ref<BookVO[]>([])
const readerLoading = ref(false)
const bookLoading = ref(false)

async function load() {
  loading.value = true
  try {
    const result = await getBorrows(query)
    rows.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

async function searchReaders(keyword = '') {
  readerLoading.value = true
  try {
    const result = await getReaders({ page: 1, pageSize: 20, keyword })
    readerOptions.value = result.records
  } finally {
    readerLoading.value = false
  }
}

async function searchBooks(keyword = '') {
  bookLoading.value = true
  try {
    const result = await getBooks({ page: 1, pageSize: 20, keyword })
    bookOptions.value = result.records
  } finally {
    bookLoading.value = false
  }
}

function readerLabel(reader: ReaderVO) {
  return `${reader.name} · ${reader.studentNo}${reader.status === 'DISABLED' ? ' · 已停用' : ''}`
}

function bookLabel(book: BookVO) {
  return `${book.title}${book.isbn ? ` · ${book.isbn}` : ''} · 可借 ${book.availableCount}`
}

function searchRecords() {
  query.page = 1
  load()
}

function resetSearch() {
  query.readerId = undefined
  query.bookId = undefined
  query.status = undefined
  searchRecords()
}

async function submitBorrow() {
  if (!borrowForm.readerId || !borrowForm.bookId) {
    ElMessage.warning('请选择读者和图书')
    return
  }
  borrowing.value = true
  try {
    await borrowBook({ readerId: borrowForm.readerId, bookId: borrowForm.bookId })
    ElMessage.success('借书成功')
    borrowForm.readerId = undefined
    borrowForm.bookId = undefined
    await Promise.all([searchReaders(), searchBooks()])
    load()
  } finally {
    borrowing.value = false
  }
}

async function submitReturn(row: BorrowRecordVO) {
  try {
    await ElMessageBox.confirm(`确认归还《${row.bookTitle || '该图书'}》？`, '办理还书', {
      confirmButtonText: '确认归还',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await returnBook(row.id)
  ElMessage.success('还书成功')
  load()
}

onMounted(() => {
  load()
  searchReaders()
  searchBooks()
})
</script>

<template>
  <section class="page-head">
    <div>
      <h1 class="page-title">借阅办理</h1>
      <p class="page-desc">为读者办理借书、还书，并查询全部借阅记录。</p>
    </div>
    <div class="page-actions">
      <el-button :icon="Refresh" @click="resetSearch">重置筛选</el-button>
    </div>
  </section>

  <section class="workspace borrow-workspace">
    <div class="borrow-panel">
      <div>
        <strong>办理借书</strong>
        <p class="page-desc">搜索读者和图书后提交，系统会自动校验库存、读者状态和借阅规则。</p>
      </div>
      <div class="borrow-controls">
        <el-select
          v-model="borrowForm.readerId"
          class="borrow-select"
          filterable
          remote
          clearable
          reserve-keyword
          placeholder="选择读者（姓名 / 学号）"
          :remote-method="searchReaders"
          :loading="readerLoading"
        >
          <el-option
            v-for="reader in readerOptions"
            :key="reader.id"
            :label="readerLabel(reader)"
            :value="reader.id"
            :disabled="reader.status === 'DISABLED'"
          />
        </el-select>
        <el-select
          v-model="borrowForm.bookId"
          class="borrow-select"
          filterable
          remote
          clearable
          reserve-keyword
          placeholder="选择图书（书名 / ISBN）"
          :remote-method="searchBooks"
          :loading="bookLoading"
        >
          <el-option
            v-for="book in bookOptions"
            :key="book.id"
            :label="bookLabel(book)"
            :value="book.id"
            :disabled="book.status === 'DISABLED' || book.availableCount <= 0"
          />
        </el-select>
        <el-button type="primary" :icon="Plus" :loading="borrowing" @click="submitBorrow">办理借书</el-button>
      </div>
    </div>

    <div class="filter-bar">
      <el-select
        v-model="query.readerId"
        filterable
        remote
        clearable
        reserve-keyword
        placeholder="按读者筛选"
        :remote-method="searchReaders"
        :loading="readerLoading"
      >
        <el-option v-for="reader in readerOptions" :key="reader.id" :label="readerLabel(reader)" :value="reader.id" />
      </el-select>
      <el-select
        v-model="query.bookId"
        filterable
        remote
        clearable
        reserve-keyword
        placeholder="按图书筛选"
        :remote-method="searchBooks"
        :loading="bookLoading"
      >
        <el-option v-for="book in bookOptions" :key="book.id" :label="bookLabel(book)" :value="book.id" />
      </el-select>
      <el-select v-model="query.status" placeholder="借阅状态" clearable>
        <el-option label="借出未还" value="BORROWED" />
        <el-option label="已归还" value="RETURNED" />
        <el-option label="已逾期" value="OVERDUE" />
      </el-select>
      <el-button :icon="Search" @click="searchRecords">查询记录</el-button>
    </div>

    <div class="table-wrap">
      <el-table v-loading="loading" :data="rows" border>
        <template #empty>
          <div class="empty-hint">暂无借阅记录，办理借书后会显示在这里。</div>
        </template>
        <el-table-column prop="id" label="编号" width="80" />
        <el-table-column prop="readerName" label="读者" min-width="120" />
        <el-table-column prop="bookTitle" label="图书" min-width="180" show-overflow-tooltip />
        <el-table-column prop="borrowTime" label="借出时间" min-width="170" />
        <el-table-column prop="dueTime" label="应还时间" min-width="170" />
        <el-table-column prop="returnTime" label="归还时间" min-width="170" />
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === 'RETURNED' ? 'success' : row.status === 'OVERDUE' ? 'danger' : 'warning'" effect="light">
              {{ row.status === 'RETURNED' ? '已归还' : row.status === 'OVERDUE' ? '已逾期' : '借出未还' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status !== 'RETURNED'" :icon="Check" link type="primary" @click="submitReturn(row)">还书</el-button>
            <span v-else class="muted">已完成</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="table-footer">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="searchRecords"
        @current-change="load"
      />
    </div>
  </section>
</template>

<style scoped>
.borrow-panel {
  display: grid;
  grid-template-columns: minmax(220px, 0.8fr) minmax(420px, 2fr);
  gap: 16px;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid var(--app-border-soft);
}

.borrow-controls {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: flex-end;
}

.borrow-select {
  width: 280px;
}

@media (max-width: 900px) {
  .borrow-panel {
    grid-template-columns: 1fr;
  }

  .borrow-controls {
    justify-content: flex-start;
  }

  .borrow-select,
  .borrow-controls .el-button {
    width: 100%;
  }
}
</style>
