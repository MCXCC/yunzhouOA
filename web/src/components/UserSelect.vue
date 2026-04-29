<template>
  <div class="user-select">
    <el-select
      v-model="selectedValue"
      :multiple="multiple"
      filterable
      remote
      :remote-method="searchUsers"
      :loading="loading"
      :placeholder="placeholder"
      :clearable="clearable"
      :disabled="disabled"
      :collapse-tags="collapseTags"
      :collapse-tags-tooltip="true"
      @change="handleChange"
    >
      <el-option
        v-for="user in userList"
        :key="user.id"
        :label="user.nickname || user.username"
        :value="user.id!"
      >
        <div class="user-option">
          <el-avatar :size="24" :src="user.avatar">
            {{ user.nickname?.charAt(0) || user.username?.charAt(0) || 'U' }}
          </el-avatar>
          <div class="user-info">
            <span class="name">{{ user.nickname || user.username }}</span>
            <span class="dept">{{ user.deptName || '-' }}</span>
          </div>
        </div>
      </el-option>
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { userApi } from '@/api/system/user'

interface UserInfo {
  id?: number
  username?: string
  nickname?: string
  avatar?: string
  deptName?: string
}

const props = withDefaults(defineProps<{
  modelValue?: number | number[] | null
  multiple?: boolean
  placeholder?: string
  clearable?: boolean
  disabled?: boolean
  collapseTags?: boolean
  deptId?: number
}>(), {
  multiple: false,
  placeholder: '请选择人员',
  clearable: true,
  disabled: false,
  collapseTags: true
})

const emit = defineEmits<{
  (e: 'update:modelValue', value: number | number[] | null): void
  (e: 'change', value: number | number[] | null, user: UserInfo | UserInfo[]): void
}>()

const loading = ref(false)
const userList = ref<UserInfo[]>([])
const selectedValue = ref<number | number[] | null>(props.modelValue ?? (props.multiple ? [] : null))

// 搜索用户
const searchUsers = async (query: string) => {
  if (!query || query.length < 1) {
    userList.value = []
    return
  }
  
  loading.value = true
  try {
    const res = await userApi.list({
      keyword: query,
      status: 0,
      deptId: props.deptId,
      pageNum: 1,
      pageSize: 20
    })
    userList.value = res.records || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 加载已选中的用户信息
const loadSelectedUsers = async () => {
  const ids = props.multiple 
    ? (props.modelValue as number[] || [])
    : (props.modelValue ? [props.modelValue as number] : [])
  
  if (ids.length === 0) return
  
  try {
    const users: UserInfo[] = []
    for (const id of ids) {
      const user = await userApi.getById(id)
      if (user) {
        users.push(user)
      }
    }
    userList.value = [...new Map([...userList.value, ...users].map(u => [u.id, u])).values()]
  } catch (error) {
    console.error(error)
  }
}

const handleChange = (value: number | number[] | null) => {
  emit('update:modelValue', value)
  
  if (props.multiple) {
    const users = userList.value.filter(u => (value as number[])?.includes(u.id!))
    emit('change', value, users)
  } else {
    const user = userList.value.find(u => u.id === value) || null
    emit('change', value, user as UserInfo)
  }
}

watch(() => props.modelValue, (val) => {
  selectedValue.value = val ?? (props.multiple ? [] : null)
  loadSelectedUsers()
}, { immediate: true })

onMounted(() => {
  loadSelectedUsers()
})
</script>

<style scoped lang="scss">
.user-select {
  width: 100%;
}

.user-option {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .user-info {
    display: flex;
    flex-direction: column;
    
    .name {
      font-size: 14px;
      color: #303133;
    }
    
    .dept {
      font-size: 12px;
      color: #909399;
    }
  }
}
</style>
