<template>
  <div class="system-page">
    <el-card>
      <template #header>
        <span>已办任务</span>
      </template>

      <el-form :model="queryParams" :inline="true" class="search-form">
        <el-form-item label="流程名称">
          <el-input v-model="queryParams.processName" placeholder="请输入流程名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="处理时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
      >
        <el-table-column prop="id" label="任务编号" width="100" align="center" />
        <el-table-column prop="processName" label="流程名称" min-width="150" align="center" />
        <el-table-column prop="nodeName" label="审批节点" width="120" align="center" />
        <el-table-column prop="发起人" label="发起人" width="100" align="center" />
        <el-table-column prop="handleTime" label="处理时间" width="180" align="center" />
        <el-table-column prop="result" label="审批结果" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.result === 'agree' ? 'success' : 'danger'">
              {{ row.result === 'agree' ? '同意' : '驳回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="审批意见" min-width="150" align="center" show-overflow-tooltip />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="getList"
          @current-change="getList"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="detailDialogVisible"
      title="任务详情"
      width="700px"
      append-to-body
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="流程名称">{{ detailData.processName }}</el-descriptions-item>
        <el-descriptions-item label="发起人">{{ detailData.发起人 }}</el-descriptions-item>
        <el-descriptions-item label="审批节点">{{ detailData.nodeName }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ detailData.handleTime }}</el-descriptions-item>
        <el-descriptions-item label="审批结果">
          <el-tag :type="detailData.result === 'agree' ? 'success' : 'danger'">
            {{ detailData.result === 'agree' ? '同意' : '驳回' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ detailData.comment }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

interface DoneTaskRecord {
  id: number
  processName: string
  nodeName: string
  发起人: string
  handleTime: string
  result: string
  comment: string
}

const loading = ref(false)
const detailDialogVisible = ref(false)
const tableData = ref<DoneTaskRecord[]>([])
const total = ref(0)
const dateRange = ref<string[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  processName: '',
  startDate: '',
  endDate: ''
})

const detailData = reactive<DoneTaskRecord>({
  id: 0,
  processName: '',
  nodeName: '',
  发起人: '',
  handleTime: '',
  result: '',
  comment: ''
})

const getList = async () => {
  loading.value = true
  try {
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.startDate = dateRange.value[0]
      queryParams.endDate = dateRange.value[1]
    } else {
      queryParams.startDate = ''
      queryParams.endDate = ''
    }
    tableData.value = [
      { id: 1, processName: '病假申请', nodeName: '部门经理审批', 发起人: '赵六', handleTime: '2026-04-28 11:30:00', result: 'agree', comment: '同意请假' },
      { id: 2, processName: '设备采购', nodeName: '总经理审批', 发起人: '孙七', handleTime: '2026-04-27 16:00:00', result: 'reject', comment: '预算超支，需重新评估' },
      { id: 3, processName: '项目立项', nodeName: '部门经理审批', 发起人: '周八', handleTime: '2026-04-26 10:45:00', result: 'agree', comment: '同意立项' }
    ]
    total.value = 3
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.processName = ''
  dateRange.value = []
  handleQuery()
}

const handleDetail = (row: DoneTaskRecord) => {
  Object.assign(detailData, row)
  detailDialogVisible.value = true
}

onMounted(() => {
  getList()
})
</script>

<style scoped lang="scss">
.system-page {
  padding: 10px;
}

.search-form {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
