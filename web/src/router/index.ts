import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true },
  },
  {
    path: '/',
    component: () => import('@/components/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'House' },
      },
    ],
  },
  {
    path: '/attendance',
    component: () => import('@/components/layout/index.vue'),
    meta: { title: '考勤管理', icon: 'Clock' },
    children: [
      {
        path: 'record',
        name: 'AttendanceRecord',
        component: () => import('@/views/attendance/record.vue'),
        meta: { title: '打卡记录' },
      },
      {
        path: 'leave',
        name: 'AttendanceLeave',
        component: () => import('@/views/attendance/leave.vue'),
        meta: { title: '请假申请' },
      },
      {
        path: 'statistics',
        name: 'AttendanceStatistics',
        component: () => import('@/views/attendance/statistics.vue'),
        meta: { title: '考勤统计' },
      },
    ],
  },
  {
    path: '/workflow',
    component: () => import('@/components/layout/index.vue'),
    meta: { title: '审批流程', icon: 'Share' },
    children: [
      {
        path: 'my-apply',
        name: 'MyApply',
        component: () => import('@/views/workflow/my-apply.vue'),
        meta: { title: '我的申请' },
      },
      {
        path: 'my-task',
        name: 'MyTask',
        component: () => import('@/views/workflow/my-task.vue'),
        meta: { title: '我的待办' },
      },
      {
        path: 'done-task',
        name: 'DoneTask',
        component: () => import('@/views/workflow/done-task.vue'),
        meta: { title: '已办任务' },
      },
    ],
  },
  {
    path: '/notice',
    component: () => import('@/components/layout/index.vue'),
    meta: { title: '公告通知', icon: 'Bell' },
    children: [
      {
        path: 'list',
        name: 'NoticeList',
        component: () => import('@/views/notice/list.vue'),
        meta: { title: '公告列表' },
      },
    ],
  },
  {
    path: '/system',
    component: () => import('@/components/layout/index.vue'),
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'SystemUser',
        component: () => import('@/views/system/user.vue'),
        meta: { title: '用户管理' },
      },
      {
        path: 'dept',
        name: 'SystemDept',
        component: () => import('@/views/system/dept.vue'),
        meta: { title: '部门管理' },
      },
      {
        path: 'role',
        name: 'SystemRole',
        component: () => import('@/views/system/role.vue'),
        meta: { title: '角色管理' },
      },
      {
        path: 'menu',
        name: 'SystemMenu',
        component: () => import('@/views/system/menu.vue'),
        meta: { title: '菜单管理' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  document.title = `${to.meta.title || ''} - 云州OA`
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
