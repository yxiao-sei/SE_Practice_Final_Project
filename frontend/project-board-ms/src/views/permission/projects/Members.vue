<template>
  <div class="page-content">
    <vxe-grid ref="xGrid" v-bind="gridOptions" v-on="gridEvent">
      <template #toolbar_buttons>
        <query-form ref="queryFormEl" :searchForm="searchForm" @updateQueryInfo="updateQueryInfo" />
      </template>
      <template #toolbar_tools>
        <ButtonGroup :actions="pageButtons" @handleCommand="(command: string) => handleCommandStart(command, null)" />
      </template>
      <template #table_action_slot_default="{ row, columns }">
        <ButtonGroup :actions="tableButtons" @handleCommand="(command: string) => handleCommandStart(command, row)" />
      </template>
    </vxe-grid>
  </div>
  <delete-modal v-model="deleteVisible" v-bind="deleteProps" @close="closeDeleteModal" />
</template>
<script lang="ts" setup>
import { DeleteModal, useDelete } from '@/components/deleteModal';
import QueryForm from "@/asyncRoute/views/QueryForm.vue";
import ButtonGroup from "@/asyncRoute/buttonGroup/Index.vue";
import { usePage } from "@/asyncRoute/hooks/usePage";
import { reactive } from "vue";
import { createProjectApi, deleteProjectApi } from "@/service/api/project";
const routeName = "Members";

const { gridOptions, gridEvent, pageButtons, handleCommand, searchForm, updateQueryInfo, tableButtons, xGrid, queryFormEl } = usePage({ routeName });
const deleteSuccessCallBack = (ids: Array<string>) => {
  closeDeleteModal();
  const promiseArr = ids.map((id: string) => {
    return deleteProjectApi(id);
  });
  Promise.all(promiseArr).then(async () => {
    const $grid = xGrid.value
    if ($grid) {
      $grid.commitProxy('query')
    }
  });
};
const { deleteProps, deleteVisible, closeDeleteModal, openDeleteModal } = useDelete({
  fields: [{
    props: {
      label: '姓名',
      prop: 'name',
      sortable: 'custom',
      showOverflowTooltip: true,
    },
    isPromptField: true,
  },
  {
    props: {
      label: '工号',
      prop: 'number',
      sortable: 'custom',
      showOverflowTooltip: true,
    },
    isPromptField: true,
  },],
  successCallBack: deleteSuccessCallBack,
});

const updateModal = reactive({
  header: '新增用户',
  subheader: '新增用户用户',
  visible: false
})
const handleCommandStart = async (command: string, row: any) => {
  switch (command) {
    case 'Add':
      updateModal.visible = true
      break;
    case 'Delete':
      openDeleteModal({ items: [row] });
      break;
    default:
      break;
  }
}
</script>