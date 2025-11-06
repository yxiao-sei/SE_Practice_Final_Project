<template>
  <div :gutter="10" class="child-page-search" v-if="searchList && searchList.length">
    <div v-for="searchEl in searchList" class="c-col"
      :style="searchEl.elementType === 'Select' ? { minWidth: `${4.16 * searchEl.span}%` } : { width: `${4.16 * searchEl.span}%` }"
      :key="searchEl.pageKey">
      <search-block :label="searchEl.label">
        <el-input @change="autoSearch" v-if="searchEl.elementType === 'Input'" placeholder="请输入" :class="searchEl.className"
          v-model.trim="queryInfo[searchEl.pageKey]" v-bind="searchEl.attributes" />
        <el-select @change="autoSearch" :class="searchEl.className" v-else-if="searchEl.elementType === 'Select'"
          placeholder="请选择" v-model="queryInfo[searchEl.pageKey]" v-bind="searchEl.attributes">
          <el-option :label="option.label" :value="option.value" :key="index"
            v-for="option, index in searchEl.options"></el-option>
        </el-select>
        <template v-else-if="searchEl.elementType === 'Date'">
          <el-date-picker @change="autoSearch" :class="searchEl.className" placeholder="请选择起始时间" style="width: 100%"
            v-model="queryInfo[searchEl.pageKey]" v-bind="searchEl.attributes" />
        </template>
      </search-block>
    </div>
  </div>
</template>

<script setup lang="ts">
import { toRefs, ref, watchEffect } from "vue";
import { remoteMethod } from "../utils";
import { findFormListApi } from "@/service/api/form-config";
import { useBaseFieldStore } from "@/stores";

const emits = defineEmits(["updateQueryInfo"]);
const props = defineProps({
  searchForm: {
    type: Array<any>,
    default: () => []
  }
});
const searchList = ref<any[]>([])
const queryInfo = ref<any>({});
const baseStore = useBaseFieldStore();
const autoSearch = () => {
  let params: any = {}
  Object.keys(queryInfo.value).map(key => {
    const paseInfo = props.searchForm.find(i => i.pageKey === key)?.paseInfo
    if (paseInfo && ((Array.isArray(queryInfo.value[key]) && queryInfo.value[key].length) || (!Array.isArray(queryInfo.value[key]) && queryInfo.value[key]))) {
      params = {
        ...params,
        ...paseInfo(queryInfo.value[key])
      }
    } else {
      params[key] = queryInfo.value[key]
    }
  })
  emits('updateQueryInfo', params);
};
const setDefaultParams = (params: any) => {
  queryInfo.value = { ...queryInfo.value, ...params }
}

watchEffect(async () => {
  if (props.searchForm && props.searchForm.length) {
    searchList.value = props.searchForm.map(item => {
      item.pageKey = item.pageKey ?? item.paramsKey;
      if (item.storeKey) {
        item.options = baseStore[item.storeKey]
      }
      return item;
    })
  } else {
    searchList.value = [];
  }
});

defineExpose({ setDefaultParams });
</script>

<style scoped></style>