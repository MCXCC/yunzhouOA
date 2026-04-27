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
        <template v-for="route in menuRoutes" :key="route.path">
          <el-sub-menu v-if="route.children?.length" :index="route.path">
            <template #title>
              <el-icon v-if="route.meta?.icon">
                <component :is="route.meta.icon" />
              </el-icon>
              <span>{{ route.meta?.title }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="route.path + '/' + child.path"
            >
              {{ child.meta?.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="route.path">
            <el-icon v-if="route.meta?.icon">
              <component :is="route.meta.icon" />
            </el-icon>
            <template #title>{{ route.meta?.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
  </el-aside>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const isCollapse = ref(false)

const activeMenu = computed(() => route.path)

const menuRoutes = computed(() => {
  return router.getRoutes()
    .filter(r => r.meta?.title && !r.meta?.hidden && r.path !== '/login')
    .map(r => ({
      path: r.path,
      name: r.name as string,
      meta: r.meta as any,
      children: r.children?.filter(c => !c.meta?.hidden).map(c => ({
        path: c.path,
        name: c.name as string,
        meta: c.meta as any,
      })),
    }))
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
