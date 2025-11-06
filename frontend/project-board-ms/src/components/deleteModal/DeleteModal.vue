<template>
  <common-dialog :width="items.length > 1 ? '44%' : '32%'">
    <el-row v-if="items.length === 1" :gutter="10">
      <el-col :span="12" v-for="(column, index) in fields" :key="index">
        <p class="field-label">{{ column.title }}</p>
        <p class="field-value">
          {{ items[0]?.[column.field] }}
        </p>
      </el-col>
    </el-row>
    <template v-else>
      <p>
        本次一共删除&nbsp;<span style="color: var(--el-color-danger)"> {{ items.length }} </span>&nbsp;条数据
      </p>
      <el-table table-layout="auto" ref="tableEl" :row-key="(row: any) => row.id" :data="items" size="default"
        cell-class-name="common-table-cell" header-cell-class-name="common-table-cell common-table-header-cell"
        header-row-class-name="common-table-header-row" style="width: 100%">
        <template v-for="column in fields" :key="column.field">
          <el-table-column show-overflow-tooltip align="center" :prop="column.field" :label="column.title" />
        </template>
      </el-table>
    </template>
    <slot></slot>
  </common-dialog>
</template>

<script setup lang="ts">
import { toRefs } from 'vue';
import commonDialog from '../enDialog/Index.vue';
const props = defineProps<{ items: any; fields: any[] }>();
const { items, fields } = toRefs(props);
</script>

<style lang="scss" scoped>
.flex-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  // height: 40px;
  min-height: 30px;
  padding: 5px 14px;
  color: #62696f;
}

.back-color {
  background-color: #eff2f5;
}
</style>
