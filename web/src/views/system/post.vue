<template>
  <div class="system-page">
    <el-card>
      <template #header>
        <span>岗位管理</span>
      </template>

      <el-form :model="queryParams" :inline="true" class="search-form">
        <el-form-item label="岗位编码">
          <el-input v-model="queryParams.postCode" placeholder="请输入岗位编码" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="岗位名称">
          <el-input v-model="queryParams.postName" placeholder="请输入岗位名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="岗位状态" clearable>
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
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
      </div>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
      >
        <el-table-column prop="id" label="岗位编号" width="100" align="center" />
        <el-table-column prop="postCode" label="岗位编码" min-width="120" align="center" />
        <el-table-column prop="postName" label="岗位名称" min-width="120" align="center" />
        <el-table-column prop="orderNum" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
        <el-table-column label="操作" width="180" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      append-to-body
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" placeholder="请输入岗位编码" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { postApi, type SysPost } from '@/api/system/post'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref<SysPost[]>([])
const total = ref(0)
const formRef = ref<FormInstance>()

const queryParams = reactive({
  postCode: '',
  postName: '',
  status: '',
  pageNum: 1,
  pageSize: 10
})

const form = reactive<SysPost>({
  id: undefined,
  postCode: '',
  postName: '',
  orderNum: 0,
  status: '0',
  remark: ''
})

const rules = reactive<FormRules>({
  postName: [
    { required: true, message: '请输入岗位名称', trigger: 'blur' }
  ],
  postCode: [
    { required: true, message: '请输入岗位编码', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
})

const getList = async () => {
  loading.value = true
  try {
    const res = await postApi.list(queryParams)
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
  queryParams.postCode = ''
  queryParams.postName = ''
  queryParams.status = ''
  handleQuery()
}

const resetForm = () => {
  form.id = undefined
  form.postCode = ''
  form.postName = ''
  form.orderNum = 0
  form.status = '0'
  form.remark = ''
  formRef.value?.resetFields()
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增岗位'
  dialogVisible.value = true
}

const handleEdit = async (row: SysPost) => {
  resetForm()
  dialogTitle.value = '编辑岗位'
  try {
    const res = await postApi.getById(row.id!)
    Object.assign(form, res)
    dialogVisible.value = true
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
      await postApi.update(form)
      ElMessage.success('修改成功')
    } else {
      await postApi.create(form)
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

const handleDelete = async (row: SysPost) => {
  await ElMessageBox.confirm(`确认删除岗位"${row.postName}"吗？`, '提示', {
    type: 'warning'
  })
  try {
    await postApi.remove(row.id!)
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
.system-page {
  padding: 10px;
}

.search-form {
  margin-bottom: 16px;
}

.toolbar {
  margin-bottom: 16px;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
