<template>
  <div class="page-menu-container">
    <span :class="activeTabComputed(r) ? 'active' : ''" v-for="r in pageMenus" :key="r.path" @click="changeTab(r)">{{
      r.meta?.menuName }}</span>
  </div>
</template>

<script lang="ts" setup>
import { computed, toRefs } from "vue";
import { useRoute, useRouter, type RouteRecordRaw } from 'vue-router';
const props: any = defineProps<{ pageMenus: any[] }>() // 获取父组件传递的参数target，用于判断当前页面是否需要显示菜单栏。
const { pageMenus } = toRefs(props)
const route = useRoute();

const router = useRouter();
const activeTabComputed = computed(() => {
  return (r: RouteRecordRaw) => {
    return route.path === r.path
  }
});
const changeTab = (r: RouteRecordRaw) => {
  router.push({ path: r.path, query: route.query, replace: true })
}
</script>

<style lang="scss" scoped>
.page-menu-container {
  text-align: left;
  color: var(--el-text-color-main);
  font-size: var(--el-font-size-medium);

  span {
    padding: 19px 20px;
    display: inline-block;
    min-width: 100px;
    text-align: center;
    cursor: pointer;
    background-color: var(--el-color-white);
    position: relative;
  }

  span:not(:last-child) {
    border-right: 0.5px solid rgba(199, 205, 235, 0.1);
  }

  .active {
    color: var(--el-text-color-primary);

    &:after {
      content: "";
      position: absolute;
      bottom: 0;
      // width: 118px;
      height: 3px;
      background-color: var(--el-text-color-primary);
      left: 30px;
      right: 30px;
    }
  }
}
</style>
