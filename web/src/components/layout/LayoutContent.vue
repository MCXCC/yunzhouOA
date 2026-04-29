<template>
  <el-main class="layout-content">
    <router-view v-slot="{ Component }">
      <keep-alive>
        <component :is="Component" :key="$route.path" />
      </keep-alive>
    </router-view>
  </el-main>
</template>

<script setup lang="ts">
import { useRoute } from 'vue-router'
const $route = useRoute()
</script>

<style scoped lang="scss">
.layout-content {
  background-color: #f0f2f5;
  padding: 16px;
  overflow-y: auto;
}
</style>
