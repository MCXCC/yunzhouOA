import { RouteRecordRaw } from 'vue-router'

export interface MenuItem {
  path: string
  name: string
  meta: {
    title: string
    icon?: string
    hidden?: boolean
  }
  children?: MenuItem[]
}

export interface PaginationParams {
  pageNum: number
  pageSize: number
}

export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}
