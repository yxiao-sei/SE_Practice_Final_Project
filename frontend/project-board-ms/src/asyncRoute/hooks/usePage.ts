import { useRouter, useRoute } from "vue-router";
import { shallowReactive, shallowRef, ref, nextTick } from "vue";
import { findPageListApi } from "@/service/api/pageConfig";
import { getTableColumnWidth, initializeMenus } from "../utils";
import { Strapi } from "@/service/strPagedApi";
import { useCacheRouteHistory } from "@/stores";
import { triggerEmit } from "@/vxTable";
import type {
  VxeGridProps,
  VxeGridInstance,
  VxeGridListeners,
} from "vxe-table";
const exportBaseUrl = "/api/";
export const usePage = ({ routeName, getDefaultFilters }: { routeName: string, getDefaultFilters?: Function }) => {
  let Api: any = {};
  const { routeHistory } = useCacheRouteHistory();
  const router = useRouter();
  const route = useRoute();
  const routesName = route.params.name as string;
  // 获取下钻的记录的查询条件
  let routerParamsList = routeHistory.filter((r) => r.name === routesName);
  const tableButtons = shallowRef<any[]>([]);
  const searchForm = shallowRef<any[]>([]);
  const queryFormEl = ref();
  const pageButtons = shallowRef<any[]>([]);
  const queryInfo = ref({});
  const xGrid = ref<VxeGridInstance<any>>();
  const emitProps = {
    getTableColumn: () => {
      const blackList = ["table_action_slot_default"];
      let fullColumn: any[] = [];
      if (xGrid.value) {
        fullColumn = xGrid.value
          ?.getTableColumn()
          .fullColumn.filter((item: any) => !blackList.includes(item.property));
      }
      return fullColumn;
    },
    fileName: route.meta.menuName,
    url: "",
    params: "",
    total: 0,
  };
  const gridEvent: VxeGridListeners<any> = {
    proxyQuery() {
      console.log("数据代理查询事件");
    },
    proxyDelete() {
      console.log("数据代理删除事件");
    },
    proxySave() {
      console.log("数据代理保存事件");
    },
  };

  const getPaseSort = (columns: any[]) => {
    const sortArr = columns.map(({ field, order }) => `${field}:${order}`);
    if (sortArr.length <= 1) return sortArr.join(",");
    else return sortArr;
  };

  const gridOptions = shallowReactive<VxeGridProps<any>>({
    border: true,
    loading: false,
    height: "auto",
    keepSource: true,
    showOverflow: true,
    align: 'center',
    id: routeName,
    resizeConfig: {
      refreshDelay: 0
    },
    columnConfig: {
      resizable: true,
    },
    rowConfig: {
      keyField: "id",
      isHover: true,
    },
    pagerConfig: {
      enabled: true,
      currentPage: 1,
      pageSize: 20,
      pageSizes: [10, 15, 20, 50, 100, 200, 500, 1000],
    },
    scrollY: { enabled: true, gt: 30 },
    scrollX: {
      enabled: true,
      gt: 30,
    },
    columns: [],
    customConfig: {
      immediate: false,
      confirmButtonText: "确定",
      storage: true,
      allowFixed: false,
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
      labelField: "id",
      reserve: true,
      highlight: true,
      range: true,
    },
    sortConfig: {
      multiple: true,
      remote: true, // 启用远程排序，当点击排序时会自动触发 query 行为
    },
    editConfig: {
      enabled: false
    },
    // layouts: ["Toolbar", "Form", "Top", "Table", "Bottom", "Pager"],
    proxyConfig: {
      seq: true, // 启用动态序号代理，每一页的序号会根据当前页数变化
      sort: true, // 启用排序代理，当点击排序时会自动触发 query 行为
      filter: false, // 启用筛选代理，当点击筛选时会自动触发 query 行为
      form: true, // 启用表单代理，当点击表单提交按钮时会自动触发 reload 行为
      props: {
        // 对应响应结果 Promise<{ result: [], page: { total: 100 } }>
        result: "result", // 配置响应结果列表字段
        total: "count", // 配置响应结果总页数字段
      },
      // 只接收Promise，具体实现自由发挥
      ajax: {
        // 当点击工具栏查询按钮或者手动提交指令 query或reload 时会被触发
        query: ({ page, sorts, filters, form }) => {
          return getData({
            sort: getPaseSort(sorts),
            "pagination[page]": page.currentPage,
            "pagination[pageSize]": page.pageSize,
          });
        },
      },
    },
  });

  const handleCommand = (command: string, row: any | null) => {
    triggerEmit(command, { row, ...emitProps, Api });
  };

  const setDefaultParams = async (result: any) => {
    const { query = null } = getDefaultFilters ? await getDefaultFilters() : {};
    const { defaultParams, searchForm: _originSearchForm }: any = { ...result };
    const originSearchForm = JSON.parse(JSON.stringify(_originSearchForm));
    if (query) {
      originSearchForm.push(...query)
    }
    if (originSearchForm && originSearchForm.length) {
      queryInfo.value = originSearchForm.reduce(
        (pre: any, cur: any) => {
          pre[cur.paramsKey] = cur.defaultValue;
          try {
            if (routerParamsList && routerParamsList.length > 0) {
              const paramsObj = JSON.parse(routerParamsList[0].params);
              pre[cur.paramsKey] = [paramsObj[cur.paramsKey]];
            }
          } catch (error) {
            console.log("记录error", error);
          }
          return pre;
        },
        defaultParams ? defaultParams : {}
      )
    }
    searchForm.value = originSearchForm || [];
    queryFormEl.value.setDefaultParams(queryInfo.value);
  };
  const initPage = async () => {
    gridOptions.loading = true;
    const resultArray: any = await findPageListApi(
      {
        filters: {
          name: {
            $eq: routeName,
          },
        },
      },
      { loading: false }
    ).finally(() => (gridOptions.loading = false));
    const result = resultArray?.result?.[0] || {};
    Api = Strapi({ url: result.apiPath });
    emitProps.url = exportBaseUrl + result.apiPath;
    tableButtons.value = initializeMenus(result.tableButtons);
    pageButtons.value = initializeMenus(result.pageButtons);
    gridOptions.columns = result.columns.map((column: any) => {
      return {
        formatter: "commonFormatter",
        ...column,
        minWidth: getTableColumnWidth(column),
      };
    });
    setDefaultParams(result);
  }
  const getData = async (queryParams: any) => {
    if (!Api?.Get) await initPage();
    const { filters = {} } = getDefaultFilters ? await getDefaultFilters() : {};
    const fullQueryParams = { ...filters, ...queryParams, ...queryInfo.value };
    emitProps.params = { ...fullQueryParams };
    const { result = [], count = 0 } = await Api.Get(fullQueryParams, { loading: false });
    emitProps.total = count;
    return { result, count };
  };

  const autoSearch = () => {
    const $grid = xGrid.value;
    if ($grid) {
      $grid.commitProxy("query");
    }
  };
  const updateQueryInfo = (info: any) => {
    // 当是表单查询的时候默认page
    queryInfo.value = {
      ...info,
      "pagination[page]": 1,
    };
    autoSearch();
  };

  return {
    gridOptions,
    gridEvent,
    pageButtons,
    handleCommand,
    searchForm,
    updateQueryInfo,
    tableButtons,
    xGrid,
    queryFormEl,
    router,
  };
};
