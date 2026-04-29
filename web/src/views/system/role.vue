<template>
  <div class="system-page">
    <el-card>
      <template #header>
        <span>角色管理</span>
      </template>

      <el-form :model="queryParams" :inline="true" class="search-form">
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="角色状态" clearable>
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px"
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
        <el-table-column prop="id" label="角色编号" width="100" align="center" />
        <el-table-column prop="roleName" label="角色名称" width="150" align="center" />
        <el-table-column prop="roleKey" label="权限字符" width="150" align="center" />
        <el-table-column prop="orderNum" label="显示顺序" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
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
      width="600px"
      append-to-body
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="角色名称" prop="roleName">
              <el-input v-model="form.roleName" placeholder="请输入角色名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限字符" prop="roleKey">
              <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
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
          <el-col :span="24">
            <el-form-item label="菜单权限" prop="menuIds">
              <el-tree
                ref="menuTreeRef"
                :data="menuOptions"
                :props="{ label: 'menuName', children: 'children' }"
                show-checkbox
                node-key="id"
                empty-text="加载中..."
                class="menu-tree"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules, ElTree } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import { roleApi, type SysRole } from '@/api/system/role'
import { menuApi, type SysMenu } from '@/api/system/menu'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref<SysRole[]>([])
const menuOptions = ref<SysMenu[]>([])
const multipleSelection = ref<SysRole[]>([])
const total = ref(0)
const formRef = ref<FormInstance>()
const menuTreeRef = ref<InstanceType<typeof ElTree>>()
const dateRange = ref<string[]>([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  status: ''
})

const form = reactive<SysRole>({
  id: undefined,
  roleName: '',
  roleKey: '',
  orderNum: 0,
  status: '0',
  remark: '',
  menuIds: []
})

const rules = reactive<FormRules>({
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  roleKey: [
    { required: true, message: '请输入权限字符', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' }
  ]
})

const getList = async () => {
  loading.value = true
  try {
    const params = {
      ...queryParams,
      beginTime: dateRange.value?.[0] || undefined,
      endTime: dateRange.value?.[1] || undefined
    }
    const res = await roleApi.list(params)
    tableData.value = res.rows || []
    total.value = res.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getMenuOptions = async () => {
  try {
    const res = await menuApi.getRoleMenus()
    menuOptions.value = res || []
  } catch (error) {
    console.error(error)
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.roleName = ''
  queryParams.status = ''
  dateRange.value = []
  handleQuery()
}

const handleSelectionChange = (selection: SysRole[]) => {
  multipleSelection.value = selection
}

const resetForm = () => {
  form.id = undefined
  form.roleName = ''
  form.roleKey = ''
  form.orderNum = 0
  form.status = '0'
  form.remark = ''
  form.menuIds = []
  menuTreeRef.value?.setCheckedKeys([])
  formRef.value?.resetFields()
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增角色'
  dialogVisible.value = true
}

const handleEdit = async (row: SysRole) => {
  resetForm()
  dialogTitle.value = '编辑角色'
  try {
    const res = await roleApi.getById(row.id!)
    Object.assign(form, res)
    const menuIds = await roleApi.getMenuIds(row.id!)
    form.menuIds = menuIds || []
    menuTreeRef.value?.setCheckedKeys(form.menuIds)
    dialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const submitForm = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const checkedKeys = menuTreeRef.value?.getCheckedKeys() || []
  const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
  form.menuIds = [...checkedKeys, ...halfCheckedKeys] as number[]

  submitLoading.value = true
  try {
    if (form.id) {
      await roleApi.update(form)
      ElMessage.success('修改成功')
    } else {
      await roleApi.create(form)
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

const handleDelete = async (row: SysRole) => {
  await ElMessageBox.confirm(`确认删除角色"${row.roleName}"吗？`, '提示', {
    type: 'warning'
  })
  try {
    await roleApi.remove(String(row.id))
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error(error)
  }
}

const handleBatchDelete = async () => {
  const ids = multipleSelection.value.map(item => item.id).join(',')
  await ElMessageBox.confirm('确认删除选中的角色吗？', '提示', {
    type: 'warning'
  })
  try {
    await roleApi.remove(ids)
    ElMessage.success('删除成功')
    getList()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  getList()
  getMenuOptions()
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

.menu-tree {
  max-height: 300px;
  overflow-y: auto;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;
  width: 100%;
}
</style>
