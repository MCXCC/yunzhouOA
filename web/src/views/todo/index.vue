<template>
  <div class="todo-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>消息通知</span>
          <el-button type="primary" link @click="handleMarkAllRead" :disabled="!unreadCount">
            <el-icon><Check /></el-icon>全部标记已读
          </el-button>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部消息" name="all">
          <div v-loading="loading" class="message-list">
            <template v-if="messageList.length">
              <div
                v-for="item in messageList"
                :key="item.id"
                class="message-item"
                :class="{ unread: item.readStatus === '0' }"
                @click="handleViewMessage(item)"
              >
                <div class="message-dot" v-if="item.readStatus === '0'"></div>
                <div class="message-content">
                  <div class="message-header">
                    <span class="message-title">{{ item.title }}</span>
                    <el-tag size="small" :type="getMessageType(item.type)">{{ item.type }}</el-tag>
                  </div>
                  <div class="message-preview">{{ item.content }}</div>
                  <div class="message-time">{{ item.createTime }}</div>
                </div>
                <el-icon class="message-arrow"><ArrowRight /></el-icon>
              </div>
            </template>
            <el-empty v-else description="暂无消息" />
          </div>
        </el-tab-pane>
        <el-tab-pane name="unread">
          <template #label>
            <span>
              未读消息
              <el-badge v-if="unreadCount" :value="unreadCount" class="unread-badge" />
            </span>
          </template>
          <div v-loading="loading" class="message-list">
            <template v-if="unreadList.length">
              <div
                v-for="item in unreadList"
                :key="item.id"
                class="message-item unread"
                @click="handleViewMessage(item)"
              >
                <div class="message-dot"></div>
                <div class="message-content">
                  <div class="message-header">
                    <span class="message-title">{{ item.title }}</span>
                    <el-tag size="small" :type="getMessageType(item.type)">{{ item.type }}</el-tag>
                  </div>
                  <div class="message-preview">{{ item.content }}</div>
                  <div class="message-time">{{ item.createTime }}</div>
                </div>
                <el-icon class="message-arrow"><ArrowRight /></el-icon>
              </div>
            </template>
            <el-empty v-else description="暂无未读消息" />
          </div>
        </el-tab-pane>
      </el-tabs>

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
      title="消息详情"
      width="600px"
      append-to-body
    >
      <div class="message-detail">
        <h3 class="detail-title">{{ currentMessage.title }}</h3>
        <div class="detail-meta">
          <el-tag size="small" :type="getMessageType(currentMessage.type)">{{ currentMessage.type }}</el-tag>
          <span class="detail-time">{{ currentMessage.createTime }}</span>
        </div>
        <el-divider />
        <div class="detail-content">{{ currentMessage.content }}</div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="currentMessage.readStatus === '0'" type="primary" @click="handleMarkRead">标记已读</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, ArrowRight } from '@element-plus/icons-vue'
import { messageApi, type SysMessage } from '@/api/system/message'

const loading = ref(false)
const dialogVisible = ref(false)
const activeTab = ref('all')
const messageList = ref<SysMessage[]>([])
const unreadList = ref<SysMessage[]>([])
const unreadCount = ref(0)
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

const currentMessage = reactive<SysMessage>({
  id: undefined,
  title: '',
  content: '',
  type: '',
  readStatus: '',
  createTime: ''
})

const getMessageType = (type?: string) => {
  switch (type) {
    case '通知':
      return 'primary'
    case '公告':
      return 'success'
    case '提醒':
      return 'warning'
    default:
      return 'info'
  }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await messageApi.list(queryParams)
    if (activeTab.value === 'all') {
      messageList.value = res.records || []
      total.value = res.total || 0
    } else {
      unreadList.value = (res.records || []).filter((item: SysMessage) => item.readStatus === '0')
      total.value = unreadList.value.length
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getUnreadCount = async () => {
  try {
    const res = await messageApi.unreadCount()
    unreadCount.value = res || 0
  } catch (error) {
    console.error(error)
  }
}

const handleTabChange = () => {
  queryParams.pageNum = 1
  getList()
}

const handleViewMessage = (row: SysMessage) => {
  Object.assign(currentMessage, row)
  dialogVisible.value = true
}

const handleMarkRead = async () => {
  try {
    await messageApi.markAsRead(currentMessage.id!)
    ElMessage.success('标记已读成功')
    currentMessage.readStatus = '1'
    getList()
    getUnreadCount()
  } catch (error) {
    console.error(error)
  }
}

const handleMarkAllRead = async () => {
  try {
    await messageApi.markAllAsRead()
    ElMessage.success('全部标记已读成功')
    getList()
    getUnreadCount()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  getList()
  getUnreadCount()
})
</script>

<style scoped lang="scss">
.todo-page {
  padding: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.message-list {
  min-height: 400px;
}

.message-item {
  display: flex;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
  position: relative;

  &:hover {
    background-color: #f5f7fa;
  }

  &.unread {
    background-color: #ecf5ff;

    &:hover {
      background-color: #d9ecff;
    }

    .message-title {
      font-weight: bold;
    }
  }

  .message-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background-color: #f56c6c;
    margin-right: 12px;
    flex-shrink: 0;
  }

  .message-content {
    flex: 1;
    min-width: 0;

    .message-header {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 8px;

      .message-title {
        font-size: 15px;
        color: #303133;
      }
    }

    .message-preview {
      font-size: 13px;
      color: #909399;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      margin-bottom: 8px;
    }

    .message-time {
      font-size: 12px;
      color: #c0c4cc;
    }
  }

  .message-arrow {
    color: #c0c4cc;
    margin-left: 12px;
  }
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.unread-badge {
  margin-left: 4px;
}

.message-detail {
  .detail-title {
    margin: 0 0 12px 0;
    font-size: 18px;
  }

  .detail-meta {
    display: flex;
    align-items: center;
    gap: 12px;
    color: #909399;
    font-size: 13px;
  }

  .detail-content {
    line-height: 1.8;
    white-space: pre-wrap;
    word-break: break-all;
    color: #303133;
  }
}
</style>
