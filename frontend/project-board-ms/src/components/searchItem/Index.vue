<!--
 * @Author: laladila1986@163.com
 * @Date: 2023-08-16 14:47:06
 * @LastEditors: laladila1986@163.com
 * @LastEditTime: 2023-09-22 16:53:31
 * @FilePath: \llbxt-web\src\components\searchItem\Index.vue
 * @Description: 
 * @Copyright: Copyright 2023, All Rights Reserved. 
-->
<template>
  <div class="search-block">
    <span class="label">{{ label }}</span>
    <slot></slot>
  </div>
</template>

<script setup lang="ts">
const { label } = defineProps({
  label: {
    required: true,
    type: String,
  },
});
</script>

<style scoped>
.search-block {
  display: flex;
  flex-direction: column;
  font-size: var(--el-font-size-medium);
  color: var(--el-text-color-main);
}

.label {
  line-height: 32px;
  min-height: 32px;
  text-align: left;
}
</style>
