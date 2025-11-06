export interface CustomOption {
  repeatRequestCancel?: boolean;
  repeatRequestUrlParams?: boolean;
  loading?: boolean;
  reductDataFormat?: boolean;
  errorMessageShow?: boolean;
  codeMessageShow?: boolean;
  successMessageShow?: boolean;
  successResult?: {
    successKey: string;
    successValue: string | number;
    entityKey: string;
  };
  loginExpired?: {
    redirect: boolean;
    code: string;
    loginPath: string;
  };
  baseUrl?: string;
}

export interface CustomLoadingOption {
  target?: HTMLElement | string;
  background?: string;
  'custom-class'?: string;
}

export const customBaseOptions: CustomOption = {
  repeatRequestCancel: false, // 是否开启取消重复请求, 默认为 false
  repeatRequestUrlParams: true, // 重复请求是否包含参数
  loading: true, // 是否开启loading层效果, 默认为false
  reductDataFormat: true, // 是否开启简洁的数据结构响应, 默认为true
  errorMessageShow: true, // 是否开启接口错误信息展示,默认为true
  codeMessageShow: true, // 是否开启失败信息提示, 默认为true
  successMessageShow: true, // 是否开启成功信息提示, 默认为true
  successResult: {
    // 成功返回数据结构
    successKey: 'code',
    successValue: 0,
    entityKey: 'result',
  },
  loginExpired: {
    redirect: true,
    code: '403',
    loginPath: '/login',
  },
  baseUrl: '/',
};

export const customLoadingBaseOptions: CustomLoadingOption = {
  background: 'rgba(255, 255, 255, 0)',
};

export const tokenErrorCode = 403;

export const authErrorCode = 401;

export const challengeErrorCode = -403;

export const requestDebounceTime = 400;

export const repeatErrorTipTime = 1000;
