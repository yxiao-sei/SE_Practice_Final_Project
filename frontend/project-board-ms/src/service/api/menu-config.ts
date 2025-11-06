import { GetStrapi, GetOneStrapi, PostStrapi, PutStrapi } from "@/service/strPagedApi";
import type { CustomOption } from "../config";

const api = "/project-board-api/board/strapi/api/config-menus";
export const findMenuListApi = (params: any, loadingOptions: CustomOption = {}) => {
  return GetStrapi({ params, url: api, loadingOptions });
};
export const findMenuByIdApi = (id: string, loadingOptions: CustomOption = {}) => {
  return GetOneStrapi({ id, url: api, loadingOptions });
};
export const createMenuApi = (data: any, loadingOptions: CustomOption = {}) => {
  return PostStrapi({ url: api, data, loadingOptions });
};
export const updateMenuApi = (id: string, data: any, loadingOptions: CustomOption = {}) => {
  return PutStrapi({ id, url: api, data, loadingOptions });
};


