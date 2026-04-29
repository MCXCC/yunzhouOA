import request from '@/utils/request'

export interface SysPost {
  id?: number
  postCode?: string
  postName?: string
  orderNum?: number
  status?: string
  createTime?: string
  remark?: string
}

export const postApi = {
  list: (params: any) => request.get<any, any>('/system/post/list', { params }),
  all: () => request.get<any, SysPost[]>('/system/post/all'),
  getById: (id: number) => request.get<any, SysPost>(`/system/post/${id}`),
  create: (data: SysPost) => request.post<any, any>('/system/post', data),
  update: (data: SysPost) => request.put<any, any>('/system/post', data),
  remove: (id: number) => request.delete<any, any>(`/system/post/${id}`),
}
