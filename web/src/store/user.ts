import { defineStore } from 'pinia'

interface MenuItem {
  id: number
  name: string
  path: string
  component?: string
  perms?: string
  menuType: string
  icon?: string
  visible: string
  children?: MenuItem[]
}

interface UserInfo {
  id: number
  username: string
  nickname: string
  avatar: string
  deptId: number
  roles: string[]
  posts: string[]
  menus: MenuItem[]
}

interface UserState {
  token: string
  userInfo: UserInfo | null
  permissions: string[]
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    token: localStorage.getItem('token') || '',
    userInfo: null,
    permissions: [],
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    nickname: (state) => state.userInfo?.nickname || '',
    avatar: (state) => state.userInfo?.avatar || '',
    roles: (state) => state.userInfo?.roles || [],
    posts: (state) => state.userInfo?.posts || [],
    menus: (state) => state.userInfo?.menus || [],
    hasPermission: (state) => (perm: string) => state.permissions.includes(perm) || state.permissions.includes('*:*:*'),
  },

  actions: {
    setToken(token: string) {
      this.token = token
      localStorage.setItem('token', token)
    },

    setUserInfo(userInfo: UserInfo) {
      this.userInfo = userInfo
      // 提取权限标识
      this.permissions = this.extractPermissions(userInfo.menus || [])
    },

    extractPermissions(menus: MenuItem[]): string[] {
      const perms: string[] = []
      const extract = (items: MenuItem[]) => {
        items.forEach(item => {
          if (item.perms) {
            perms.push(item.perms)
          }
          if (item.children) {
            extract(item.children)
          }
        })
      }
      extract(menus)
      return perms
    },

    logout() {
      this.token = ''
      this.userInfo = null
      this.permissions = []
      localStorage.removeItem('token')
    },
  },
})
