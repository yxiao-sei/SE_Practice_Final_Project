<template>
    <div class="page-content">
        <vxe-grid ref="xGrid" v-bind="gridOptions" v-on="gridEvent">
            <template #toolbar_buttons>
                <query-form ref="queryFormEl" :searchForm="searchFormList" @updateQueryInfo="updateQueryInfo" />
            </template>
            <template #toolbar_tools>
                <ButtonGroup :actions="pageButtonList"
                    @handleCommand="(command: string) => handleCommand(command, null)" />
            </template>
            <template #result="{ row }">
                <span :style="{ color: status_color(row.result).color }">{{
                    status_color(row.result).cn }}</span>
            </template>
            <template #comment="{ row }">
                <el-tooltip :content="row.comment" raw-content>
                    <p class="ellipsis" v-html="row.comment"></p>
                </el-tooltip>
            </template>
            <template #progress="{ row }">
                <el-progress :percentage="calculateProgress(row)" :status="progressStatus(row)" />
            </template>
            progress
            <template #table_action_slot_default="{ row }">
                <el-button v-for="btn in tableButtonList" :key="btn.command" @click="handleCommand(btn.command, row)"
                    v-bind="btn.attributes" :loading="btn.attributes.loading == row.id ? true : false">{{
                        btn.label }}
                </el-button>
                <!-- <ButtonGroup :actions="tableButtonList" :rowId="row.id"
                    @handleCommand="(command: string) => handleCommand(command, row)" /> -->
            </template>
        </vxe-grid>
    </div>
</template>

<script setup lang="ts">
import QueryForm from "@/asyncRoute/views/QueryForm.vue";
import ButtonGroup from "@/asyncRoute/buttonGroup/Index.vue";
import { initializeMenus } from "@/asyncRoute/utils";
import { type VxeGridProps, type VxeTablePropTypes, type VxeGridListeners, type VxeGridInstance, Button } from "vxe-table";
import { shallowReactive, reactive, ref, computed, onMounted, onBeforeUnmount } from "vue";
import { useBaseFieldStore, useCacheUserInfoStore } from '@/stores';
import { useRoute } from "vue-router";
import { status_color } from "@/utils/index";
import { getImportRecord, getRecordFile } from "@/service/api/auditRecord/index";
import { searchFormList, pageButtons, tableButtons, tableColumns } from "../index";
const { username } = useCacheUserInfoStore();

const route = useRoute();
const defaultSearchParams = {};

const pageButtonList = initializeMenus(pageButtons);
const tableButtonList = ref(initializeMenus(tableButtons as any));

// 计算进度百分比
const calculateProgress = computed(() => (row: any) => {
    if (!row.totalRecordNumber || row.totalRecordNumber === 0) {
        return 0;
    }
    const progress = (row.progressedRecordNumber / row.totalRecordNumber) * 100;
    return parseFloat(progress.toFixed(1));
})
// 计算进度百分比
// 根据进度状态返回进度条状态
const progressStatus = computed(() => (row: any) => {
    if (row.result === 'success') {
        return 'success';
    } else if (row.result === 'error') {
        return 'exception';
    } else {
        return '';
    }
});

const xGrid = ref<VxeGridInstance<any>>()

const gridEvent: VxeGridListeners<any> = {
    proxyQuery() {
        console.log('数据代理查询事件')
    },
    proxyDelete() {
        console.log('数据代理删除事件')
    },
    proxySave() {
        console.log('数据代理保存事件')
    }
}

const getPaseSort = (columns: any[]) => {//表格排序
    const sortArr = columns.map(({ field, order }) =>
        `${field}:${order}`
    )
    if (sortArr.length <= 1) return sortArr.join(',');
    else return sortArr;
};

const queryInfo = ref(!searchFormList.length
    ? {}
    : searchFormList.reduce(
        (pre: any, cur: any) => {
            pre[cur.paramsKey] = cur.defaultValue;
            return pre;
        },
        defaultSearchParams ? defaultSearchParams : {}
    ));

const autoSearch = () => {
    queryInfo.value = { ...queryInfo.value, page: 1 };
    const $grid = xGrid.value
    if ($grid) {
        $grid.commitProxy('query')
    }
}
const updateQueryInfo = (info: any) => {
    // 当是表单查询的时候默认page
    queryInfo.value = {
        ...info,
        page: 1,
    };
    autoSearch();
};

