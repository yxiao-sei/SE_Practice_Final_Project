<template>
<div class="page-breadcrumb">
  <span @click="breadcrumbClick(index)" v-for="r, index in routeHistory" :key="index"
    :class="r.name === routeName ? 'active' : ''">{{ r.menuName }}
    <el-icon v-if="index < routeHistory.length - 1" size="var(--el-font-size-medium)">
      <ArrowRight />
    </el-icon>
  </span>
</div>
<div class="page-content">
  <vxe-grid ref="xGrid" v-bind="gridOptions" v-on="gridEvent">
    <template #toolbar_buttons>
      <query-form ref="queryFormEl" :searchForm="searchForm" @updateQueryInfo="updateQueryInfo" />
    </template>
    <template #toolbar_tools>
      <ButtonGroup :actions="pageButtons" @handleCommand="(command: string) => handleCommand(command, null)" />
    </template>
    <template #table_action_slot_default="{ row }">
      <ButtonGroup :actions="tableButtons" @handleCommand="(command: string) => handleCommand(command, row)" />
    </template>
  </vxe-grid>
</div>
</template>

<script lang="ts" setup>
import { ArrowRight } from "@element-plus/icons-vue";
import QueryForm from "./QueryForm.vue";
import ButtonGroup from "../buttonGroup/Index.vue";
import { usePage } from "../hooks/usePage";
import { useRoute } from "vue-router";
import { useCacheRouteHistory } from "@/stores";

const route = useRoute();
const routeName = route.params.name as string;
const {
  gridOptions,
  gridEvent,
  pageButtons,
  searchForm,
  updateQueryInfo,
  router,
  tableButtons, xGrid, queryFormEl, handleCommand } = usePage({ routeName });

const { routeHistory, removeRoute, addRouteHistory } = useCacheRouteHistory();
const breadcrumbClick = (index: number) => {
  if (index === routeHistory.length - 1) return;
  removeRoute(index);
  router.go(-(index + 1));
}
</script>

<style lang="scss" scoped>
.page-breadcrumb {
  font-size: var(--el-font-size-medium);
  color: var(--el-text-color-main);
  text-align: left;
  padding-bottom: 20px;

  span {
    cursor: pointer;
  }

  :global(.el-icon) {
    vertical-align: -2px;
  }
}

.page-breadcrumb .active {
  cursor: default;
  color: var(--el-text-color-primary);
}
</style>
