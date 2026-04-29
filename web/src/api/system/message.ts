import request from '@/utils/request'

export interface SysMessage {
  id?: number
  title?: string
  content?: string
  type?: string
  senderId?: number
  receiverId?: number
  readStatus?: string
  createTime?: string
}

export const messageApi = {
  list: (params: any) => request.get<any, any>('/system/message/list', { params }),
  unreadCount: () => request.get<any, number>('/system/message/unreadCount'),
  markAsRead: (id: number) => request.put<any, any>(`/system/message/read/${id}`),
  markAllAsRead: () => request.put<any, any>('/system/message/readAll'),
}
