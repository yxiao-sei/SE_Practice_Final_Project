/*
 * @Author: laladila1986@163.com
 * @Date: 2024-01-03 17:37:50
 * @LastEditors: laladila1986@163.com
 * @LastEditTime: 2024-01-23 17:50:39
 * @FilePath: \plms-ms\src\service\axiosCancel.ts
 * @Description:
 * @Copyright: Copyright 2024, All Rights Reserved.
 */

import type { AxiosRequestConfig } from 'axios';
import axios from 'axios';
import { getRequestId } from './requestUtil';

export default class AxiosCancel {
  // 存储请求标识 和 取消函数
  pending = new Map();
  isRemove = false;

  /**
   * @description: 添加请求
   * @param {AxiosRequestConfig} config
   */
  addPending = (config: AxiosRequestConfig, repeatRequestUrlParams: boolean | undefined) => {
    const url = getRequestId(config, repeatRequestUrlParams);
    const typeConfig = config;
    typeConfig.cancelToken =
      typeConfig.cancelToken ||
      new axios.CancelToken((cancel) => {
        if (!this.pending.has(url)) {
          // 请求队列中不存在当前请求，添加请求
          this.pending.set(url, cancel);
        }
      });
  };

  /**
   * @description: 移除请求
   * @param {AxiosRequestConfig} config
   * @return {*}
   */
  removePending = (config: AxiosRequestConfig, repeatRequestUrlParams: boolean | undefined) => {
    const url = getRequestId(config, repeatRequestUrlParams);

    if (this.pending.has(url)) {
      // 如果在 pending 中存在当前请求标识，需要取消当前请求，并且移除
      // console.log("请求队列中已存在当前请求，不再重复请求")
      const cancel = this.pending.get(url);
      this.isRemove = true;
      cancel(url); // 取消请求
      this.pending.delete(url);
    }
  };

  /**
   * @description: 清空 pending 中的请求（在路由跳转时调用）
   */
  clearPending = () => {
    for (const [url, cancel] of this.pending) {
      cancel(url); // 取消请求
    }
    this.pending.clear(); // 清空请求
  };
}