const gridOptions = reactive<VxeGridProps<any>>({
    border: true,
    loading: false,
    height: "auto",
    keepSource: true,
    showOverflow: true,
    id: route.name as string,
    columnConfig: {
        resizable: true,
    },
    rowConfig: {
        keyField: 'id',
        isHover: true
    },
    pagerConfig: {
        enabled: true,
        currentPage: 1,
        pageSize: 20,
        pageSizes: [5, 10, 15, 20, 50, 100, 200, 500, 1000]
    },
    scrollY: { enabled: true, gt: 30 },
    columns: tableColumns as any[],
    customConfig: {
        immediate: false,
        confirmButtonText: "确定",
        storage: true,
        allowFixed: false
    },
    toolbarConfig: {
        className: "custom-toolbar-layout", // 自定义工具栏样式类名，用于自定义样式
        slots: {
            buttons: "toolbar_buttons",
            tools: 'toolbar_tools'
        },
        refresh: {
            code: "query",
        }, // 显示刷新按钮
        zoom: true, // 显示全屏按钮
        custom: true,
    },
    checkboxConfig: {
        labelField: 'id',
        reserve: true,
        highlight: true,
        range: true
    },
    sortConfig: {
        multiple: true,
        remote: true // 启用远程排序，当点击排序时会自动触发 query 行为
    },
    proxyConfig: {
        seq: true, // 启用动态序号代理，每一页的序号会根据当前页数变化
        sort: true, // 启用排序代理，当点击排序时会自动触发 query 行为
        filter: true, // 启用筛选代理，当点击筛选时会自动触发 query 行为
        form: true, // 启用表单代理，当点击表单提交按钮时会自动触发 reload 行为
        props: {
            // 对应响应结果 Promise<{ result: [], page: { total: 100 } }>
            result: 'result', // 配置响应结果列表字段
            total: 'count' // 配置响应结果总页数字段
        },
        // 只接收Promise，具体实现自由发挥
        ajax: {
            // 当点击工具栏查询按钮或者手动提交指令 query或reload 时会被触发
            query: ({ page, sorts, filters, form }) => {
                return getData({
                    // sort: getPaseSort(sorts),
                    page: page.currentPage,
                    pageSize: page.pageSize,
                })
            },
        }
    }
});
const pageInfo = ref()
const getData = async (queryParams: any) => {
    if (queryInfo.value.beginTime && queryInfo.value.endTime) {
        queryInfo.value.createdTime = queryInfo.value.beginTime + "," + queryInfo.value.endTime
    }
    delete queryInfo.value.beginTime;
    delete queryInfo.value.endTime;
    gridOptions.loading = false;
    pageInfo.value = queryParams;
    return getImportRecord({ ...queryParams, ...queryInfo.value, username: username })
};

const closeModal = async (status?: string) => {
    if (status === 'success') {
        // const result = await Api.findListApi({ ...queryInfo.value }, { loading: false });
        // xGrid.value?.reloadData(result.result);
    }
};
const handleCommand = async (command: string, row: any) => {
    switch (command) {
        case 'download':
            try {
                let params = {
                    fileName: row.outerDataSourceFileName
                };
                setButtonLoding("download", row.id)
                await getRecordFile(params).catch(() => {
                    setButtonLoding("download", false)
                }).finally(() => {
                    setButtonLoding("download", false)
                });
            } catch (err) {
                console.error(err);
            }
            break;
    }
};

const setButtonLoding = (command: string, id: any) => {
    tableButtonList.value = tableButtonList.value.map((item: any) => {
        if (item.command == command) {
            item.attributes.loading = id;
        }
        return item;
    })
}

// 启动轮询
let pollingInterval: any | null = null;

const startPolling = () => {
    pollingInterval = setInterval(async () => {
        const $grid = xGrid.value;// 自动刷新时传递一个标志位
        if ($grid) {
            if (queryInfo.value.beginTime && queryInfo.value.endTime) {
                queryInfo.value.createdTime = queryInfo.value.beginTime + "," + queryInfo.value.endTime
            }
            delete queryInfo.value.beginTime;
            delete queryInfo.value.endTime;
            const { result } = await getImportRecord({ ...pageInfo.value, ...queryInfo.value, username: username });
            $grid.loadData(result);
            // $grid.commitProxy("query");
        }
    }, 10000); // 每10秒刷新一次数据
};
// 停止轮询
const stopPolling = () => {
    if (pollingInterval) {
        clearInterval(pollingInterval);
        pollingInterval = null; // 确保停止轮询后将变量重置为null
    }
}

onMounted(() => {
    startPolling();//开启轮询
});

onBeforeUnmount(() => {
    stopPolling();
})

</script>

<style scoped>
.ellipsis {
    display: inline-block;
    max-width: 200px;
    /* 设定最大宽度 */
    white-space: nowrap;

}
</style>