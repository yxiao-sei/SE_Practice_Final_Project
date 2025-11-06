import router from '@/router';
import { useLogin } from '@/hooks/login';
import type { AxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';
import { repeatErrorTipTime } from './config';

// 禁止重复报错提示
let errMsg: string | null = null;
let messageInstance: any = null;
const showMessage = (message: string, type: any) => {
  if (errMsg !== message) {
    if (messageInstance) messageInstance.close();
    messageInstance = ElMessage({
      message,
      type,
    });
    errMsg = message;
    setTimeout(() => {
      errMsg = null;
    }, repeatErrorTipTime);
  }
};

export const handleAuthError = (errno: string, message?: string) => {
  if (router.currentRoute.value.path === '/login' && errno === '-403') return;
  const { loginUtil } = useLogin();
  const authErrMap: any = {
    '-403': '登录失效，需要重新登录', // token 失效
    401: '未授权，请重新登录',
    403: '禁止访问',
  };

  if (Object.hasOwnProperty.call(authErrMap, errno)) {
    const msg = message || authErrMap[errno];
    showMessage(msg, 'error');
    loginUtil();
    // 授权错误，登出账户
    // logout()
  }
};
/**
 * @description: 错误消息处理
 * @param {number} status
 * @return {*}
 */
export const httpErrorStatusHandle = (errStatus: number, msg: string) => {
  let message = '';
  if (msg) {
    message = msg;
  } else if (errStatus) {
    switch (errStatus) {
      case 400:
        message = '错误的请求';
        break;
      case 401:
        message = '未授权，请重新登录';
        break;
      case 403:
        message = '登录失效，需要重新登录';
        break;
      case 404:
        message = '请求错误，未找到该资源';
        break;
      case 405:
        message = '请求方法未允许';
        break;
      case 408:
        message = '请求超时';
        break;
      case 500:
        message = '服务器端出错';
        break;
      case 501:
        message = '网络未实现';
        break;
      case 502:
        message = '网络错误';
        break;
      case 503:
        message = '服务不可用';
        break;
      case 504:
        message = '网络超时';
        break;
      case 505:
        message = 'http版本不支持该请求';
        break;
      default:
        message = `其他连接错误 --${errStatus}`;
    }
  } else {
    message = '无法连接到服务器！';
  }
  showMessage(message, 'error');
};

export const handleGeneralSuccess = (method: string, message: string, url: string) => {
  let successMessage: string;
  if (message && message !== 'ok' && message !== 'OK') {
    successMessage = message;
  } else if (method === 'post' || method === 'put') {
    successMessage = '操作成功';
  } else if (method === 'delete') {
    successMessage = '删除成功';
  } else {
    successMessage = '';
  }
  showMessage(url === '/auth/oauth/login' ? '登录成功' : successMessage, 'success');
};

export const handleGeneralError = (errno: string | number, message: string) => {
  if (errno !== '0' && errMsg !== message) {
    showMessage(message, 'error');
  }
};

export const getRequestId = (
  config: AxiosRequestConfig,
  repeatRequestUrlParams: boolean | undefined,
) => {
  if (repeatRequestUrlParams) {
    return [
      config.method,
      config.url,
      JSON.stringify(config.params),
      JSON.stringify(config.data),
    ].join('&');
  }
  return [config.method, config.url].join('&');
};
