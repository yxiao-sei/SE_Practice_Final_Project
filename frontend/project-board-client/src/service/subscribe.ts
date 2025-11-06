import { GetStrapi, GetOneStrapi, PostStrapi, PutStrapi, DeleteStrapi } from "./strapi";
import type { CustomOption } from "./config";
const url = "/project-board-api/board/strapi/api/subscribe-settings";
export const findSubscribeListApi = (params: any, loadingOptions: CustomOption = {}) => {
  return GetStrapi({ params, url, loadingOptions });
};
export const findSubscribeByIdApi = (id: string, loadingOptions: CustomOption = {}) => {
  return GetOneStrapi({ id, url, loadingOptions });
};
export const createSubscribeApi = (data: any, loadingOptions: CustomOption = {}) => {
  return PostStrapi({ url: url, data, loadingOptions });
};
export const updateSubscribeApi = (id: string, data: any, loadingOptions: CustomOption = {}) => {
  return PutStrapi({ id, url, data, loadingOptions });
};
export const deleteSubscribeApi = (id: string, loadingOptions: CustomOption = {}) => {
  return DeleteStrapi({ url, id, loadingOptions });
};
