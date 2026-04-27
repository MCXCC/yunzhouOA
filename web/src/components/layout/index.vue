<template>
  <div class="layout-container">
    <layout-aside />
    <div class="layout-main">
      <layout-header />
      <layout-content />
    </div>
  </div>
</template>

<script setup lang="ts">
import LayoutAside from './LayoutAside.vue'
import LayoutHeader from './LayoutHeader.vue'
import LayoutContent from './LayoutContent.vue'
</script>

<style scoped lang="scss">
.layout-container {
  display: flex;
  height: 100vh;
  width: 100%;
}

.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
</style>
