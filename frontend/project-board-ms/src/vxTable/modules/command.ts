import ExportModal from "../modal/ExportModal.vue";
import FormModal from "../modal/FormModal.vue";
import { showModal } from "../modal/service";
import router from "@/router";
import { useCacheRouteHistory } from "@/stores";
import { queryStringToObject, replaceTemplateValue } from "../utils";
// import { findListApi } from "@/service/api/pageForm";
import { remoteMethod } from "@/asyncRoute/utils";
import { type EditRender } from "@/model/table"
export const findParentPath = () => {
  const routes = router.currentRoute.value.matched;
  const parentRoute = routes.reverse().find((r) => r.meta?.isMenu);
  if (!parentRoute) return null;
  return parentRoute.redirect ?? parentRoute.path;
};

const exportFn = ({ getTableColumn, row, url, params, fileName, total }: any) => {
  const columns = getTableColumn();
  showModal(
    {
      header: "导出",
      subheader: "选择所需字段导出",
      columns: columns,
      url,
      params: JSON.stringify({
        ...params,
        "pagination[page]": 1,
        "pagination[pageSize]": total,
      }),
      fileName,
    },
    ExportModal
  );
};

const linkFn = ({ row, queryString }: any) => {
  const path = findParentPath();
  const { route, params } = queryStringToObject(queryString, row);
  useCacheRouteHistory().addRouteHistory(
    {
      name: route.name,
      menuName: route.menuName,
      params: JSON.stringify(params),
    },
    !!router.currentRoute.value?.meta?.isMenu
  );
  router.push({ path: `${path}/${route.name}` });
}
const editFn = async ({ row, getTableColumn, Api }: any) => {
  const headers = row ? { header: "编辑", subheader: "编辑当前数据" } : { header: "新增", subheader: "新增一条数据" };
  const columns = getTableColumn();
  const editColumns: EditRender[] = columns.filter((c: any) => !!c?.editRender?.enabled).map((c: any) => ({ ...c.editRender, field: c.property }));
  const remoteNames = editColumns.filter((c: any) => c.enabled === 'remote' && c?.formItem?.name).map((c: any) => c.formItem.name);

  let formResults: any[] = []
  if (remoteNames?.length) {
    // const { result } = await findListApi({ "filters[name][$in]": remoteNames, "pagination[pageSize]": 999 }, { loading: true })
    // formResults = result
  }
  for (let i = 0; i < editColumns.length; i++) {
    let { options = [] as any, element, formItem, defaultValue } = editColumns[i];
    defaultValue = replaceTemplateValue(defaultValue, row);
    if (element[0] === 'elSelect' && !options?.length && formItem?.name) {
      const remoteItem = formResults.find((item: any) => item.name === formItem?.name)
      if (remoteItem) {
        editColumns[i].formItem = {
          name: formItem.name,
          config: { ...remoteItem.config, ...(formItem?.config || {}) }
        }
        options = await remoteMethod(editColumns[i].formItem!.config)
      }
      editColumns[i].options = options
      editColumns[i].defaultValue = defaultValue
    }
  }
  showModal(
    {
      ...headers,
      columns: editColumns,
      row,
      Api
    },
    FormModal
  );
}

export const globalEmits = {
  export: (params: any) => exportFn(params),
  link: (params: any) => linkFn(params),
  edit: (params: any) => editFn(params),
};

export const triggerEmit = (command: string, props: any) => {
  try {
    const splitCommand = command.split(":");
    const commandName = splitCommand[0];
    const queryString = splitCommand[1];
    // if (!globalEmits[commandName]) return ElMessage.error(`无法处理当前操作[command]: ${commandName}`);
    if (!globalEmits[commandName as keyof typeof globalEmits]) return;
    globalEmits[commandName as keyof typeof globalEmits]({ queryString, ...props }); // 触发全局事件
  } catch (error) {
    console.log(error)
  }
}
