<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { createReader, deleteReader, getReaders, updateReader } from '../api/reader'
import { getUsers } from '../api/user'
import type { ReaderStatus, ReaderVO, UserVO } from '../types'

const rows = ref<ReaderVO[]>([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number>()
const formRef = ref<FormInstance>()
const studentNoInput = ref()
const userOptions = ref<UserVO[]>([])
const userLoading = ref(false)
const query = reactive({ page: 1, pageSize: 10, keyword: '' })
const form = reactive({ studentNo: '', name: '', college: '', phone: '', userId: undefined as number | undefined, status: 'ACTIVE' as ReaderStatus })
const collegeOptions = ['计算机学院', '电子信息学院', '数学学院', '物理学院', '化学学院', '生命科学学院', '遥感学院']
const rules: FormRules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{
    validator: (_rule, value, callback) => {
      if (!value || /^1\d{10}$/.test(value)) callback()
      else callback(new Error('请输入 11 位手机号码'))
    },
    trigger: 'blur'
  }]
}
const usersById = computed(() => new Map(userOptions.value.map((user) => [user.id, user])))

async function load() {
  loading.value = true
  try {
    const result = await getReaders(query)
    rows.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

async function searchUsers(keyword = '') {
  userLoading.value = true
  try {
    const result = await getUsers({ page: 1, pageSize: 50, keyword })
    userOptions.value = result.records
  } finally {
    userLoading.value = false
  }
}

function userLabel(user: UserVO) {
  return `${user.username} · ${user.role}${user.enabled ? '' : ' · 已停用'}`
}

function readerUserLabel(userId?: number) {
  if (!userId) return '未关联'
  const user = usersById.value.get(userId)
  return user ? userLabel(user) : `用户 ${userId}`
}

function search() {
  query.page = 1
  load()
}

function resetSearch() {
  query.keyword = ''
  search()
}

function focusStudentNo() {
  nextTick(() => studentNoInput.value?.focus())
}

function openCreate() {
  editingId.value = undefined
  Object.assign(form, { studentNo: '', name: '', college: '', phone: '', userId: undefined, status: 'ACTIVE' })
  drawerVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
  focusStudentNo()
}

function openEdit(row: ReaderVO) {
  editingId.value = row.id
  Object.assign(form, row)
  drawerVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
  focusStudentNo()
}

async function save() {
  await formRef.value?.validate()
  saving.value = true
  try {
    if (editingId.value) await updateReader(editingId.value, form)
    else await createReader(form)
    ElMessage.success('保存成功')
    drawerVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function remove(row: ReaderVO) {
  try {
    await ElMessageBox.confirm(`删除读者「${row.name}」后无法恢复，存在未归还记录时系统会阻止删除。确认继续？`, '删除读者', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteReader(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(() => {
  load()
  searchUsers()
})
</script>

<template>
  <section class="page-head">
    <div>
      <h1 class="page-title">读者管理</h1>
      <p class="page-desc">维护读者档案、借阅资格，并关联学生登录账号。</p>
    </div>
    <div class="page-actions">
      <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
      <el-button type="primary" :icon="Plus" @click="openCreate">新增读者</el-button>
    </div>
  </section>

  <section class="workspace">
    <div class="filter-bar">
      <el-input v-model="query.keyword" placeholder="姓名 / 学号" clearable @keyup.enter="search" />
      <el-button :icon="Search" @click="search">查询</el-button>
    </div>
    <div class="table-wrap">
      <el-table v-loading="loading" :data="rows" border>
        <template #empty>
          <div class="empty-hint">暂无读者，调整搜索条件或新增读者档案。</div>
        </template>
        <el-table-column prop="studentNo" label="学号" min-width="130" />
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="college" label="学院" min-width="140" show-overflow-tooltip />
        <el-table-column prop="phone" label="电话" min-width="130" />
        <el-table-column label="登录账号" min-width="180" show-overflow-tooltip>
          <template #default="{ row }">{{ readerUserLabel(row.userId) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'" effect="light">
              {{ row.status === 'ACTIVE' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
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

  <el-drawer v-model="drawerVisible" :title="editingId ? '编辑读者' : '新增读者'" size="460px">
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="学号" prop="studentNo"><el-input ref="studentNoInput" v-model.trim="form.studentNo" placeholder="请输入学号" /></el-form-item>
      <el-form-item label="姓名" prop="name"><el-input v-model.trim="form.name" placeholder="请输入姓名" /></el-form-item>
      <el-form-item label="学院">
        <el-select v-model="form.college" clearable filterable placeholder="请选择学院">
          <el-option v-for="college in collegeOptions" :key="college" :label="college" :value="college" />
        </el-select>
      </el-form-item>
      <el-form-item label="电话" prop="phone"><el-input v-model.trim="form.phone" maxlength="11" placeholder="请输入 11 位手机号码" /></el-form-item>
      <el-form-item label="登录账号">
        <el-select
          v-model="form.userId"
          filterable
          remote
          clearable
          reserve-keyword
          placeholder="选择学生登录账号"
          :remote-method="searchUsers"
          :loading="userLoading"
        >
          <el-option
            v-for="user in userOptions"
            :key="user.id"
            :label="userLabel(user)"
            :value="user.id"
            :disabled="user.role !== 'STUDENT' || !user.enabled"
          />
        </el-select>
      </el-form-item>
      <el-form-item v-if="editingId" label="读者状态">
        <el-select v-model="form.status">
          <el-option label="正常" value="ACTIVE" />
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
