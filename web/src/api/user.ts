import request from '@/utils/request'

export interface PageParams {
  pageNum: number
  pageSize: number
}

export interface PageResult<T> {
  records: T[]
  total: number
  pageNum: number
  pageSize: number
  pages: number
}

export interface User {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  gender: number
  avatar: string
  deptId: number
  status: number
  createTime: string
}

export const userApi = {
  getList: (params: PageParams & { keyword?: string }) => {
    return request.get<any, PageResult<User>>('/system/user', { params })
  },

  getById: (id: number) => {
    return request.get<any, User>(`/system/user/${id}`)
  },

  create: (data: Partial<User>) => {
    return request.post<any, void>('/system/user', data)
  },

  update: (id: number, data: Partial<User>) => {
    return request.put<any, void>(`/system/user/${id}`, data)
  },

  delete: (id: number) => {
    return request.delete<any, void>(`/system/user/${id}`)
  },

  resetPassword: (id: number) => {
    return request.put<any, void>(`/system/user/${id}/reset-password`)
  },
}
