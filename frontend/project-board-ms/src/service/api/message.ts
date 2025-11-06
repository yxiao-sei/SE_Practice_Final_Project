import { Delete, Get, Post, Put } from '@/service';

const baseURL = '/project-board-api/board/msg/message';

/**
 * 获取消息列表接口
 * @param params 请求参数
 * @param loading 是否显示加载中，默认为true
 * @returns Promise
 */
export const getMessageListApi = (params: any, loading = true) => {
  return Get(
    {
      url: `${baseURL}/sendMessage`,
      params,
    },
    { reductDataFormat: false, loading },
  );
};

/**
 * 获取轮询消息列表接口
 * @param params 请求参数
 * @returns Promise
 */
export const getMessagePollListApi = (params: any, options = {}) => {
  return Get(
    {
      url: `${baseURL}/list`,
      params,
    },
    {
      reductDataFormat: false,
      loading: false,
      errorMessageShow: true,
      codeMessageShow: true,
      ...options,
    },
  );
};

/**
 * 标记全部消息为已读接口
 * @returns Promise
 */
export const readAllMsgApi = () => {
  return Post({
    url: `${baseURL}`,
    method: 'post',
  });
};

/**
 * 标记单条消息为已读接口
 * @param id 消息ID
 * @returns Promise
 */
export const readMessageApi = (id: string, option = {}) => {
  return Post(
    {
      url: `${baseURL}/${id}`,
      method: 'post',
    },
    { reductDataFormat: false, ...option },
  );
};

/**
 * 删除单条消息接口
 * @param id 消息ID
 * @returns Promise
 */
export const deleteMessageApi = (id: string) => {
  return Delete(
    {
      url: `${baseURL}/${id}`,
    },
    { reductDataFormat: false },
  );
};

/**
 * 删除全部消息接口
 * @returns Promise
 */
export const deleteAllMsgApi = () => {
  return Delete(
    {
      url: `${baseURL}`,
      method: 'delete',
    },
    { reductDataFormat: false },
  );
};

/**
 * 发送消息接口
 * @param data 消息数据
 * @returns Promise
 */
export const sendMessageApi = (data: any) => {
  return Put(
    {
      url: `${baseURL}`,
      data,
    },
    { loading: true },
  );
};

/**
 * 获取群组消息接口
 * @param messageGroupId 群组ID
 * @returns Promise
 */
export const getGroupMessageApi = (messageGroupId: string) => {
  return Get(
    {
      url: `${baseURL}/${messageGroupId}`,
    },
    { reductDataFormat: false, loading: true },
  );
};

/**
 * 获取消息群组列表接口
 * @param params 请求参数
 * @returns Promise
 */
export const getMessageGroupListApi = (params: any) => {
  return Get(
    {
      url: `${baseURL}/group/list`,
      params,
    },
    { reductDataFormat: false, loading: true },
  );
};
