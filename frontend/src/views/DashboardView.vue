<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Notebook, Reading, Tickets, User } from '@element-plus/icons-vue'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()
const router = useRouter()

const roleName = computed(() => {
  if (auth.role === 'ADMIN') return '系统管理员'
  if (auth.role === 'LIBRARIAN') return '图书管理员'
  return '学生读者'
})

const actions = computed(() => {
  if (auth.role === 'ADMIN') {
    return [
      { label: '维护系统账号', desc: '新增账号、调整角色、停用异常账号', path: '/users', icon: User },
      { label: '管理图书馆藏', desc: '维护图书信息、库存和可借状态', path: '/books', icon: Notebook },
      { label: '维护读者档案', desc: '绑定学生账号并管理借阅资格', path: '/readers', icon: Reading },
      { label: '办理借还书', desc: '搜索读者和图书完成借阅流转', path: '/borrows', icon: Tickets }
    ]
  }
  if (auth.role === 'LIBRARIAN') {
    return [
      { label: '管理图书馆藏', desc: '维护图书信息、库存和可借状态', path: '/books', icon: Notebook },
      { label: '维护读者档案', desc: '绑定学生账号并管理借阅资格', path: '/readers', icon: Reading },
      { label: '办理借还书', desc: '搜索读者和图书完成借阅流转', path: '/borrows', icon: Tickets }
    ]
  }
  return [
    { label: '查询图书', desc: '按书名、ISBN 或作者检索馆藏', path: '/books', icon: Notebook },
    { label: '查看我的借阅', desc: '查看当前借阅、归还和逾期状态', path: '/my-borrows', icon: Tickets }
  ]
})
</script>

<template>
  <section class="page-head">
    <div>
      <h1 class="page-title">工作台</h1>
      <p class="page-desc">{{ auth.username }}，当前以{{ roleName }}身份使用系统。</p>
    </div>
  </section>

  <div class="metric-strip">
    <div class="metric-item">
      <div class="metric-label">当前角色</div>
      <div class="metric-value">{{ auth.role }}</div>
    </div>
    <div class="metric-item">
      <div class="metric-label">可用模块</div>
      <div class="metric-value">{{ actions.length }}</div>
    </div>
    <div class="metric-item">
      <div class="metric-label">账号状态</div>
      <div class="metric-value">已登录</div>
    </div>
    <div class="metric-item">
      <div class="metric-label">操作入口</div>
      <div class="metric-value">快速访问</div>
    </div>
  </div>

  <section class="workspace">
    <div class="filter-bar">
      <strong>常用工作</strong>
      <span class="muted">选择一个入口开始处理业务</span>
    </div>
    <el-table :data="actions" border>
      <el-table-column label="模块" min-width="180">
        <template #default="{ row }">
          <span class="status-dot">
            <el-icon><component :is="row.icon" /></el-icon>
            {{ row.label }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="desc" label="说明" min-width="260" />
      <el-table-column label="操作" width="130" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="router.push(row.path)">进入</el-button>
        </template>
      </el-table-column>
    </el-table>
  </section>
</template>
