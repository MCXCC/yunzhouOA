import { defineStore } from 'pinia'

interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  deptId: number
  roles: string[]
}

interface UserState {
  token: string
  userInfo: UserInfo | null
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token') || '',
    userInfo: null,
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    nickname: (state) => state.userInfo?.nickname || '',
    avatar: (state) => state.userInfo?.avatar || '',
    roles: (state) => state.userInfo?.roles || [],
  },

  actions: {
    setToken(token: string) {
      this.token = token
      localStorage.setItem('token', token)
    },

    setUserInfo(userInfo: UserInfo) {
      this.userInfo = userInfo
    },

    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
    },
  },
})
