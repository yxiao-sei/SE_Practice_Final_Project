<template>
<div class="page-content">
  <vxe-grid @cell-dblclick="({ row }: any) => handleCommandStart('Detail', row)" ref="xGrid" v-bind="gridOptions"
    v-on="gridEvent">
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
      <ButtonGroup :actions="tableButtons" @handleCommand="(command: string) => handleCommandStart(command, row)" />
    </template>
  </vxe-grid>
</div>
<project-modal @close-modal="closeModal" v-model="modalProps.visible" :fillData="fillData" v-bind="modalProps" />
<delete-modal v-model="deleteVisible" v-bind="deleteProps" @close="closeDeleteModal" />
</template>
<script lang="ts" setup>
import { DeleteModal, useDelete } from '@/components/deleteModal';
import QueryForm from "@/asyncRoute/views/QueryForm.vue";
import ButtonGroup from "@/asyncRoute/buttonGroup/Index.vue";
import { usePage } from "@/asyncRoute/hooks/usePage";
import { useRouter } from "vue-router";
import { useBaseFieldStore, useCacheUserInfoStore } from "@/stores";
import ProjectModal from "./ProjectModal.vue";
import { reactive, ref, nextTick, onMounted } from "vue";
import { fetchBaseFieldEvent } from '@/events';
import { deleteProjectApi } from "@/service/api/project";
import { storeToRefs } from "pinia"
import TableFile from "@/components/TableFile.vue";

const { userInfo } = storeToRefs(useCacheUserInfoStore());
const { dispatchFetchDepartmentList } = useBaseFieldStore();
const routeName = "Projects";
const {
  gridOptions,
  gridEvent,
  pageButtons,
  searchForm,
  updateQueryInfo,
  tableButtons,
  xGrid,
  queryFormEl
} = usePage({ routeName });

dispatchFetchDepartmentList()
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
    "align": "center",
    "field": "name",
    "title": "课题名称",
    "sortable": true
  },
  {
    "align": "center",
    "field": "code",
    "title": "课题编号",
    "sortable": true
  },
  {
    "align": "center",
    "field": "description",
    "title": "课题介绍",
    "sortable": true
  },
  {
    "align": "center",
    "field": "researchStatus",
    "title": "课题状态"
  }] as any,
  successCallBack: deleteSuccessCallBack,
});

const modalProps = reactive({
  header: '新增课题',
  visible: false
})
const fillData = ref<any>(null)

const closeModal = (command = '') => {
  if (command === 'refresh') {
    const $grid = xGrid.value
    if ($grid) {
      $grid.commitProxy('query')
    }
  }
  modalProps.visible = false
  fillData.value = null
}
const router = useRouter()
const handleCommandStart = async (command: string, row: any) => {
  switch (command) {
    case 'Add':
      modalProps.visible = true
      modalProps.header = '新增课题'
      await nextTick()
      fillData.value = {
        leadingTeacherName: userInfo.value.realName,
        leadingTeacherNumber: userInfo.value.username,
        departmentName: userInfo.value.departmentName
      }
      break;
    case 'Edit':
      modalProps.header = '编辑课题'
      modalProps.visible = true
      await nextTick()
      fillData.value = row
      break;
    case 'Delete':
      openDeleteModal({ items: [row] });
      break;
    case 'Detail':
      router.push({ name: 'ProjectDetail', query: { project_code: row.code } })
      break;
    default:
      break;
  }
}
onMounted(() => {
  fetchBaseFieldEvent('projectLabels')
  fetchBaseFieldEvent('projectOfDepartments')
  fetchBaseFieldEvent('memberRecruitmentStatus')
  fetchBaseFieldEvent('projectStatus')
  fetchBaseFieldEvent('metadataDirection')
})
</script>