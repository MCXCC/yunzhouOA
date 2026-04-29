import request from '@/utils/request'

export interface SysNotice {
  id?: number
  title?: string
  type?: string
  content?: string
  status?: string
  createBy?: string
  createTime?: string
  remark?: string
}

export const noticeApi = {
  list: (params: any) => request.get<any, any>('/system/notice/list', { params }),
  getById: (id: number) => request.get<any, SysNotice>(`/system/notice/${id}`),
  create: (data: SysNotice) => request.post<any, any>('/system/notice', data),
  update: (data: SysNotice) => request.put<any, any>('/system/notice', data),
  remove: (ids: string) => request.delete<any, any>(`/system/notice/${ids}`),
}
