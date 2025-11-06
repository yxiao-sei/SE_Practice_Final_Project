import { emptyRendererValue } from "@/vxTable";
/**
 * @description: 时间格式化
 * @return {*}
 */
export const parseTime = (
  time?: object | string | number | null,
  cFormat?: string
): string | null => {
  if (time === undefined || !time) {
    return null;
  }
  const format = cFormat || "{y}-{m}-{d} {h}:{i}:{s}";
  let date: Date;
  let timeFace: any = time;
  if (typeof timeFace === "object") {
    date = timeFace as Date;
  } else {
    if (typeof timeFace === "string") {
      if (/^[0-9]+$/.test(timeFace)) {
        timeFace = parseInt(timeFace, 10);
      } else {
        timeFace = timeFace.replace(new RegExp(/-/gm), "/");
      }
    }
    if (typeof timeFace === "number" && timeFace.toString().length === 10) {
      timeFace *= 1000;
    }
    date = new Date(timeFace);
  }
  const formatObj: { [key: string]: number } = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay(),
  };
  const timeStr = format.replace(/{([ymdhisa])+}/g, (result, key) => {
    const value = formatObj[key];
    if (key === "a") {
      return ["日", "一", "二", "三", "四", "五", "六"][value];
    }
    return value.toString().padStart(2, "0");
  });
  return timeStr;
};

export const findLabelsPrecisely = (labels: string, target: string) => {
  try {
    const list = JSON.parse(labels);
    if (!Array.isArray(list)) return null;
    return list.find((i: any) => i.label === target);
  } catch (error) {
    return null;
  }
};

export const formattingLabels = (labelJson: string) => {
  try {
    const list = JSON.parse(labelJson);
    if (!Array.isArray(list)) return [];
    return list;
  } catch (error) {
    return [];
  }
};

export const formattingValue = (value: string, fieldName: string) => {
  if (fieldName === "isAlive" || fieldName === "是否已过世") {
    if (value === "false") return "是";
    else if (value === "true") return "否";
    return "";
  } else if (fieldName === "isRemoved" || fieldName === "从名单中移除") {
    if (!value) return "否";
    else if (value === "true") return "是";
    return "";
  }
  return value;
};

export const transformFilesToFileNames = (apiFiles: string): any => {
  if (!apiFiles || typeof apiFiles !== "string") return "-";
  return apiFiles.split(",").map((i) => {
    return {
      name: i,
    };
  });
};

export const removeDuplicates = (arr: any[], key: string | number): any[] => {
  const target: any[] = [];
  arr.forEach((item: { [x: string]: any }) => {
    if (item[key] && !target.find((tar) => tar[key] === item[key])) {
      target.push(item);
    }
  });
  return target;
};

export const hasPermission = (resourceId: string, privilege: string) => {
  try {
    const permission = JSON.parse(
      sessionStorage.getItem("authorities") as string
    )[resourceId];
    if (!permission) return false;
    return permission[privilege];
  } catch (error) {
    return false;
  }
};

export const removeInvalidValues = (params: any) =>
  Object.keys(params).reduce((target, current) => {
    const _target = Object.assign({}, target);
    if (
      (params[current] !== undefined &&
        params[current] !== null &&
        params[current] !== "") ||
      (Array.isArray(params[current]) && params[current].length > 0)
    ) {
      _target[current] = params[current];
    }
    return _target;
  }, {});

