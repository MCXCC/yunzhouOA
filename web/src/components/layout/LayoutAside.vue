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
  
  return userMenus
    .filter(item => item.menuType === 'M' && item.visible === '0')
    .map(item => ({
      path: '/' + item.path,
      name: item.name,
      icon: item.icon,
      children: item.children
        ?.filter(c => c.menuType === 'C' && c.visible === '0')
        .map(c => ({
          path: '/' + item.path + '/' + c.path,
          name: c.name,
        })) || []
    }))
    .filter(item => item.children.length > 0 || item.path === '/dashboard')
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
