/*
 * @Author: lanseliuyiya laladila1986@163.com
 * @Date: 2022-12-02 18:09:06
 * @LastEditors: lanseliuyiya laladila1986@163.com
 * @LastEditTime: 2022-12-02 18:25:45
 * @FilePath: \model-validation\src\model\baseModel.ts
 * @Description:
 *
 * Copyright (c) 2022 by lanseliuyiya laladila1986@163.com, All Rights Reserved.
 */

/**
 * Response
 */
export interface BaseResponse<T> {
  /**
   * 状态码，0-成功，其他-失败
   */
  code: number;
  /**
   * 错误信息，错误信息
   */
  msg: null | string;
  /**
   * 数据信息
   */
  result: T;
}

export interface BaseObj<T = string> {
  [key: string]: T;
}

export interface DepartmentTree {
  label: string;
  value: string;
  children: Array<DepartmentTree>;
}

export interface Department {
  departmentName: string;
  departmentCode: string;
  buinDepartmentName: string;
  buinCode: string;
  performanceDepartmentName?: string;
  id: string;
}

export interface ItemType {
  field: string;
  title: string;
}

export interface AssessmentType {
  field: string;
  title: string;
  children: ItemType[];
  proportion: string;
}

export interface GroupedItem {
  id: number;
  departmentName: string;
  tertiaryIndex: string;
  score: number;
  createdAt: string;
  children: null | GroupedItem[];
  publishedAt: string;
  departmentCode: string;
  secondaryIndex: string;
  primaryIndex: string;
  updatedAt: string;
}
