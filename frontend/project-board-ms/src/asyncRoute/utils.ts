import { getActualWidthOfChars } from "@/utils";
import * as request from "@/service";
import { getRoleFilter } from "@/service/strPagedApi";
import qs from "qs";
// @ts-ignore
export const deepClone = (data) => {
  // @ts-ignore
  var obj: any;
  // 判断是否为对象
  if (Object.prototype.toString.call(data) === "[object Object]") {
    obj = new Object();
    // 继续遍历对象
    Object.keys(data).forEach((key) => {
      // 递归调用
      obj[key] = deepClone(data[key]);
    });
    // 判断是否为数组
  } else if (Object.prototype.toString.call(data) === "[object Array]") {
    obj = new Array();
    // 继续遍历数组
    data.forEach((d: any) => {
      obj.push(deepClone(d));
    });
  } else {
    obj = data;
  }
  return obj;
};
export const isPermissions = (key: string | number, value: string | number) => {
  let authorities = JSON.parse(sessionStorage.getItem("authorities") as string);
  try {
    return (
      authorities[key] !== undefined && authorities[key][value] != undefined
    );
  } catch (e) {
    return false;
  }
};
export const getObjVal = (obj: any, path: string) => {
  let res = obj,
    currProp,
    props = path.split(".");
  while ((currProp = props.shift())) {
    res = res[currProp];
  }
  return res;
};

export interface Menu {
  command: string;
  privilege: string;
  resourceId: string;
  label: string;
  attributes: {
    type:
    | "default"
    | "success"
    | "warning"
    | "info"
    | "text"
    | "primary"
    | "danger";
    size?: string;
  };
}

export const initializeMenus = (menus: Menu[]) => {
  try {
    const permission = JSON.parse(
      sessionStorage.getItem("authorities") as string
    );
    if (!menus.length) return [];
    return menus.filter(
      (i) =>
        !i.resourceId || !i.privilege || permission[i.resourceId][i.privilege]
    );
  } catch (error) {
    return [];
  }
};
function findLongestStringLength(arr: string[]) {
  let maxLength = 0;
  let newString = "";
  for (let i = 0; i < arr.length; i++) {
    if (arr[i].length > maxLength) {
      maxLength = arr[i].length;
      newString = arr[i]
    }
  }
  return newString;
}
const getActualPx = (designPx: number) => {
  const rootFontSize = parseFloat(getComputedStyle(document.documentElement).fontSize);
  const percent = rootFontSize ? parseFloat((192 / rootFontSize).toFixed(2)) : 1;
  const actualPx = Math.ceil(designPx / percent);
  return actualPx;
}

export const getTableColumnWidth = (column: any) => {
  const { sortable, width, minWidth, title } = column;
  const paddingTotal = 20;
  const newTitle = findLongestStringLength(title.split(/\n/g)) || title;
  const titleWidth = paddingTotal + (sortable ? 20 : 0) + getActualWidthOfChars(newTitle);
  const maxWidth = Math.max(
    titleWidth,
    width || 0,
    minWidth || 0
  )
  return getActualPx(maxWidth)
};

export const remoteMethod = async (config: any) => {
  if (!config) return [];
  const { method, url, params = {}, key, value, label } = config
  if (params.sort && Array.isArray(params.sort)) {
    params.sort.push("updatedAt:desc");
  } else if (params.sort) {
    params.sort = [params.sort, "updatedAt:desc"];
  } else {
    params.sort = "updatedAt:desc";
  };
  const data: any = await request.Get({
    method, url: url,
    params: { ...getRoleFilter(params, url), "pagination[pageSize]": 100000 },
    paramsSerializer: (_params) => {
      return qs.stringify(_params);
    },
  }, {
    loading: false,
    codeMessageShow: true,
    successMessageShow: false,
    reductDataFormat: true,
  });
  const result = data.data.map((i: any) => ({ id: i[key], label: i.attributes[label], value: i.attributes[value] }));
  return result;
};

