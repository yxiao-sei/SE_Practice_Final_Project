<template>
  <el-config-provider :locale="zhCn">
    <router-view></router-view>
  </el-config-provider>
</template>

<script lang="ts" setup>
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
import { ElConfigProvider } from 'element-plus';
</script>

<style>
* {
  padding: 0;
  margin: 0;
  box-sizing: border-box;
}

html,
body {
  width: 100%;
  height: 100%;
  font-size: var(--el-font-size-base);
}

#app {
  width: 100%;
  height: 100vh;
  min-width: 925px;
  background-color: var(--main-bg-color);
  /* min-height: 925PX; */
}
</style>
