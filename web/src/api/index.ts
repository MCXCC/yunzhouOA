import request from '@/utils/request'

export const indexApi = {
  statistics: () => request.get<any, any>('/index/statistics'),
}
