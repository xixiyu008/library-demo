<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import { createUser, deleteUser, getUsers, updateUser } from '../api/user'
import type { Role, UserVO } from '../types'

const rows = ref<UserVO[]>([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const drawerVisible = ref(false)
const editingId = ref<number>()
const formRef = ref<FormInstance>()
const usernameInput = ref()
const query = reactive({ page: 1, pageSize: 10, keyword: '' })
const form = reactive<{ username: string; password: string; role: Role; enabled: boolean }>({
  username: '',
  password: '',
  role: 'STUDENT',
  enabled: true
})
const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }],
  password: [{
    validator: (_rule, value, callback) => {
      if (!editingId.value && !value) callback(new Error('请输入初始密码'))
      else callback()
    },
    trigger: 'blur'
  }]
}

async function load() {
  loading.value = true
  try {
    const result = await getUsers(query)
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
  query.page = 1
  load()
}

function focusFirstField() {
  nextTick(() => usernameInput.value?.focus())
}

function openCreate() {
  editingId.value = undefined
  Object.assign(form, { username: '', password: '', role: 'STUDENT', enabled: true })
  drawerVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
  focusFirstField()
}

function openEdit(row: UserVO) {
  editingId.value = row.id
  Object.assign(form, { username: row.username, password: '', role: row.role, enabled: row.enabled })
  drawerVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
  focusFirstField()
}

async function save() {
  await formRef.value?.validate()
  saving.value = true
  try {
    if (editingId.value) {
      await updateUser(editingId.value, { password: form.password || undefined, role: form.role, enabled: form.enabled })
    } else {
      await createUser(form)
    }
    ElMessage.success('保存成功')
    drawerVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function remove(row: UserVO) {
  try {
    await ElMessageBox.confirm(`删除用户「${row.username}」后无法恢复，确认继续？`, '删除用户', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<template>
  <section class="page-head">
    <div>
      <h1 class="page-title">用户管理</h1>
      <p class="page-desc">管理后台登录账号、角色权限和启用状态。</p>
    </div>
    <div class="page-actions">
      <el-button :icon="Refresh" @click="resetSearch">重置</el-button>
      <el-button type="primary" :icon="Plus" @click="openCreate">新增用户</el-button>
    </div>
  </section>

  <section class="workspace">
    <div class="filter-bar">
      <el-input v-model="query.keyword" placeholder="搜索用户名" clearable @keyup.enter="search" />
      <el-button :icon="Search" @click="search">查询</el-button>
    </div>
    <div class="table-wrap">
      <el-table v-loading="loading" :data="rows" border>
        <template #empty>
          <div class="empty-hint">暂无用户，调整搜索条件或新增用户。</div>
        </template>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="160" />
        <el-table-column prop="role" label="角色" width="140">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : row.role === 'LIBRARIAN' ? 'warning' : 'info'" effect="light">
              {{ row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="启用" width="110">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'" effect="light">{{ row.enabled ? '启用' : '停用' }}</el-tag>
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

  <el-drawer v-model="drawerVisible" :title="editingId ? '编辑用户' : '新增用户'" size="420px">
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="用户名" prop="username">
        <el-input ref="usernameInput" v-model.trim="form.username" :disabled="!!editingId" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item :label="editingId ? '新密码' : '初始密码'" prop="password">
        <el-input v-model="form.password" type="password" show-password :placeholder="editingId ? '留空则不修改密码' : '请输入初始密码'" />
      </el-form-item>
      <el-form-item label="角色" prop="role">
        <el-select v-model="form.role" placeholder="请选择角色">
          <el-option label="系统管理员" value="ADMIN" />
          <el-option label="图书管理员" value="LIBRARIAN" />
          <el-option label="学生读者" value="STUDENT" />
        </el-select>
      </el-form-item>
      <el-form-item label="账号状态">
        <el-switch v-model="form.enabled" active-text="启用" inactive-text="停用" />
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
