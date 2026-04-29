import request from '@/utils/request'

export interface SysDept {
  id?: number
  parentId?: number
  ancestors?: string
  deptName?: string
  orderNum?: number
  leader?: string
  phone?: string
  email?: string
  status?: string
  children?: SysDept[]
}

export const deptApi = {
  list: (params: any) => request.get<any, SysDept[]>('/system/dept/list', { params }),
  getById: (id: number) => request.get<any, SysDept>(`/system/dept/${id}`),
  create: (data: SysDept) => request.post<any, any>('/system/dept', data),
  update: (data: SysDept) => request.put<any, any>('/system/dept', data),
  remove: (id: number) => request.delete<any, any>(`/system/dept/${id}`),
}
