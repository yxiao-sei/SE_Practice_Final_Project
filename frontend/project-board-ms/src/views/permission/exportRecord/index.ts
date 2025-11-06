import { hasPermission } from "@/utils";

export const resourceId = "";

export const searchFormList = [
  // {
  //   span: 3,
  //   label: "导入导出",
  //   options: [
  //     {
  //       label: "导入",
  //       value: "导入",
  //     },
  //     {
  //       label: "导出",
  //       value: "导出",
  //     },
  //   ],
  //   paramsKey: "dataSourceTypeName",
  //   attributes: {
  //     size: "large",
  //     clearable: true,
  //     filterable: true,
  //   },
  //   elementType: "Select",
  //   defaultValue: "",
  // },
  {
    span: 3,
    label: "操作类型",
    paramsKey: "dataSourceTypeName",
    attributes: {
      size: "large",
      clearable: true,
      filterable: true,
    },
    elementType: "Input",
    defaultValue: "",
  },
  {
    span: 3,
    label: "操作时间",
    paramsKey: "beginTime",
    attributes: {
      size: "large",
      type: "date",
      clearable: true,
      placeholder: "起始时间",
      valueFormat: "x",
    },
    elementType: "Date",
    defaultValue: "",
  },
  {
    span: 3,
    label: "",
    paramsKey: "endTime",
    attributes: {
      size: "large",
      type: "date",
      clearable: true,
      placeholder: "结束时间",
      valueFormat: "x",
    },
    elementType: "Date",
    defaultValue: "",
  },
];

export const pageButtons = [];

export const tableButtons = [
  {
    label: "下载",
    command: "download",
    privilege: "",
    attributes: {
      loading: false,
      size: "default",
      type: "info",
    },
    isCollapse: false,
    resourceId: "",
  },
];

export const tableColumns = [
  { type: "seq", sortable: true, align: "center", title: "序号", width: 60 },
  {
    title: "操作类型",
    sortable: true,
    field: "dataSourceTypeName",
    align: "center",
  },
  {
    title: "操作时间",
    sortable: true,
    field: "createdTime",
    align: "center",
    formatter: ["dateString", "yyyy-MM-dd HH:mm"],
  },
  { title: "操作人", sortable: true, field: "creatorName", align: "center" },
  {
    title: "状态",
    sortable: true,
    field: "result",
    align: "center",
    slots: { default: "result" },
  },
  {
    title: "错误原因",
    field: "comment",
    align: "center",
    slots: { default: "comment" },
  },
  {
    title: "进度",
    sortable: true,
    field: "progress",
    align: "center",
    slots: { default: "progress" },
  },
  {
    field: "table_action_slot_default",
    title: "操作",
    align: "center",
    width: 100,
    slots: { default: "table_action_slot_default" },
  },
];
