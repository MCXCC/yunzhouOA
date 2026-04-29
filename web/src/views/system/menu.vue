<template>
  <div class="system-page">
    <el-card>
      <template #header>
        <span>菜单管理</span>
      </template>

      <el-form :model="queryParams" :inline="true" class="search-form">
        <el-form-item label="菜单名称">
          <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="菜单状态" clearable>
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
        <el-table-column prop="menuName" label="菜单名称" min-width="160" />
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="{ row }">
            <el-icon v-if="row.icon">
              <component :is="row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="80" align="center" />
        <el-table-column prop="perms" label="权限标识" min-width="150" align="center" />
        <el-table-column prop="component" label="组件路径" min-width="150" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '0' ? 'success' : 'danger'">
              {{ row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
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
      width="700px"
      append-to-body
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="上级菜单" prop="parentId">
              <el-tree-select
                v-model="form.parentId"
                :data="menuOptions"
                :props="{ label: 'menuName', value: 'id', children: 'children' }"
                value-key="id"
                placeholder="选择上级菜单"
                check-strictly
                clearable
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio value="M">目录</el-radio>
                <el-radio value="C">菜单</el-radio>
                <el-radio value="F">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图标" prop="icon">
              <el-input v-model="form.icon" placeholder="请输入图标名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="orderNum">
              <el-input-number v-model="form.orderNum" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType !== 'F'" :span="12">
            <el-form-item label="路由地址" prop="path">
              <el-input v-model="form.path" placeholder="请输入路由地址" />
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType === 'C'" :span="12">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="form.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType !== 'M'" :span="12">
            <el-form-item label="权限标识" prop="perms">
              <el-input v-model="form.perms" placeholder="请输入权限标识" />
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType !== 'F'" :span="12">
            <el-form-item label="是否外链" prop="isFrame">
              <el-radio-group v-model="form.isFrame">
                <el-radio value="0">是</el-radio>
                <el-radio value="1">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType === 'C'" :span="12">
            <el-form-item label="是否缓存" prop="isCache">
              <el-radio-group v-model="form.isCache">
                <el-radio value="0">缓存</el-radio>
                <el-radio value="1">不缓存</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col v-if="form.menuType !== 'F'" :span="12">
            <el-form-item label="显示状态" prop="visible">
              <el-radio-group v-model="form.visible">
                <el-radio value="0">显示</el-radio>
                <el-radio value="1">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单状态" prop="status">
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
import { menuApi, type SysMenu } from '@/api/system/menu'

const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const tableData = ref<SysMenu[]>([])
const menuOptions = ref<SysMenu[]>([])
const isExpandAll = ref(true)
const refreshTable = ref(true)
const formRef = ref<FormInstance>()

const queryParams = reactive({
  menuName: '',
  status: ''
})

const form = reactive<SysMenu>({
  id: undefined,
  parentId: 0,
  menuName: '',
  icon: '',
  menuType: 'M',
  orderNum: 0,
  isFrame: '1',
  isCache: '0',
  visible: '0',
  status: '0',
  path: '',
  component: '',
  perms: ''
})

const rules = reactive<FormRules>({
  menuName: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
})

const getList = async () => {
  loading.value = true
  try {
    const res = await menuApi.list(queryParams)
    tableData.value = res || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getMenuOptions = async () => {
  try {
    const res = await menuApi.list({})
    menuOptions.value = [{ id: 0, menuName: '主类目', children: res || [] }]
  } catch (error) {
    console.error(error)
  }
}

const handleQuery = () => {
  getList()
}

const resetQuery = () => {
  queryParams.menuName = ''
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
  form.parentId = 0
  form.menuName = ''
  form.icon = ''
  form.menuType = 'M'
  form.orderNum = 0
  form.isFrame = '1'
  form.isCache = '0'
  form.visible = '0'
  form.status = '0'
  form.path = ''
  form.component = ''
  form.perms = ''
  formRef.value?.resetFields()
}

const handleAdd = (row?: SysMenu) => {
  resetForm()
  dialogTitle.value = '新增菜单'
  if (row?.id) {
    form.parentId = row.id
  }
  dialogVisible.value = true
}

const handleEdit = async (row: SysMenu) => {
  resetForm()
  dialogTitle.value = '编辑菜单'
  try {
    const res = await menuApi.getById(row.id!)
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
      await menuApi.update(form)
      ElMessage.success('修改成功')
    } else {
      await menuApi.create(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
    getMenuOptions()
  } catch (error) {
    console.error(error)
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: SysMenu) => {
  await ElMessageBox.confirm(`确认删除菜单"${row.menuName}"吗？`, '提示', {
    type: 'warning'
  })
  try {
    await menuApi.remove(row.id!)
    ElMessage.success('删除成功')
    getList()
    getMenuOptions()
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
</style>
