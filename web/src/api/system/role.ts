import request from '@/utils/request'

export interface SysRole {
  id?: number
  roleName?: string
  roleKey?: string
  orderNum?: number
  dataScope?: string
  status?: string
  createTime?: string
  remark?: string
  menuIds?: number[]
}

export const roleApi = {
  list: (params: any) => request.get<any, any>('/system/role/list', { params }),
  getById: (id: number) => request.get<any, SysRole>(`/system/role/${id}`),
  create: (data: SysRole) => request.post<any, any>('/system/role', data),
  update: (data: SysRole) => request.put<any, any>('/system/role', data),
  remove: (ids: string) => request.delete<any, any>(`/system/role/${ids}`),
  getMenuIds: (roleId: number) => request.get<any, number[]>(`/system/role/menuIds/${roleId}`),
}
