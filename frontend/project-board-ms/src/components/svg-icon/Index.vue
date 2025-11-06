<!--
 * @Author: lanseliuyiya laladila1986@163.com
 * @Date: 2022-12-05 10:56:02
 * @LastEditors: lanseliuyiya laladila1986@163.com
 * @LastEditTime: 2022-12-05 10:56:23
 * @FilePath: \model-validation\src\components\svg-icon\Index.vue
 * @Description: 
 * 
 * Copyright (c) 2022 by lanseliuyiya laladila1986@163.com, All Rights Reserved. 
-->

<template>
  <svg :class="svgClass" aria-hidden="true" :style="iconFontStyle">
    <use :xlink:href="iconClassName" :fill="color" />
  </svg>
</template>
<script setup lang="ts">
import { computed } from 'vue';
const props = defineProps({
  iconName: {
    type: String,
    required: true,
  },
  className: {
    type: String,
    default: '',
  },
  color: {
    type: String,
    default: '',
  },
  size: {
    type: Number,
    default: 16,
  },
});
// 图标在 iconfont 中的名字
const iconClassName = computed(() => {
  return `#${props.iconName}`;
});
const iconFontStyle = computed(() => {
  return `font-size: ${props.size}px`;
});
// 给图标添加上类名
const svgClass = computed(() => {
  if (props.className) {
    return `svg-icon ${props.className}`;
  }
  return 'svg-icon';
});
</script>
<style scoped>
.svg-icon {
  width: 1em;
  height: 1em;
  position: relative;
  fill: currentColor;
  vertical-align: -2px;
}
</style>
