<template>
<div class="page-content">
  <vxe-grid ref="xGrid" v-bind="gridOptions" v-on="gridEvent">
    <template #toolbar_buttons>
      <query-form ref="queryFormEl" :searchForm="searchForm" @updateQueryInfo="updateQueryInfo" />
    </template>
    <template #toolbar_tools>
      <ButtonGroup :actions="pageButtons" @handleCommand="(command: string) => handleCommand(command, null)" />
    </template>
    <template #table_action_slot_default="{ row, columns }">
      <ButtonGroup :actions="tableButtons" @handleCommand="(command: string) => handleCommand(command, row)" />
    </template>
  </vxe-grid>
</div>
</template>

<script lang="ts" setup>
import QueryForm from "./QueryForm.vue";
import ButtonGroup from "../buttonGroup/Index.vue";
import { usePage } from "../hooks/usePage";
import { useRoute } from "vue-router";
const route = useRoute();
const routeName = route.name as string;
const { gridOptions, gridEvent, pageButtons, handleCommand, searchForm, updateQueryInfo, tableButtons, xGrid, queryFormEl } = usePage({ routeName });
</script>
