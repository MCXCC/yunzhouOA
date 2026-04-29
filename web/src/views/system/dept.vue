<template>
  <div class="system-page">
    <el-card>
      <template #header>
        <span>部门管理</span>
      </template>

      <el-form :model="queryParams" :inline="true" class="search-form">
        <el-form-item label="部门名称">
          <el-input v-model="queryParams.deptName" placeholder="请输入部门名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="部门状态" clearable>
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
        <el-button @click="toggleExpandAll">
          <el-icon><Sort /></el-icon>{{ isExpandAll ? '折叠' : '展开' }}
        </el-button>
      </div>

      <el-table
        v-if="refreshTable"
        v-loading="loading"
        :data="tableData"
        row-key="id"
        :default-expand-all="isExpandAll"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
        stripe
      >
        <el-table-column prop="deptName" label="部门名称" min-width="200" />
        <el-table-column prop="orderNum" label="排序" width="100" align="center" />
        <el-table-column prop="leader" label="负责人" width="120" align="center" />
        <el-table-column prop="phone" label="联系电话" width="130" align="center" />
        <el-table-column prop="email" label="邮箱" min-width="180" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleAdd(row)">新增</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      append-to-body
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="上级部门" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="deptOptions"
                :props="{ label: 'deptName', value: 'id', children: 'children' }"
                placeholder="请选择上级部门"
                check-strictly
                clearable
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="form.deptName" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示顺序" prop="orderNum">
              <el-input-number v-model="form.orderNum" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio value="0">正常</el-radio>
                <el-radio value="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Sort } from '@element-plus/icons-vue'
import { deptApi, type SysDept } from '@/api/system/dept'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref<SysDept[]>([])
const deptOptions = ref<SysDept[]>([])
const isExpandAll = ref(true)
const refreshTable = ref(true)
const formRef = ref<FormInstance>()

const queryParams = reactive({
  deptName: '',
  status: ''
})

const form = reactive<SysDept>({
  id: undefined,
  parentId: undefined,
  deptName: '',
  orderNum: 0,
  leader: '',
  phone: '',
  email: '',
  status: '0'
})

const rules = reactive<FormRules>({
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
})

const getList = async () => {
  loading.value = true
  try {
    const res = await deptApi.list(queryParams)
    tableData.value = res || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getDeptOptions = async () => {
  try {
    const res = await deptApi.list({})
    deptOptions.value = res || []
  } catch (error) {
    console.error(error)
  }
}

const handleQuery = () => {
  getList()
}

const resetQuery = () => {
  queryParams.deptName = ''
  queryParams.status = ''
  handleQuery()
}

const toggleExpandAll = async () => {
  refreshTable.value = false
  isExpandAll.value = !isExpandAll.value
  await nextTick()
  refreshTable.value = true
}

const resetForm = () => {
  form.id = undefined
  form.parentId = undefined
  form.deptName = ''
  form.orderNum = 0
  form.leader = ''
  form.phone = ''
  form.email = ''
  form.status = '0'
  formRef.value?.resetFields()
}

const handleAdd = (row?: SysDept) => {
  resetForm()
  dialogTitle.value = '新增部门'
  if (row?.id) {
    form.parentId = row.id
  }
  dialogVisible.value = true
}

const handleEdit = async (row: SysDept) => {
  resetForm()
  dialogTitle.value = '编辑部门'
  try {
    const res = await deptApi.getById(row.id!)
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
      await deptApi.update(form)
      ElMessage.success('修改成功')
    } else {
      await deptApi.create(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
    getDeptOptions()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: SysDept) => {
  await ElMessageBox.confirm(`确认删除部门"${row.deptName}"吗？`, '提示', {
    type: 'warning'
  })
  try {
    await deptApi.remove(row.id!)
    ElMessage.success('删除成功')
    getList()
    getDeptOptions()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  getList()
  getDeptOptions()
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
</style>
