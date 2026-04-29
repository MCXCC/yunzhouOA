<template>
  <div class="system-page">
    <el-card>
      <template #header>
        <span>我的待办</span>
      </template>

      <el-form :model="queryParams" :inline="true" class="search-form">
        <el-form-item label="流程名称">
          <el-input v-model="queryParams.processName" placeholder="请输入流程名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="任务时间">
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
        <el-table-column prop="nodeName" label="当前节点" width="120" align="center" />
        <el-table-column prop="发起人" label="发起人" width="100" align="center" />
        <el-table-column prop="createTime" label="任务时间" width="180" align="center" />
        <el-table-column prop="priority" label="优先级" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.priority)">
              {{ getPriorityText(row.priority) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button type="success" link @click="handleApprove(row)">办理</el-button>
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
      v-model="approveDialogVisible"
      title="审批办理"
      width="500px"
      append-to-body
      @close="resetApproveForm"
    >
      <el-form ref="approveFormRef" :model="approveForm" :rules="approveRules" label-width="100px">
        <el-form-item label="审批结果" prop="result">
          <el-radio-group v-model="approveForm.result">
            <el-radio value="agree">同意</el-radio>
            <el-radio value="reject">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见" prop="comment">
          <el-input v-model="approveForm.comment" type="textarea" :rows="4" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitApprove">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'

interface TaskRecord {
  id: number
  processName: string
  nodeName: string
  发起人: string
  createTime: string
  priority: string
}

const loading = ref(false)
const submitLoading = ref(false)
const approveDialogVisible = ref(false)
const tableData = ref<TaskRecord[]>([])
const total = ref(0)
const dateRange = ref<string[]>([])
const approveFormRef = ref<FormInstance>()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  processName: '',
  startDate: '',
  endDate: ''
})

const approveForm = reactive({
  result: 'agree',
  comment: ''
})

const approveRules = reactive<FormRules>({
  result: [
    { required: true, message: '请选择审批结果', trigger: 'change' }
  ],
  comment: [
    { required: true, message: '请输入审批意见', trigger: 'blur' }
  ]
})

const getPriorityType = (priority: string) => {
  const map: Record<string, string> = {
    'high': 'danger',
    'medium': 'warning',
    'low': 'info'
  }
  return map[priority] || 'info'
}

const getPriorityText = (priority: string) => {
  const map: Record<string, string> = {
    'high': '高',
    'medium': '中',
    'low': '低'
  }
  return map[priority] || '未知'
}

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
      { id: 1, processName: '年假申请', nodeName: '部门经理审批', 发起人: '张三', createTime: '2026-04-28 10:00:00', priority: 'high' },
      { id: 2, processName: '差旅报销', nodeName: '财务审批', 发起人: '李四', createTime: '2026-04-27 14:30:00', priority: 'medium' },
      { id: 3, processName: '采购申请', nodeName: '总经理审批', 发起人: '王五', createTime: '2026-04-26 09:15:00', priority: 'low' }
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

const handleDetail = (row: TaskRecord) => {
  ElMessage.info(`查看任务详情: ${row.processName}`)
}

let currentTask: TaskRecord | null = null

const handleApprove = (row: TaskRecord) => {
  currentTask = row
  approveDialogVisible.value = true
}

const resetApproveForm = () => {
  approveForm.result = 'agree'
  approveForm.comment = ''
  approveFormRef.value?.resetFields()
  currentTask = null
}

const submitApprove = async () => {
  const valid = await approveFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    ElMessage.success('审批提交成功')
    approveDialogVisible.value = false
    getList()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
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
