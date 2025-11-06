/*
 * @Author: lanseliuyiya
 * @Date: 2024-01-09 13:33:21
 * @LastEditors: laladila1986@163.com
 * @LastEditTime: 2024-01-30 16:24:30
 * @FilePath: \plms-ms\src\utils\transformTableValueByUnit.ts
 * @Description:
 * @Copyright: Copyright 2024, All Rights Reserved.
 */
import { useBaseFieldStore } from '@/stores';
import { parseTime } from './index';

export const transformRoleCodeToLabel = (codes: string) => {
  const roleArray = useBaseFieldStore().roles;
  const roles = codes.split(',');
  return roles
    .map((i) => {
      return roleArray.find((r) => r.value === i)?.label;
    })
    .join('，');
};

export const isEmptyValue = (value: any) => {
  if (typeof value === 'string') return !value;
  if (typeof value === 'boolean') return false;
  if (Array.isArray(value)) return !value.length;
  if (value && typeof value === 'object') return !Object.keys(value).length;
  if (typeof value !== 'number') return !value;
  return false;
};

export const transformIncludesUnits = (units: string, value: number | undefined | null) => {
  let target: any = value;
  switch (units) {
    case '%':
      target = value ? `${value}%` : '';
      break;
    case '￥':
      target = value
        ? Number(value).toLocaleString('zh', { style: 'currency', currency: 'CNY' })
        : '';
      break;
    default:
      target = value;
  }
  return target;
};

export const transformIncludesDateType = (value: any, formate = '{y}-{m}-{d}') => {
  let targetValue = value;
  if (typeof targetValue === 'string') targetValue = +new Date(value);
  return parseTime(targetValue, formate);
};

export const transformIncludesDateRangeType = (value: any) => {
  return value
    ? `${parseTime(value.split(',')[0], '{y}-{m}-{d}') || ''} - ${
      parseTime(value.split(',')[1], '{y}-{m}-{d}') || ''
    }`
    : '';
};

export const transformValueByKey = (value: string | any[], key: string) => {
  const targetValue = Array.isArray(value) ? value : JSON.parse(value);
  return targetValue.map((i: { name: any }) => i[key]).join(',');
};

export const transformValueByObjectKey = (value: any, key: string) => {
  const targetValue = value !== null && typeof value === 'object' ? value : JSON.parse(value);
  return targetValue?.[key] || value;
};

export const transformFieldUnitAndFieldType = (unit = '', type = '', value: any) => {
  if (isEmptyValue(value)) return '-';
  if (unit) return transformIncludesUnits(unit, value);
  try {
    if (type.includes('Array.')) return transformValueByKey(value, type.split('Array.')[1]);
    if (type.includes('Object.')) return transformValueByObjectKey(value, type.split('Object.')[1]);
    switch (type) {
      case 'Date':
        return transformIncludesDateType(value);
      case 'DateRange':
        return transformIncludesDateRangeType(value);
      case 'DateTime':
        return transformIncludesDateType(value, '{y}/{m}/{d} {h}:{i}:{s}');
      case 'DateTimeMinute':
        return transformIncludesDateType(value, '{y}/{m}/{d} {h}:{i}');
      case 'Amount':
        return Number(value).toLocaleString();
      case 'Boolean':
        if (typeof value === 'boolean') {
          return value ? '是' : '否';
        }
        return value;
      case 'Role':
        return transformRoleCodeToLabel(value);
      default:
        return value;
    }
  } catch (error) {
    return value;
  }
};
