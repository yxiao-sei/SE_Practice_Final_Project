<template>
<el-container>
  <el-header>
    <primary-header></primary-header>
  </el-header>
  <el-container>
    <el-aside class="aside">
      <!-- <async-menu></async-menu> -->
      <primary-menu></primary-menu>
    </el-aside>
    <el-main class="page-main">
      <router-view />
    </el-main>
  </el-container>
</el-container>
</template>

<script lang="ts" setup>
import { getUserApi } from '@/service/api/user';
import { useCacheUserInfoStore } from '@/stores';
import { provide, ref } from 'vue';
import primaryHeader from './components/primaryHeader.vue';
import primaryMenu from './components/primaryMenu.vue';

const newName = ref();
provide('updateBreadcrumb', {
  updateLastBreadcrumbName(_newName: string) {
    newName.value = _newName;
  },
});
</script>

<style scoped>
.page-main {
  display: flex;
  flex-direction: column;
  height: calc(100% - var(--page-padding) - var(--page-padding));
  position: relative;
}

.aside {
  --el-aside-width: 240px;
}
</style>
