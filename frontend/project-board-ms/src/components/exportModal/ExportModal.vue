<!--
 * @Author: laladila1986@163.com
 * @Date: 2024-01-03 17:37:50
 * @LastEditors: laladila1986@163.com
 * @LastEditTime: 2024-02-01 16:17:47
 * @FilePath: \plms-ms\src\components\exportModal\ExportModal.vue
 * @Description: 
 * @Copyright: Copyright 2024, All Rights Reserved. 
-->

<template>
  <common-dialog @close="closeModal" :loading="loading">
    <div class="flex-center">
      <el-checkbox
        v-model="checkAll"
        :indeterminate="isIndeterminate"
        @change="handleCheckAllChange"
        >全选</el-checkbox
      >
      <el-button type="primary" style="margin-left: 10px" @click="handleCheckInvertChange" text
        >反选</el-button
      >
    </div>
    <el-checkbox-group
      class="common-checkbox-group"
      v-model="isCheckedList"
      @change="isCheckedListChange"
    >
      <el-checkbox
        size="large"
        v-for="head in columns"
        :key="head.props.label"
        :label="head.props.label"
      ></el-checkbox>
    </el-checkbox-group>
  </common-dialog>
</template>

<script lang="ts" setup>
import type { Columns } from '@/model/pageTable';
import { downLoadStrapi } from '@/utils/downLoad';
import { ElMessage } from 'element-plus';
import { ref, toRefs, watch } from 'vue';
import commonDialog from '../enDialog/Index.vue';

const emits = defineEmits(['closeModal']);

const loading = ref(false);
const props = defineProps<{ columns: Columns; params: string; fileName: string; url: string }>();
const { columns, params, fileName, url } = toRefs(props);
const closeModal = (status: string) => {
  if (status === 'confirm') {
    if (!isCheckedList.value.length) return ElMessage.warning('请选择导出字段');
    loading.value = true;
    downLoadStrapi({
      url: url.value,
      params: params.value,
      titles: columns.value
        .filter((item: any) => isCheckedList.value.includes(item.props.label))
        .map((i: any) => {
          return { [i.props.prop]: i.props.label };
        }),
      fileName: fileName.value,
    })
      .then(() => {
        emits('closeModal');
      })
      .finally(() => {
        loading.value = false;
      });
  } else {
    emits('closeModal');
  }
};

const isCheckedList = ref<any[]>([]);
const checkAll = ref(false);
const isIndeterminate = ref(true);

const isCheckedListChange = () => {
  if (isCheckedList.value.length === columns.value.length) {
    checkAll.value = true;
    isIndeterminate.value = false;
  } else if (!isCheckedList.value.length) checkAll.value = false;
  else {
    checkAll.value = false;
    isIndeterminate.value = true;
  }
};

const handleCheckAllChange = (val: any) => {
  isCheckedList.value = val ? columns.value.map((i) => i.props.label) : [];
  isIndeterminate.value = false;
  isCheckedListChange();
};

const handleCheckInvertChange = () => {
  const newList = columns.value
    .filter((i) => !isCheckedList.value.includes(i.props.label))
    .map((i) => i.props.label);
  isCheckedList.value = newList;
  isCheckedListChange();
};

watch(
  () => props.columns,
  (value) => {
    if (value) {
      isCheckedList.value = value.filter((i) => i.isChecked).map((i) => i.props.label);
    } else {
      isCheckedList.value = [];
    }
    isCheckedListChange();
  }
);
</script>

<style lang="scss" scoped>
.flex-center {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
</style>
