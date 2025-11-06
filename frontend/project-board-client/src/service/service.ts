import AxiosCancel from './axiosCancel';
import AxiosLoading from './axiosLoading';
import { showNotify } from 'vant';
import { showLoadingToast } from 'vant';

import { useUserInfo } from '@/stores/cacheInfo';
import axios, { type AxiosRequestConfig, type AxiosResponse } from 'axios';
import {
  authErrorCode,
  challengeErrorCode,
  customBaseOptions,
  tokenErrorCode,
  type CustomLoadingOption,
  type CustomOption,
} from './config';
import {
  handleAuthError,
  handleGeneralError,
  handleGeneralSuccess,
  httpErrorStatusHandle,
} from './requestUtil';

const axiosCancel = new AxiosCancel();
const loadingInstance = new AxiosLoading();
let showLoginInvalidBox = false;
export interface AnyObj {
  [key: string]: any;
}

function request(customOptions?: CustomOption, loadingOptions?: CustomLoadingOption) {
  const custom_options = { ...customBaseOptions, ...(customOptions || {}) };

  const service = axios.create({
    // baseURL: import.meta.env.VITE_BASE_URL,
    baseURL: "",
    withCredentials: false,
    // timeout: 30000,
    // 在传递给 then/catch 前，允许修改响应数据
    // transformResponse: [(data) => {
    //   if (typeof data === 'string' && data.startsWith('{')) {
    //     data = JSON.parse(data)
    //   }
    //   return data
    // }]
  });
  service.interceptors.request.use(
    (config) => {
      const repeatRequestUrlParams = custom_options?.repeatRequestUrlParams;
      axiosCancel.removePending(config, repeatRequestUrlParams); // 在请求开始前，对之前的请求做检查取消操作
      if (custom_options.repeatRequestCancel) {
        axiosCancel.addPending(config, repeatRequestUrlParams);
      } // 将当前请求添加到 pending 中
      // 创建loading实例
      if (custom_options.loading) {
        loadingInstance.count++;
        if (loadingInstance.count === 1) {
          loadingInstance._target = showLoadingToast({
            duration: 0, // 持续展示 toast
            forbidClick: true,
          });
        }
      }
      // 获取token，并将其添加至请求头中
      if (useUserInfo && useUserInfo().token) {
        config.headers.Authorization = `Bearer ${useUserInfo().token?.access_token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    },
  );

  // 响应拦截
  service.interceptors.response.use(
    (response: AxiosResponse) => {
      axiosCancel.removePending(response.config, custom_options.repeatRequestUrlParams);
      custom_options.loading && loadingInstance.closeLoading(custom_options.loading); // 关闭loading
      if (response.request.responseType === 'blob') return response; // 文件类型直接返回
      const { data } = response;
      if (!custom_options.codeMessageShow) return data; // 不显示code错误信息，全量返回
      if (
        data[custom_options.successResult?.successKey as string] ===
        custom_options.successResult?.successValue
      ) {
        return custom_options.reductDataFormat
          ? data[custom_options.successResult?.entityKey as string]
          : data; // 返回成功状态
      } else {
        // 过期跳转登录
        if (
          custom_options?.loginExpired?.redirect &&
          custom_options?.loginExpired?.code === data.code
        ) {
          if (!showLoginInvalidBox) {
            showLoginInvalidBox = true;
            showNotify({
              type: 'danger',
              duration: 0,
              message: '登录超时，请重新登录',
            });
          }
        } else if (
          data.code !== tokenErrorCode &&
          data.code !== authErrorCode &&
          data.code !== challengeErrorCode
        ) {
          handleGeneralError(
            data.code,
            data.message || '请求超时或服务器异常，请检查网络或联系管理员！',
          ); // 处理错误信息
        } else {
          handleAuthError(data.code.toString());
        }
        return Promise.reject(data);
      }
    },
    (error) => {
      custom_options.loading && loadingInstance.closeLoading(custom_options.loading); // 关闭loading
      if (axios.isCancel(error)) {
        if (axiosCancel.isRemove) {
          // 主动取消请求
          // console.log('重复请求: ' + error.message)
          axiosCancel.isRemove = false;
        }
      } else {
        error.config &&
          axiosCancel.removePending(error.config, custom_options.repeatRequestUrlParams);
        /* 处理 http 错误，抛到业务代码 */
      }
      if (!custom_options.errorMessageShow) return Promise.reject(error);
      if (error.response?.status === tokenErrorCode || error.response?.status === authErrorCode) {
        handleAuthError(error.response.status.toString());
      } else {
        custom_options.errorMessageShow &&
          httpErrorStatusHandle(error?.response?.status, error?.response?.data?.msg);
      }
      return Promise.reject(error);
    },
  );

  return service;
}

export default request;

export const Get = (
  config: AxiosRequestConfig,
  customOption?: CustomOption,
  customLoadingOptions?: CustomLoadingOption,
): Promise<any> => {
  const service = request(customOption, customLoadingOptions);
  return service({ ...config, method: 'get' });
};
export const Post = (
  config: AxiosRequestConfig,
  customOption?: CustomOption,
  customLoadingOptions?: CustomLoadingOption,
): Promise<any> => {
  const service = request(customOption, customLoadingOptions);
  return service({ ...config, method: 'post' });
};
export const Put = (
  config: AxiosRequestConfig,
  customOption?: CustomOption,
  customLoadingOptions?: CustomLoadingOption,
): Promise<any> => {
  const service = request(customOption, customLoadingOptions);
  return service({ ...config, method: 'put' });
};
export const Delete = (
  config: AxiosRequestConfig,
  customOption?: CustomOption,
  customLoadingOptions?: CustomLoadingOption,
): Promise<any> => {
  const service = request(customOption, customLoadingOptions);
  return service({ ...config, method: 'delete' });
};
