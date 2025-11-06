<template>
  <el-menu :default-active="currentRoutePath" :unique-opened="true" router @open="handleOpen" @close="handleClose">
    <el-scrollbar height="100%">
      <template v-for="(route, index) in routerOptions" :key="index">
        <el-menu-item :index="route.path" v-if="route.meta?.isMenu">
          <el-icon class="mr-17">
            <svg-icon :icon-name="route.meta?.icon"></svg-icon>
          </el-icon>
          <span>{{ route.meta?.menuName }}</span>
        </el-menu-item>
        <el-sub-menu :index="route.path" v-else-if="route.meta?.isSub">
          <template #title>
            <el-icon class="mr-17">
              <svg-icon :icon-name="route.meta?.icon"></svg-icon>
            </el-icon>
            <span>{{ route.meta?.menuName }}</span>
          </template>
          <template v-for="child in route.children" :key="child.path">
            <el-menu-item :index="child.path" v-if="child.meta?.isMenu">
              <span>{{ child.meta?.menuName }}</span>
            </el-menu-item>
          </template>
        </el-sub-menu>
      </template>
    </el-scrollbar>
    <span class="version">v{{ pack.version }}</span>
  </el-menu>
</template>

<script lang="ts" setup>
import pack from '/package.json';
import { computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
const router = useRouter();
const route = useRoute();
const currentRoutePath: any = computed(() => {
  return route.meta.isMenu ? route.path : route.meta.target;
});

const routerOptions = computed(() => {
  const routerRecord = router.getRoutes();
  const menuRoutes =
    routerRecord
      .find((_route) => {
        return _route.path === '/layout';
      })
      ?.children.filter((i) => (i.meta && i.meta.isMenu) || (i.meta && i.meta.isSub))
      .sort((a: any, b: any) => a.meta?.order - b.meta?.order) || [];
  return menuRoutes as any; 
});

const handleOpen = (key: string, keyPath: string[]) => { };

const handleClose = (key: string, keyPath: string[]) => { };
</script>

<style scoped>
.version {
  position: absolute;
  bottom: 0;
  width: 100%;
  line-height: 40px;
  text-align: center;
  color: var(--el-menu-text-color);
  opacity: 0.5;
}
.mr-17 {
  margin-right: 17px;
}
</style>
