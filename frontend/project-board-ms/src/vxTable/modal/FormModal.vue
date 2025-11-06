<template>
<commonDialog v-model="visible" :loading="loading" @close="closeModal">
  <el-form label-position="top">
    <el-row :gutter="12">
      <el-col v-for="item in renderColumns" :key="item.name" :span="item.span">
        <el-form-item :label="item.title">
          <el-input v-if="item.name === 'elInput'" placeholder="请输入" v-bind="item.attributes" />
          <el-select v-else-if="item.name === 'elSelect'" placeholder="请选择" v-bind="item.attributes">
            <el-option :label="option.label" :value="option.value ?? ''" :key="index"
              v-for="option, index in item.options"></el-option>
          </el-select>
          <el-date-picker v-else-if="item.name === 'elDate'" placeholder="请选择起始时间" style="width: 100%"
            v-bind="item.attributes" />
        </el-form-item>
      </el-col>
    </el-row>

  </el-form>
</commonDialog>
</template>

<script lang="ts" setup>
import commonDialog from '~/enDialog/Index.vue';
import { ElMessage } from 'element-plus';
import { ref, toRef, shallowRef, type PropType, watchPostEffect } from "vue";
type Column = {
  title: string,
  visible: boolean,
  property: string
}
const props = defineProps({
  closeModal: Function,
  visible: Boolean,
  columns: Array as PropType<any[]>
})
const visible = ref(props.visible);
const loading = ref(false);
const renderColumns = shallowRef<any[]>([]);
const close = () => {
  visible.value = false;
  setTimeout(() => {
    if (props.closeModal) {
      props.closeModal();
    }
  }, 2000);
}
const closeModal = async (status: string) => {
  if (status === 'confirm') {
  } else {
    close();
  }
}

watchPostEffect(() => {
  if (props.columns?.length) {
    renderColumns.value = props.columns.map((item) => {
      return {
        ...item,
        name: item.element[0],
        attributes: item.element[1] ?? {}
      }
    })
  }
})
</script>
