import request from '@/utils/request'

export interface SysUser {
  id?: number
  deptId?: number
  username?: string
  nickname?: string
  email?: string
  phone?: string
  gender?: string
  avatar?: string
  status?: string
  createTime?: string
  updateTime?: string
  remark?: string
}

export const userApi = {
  list: (params: any) => request.get<any, any>('/system/user/list', { params }),
  getById: (id: number) => request.get<any, SysUser>(`/system/user/${id}`),
  create: (data: SysUser) => request.post<any, any>('/system/user', data),
  update: (data: SysUser) => request.put<any, any>('/system/user', data),
  remove: (ids: string) => request.delete<any, any>(`/system/user/${ids}`),
  resetPwd: (id: number, password: string) => request.put<any, any>('/system/user/resetPwd', { id, password }),
  getProfile: () => request.get<any, SysUser>('/system/user/profile'),
  updateProfile: (data: SysUser) => request.put<any, any>('/system/user/profile', data),
  updatePassword: (data: { oldPassword: string; newPassword: string }) => request.put<any, any>('/system/user/password', data),
}
