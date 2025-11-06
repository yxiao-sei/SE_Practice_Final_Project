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
  <recruit-modal @close-modal="closeModal" v-model="modalProps.visible" :fillData="fillData" v-bind="modalProps" />
  <delete-modal v-model="deleteVisible" v-bind="deleteProps" @close="closeDeleteModal" />
</template>
<script lang="ts" setup>
import { DeleteModal, useDelete } from '@/components/deleteModal';
import QueryForm from "@/asyncRoute/views/QueryForm.vue";
import ButtonGroup from "@/asyncRoute/buttonGroup/Index.vue";
import { usePage } from "@/asyncRoute/hooks/usePage";
import { useRouter } from "vue-router";
import { useBaseFieldStore } from "@/stores";
import RecruitModal from "./RecruitModal.vue";
import { reactive, ref, nextTick, onMounted, computed } from "vue";
import { deleteRequirementApi } from "@/service/api/requirement";
import { fetchBaseFieldEvent } from '@/events'
const routeName = "RecruitRequirements";
const { dispatchFetchDepartmentList } = useBaseFieldStore();
const projectCode = computed(() => router.currentRoute.value.query?.project_code)
const getDefaultFilters = async () => {
  if (projectCode.value) {
    return {
      filters: {
        "filters": {
          "projectCode": {
            "$eq": projectCode.value
          }
        },
      }
    }
  }
  return {
    filters: {}
  }
}
const { gridOptions, gridEvent, pageButtons, searchForm, updateQueryInfo, tableButtons, xGrid, queryFormEl } = usePage({ routeName, getDefaultFilters });

dispatchFetchDepartmentList()
const deleteSuccessCallBack = (ids: Array<string>) => {
  closeDeleteModal();
  const promiseArr = ids.map((id: string) => {
    return deleteRequirementApi(id);
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
    "title": "应聘课题名称"
  },
  {
    "align": "center",
    "field": "projectCode",
    "title": "应聘课题编号"
  }] as any,
  successCallBack: deleteSuccessCallBack,
});

const modalProps = reactive({
  header: '新增课题',
  visible: false
})
const fillData = ref(null)

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
      modalProps.header = '新增岗位需求'
      break;
    case 'Edit':
      modalProps.header = '编辑岗位需求'
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
onMounted(() => {
  fetchBaseFieldEvent('projectLabels')
  fetchBaseFieldEvent('projectOfDepartments')
  fetchBaseFieldEvent('memberRecruitmentStatus')
  fetchBaseFieldEvent('projectStatus')
  fetchBaseFieldEvent('postStatus')
})
</script>