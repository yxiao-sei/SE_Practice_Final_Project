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
  <update-item-modal role="teacher" :fillData="fillData" v-model="updateModal.visible" v-bind="updateModal"
    @close-modal="closeModal" />
</template>
<script lang="ts" setup>
import { DeleteModal, useDelete } from '@/components/deleteModal';
import QueryForm from "@/asyncRoute/views/QueryForm.vue";
import ButtonGroup from "@/asyncRoute/buttonGroup/Index.vue";
import { usePage } from "@/asyncRoute/hooks/usePage";
import { useRoute } from "vue-router";
import { useBaseFieldStore } from "@/stores";
import { reactive, toRefs, ref, nextTick } from "vue";
import { deleteAdministratorApi} from "@/service/api/administrator";
import UpdateItemModal from "./UpdateModal.vue";
const { dispatchRoleList, dispatchFetchDepartmentList } = useBaseFieldStore();
const routeName = "SystemUser";
const getDefaultFilters = async () => {
  const { roleTeacherList }: any = await dispatchRoleList()
  return {
    filters: {
      "filters": {
        "$or": roleTeacherList.map((item: any) => {
          return {
            "roles": {
              "$contains": item.value
            }
          }
        })
      },
    },
    query: [
      {
        "elementType": "Select",
        "span": 3,
        "label": "选择角色搜索",
        "pageKey": "roles",
        "formItem": {
          "name": "label",
          "config": {}
        },
        "options": roleTeacherList,
        "attributes": {
          "size": "large",
          "multiple": true,
          "clearable": true,
          "filterable": true,
          "collapse-tags": true
        },
        paseInfo: (value: string[]) => {
          return {
            "filters": {
              "$and": value.map((item: any) => {
                return {
                  "roles": {
                    "$contains": item
                  }
                }
              })
            }
          }
        }
      }
    ]
  }
}

const { gridOptions, gridEvent, pageButtons, handleCommand, searchForm, updateQueryInfo, tableButtons, xGrid, queryFormEl } = usePage({ routeName, getDefaultFilters });

dispatchFetchDepartmentList()
const deleteSuccessCallBack = (ids: Array<string>) => {
  closeDeleteModal();
  const promiseArr = ids.map((id: string) => {
    return deleteAdministratorApi(id);
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
const fillData = ref(null)

const closeModal = (command = '') => {
  if (command === 'refresh') {
    const $grid = xGrid.value
    if ($grid) {
      $grid.commitProxy('query')
    }
  }
  updateModal.visible = false
  fillData.value = null
}
const handleCommandStart = async (command: string, row: any) => {
  if (command === "Add") {
    updateModal.visible = true
  } else if (command === 'Edit') {
    updateModal.header = '编辑用户'
    updateModal.subheader = '编辑院系秘书用户'
    updateModal.visible = true
    await nextTick()
    fillData.value = row
  } else if (command === 'Delete') {
    openDeleteModal({ items: [row] });
  } else {
    handleCommand(command, row)
  }
}
</script>