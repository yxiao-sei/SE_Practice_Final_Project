<template>
  <div class="page-content">
    <vxe-grid ref="xGrid" v-bind="gridOptions" v-on="gridEvent">
      <template #attachment="{ row }">
        <TableFile :attachments="row.attachments" />
      </template>
      <template #toolbar_buttons>
        <query-form ref="queryFormEl" :searchForm="searchForm" @updateQueryInfo="updateQueryInfo" />
      </template>
      <template #toolbar_tools>
        <ButtonGroup :actions="pageButtons" @handleCommand="(command: string) => handleCommandStart(command, null)" />
      </template>
      <template #table_action_slot_default="{ row, columns }">
        <ButtonGroup :actions="computedActions(row.state)"
          @handleCommand="(command: string) => handleCommandStart(command, row)" />
      </template>
    </vxe-grid>
  </div>
  <application-modal @close-modal="closeModal" v-model="modalProps.visible" :fillData="fillData" v-bind="modalProps" />
  <approve-modal @close-modal="closeModal" v-model="approveProps.visible" :fillData="approveData"
    v-bind="approveProps" />
  <delete-modal v-model="deleteVisible" v-bind="deleteProps" @close="closeDeleteModal" />
</template>
<script lang="ts" setup>
import { DeleteModal, useDelete } from '@/components/deleteModal';
import QueryForm from "@/asyncRoute/views/QueryForm.vue";
import ButtonGroup from "@/asyncRoute/buttonGroup/Index.vue";
import { usePage } from "@/asyncRoute/hooks/usePage";
import ApplicationModal from "./ApplicationModal.vue";
import ApproveModal from "./ApproveModal.vue";
import { reactive, ref, nextTick, computed } from "vue";
import { deleteAppointmentApi } from "@/service/api/appointment";
import TableFile from "@/components/TableFile.vue";
const routeName = "Appointment";

const computedActions = computed(() => {
  return (state: string) => {
    return tableButtons.value;
  }
})

const { gridOptions, gridEvent, pageButtons, searchForm, updateQueryInfo, tableButtons, xGrid, queryFormEl } = usePage({ routeName });

const deleteSuccessCallBack = (ids: Array<string>) => {
  closeDeleteModal();
  const promiseArr = ids.map((id: string) => {
    return deleteAppointmentApi(id);
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
    "align": "center",
    "field": "studentNumber",
    "title": "学号",
    "sortable": true
  },
  {
    "align": "center",
    "field": "studentName",
    "title": "姓名",
    "sortable": true
  },
  {
    "align": "center",
    "field": "projectName",
    "title": "课题名称"
  },
  {
    "align": "center",
    "field": "projectCode",
    "title": "课题编号"
  }] as any,
  successCallBack: deleteSuccessCallBack,
});

const modalProps = reactive({
  header: '新增课题',
  visible: false
})
const fillData = ref(null)

const approveProps = reactive({
  header: '预约审批',
  subheader: '查看预约详情，确认是否接受预约',
  visible: false
})
const approveData = ref(null)

const closeModal = (command = '') => {
  if (command === 'refresh') {
    const $grid = xGrid.value
    if ($grid) {
      $grid.commitProxy('query')
    }
  }
  modalProps.visible = false
  approveProps.visible = false
  fillData.value = null
  approveData.value = null
}
const handleCommandStart = async (command: string, row: any) => {
  switch (command) {
    case 'Add':
      modalProps.visible = true
      modalProps.header = '新增预约'
      break;
    case 'Approve':
      approveProps.visible = true
      approveData.value = row
      break;
    case 'Edit':
      modalProps.header = '编辑预约'
      modalProps.visible = true
      await nextTick()
      break;
    case 'Delete':
      openDeleteModal({ items: [row] });
      break;
    default:
      break;
  }
  if (command === "Add") {
  } else if (command === 'Edit') {
    fillData.value = row
  } else if (command === 'Delete') {
  } else {
  }
}
</script>