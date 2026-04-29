<template>
  <div class="notice-page">
    <el-card>
      <template #header>
        <span>公告管理</span>
      </template>

      <el-form :model="queryParams" :inline="true" class="search-form">
        <el-form-item label="公告标题">
          <el-input v-model="queryParams.title" placeholder="请输入公告标题" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="公告类型">
          <el-select v-model="queryParams.type" placeholder="公告类型" clearable>
            <el-option label="通知" value="0" />
            <el-option label="公告" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间">
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

      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>新增
        </el-button>
        <el-button type="danger" :disabled="!multipleSelection.length" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>删除
        </el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
        border
        stripe
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column prop="id" label="序号" width="80" align="center" />
        <el-table-column prop="title" label="公告标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="type" label="公告类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.type === '0' ? 'primary' : 'success'">
              {{ row.type === '0' ? '通知' : '公告' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="创建者" width="120" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      append-to-body
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio value="0">通知</el-radio>
            <el-radio value="1">公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">关闭</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="viewDialogVisible"
      title="公告详情"
      width="700px"
      append-to-body
    >
      <div class="notice-detail">
        <h2 class="notice-title">{{ viewForm.title }}</h2>
        <div class="notice-meta">
          <span>类型：<el-tag :type="viewForm.type === '0' ? 'primary' : 'success'" size="small">{{ viewForm.type === '0' ? '通知' : '公告' }}</el-tag></span>
          <span>发布者：{{ viewForm.createBy }}</span>
          <span>发布时间：{{ viewForm.createTime }}</span>
        </div>
        <el-divider />
        <div class="notice-content">{{ viewForm.content }}</div>
        <div v-if="viewForm.remark" class="notice-remark">
          <el-divider />
          <p><strong>备注：</strong>{{ viewForm.remark }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { noticeApi, type SysNotice } from '@/api/system/notice'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const viewDialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref<SysNotice[]>([])
const multipleSelection = ref<SysNotice[]>([])
const total = ref(0)
const formRef = ref<FormInstance>()
const dateRange = ref<string[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  type: '',
  beginTime: '',
  endTime: ''
})

const form = reactive<SysNotice>({
  id: undefined,
  title: '',
  type: '0',
  content: '',
  status: '0',
  remark: ''
})

const viewForm = reactive<SysNotice>({
  id: undefined,
  title: '',
  type: '0',
  content: '',
  status: '0',
  createBy: '',
  createTime: '',
  remark: ''
})

const rules = reactive<FormRules>({
  title: [
    { required: true, message: '请输入公告标题', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择公告类型', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入公告内容', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
})

const getList = async () => {
  loading.value = true
  try {
    if (dateRange.value && dateRange.value.length === 2) {
      queryParams.beginTime = dateRange.value[0]
      queryParams.endTime = dateRange.value[1]
    } else {
      queryParams.beginTime = ''
      queryParams.endTime = ''
    }
    const res = await noticeApi.list(queryParams)
    tableData.value = res.records || []
    total.value = res.total || 0
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
  queryParams.title = ''
  queryParams.type = ''
  dateRange.value = []
  handleQuery()
}

const handleSelectionChange = (selection: SysNotice[]) => {
  multipleSelection.value = selection
}

const resetForm = () => {
  form.id = undefined
  form.title = ''
  form.type = '0'
  form.content = ''
  form.status = '0'
  form.remark = ''
  formRef.value?.resetFields()
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增公告'
  dialogVisible.value = true
}

const handleEdit = async (row: SysNotice) => {
  resetForm()
  dialogTitle.value = '编辑公告'
  try {
    const res = await noticeApi.getById(row.id!)
    Object.assign(form, res)
    dialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleView = async (row: SysNotice) => {
  try {
    const res = await noticeApi.getById(row.id!)
    Object.assign(viewForm, res)
    viewDialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const submitForm = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (form.id) {
      await noticeApi.update(form)
      ElMessage.success('修改成功')
    } else {
      await noticeApi.create(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: SysNotice) => {
  await ElMessageBox.confirm(`确认删除公告"${row.title}"吗？`, '提示', {
    type: 'warning'
  })
  try {
    await noticeApi.remove(String(row.id))
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error(error)
  }
}

const handleBatchDelete = async () => {
  const ids = multipleSelection.value.map(item => item.id).join(',')
  await ElMessageBox.confirm('确认删除选中的公告吗？', '提示', {
    type: 'warning'
  })
  try {
    await noticeApi.remove(ids)
    ElMessage.success('删除成功')
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
.notice-page {
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

.notice-detail {
  .notice-title {
    text-align: center;
    margin-bottom: 16px;
    font-size: 20px;
  }

  .notice-meta {
    display: flex;
    justify-content: center;
    gap: 20px;
    color: #909399;
    font-size: 14px;
  }

  .notice-content {
    line-height: 1.8;
    white-space: pre-wrap;
    word-break: break-all;
  }

  .notice-remark {
    color: #909399;
    font-size: 14px;
  }
}
</style>
