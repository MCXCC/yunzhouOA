<template>
  <div class="system-page">
    <el-card>
      <template #header>
        <span>我的申请</span>
      </template>

      <el-form :model="queryParams" :inline="true" class="search-form">
        <el-form-item label="流程名称">
          <el-input v-model="queryParams.processName" placeholder="请输入流程名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="申请时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="审批中" value="0" />
            <el-option label="已通过" value="1" />
            <el-option label="已驳回" value="2" />
            <el-option label="已撤销" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">搜索</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" @click="handleApply">
          <el-icon><Plus /></el-icon>发起申请
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
      >
        <el-table-column prop="id" label="申请编号" width="100" align="center" />
        <el-table-column prop="processName" label="流程名称" min-width="150" align="center" />
        <el-table-column prop="processType" label="流程类型" width="120" align="center" />
        <el-table-column prop="applyTime" label="申请时间" width="180" align="center" />
        <el-table-column prop="currentNode" label="当前节点" width="120" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === '0'" type="danger" link @click="handleRevoke(row)">撤销</el-button>
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
      v-model="applyDialogVisible"
      title="发起申请"
      width="600px"
      append-to-body
      @close="resetApplyForm"
    >
      <el-form ref="applyFormRef" :model="applyForm" :rules="applyRules" label-width="100px">
        <el-form-item label="流程类型" prop="processType">
          <el-select v-model="applyForm.processType" placeholder="请选择流程类型" style="width: 100%">
            <el-option label="请假申请" value="leave" />
            <el-option label="报销申请" value="reimbursement" />
            <el-option label="出差申请" value="business" />
            <el-option label="加班申请" value="overtime" />
            <el-option label="采购申请" value="purchase" />
            <el-option label="其他申请" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请标题" prop="title">
          <el-input v-model="applyForm.title" placeholder="请输入申请标题" />
        </el-form-item>
        <el-form-item label="申请说明" prop="description">
          <el-input v-model="applyForm.description" type="textarea" :rows="4" placeholder="请输入申请说明" />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            action="#"
            :auto-upload="false"
            :limit="5"
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>选择文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">最多上传5个文件</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitApply">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Upload } from '@element-plus/icons-vue'

interface ApplyRecord {
  id: number
  processName: string
  processType: string
  applyTime: string
  currentNode: string
  status: string
}

const loading = ref(false)
const submitLoading = ref(false)
const applyDialogVisible = ref(false)
const tableData = ref<ApplyRecord[]>([])
const total = ref(0)
const dateRange = ref<string[]>([])
const applyFormRef = ref<FormInstance>()

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  processName: '',
  status: '',
  startDate: '',
  endDate: ''
})

const applyForm = reactive({
  processType: '',
  title: '',
  description: ''
})

const applyRules = reactive<FormRules>({
  processType: [
    { required: true, message: '请选择流程类型', trigger: 'change' }
  ],
  title: [
    { required: true, message: '请输入申请标题', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入申请说明', trigger: 'blur' }
  ]
})

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    '0': 'warning',
    '1': 'success',
    '2': 'danger',
    '3': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    '0': '审批中',
    '1': '已通过',
    '2': '已驳回',
    '3': '已撤销'
  }
  return map[status] || '未知'
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
      { id: 1, processName: '年假申请', processType: '请假申请', applyTime: '2026-04-28 10:00:00', currentNode: '部门经理审批', status: '0' },
      { id: 2, processName: '差旅报销', processType: '报销申请', applyTime: '2026-04-27 14:30:00', currentNode: '-', status: '1' },
      { id: 3, processName: '加班申请', processType: '加班申请', applyTime: '2026-04-26 09:15:00', currentNode: '-', status: '2' }
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
  queryParams.status = ''
  dateRange.value = []
  handleQuery()
}

const handleApply = () => {
  applyDialogVisible.value = true
}

const resetApplyForm = () => {
  applyForm.processType = ''
  applyForm.title = ''
  applyForm.description = ''
  applyFormRef.value?.resetFields()
}

const submitApply = async () => {
  const valid = await applyFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    ElMessage.success('申请提交成功')
    applyDialogVisible.value = false
    getList()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleDetail = (row: ApplyRecord) => {
  ElMessage.info(`查看申请详情: ${row.processName}`)
}

const handleRevoke = async (row: ApplyRecord) => {
  await ElMessageBox.confirm('确认撤销该申请吗？', '提示', {
    type: 'warning'
  })
  try {
    ElMessage.success('撤销成功')
    getList()
  } catch (error) {
    console.error(error)
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

.toolbar {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
