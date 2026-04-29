<template>
  <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
    <div class="logo">
      <img src="@/assets/logo.svg" alt="logo" v-if="!isCollapse" />
      <span v-if="!isCollapse">云州OA</span>
    </div>
    <el-scrollbar>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <template v-for="item in menuList" :key="item.path">
          <el-sub-menu v-if="item.children?.length" :index="item.path">
            <template #title>
              <el-icon v-if="item.icon">
                <component :is="item.icon" />
              </el-icon>
              <span>{{ item.name }}</span>
            </template>
            <el-menu-item
              v-for="child in item.children"
              :key="child.path"
              :index="child.path"
            >
              {{ child.name }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="item.path">
            <el-icon v-if="item.icon">
              <component :is="item.icon" />
            </el-icon>
            <template #title>{{ item.name }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
  </el-aside>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const userStore = useUserStore()

const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

// 根据用户权限动态生成菜单
const menuList = computed(() => {
  const userMenus = userStore.menus
  if (!userMenus || userMenus.length === 0) return []
  
  const result: any[] = []
  
  // 处理一级菜单（目录类型 M 和首页）
  userMenus.forEach(item => {
    // 首页单独处理
    if (item.path === 'dashboard' && item.menuType === 'C') {
      result.push({
        path: '/dashboard',
        name: item.name || '首页',
        icon: item.icon || 'House',
        children: []
      })
      return
    }
    
    // 只处理目录类型且可见的菜单
    if (item.menuType === 'M' && item.visible === '0') {
      const children = item.children
        ?.filter(c => c.menuType === 'C' && c.visible === '0')
        .map(c => ({
          path: '/' + item.path + '/' + c.path,
          name: c.name,
        })) || []
      
      if (children.length > 0) {
        result.push({
          path: '/' + item.path,
          name: item.name,
          icon: item.icon,
          children
        })
      }
    }
  })
  
  return result
})
</script>

<style scoped lang="scss">
.layout-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;

  img {
    width: 32px;
    height: 32px;
  }
}

:deep(.el-menu) {
  border-right: none;
}
</style>
