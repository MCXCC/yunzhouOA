<template>
  <div class="attendance-record">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>打卡记录</span>
          <el-button type="primary" @click="handleCheckIn">
            <el-icon><Clock /></el-icon>
            立即打卡
          </el-button>
        </div>
      </template>

      <el-form :inline="true" class="search-form">
        <el-form-item label="日期">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select placeholder="请选择" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="迟到" :value="1" />
            <el-option label="早退" :value="2" />
            <el-option label="缺勤" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary">查询</el-button>
          <el-button>重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="records" stripe>
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="checkInTime" label="上班打卡" width="180" />
        <el-table-column prop="checkOutTime" label="下班打卡" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { Clock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const dateRange = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 100,
})

const records = ref([
  { date: '2024-01-15', checkInTime: '08:55:32', checkOutTime: '18:05:21', status: 0, remark: '' },
  { date: '2024-01-14', checkInTime: '09:15:22', checkOutTime: '18:00:11', status: 1, remark: '迟到15分钟' },
  { date: '2024-01-13', checkInTime: '08:48:55', checkOutTime: '17:30:00', status: 2, remark: '早退30分钟' },
])

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 0: 'success', 1: 'warning', 2: 'warning', 3: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = { 0: '正常', 1: '迟到', 2: '早退', 3: '缺勤' }
  return map[status] || '未知'
}

const handleCheckIn = () => {
  ElMessage.success('打卡成功')
}
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 16px;
}
</style>
