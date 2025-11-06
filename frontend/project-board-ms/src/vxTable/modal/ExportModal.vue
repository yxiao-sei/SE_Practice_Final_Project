<template>
  <commonDialog v-model="visible" :disabled="selectedList.length === 0" :loading="loading" @close="closeModal">
    <div class="flex-center">
      <el-checkbox v-model="checkAll" size="large" :indeterminate="isIndeterminate"
        @change="handleCheckAllChange">全选</el-checkbox>
      <span class="el-checkbox el-checkbox--large text-button" @click="handleCheckInvertChange">反选</span>
    </div>
    <el-checkbox-group class="common-checkbox-group" v-model="selectedList" @change="selectedListChange">
      <el-checkbox size="large" v-for="head in columns" :key="head.title" :label="head.title"></el-checkbox>
    </el-checkbox-group>

    <div class="rules-style">
      <el-checkbox v-model="single">
        我已阅读并会遵守<a href="javascript: void(0);" @click="rules = true">《研究生教育质量数据使用与保密条款》</a>
      </el-checkbox>
    </div>
    <RulesModal :rulesVisible="rules" @closeRulesMoadl="closeRulesMoadl"></RulesModal>
  </commonDialog>
</template>

<script lang="ts" setup>
import commonDialog from '~/enDialog/Index.vue';
import RulesModal from './RulesModal.vue';
import { downLoadStrapi, exportFiles } from '@/utils/downLoad';
import { ElMessage } from 'element-plus';
import { ref, toRefs, type PropType, shallowRef } from "vue";
type Column = {
  title: string,
  visible: boolean,
  property: string
}
const props = defineProps({
  closeModal: Function,
  visible: Boolean,
  url: String,
  fileName: String,
  params: String,
  columns: Array as PropType<Column[]>
})
const columns = shallowRef(props.columns || []);
const visible = ref(props.visible);
const loading = ref(false);
const selectedList = ref<any[]>(columns.value.filter((i) => i.visible).map((i) => i.title));
const close = () => {
  visible.value = false;
  setTimeout(() => {
    if (props.closeModal) {
      props.closeModal();
    }
  }, 2000);
}

const checkAll = ref(false);
const isIndeterminate = ref(true);

const selectedListChange = () => {
  if (selectedList.value.length === columns.value.length) {
    checkAll.value = true;
    isIndeterminate.value = false;
  } else if (!selectedList.value.length) checkAll.value = false;
  else {
    checkAll.value = false;
    isIndeterminate.value = true;
  }
};

const handleCheckAllChange = (val: any) => {
  selectedList.value = val ? columns.value.map((i) => i.title) : [];
  isIndeterminate.value = false;
  selectedListChange();
};

const handleCheckInvertChange = () => {
  const newList = columns.value
    .filter((i) => !selectedList.value.includes(i.title))
    .map((i) => i.title);
  selectedList.value = newList;
  selectedListChange();
};

const rules = ref(false);
const single = ref(false);
const closeModal = async (status: string) => {
  if (status === 'confirm') {
    if(!single.value){
      ElMessage.warning('请先阅读《研究生教育质量数据使用与保密条款》');
      return;
    }
    loading.value = true;
    const { url = "", params = "", fileName = "下载文件" } = props;
    const titles = columns.value
      .filter((item) => selectedList.value.includes(item.title))
      .map((i: any) => {
        return { [i.property]: i.title };
      })
    // await downLoadStrapi({
    //   url,
    //   params,
    //   fileName,
    //   titles
    // }).finally(() => {
    //   loading.value = false;
    // })
    /* 虚假下载 开始*/
    await exportFiles('/app/fileExport/export', {
      url,
      params,
      fileName,
      titles
    })
    /* 虚假下载 结束*/
    close();
  } else {
    close();
  }
}

// 关闭规则弹框
const closeRulesMoadl = () => {
  rules.value = false;
}
</script>

<style lang="scss" scoped>
.flex-center {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.text-button {
  margin-left: 20px;
}

.rules-style {
  margin-top: 20px;
  text-align: right;
}
</style>
