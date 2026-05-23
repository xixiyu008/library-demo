import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue'
import UserListView from '../views/UserListView.vue'
import BookListView from '../views/BookListView.vue'
import ReaderListView from '../views/ReaderListView.vue'
import BorrowRecordView from '../views/BorrowRecordView.vue'
import MyBorrowView from '../views/MyBorrowView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: LoginView },
    { path: '/', component: DashboardView },
    { path: '/users', component: UserListView },
    { path: '/books', component: BookListView },
    { path: '/readers', component: ReaderListView },
    { path: '/borrows', component: BorrowRecordView },
    { path: '/my-borrows', component: MyBorrowView },
    { path: '/:pathMatch(.*)*', redirect: '/' }
  ]
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.path !== '/login' && !auth.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.path === '/login' && auth.token) {
    return '/'
  }
  if (to.path === '/users' && auth.role !== 'ADMIN') {
    return '/'
  }
  if ((to.path === '/readers' || to.path === '/borrows') && auth.role === 'STUDENT') {
    return '/'
  }
  if (to.path === '/my-borrows' && auth.role !== 'STUDENT') {
    return '/'
  }
  return true
})

export default router
