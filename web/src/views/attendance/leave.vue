<template>
  <div class="leave-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>请假申请</span>
          <el-button type="primary" @click="handleApply">新建申请</el-button>
        </div>
      </template>

      <el-table :data="leaveList" stripe>
        <el-table-column prop="leaveType" label="请假类型" width="100" />
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column prop="duration" label="时长(天)" width="100" />
        <el-table-column prop="reason" label="请假原因" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
const leaveList = ref([
  { leaveType: '年假', startTime: '2024-01-20', endTime: '2024-01-22', duration: 3, reason: '春节回家', status: 1 },
  { leaveType: '病假', startTime: '2024-01-10', endTime: '2024-01-11', duration: 2, reason: '感冒发烧', status: 2 },
])

const getStatusType = (status: number) => {
  const map: Record<number, string> = { 0: 'info', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status: number) => {
  const map: Record<number, string> = { 0: '待审批', 1: '已通过', 2: '已驳回' }
  return map[status] || '未知'
}

const handleApply = () => {
  // TODO: 打开申请表单
}
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