export const getActualWidthOfChars = (
  text: string,
  options = {
    size: 14,
    family:
      "-apple-system, BlinkMacSystemFont, Segoe UI, PingFang SC, Hiragino Sans GB, Microsoft YaHei, Helvetica Neue, Helvetica, Arial, sans-serif, Apple Color Emoji, Segoe UI Emoji, Segoe UI Symbol",
  }
) => {
  const { size, family } = options;
  const canvas = document.createElement("canvas");
  const ctx = canvas.getContext("2d");
  ctx!.font = `${size}px ${family}`;
  const regex = /[^[\u4e00-\u9fa5]/g;
  const newText = text.replace(regex, "测");
  const metrics = ctx!.measureText(newText);
  const actual =
    Math.abs(metrics.actualBoundingBoxLeft) +
    Math.abs(metrics.actualBoundingBoxRight);
  return Math.ceil(Math.max(metrics.width, actual));
};

export const generateFiledList = (list: any[], tableColumns: any) => {
  return list.map((prop: any) => {
    if (typeof prop === "string") {
      return tableColumns.find((column: any) => column.props.prop === prop);
    } else {
      return prop;
    }
  });
};

export const listRemovalDuplicates = (
  origin: any[] | string,
  targetList: any[],
  key: string
) => {
  if (!origin || !origin.length) return targetList;
  return targetList.filter((item: any) => {
    if (Array.isArray(origin)) return !origin.includes(item[key]);
    else return item[key] !== origin;
  });
};

function convertVhToPx(vh: number) {
  const viewportHeight = window.innerHeight;
  return Math.floor(viewportHeight * (vh / 100));
}
// maxHeight:55vh
export const getHeightOverflowByVirtualDom = (
  virtualHeight: number,
  _maxHeight = 55
): boolean => {
  const maxHeight = convertVhToPx(_maxHeight);
  if (virtualHeight > maxHeight) {
    return true;
  }
  return false;
};

import type { Department, DepartmentTree } from "@/model/base";

export const generateDepartmentTree = (
  departments: Array<Department>
): Array<DepartmentTree> => {
  let tree: Array<DepartmentTree> = [];
  departments.forEach((item) => {
    let parentNode = tree.find(
      (node) => node.value === item.performanceDepartmentName
    );
    if (!parentNode) {
      parentNode = {
        value: item.performanceDepartmentName,
        label: item.performanceDepartmentName,
        children: [],
      };
      tree.push(parentNode);
    }
    if (item.departmentName) {
      let subNode = parentNode.children.find(
        (node) => node.value === item.departmentName
      );
      if (!subNode) {
        subNode = {
          label: item.departmentName,
          value: item.departmentName,
          children: [],
        };
        parentNode.children.push(subNode);
      }
    }
  });
  return tree;
};

export const sumNum = (list: any[], field: string) => {
  let count = 0;
  list.forEach((item) => {
    if (
      item[field] &&
      item[field] != emptyRendererValue &&
      item[field] != null
    ) {
      const number = Math.round(item[field] * 1000) / 1000;
      count += Number(number) * 10000;
    }
  });
  return count / 10000;
};

export const mergeData = (data: any[]): any[] => {
  const mergedMap = new Map<string, any>();
  data.forEach((item) => {
    const key = `${item.primaryIndex}-${item.secondaryIndex}-${item.tertiaryIndex}-${item.scoringRules}`;

    if (mergedMap.has(key)) {
      const existingItem = mergedMap.get(key)!;
      existingItem.scoringData =
        (existingItem.scoringData || 0) + (item.scoringData || 0);
      existingItem.score = (existingItem.score || 0) + (item.score || 0);
      existingItem.money = (existingItem.money || 0) + (item.money || 0);
    } else {
      // 确保初始化 score 和 money 为 0 而不是 null
      mergedMap.set(key, {
        ...item,
        scoringData: item.scoringData || 0,
        score: item.score || 0,
        money: item.money || 0,
      });
    }
  });

  return Array.from(mergedMap.values());
};

export const status_color = function (status: string) {
  switch (status) {
    case "progress":
      return {
        cn: "任务执行中",
        color: "#3e9bd9",
      };
    case "initial":
      return {
        cn: "任务初始化中",
        color: "#b70031",
      };
    case "aborted":
      return {
        cn: "任务已终止",
        color: "#f4743c",
      };
    case "error":
      return {
        cn: "任务执行出错",
        color: "#f0aebe",
      };
    case "completed":
      return {
        cn: "任务已完成",
        color: "#19be6b",
      };
    default:
      return {
        cn: "-",
        color: "-",
      };
  }
};
