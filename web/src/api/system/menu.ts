import request from '@/utils/request'

export interface SysMenu {
  id?: number
  menuName?: string
  parentId?: number
  orderNum?: number
  path?: string
  component?: string
  query?: string
  routeName?: string
  isFrame?: string
  isCache?: string
  menuType?: string
  visible?: string
  status?: string
  perms?: string
  icon?: string
  children?: SysMenu[]
}

export const menuApi = {
  list: (params: any) => request.get<any, SysMenu[]>('/system/menu/list', { params }),
  getById: (id: number) => request.get<any, SysMenu>(`/system/menu/${id}`),
  create: (data: SysMenu) => request.post<any, any>('/system/menu', data),
  update: (data: SysMenu) => request.put<any, any>('/system/menu', data),
  remove: (id: number) => request.delete<any, any>(`/system/menu/${id}`),
  getRoleMenus: () => request.get<any, SysMenu[]>('/system/menu/roleMenus'),
}
