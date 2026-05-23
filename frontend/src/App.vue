<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { House, Notebook, Reading, SwitchButton, Tickets, User } from '@element-plus/icons-vue'
import { logout as logoutApi } from './api/auth'
import { useAuthStore } from './stores/auth'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const isLogin = computed(() => route.path === '/login')

const menus = computed(() => [
  { path: '/', label: '工作台', icon: House, visible: true },
  { path: '/users', label: '用户管理', icon: User, visible: auth.role === 'ADMIN' },
  { path: '/books', label: auth.role === 'STUDENT' ? '图书查询' : '图书管理', icon: Notebook, visible: true },
  { path: '/readers', label: '读者管理', icon: Reading, visible: auth.role === 'ADMIN' || auth.role === 'LIBRARIAN' },
  { path: '/borrows', label: '借阅办理', icon: Tickets, visible: auth.role === 'ADMIN' || auth.role === 'LIBRARIAN' },
  { path: '/my-borrows', label: '我的借阅', icon: Tickets, visible: auth.role === 'STUDENT' }
].filter((item) => item.visible))

const currentTitle = computed(() => menus.value.find((item) => item.path === route.path)?.label || '大学图书管理系统')
const roleLabel = computed(() => {
  if (auth.role === 'ADMIN') return '系统管理员'
  if (auth.role === 'LIBRARIAN') return '图书管理员'
  if (auth.role === 'STUDENT') return '学生读者'
  return '未登录'
})
const initials = computed(() => (auth.username || 'U').slice(0, 1).toUpperCase())

async function handleLogout() {
  try {
    await ElMessageBox.confirm('退出后需要重新登录，确认退出当前账号？', '退出登录', {
      confirmButtonText: '退出',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  try {
    await logoutApi()
  } finally {
    auth.logoutLocal()
    router.push('/login')
  }
}
</script>

<template>
  <router-view v-if="isLogin" />
  <el-container v-else class="app-shell">
    <el-aside width="240px" class="sidebar">
      <div class="brand">
        <div class="brand-mark">书</div>
        <div class="brand-name">
          <div class="brand-title">大学图书管理系统</div>
          <div class="brand-subtitle">Library Admin</div>
        </div>
      </div>
      <el-menu class="app-menu" :default-active="$route.path" router>
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="topbar">
        <div>
          <div class="topbar-title">{{ currentTitle }}</div>
          <div class="topbar-meta">当前身份：{{ roleLabel }}</div>
        </div>
        <div class="topbar-actions">
          <div class="user-chip">
            <div class="user-avatar">{{ initials }}</div>
            <div>
              <div>{{ auth.username }}</div>
              <div class="topbar-meta">{{ auth.role }}</div>
            </div>
          </div>
          <el-button :icon="SwitchButton" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
