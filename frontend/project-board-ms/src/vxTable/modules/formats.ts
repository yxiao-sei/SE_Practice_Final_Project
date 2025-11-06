import XEUtils from "xe-utils";
import { useBaseFieldStore } from "@/stores";
const isEmptyValue = (cellValue: any) => {
  return (
    cellValue === null ||
    cellValue === undefined ||
    cellValue === "" ||
    JSON.stringify(cellValue) === "{}"
  );
};
const getRolesLabel = (roleStr: string) => {
  const { roleDepartmentList, roleTeacherList, roleStudentList } = useBaseFieldStore();
  const roles = roleStr.split(',');
  return roles
    .map((i) => {
      return [...roleDepartmentList, ...roleTeacherList, ...roleStudentList].find((r: any) => r.value === i)?.label;
    })
    .join('，');
};
export const emptyRendererValue = "-";
export const formats: any = {
  amount: ({ cellValue }: any, currency: string) => {
    const newValue = formats.commonFormatter({ cellValue });
    return newValue === emptyRendererValue
      ? newValue
      : Number(cellValue).toLocaleString("zh", {
        style: "currency",
        currency: currency,
      });
  },
  jsonObj: ({ cellValue }: any, key: string) => {
    const newValue = formats.commonFormatter({ cellValue });
    return newValue === emptyRendererValue
      ? newValue
      : formats.commonFormatter({ cellValue: cellValue[key] });
  },
  // 根据传入的label value转换值
  labelFormatter: ({ cellValue }: any, format: any) => {
    const newValue = formats.commonFormatter({ cellValue });
    if (format === 'AuthenticationMethods') {
      const { authenticationMethods } = useBaseFieldStore();
      const item = authenticationMethods.find((item) => item.value === cellValue)
      return newValue === emptyRendererValue ? newValue : item ? item.label : "";
    } else if (format === 'Role') {
      return getRolesLabel(cellValue)
    } else {
      const item = format?.find((item: { label: string, value: string }) => item.value === cellValue);
      return newValue === emptyRendererValue ? newValue : item ? item.label : "";
    }
  },
  toFixedFormatter: ({ cellValue }: any, format: number) => {
    const newValue = formats.commonFormatter({ cellValue });
    const number = Math.round(cellValue * format) / format;
    return newValue === emptyRendererValue ? newValue : number;
  },
  dateString: ({ cellValue }: any, format: string) => {
    const newValue = formats.commonFormatter({ cellValue });
    return newValue === emptyRendererValue
      ? newValue
      : XEUtils.toDateString(cellValue, format);
  },
  commonFormatter: ({ cellValue }: any) => {
    return isEmptyValue(cellValue) ? emptyRendererValue : cellValue;
  },
};

export const useFormats = (VXETable: any) => {
  for (const key in formats) {
    VXETable.formats.add(key, {
      cellFormatMethod({ cellValue }: any, format: string) {
        return formats[key]({ cellValue }, format);
      },
    });
  };
};