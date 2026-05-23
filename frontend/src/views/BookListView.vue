<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { createBook, deleteBook, getBooks, updateBook } from '../api/book'
import { useAuthStore } from '../stores/auth'
import type { BookStatus, BookVO } from '../types'

const auth = useAuthStore()
const rows = ref<BookVO[]>([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number>()
const formRef = ref<FormInstance>()
const titleInput = ref()
const query = reactive({ page: 1, pageSize: 10, keyword: '' })
const form = reactive({
  isbn: '',
  title: '',
  author: '',
  publisher: '',
  category: '',
  totalCount: 1,
  availableCount: 1,
  status: 'AVAILABLE' as BookStatus
})
const rules: FormRules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  totalCount: [{ required: true, message: '请输入总数量', trigger: 'change' }],
  availableCount: [{
    validator: (_rule, value, callback) => {
      if (value == null) callback(new Error('请输入可借数量'))
      else if (value > form.totalCount) callback(new Error('可借数量不能大于总数量'))
      else callback()
    },
    trigger: 'change'
  }]
}

async function load() {
  loading.value = true
  try {
    const result = await getBooks(query)
    rows.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1
  load()
}

function resetSearch() {
  query.keyword = ''
  search()
}

function focusTitle() {
  nextTick(() => titleInput.value?.focus())
}

function openCreate() {
  editingId.value = undefined
  Object.assign(form, { isbn: '', title: '', author: '', publisher: '', category: '', totalCount: 1, availableCount: 1, status: 'AVAILABLE' })
  drawerVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
  focusTitle()
}

function openEdit(row: BookVO) {
  editingId.value = row.id
  Object.assign(form, row)
  drawerVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
  focusTitle()
}

async function save() {
  await formRef.value?.validate()
  saving.value = true
  try {
    if (editingId.value) await updateBook(editingId.value, form)
    else await createBook(form)
    ElMessage.success('保存成功')
    drawerVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function remove(row: BookVO) {
  try {
    await ElMessageBox.confirm(`删除《${row.title}》后无法恢复，确认继续？`, '删除图书', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteBook(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<template>
  <section class="page-head">
    <div>
      <h1 class="page-title">{{ auth.role === 'STUDENT' ? '图书查询' : '图书管理' }}</h1>
      <p class="page-desc">按书名、ISBN 或作者检索馆藏，维护库存和可借状态。</p>
    </div>
    <div class="page-actions">
      <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
      <el-button v-if="auth.role !== 'STUDENT'" type="primary" :icon="Plus" @click="openCreate">新增图书</el-button>
    </div>
  </section>

  <section class="workspace">
    <div class="filter-bar">
      <el-input v-model="query.keyword" placeholder="书名 / ISBN / 作者" clearable @keyup.enter="search" />
      <el-button :icon="Search" @click="search">查询</el-button>
    </div>
    <div class="table-wrap">
      <el-table v-loading="loading" :data="rows" border>
        <template #empty>
          <div class="empty-hint">暂无图书，调整搜索条件或新增馆藏。</div>
        </template>
        <el-table-column prop="title" label="书名" min-width="180" show-overflow-tooltip />
        <el-table-column prop="isbn" label="ISBN" min-width="140" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" min-width="120" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" show-overflow-tooltip />
        <el-table-column label="库存" width="130">
          <template #default="{ row }">
            <span>{{ row.availableCount }} / {{ row.totalCount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === 'AVAILABLE' ? 'success' : 'info'" effect="light">
              {{ row.status === 'AVAILABLE' ? '可用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="auth.role !== 'STUDENT'" label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <div class="split-actions">
              <el-button :icon="Edit" link type="primary" @click="openEdit(row)">编辑</el-button>
              <el-button :icon="Delete" link type="danger" @click="remove(row)">删除</el-button>
            </div>
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
        @size-change="search"
        @current-change="load"
      />
    </div>
  </section>

  <el-drawer v-model="drawerVisible" :title="editingId ? '编辑图书' : '新增图书'" size="460px">
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="书名" prop="title"><el-input ref="titleInput" v-model.trim="form.title" placeholder="请输入书名" /></el-form-item>
      <el-form-item label="ISBN"><el-input v-model.trim="form.isbn" placeholder="请输入 ISBN" /></el-form-item>
      <el-form-item label="作者"><el-input v-model.trim="form.author" placeholder="请输入作者" /></el-form-item>
      <el-form-item label="出版社"><el-input v-model.trim="form.publisher" placeholder="请输入出版社" /></el-form-item>
      <el-form-item label="分类"><el-input v-model.trim="form.category" placeholder="请输入分类" /></el-form-item>
      <el-form-item label="总数量" prop="totalCount"><el-input-number v-model="form.totalCount" :min="0" /></el-form-item>
      <el-form-item label="可借数量" prop="availableCount"><el-input-number v-model="form.availableCount" :min="0" :max="form.totalCount" /></el-form-item>
      <el-form-item v-if="editingId" label="状态">
        <el-select v-model="form.status">
          <el-option label="可用" value="AVAILABLE" />
          <el-option label="停用" value="DISABLED" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="drawer-footer">
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="save">保存</el-button>
      </div>
    </template>
  </el-drawer>
</template>
