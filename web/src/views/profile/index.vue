<template>
  <div class="profile-page">
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>个人信息</span>
          </template>
          <div class="user-info">
            <div class="avatar-wrapper">
              <el-upload
                class="avatar-uploader"
                action="/api/system/user/avatar"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <el-avatar :size="100" :src="userForm.avatar || defaultAvatar">
                  {{ userForm.nickname?.charAt(0) || 'U' }}
                </el-avatar>
                <div class="avatar-upload-tip">点击上传头像</div>
              </el-upload>
            </div>
            <div class="info-list">
              <div class="info-item">
                <el-icon><User /></el-icon>
                <span class="label">用户名：</span>
                <span class="value">{{ userForm.username }}</span>
              </div>
              <div class="info-item">
                <el-icon><Iphone /></el-icon>
                <span class="label">手机号：</span>
                <span class="value">{{ userForm.phone || '-' }}</span>
              </div>
              <div class="info-item">
                <el-icon><Message /></el-icon>
                <span class="label">邮箱：</span>
                <span class="value">{{ userForm.email || '-' }}</span>
              </div>
              <div class="info-item">
                <el-icon><OfficeBuilding /></el-icon>
                <span class="label">部门：</span>
                <span class="value">{{ userForm.deptName || '-' }}</span>
              </div>
              <div class="info-item">
                <el-icon><Postcard /></el-icon>
                <span class="label">岗位：</span>
                <span class="value">
                  <el-tag v-for="post in userForm.posts" :key="post" size="small" style="margin-right: 4px;">
                    {{ post }}
                  </el-tag>
                  <span v-if="!userForm.posts?.length">-</span>
                </span>
              </div>
              <div class="info-item">
                <el-icon><UserFilled /></el-icon>
                <span class="label">角色：</span>
                <span class="value">
                  <el-tag v-for="role in userForm.roles" :key="role" size="small" type="success" style="margin-right: 4px;">
                    {{ role }}
                  </el-tag>
                  <span v-if="!userForm.roles?.length">-</span>
                </span>
              </div>
              <div class="info-item">
                <el-icon><Clock /></el-icon>
                <span class="label">创建时间：</span>
                <span class="value">{{ userForm.createTime || '-' }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <el-card>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="基本资料" name="profile">
              <el-form ref="profileFormRef" :model="profileForm" :rules="profileRules" label-width="100px" style="max-width: 500px; margin: 20px auto;">
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="profileForm.nickname" placeholder="请输入昵称" />
                </el-form-item>
                <el-form-item label="手机号" prop="phone">
                  <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="profileLoading" @click="handleUpdateProfile">保存</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="password">
              <el-form ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="100px" style="max-width: 500px; margin: 20px auto;">
                <el-form-item label="旧密码" prop="oldPassword">
                  <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入旧密码" show-password />
                </el-form-item>
                <el-form-item label="新密码" prop="newPassword">
                  <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" :loading="passwordLoading" @click="handleUpdatePassword">保存</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules, type UploadProps } from 'element-plus'
import { User, Iphone, Message, OfficeBuilding, Clock, Postcard, UserFilled } from '@element-plus/icons-vue'
import { userApi } from '@/api/system/user'

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const activeTab = ref('profile')
const profileLoading = ref(false)
const passwordLoading = ref(false)
const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()

const userForm = reactive({
  id: undefined as number | undefined,
  username: '',
  nickname: '',
  phone: '',
  email: '',
  avatar: '',
  deptName: '',
  createTime: '',
  roles: [] as string[],
  posts: [] as string[]
})

const profileForm = reactive({
  nickname: '',
  phone: '',
  email: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = reactive<FormRules>({
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
})

const passwordRules = reactive<FormRules>({
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule: any, value: string, callback: Function) => {
        if (value !== passwordForm.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

const getUserInfo = async () => {
  try {
    const res = await userApi.getProfile()
    Object.assign(userForm, res)
    profileForm.nickname = res.nickname || ''
    profileForm.phone = res.phone || ''
    profileForm.email = res.email || ''
  } catch (error) {
    console.error(error)
  }
}

const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    userForm.avatar = response.data
    ElMessage.success('头像上传成功')
  }
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/gif'].includes(file.type)
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('头像只能是 JPG/PNG/GIF 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过 2MB!')
  }
  return isImage && isLt2M
}

const handleUpdateProfile = async () => {
  const valid = await profileFormRef.value?.validate().catch(() => false)
  if (!valid) return

  profileLoading.value = true
  try {
    await userApi.updateProfile({
      id: userForm.id,
      nickname: profileForm.nickname,
      phone: profileForm.phone,
      email: profileForm.email
    })
    ElMessage.success('修改成功')
    getUserInfo()
  } catch (error) {
    console.error(error)
  } finally {
    profileLoading.value = false
  }
}

const handleUpdatePassword = async () => {
  const valid = await passwordFormRef.value?.validate().catch(() => false)
  if (!valid) return

  passwordLoading.value = true
  try {
    await userApi.updatePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    passwordFormRef.value?.resetFields()
  } catch (error) {
    console.error(error)
  } finally {
    passwordLoading.value = false
  }
}

onMounted(() => {
  getUserInfo()
})
</script>

<style scoped lang="scss">
.profile-page {
  padding: 10px;
}

.user-info {
  text-align: center;

  .avatar-wrapper {
    margin-bottom: 20px;

    .avatar-uploader {
      cursor: pointer;

      .avatar-upload-tip {
        margin-top: 8px;
        font-size: 12px;
        color: #909399;
      }
    }
  }

  .info-list {
    text-align: left;
    padding: 0 20px;

    .info-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .el-icon {
        margin-right: 8px;
        color: #909399;
      }

      .label {
        color: #909399;
        margin-right: 8px;
      }

      .value {
        color: #303133;
      }
    }
  }
}
</style>
