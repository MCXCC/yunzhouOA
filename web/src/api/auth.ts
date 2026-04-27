import request from '@/utils/request'

export interface LoginParams {
  username: string
  password: string
}

export interface LoginResult {
  token: string
}

export const authApi = {
  login: (data: LoginParams) => {
    return request.post<any, LoginResult>('/auth/login', data)
  },

  logout: () => {
    return request.post<any, void>('/auth/logout')
  },

  getUserInfo: () => {
    return request.get<any, any>('/auth/user-info')
  },
}
