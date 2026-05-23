<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Refresh } from '@element-plus/icons-vue'
import { getMyBorrows } from '../api/borrow'
import type { BorrowRecordVO } from '../types'

const route = useRoute()
const router = useRouter()
const rows = ref<BorrowRecordVO[]>([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, pageSize: 10 })
const overdueOnly = computed(() => route.query.status === 'OVERDUE')
const visibleRows = computed(() => overdueOnly.value ? rows.value.filter((row) => row.status === 'OVERDUE') : rows.value)

async function load() {
  loading.value = true
  try {
    const result = await getMyBorrows(query)
    rows.value = result.records
    total.value = result.total
  } finally {
    loading.value = false
  }
}

function showAll() {
  router.push('/my-borrows')
}

function showOverdue() {
  router.push('/my-borrows?status=OVERDUE')
}

onMounted(load)
watch(() => route.query.status, () => {
  query.page = 1
  load()
})
</script>

<template>
  <section class="page-head">
    <div>
      <h1 class="page-title">{{ overdueOnly ? '逾期状态查看' : '我的借阅' }}</h1>
      <p class="page-desc">查看本人借阅、归还和逾期状态。</p>
    </div>
    <div class="page-actions">
      <el-button :icon="Refresh" @click="load">刷新</el-button>
      <el-button :type="overdueOnly ? 'default' : 'primary'" @click="showAll">全部记录</el-button>
      <el-button :type="overdueOnly ? 'primary' : 'default'" @click="showOverdue">只看逾期</el-button>
    </div>
  </section>

  <section class="workspace">
    <div class="table-wrap">
      <el-table v-loading="loading" :data="visibleRows" border>
        <template #empty>
          <div class="empty-hint">{{ overdueOnly ? '当前没有逾期记录。' : '暂无借阅记录。' }}</div>
        </template>
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
      </el-table>
    </div>
    <div class="table-footer">
      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="load"
        @current-change="load"
      />
    </div>
  </section>
</template>
