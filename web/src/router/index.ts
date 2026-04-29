import type {RouteRecordRaw} from 'vue-router'
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

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
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', hidden: true },
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
        meta: { title: '打卡记录', permission: 'attendance:record:list' },
      },
      {
        path: 'leave',
        name: 'AttendanceLeave',
        component: () => import('@/views/attendance/leave.vue'),
        meta: { title: '请假申请', permission: 'attendance:leave:list' },
      },
      {
        path: 'statistics',
        name: 'AttendanceStatistics',
        component: () => import('@/views/attendance/statistics.vue'),
        meta: { title: '考勤统计', permission: 'attendance:statistics:list' },
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
        meta: { title: '我的申请', permission: 'workflow:apply:list' },
      },
      {
        path: 'my-task',
        name: 'MyTask',
        component: () => import('@/views/workflow/my-task.vue'),
        meta: { title: '我的待办', permission: 'workflow:task:list' },
      },
      {
        path: 'done-task',
        name: 'DoneTask',
        component: () => import('@/views/workflow/done-task.vue'),
        meta: { title: '已办任务', permission: 'workflow:done:list' },
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
        meta: { title: '公告列表', permission: 'notice:list:list' },
      },
      {
        path: 'todo',
        name: 'TodoList',
        component: () => import('@/views/todo/index.vue'),
        meta: { title: '待办通知' },
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
        meta: { title: '用户管理', permission: 'system:user:list' },
      },
      {
        path: 'dept',
        name: 'SystemDept',
        component: () => import('@/views/system/dept.vue'),
        meta: { title: '部门管理', permission: 'system:dept:list' },
      },
      {
        path: 'role',
        name: 'SystemRole',
        component: () => import('@/views/system/role.vue'),
        meta: { title: '角色管理', permission: 'system:role:list' },
      },
      {
        path: 'menu',
        name: 'SystemMenu',
        component: () => import('@/views/system/menu.vue'),
        meta: { title: '菜单管理', permission: 'system:menu:list' },
      },
      {
        path: 'post',
        name: 'SystemPost',
        component: () => import('@/views/system/post.vue'),
        meta: { title: '岗位管理', permission: 'system:post:list' },
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
  const userStore = useUserStore()
  const token = localStorage.getItem('token')
  
  // 登录页直接放行
  if (to.path === '/login') {
    next()
    return
  }
  
  // 未登录跳转到登录页
  if (!token) {
    next('/login')
    return
  }
  
  // 检查权限（除首页和个人中心外）
  const permission = to.meta.permission as string
  if (permission && !userStore.hasPermission(permission)) {
    // 没有权限，跳转到首页或显示无权限页面
    next('/dashboard')
    return
  }
  
  next()
})

export default router
