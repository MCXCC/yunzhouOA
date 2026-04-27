<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #409eff">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">128</div>
              <div class="stat-label">员工总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">95%</div>
              <div class="stat-label">今日出勤率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">12</div>
              <div class="stat-label">待办审批</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon><Bell /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">3</div>
              <div class="stat-label">未读公告</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>待办事项</span>
          </template>
          <el-table :data="todoList" stripe>
            <el-table-column prop="type" label="类型" width="100" />
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="applicant" label="申请人" width="120" />
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column label="操作" width="120">
              <template #default>
                <el-button type="primary" link>处理</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>公告通知</span>
          </template>
          <div class="notice-list">
            <div class="notice-item" v-for="item in noticeList" :key="item.id">
              <el-tag :type="item.type" size="small">{{ item.tag }}</el-tag>
              <span class="notice-title">{{ item.title }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { User, Clock, Document, Bell } from '@element-plus/icons-vue'

const todoList = ref([
  { type: '请假', title: '年假申请 - 张三', applicant: '张三', time: '2024-01-15 09:30' },
  { type: '报销', title: '差旅费用报销 - 李四', applicant: '李四', time: '2024-01-15 10:20' },
  { type: '采购', title: '办公用品采购申请', applicant: '王五', time: '2024-01-15 11:00' },
])

const noticeList = ref([
  { id: 1, tag: '重要', title: '关于2024年春节放假安排的通知', type: 'danger' as const },
  { id: 2, tag: '通知', title: '公司年会将于下周举行', type: 'warning' as const },
  { id: 3, tag: '公告', title: '新OA系统上线通知', type: 'success' as const },
])
</script>

<style scoped lang="scss">
.dashboard {
  .stat-card {
    .stat-item {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .stat-icon {
      width: 56px;
      height: 56px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 24px;
    }

    .stat-value {
      font-size: 24px;
      font-weight: bold;
      color: #303133;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
    }
  }

  .notice-list {
    .notice-item {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 0;
      border-bottom: 1px solid #ebeef5;

      &:last-child {
        border-bottom: none;
      }
    }

    .notice-title {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 14px;
    }
  }
}
</style>
