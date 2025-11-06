<template>
<div class="page-content">
    <vxe-grid ref="xGrid" v-bind="gridOptions" v-on="gridEvent">
        <template #toolbar_buttons>
            <query-form ref="queryFormEl" :searchForm="searchForm" @updateQueryInfo="updateQueryInfo" />
        </template>
        <template #toolbar_tools>
            <ButtonGroup :actions="pageButtons" @handleCommand="(command: string) => handleCommand(command, null)" />
        </template>
        <template #jsonData="{ row }">
            <span v-if="!row.jsonData || row.jsonData === '-'">{{ row.jsonData }}</span>
            <template v-else>
                <span class="td-line" v-for="(i, index) in row.jsonData" :key="index">
                    <span v-if="i.label">{{ i.label }}</span>
                    <span v-if="i.value || i.type === 'number'">：{{ i.value }}</span>
                </span>
            </template>
        </template>
        <template #table_action_slot_default="{ row }">
            <ButtonGroup :actions="tableButtons" @handleCommand="(command: string) => handleCommand(command, row)" />
        </template>
    </vxe-grid>
</div>
<dictionary-item-modal :header="dictionaryReactive.header" :subheader="dictionaryReactive.subheader"
    v-model="dictionaryReactive.visible" :fillData="fillData" @closeModal="closeModal" />
<dictionary-category-modal :header="categoryReactive.header" :subheader="categoryReactive.subheader"
    v-model="categoryReactive.visible" :fillData="categoryData" @closeModal="closeModal" />
<delete-modal v-model="deleteVisible" v-bind="deleteProps" @close="closeDeleteModal" />
</template>

<script setup lang="ts">
import { DeleteModal, useDelete } from '@/components/deleteModal';
import QueryForm from "@/asyncRoute/views/QueryForm.vue";
import { reactive, ref, nextTick } from "vue";
import DictionaryItemModal from './DictionaryItem.vue';
import DictionaryCategoryModal from './DictionaryCategory.vue';
import ButtonGroup from "@/asyncRoute/buttonGroup/Index.vue";
import { deleteMetaDataApi } from '@/service/api/meta-data';
import { usePage } from "@/asyncRoute/hooks/usePage";
import { fetchBaseFieldEvent } from "@/events";

const routeName = "Dictionary";
const { gridOptions, gridEvent, pageButtons, searchForm, updateQueryInfo, tableButtons, xGrid, queryFormEl } =
    usePage({ routeName });
const transformJsonData = (jsonData: string) => {
    try {
        return Object.keys(jsonData).map((key) => {
            return {
                key,
                value: jsonData[key],
            };
        });
    } catch (error) {
        return [];
    }
};


const fillData = ref<any>(null);
const dictionaryReactive = reactive({
    header: '添加字典',
    subheader: '新增一个字典',
    visible: false,
});
const categoryData = ref<any>(null)
const categoryReactive = reactive({
    header: '添加字典',
    subheader: '新增一个字典',
    visible: false,
});

const closeModal = async (status?: string) => {
    if (status === 'success') {
        const $grid = xGrid.value
        if ($grid) {
            $grid.commitProxy('query')
        }
        fetchBaseFieldEvent('metadataCategory');
    }
    dictionaryReactive.visible = false
    categoryReactive.visible = false;
    categoryData.value = null;
    fillData.value = null;
};

// 删除成功的回调
const deleteSuccessCallBack = (ids: Array<string>) => {
    closeDeleteModal();
    const promiseArr = ids.map((id: string) => {
        return deleteMetaDataApi(id);
    });
    Promise.all(promiseArr).then(async () => {
        const $grid = xGrid.value
        if ($grid) {
            $grid.commitProxy('query')
        }
    });
};
// 点击删除
const { deleteProps, deleteVisible, closeDeleteModal, openDeleteModal } = useDelete({
    fields: [
        {
            "field": "category",
            "sortable": true,
            "align": "center",
            "title": "字典类别"
        },
        {
            "field": "name",
            "sortable": true,
            "align": "center",
            "title": "字典名称"
        },
        {
            "field": "description",
            "sortable": true,
            "align": "center",
            "title": "描述"
        }
    ] as any,
    successCallBack: deleteSuccessCallBack,
});
const handleCommand = async (command: string, row: any) => {
    switch (command) {
        case 'add':
            fetchBaseFieldEvent('metadataCategory');
            dictionaryReactive.visible = true;
            dictionaryReactive.header = '添加字典';
            dictionaryReactive.subheader = '新增一个字典';
            break;
        case 'addCategory':
            categoryReactive.visible = true;
            categoryReactive.header = '添加字典类别';
            break
        case 'update':
            fetchBaseFieldEvent('metadataCategory');
            if (row.isModifiable) {
                dictionaryReactive.visible = true;
                dictionaryReactive.header = '编辑字典';
                await nextTick();
                fillData.value = row;
            } else {
                categoryReactive.visible = true;
                categoryReactive.header = '编辑字典类别';
                await nextTick();
                categoryData.value = row;
            }
            break;
        case 'delete':
            openDeleteModal({ items: [row] });
            break;
    }
};

fetchBaseFieldEvent('metadataCategory');
</script>

<style scoped>
.td-line {
    display: block;
    line-height: 21px;
}
</style>