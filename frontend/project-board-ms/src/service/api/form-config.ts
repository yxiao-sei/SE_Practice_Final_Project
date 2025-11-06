import { GetStrapi, GetOneStrapi, PostStrapi, PutStrapi } from "@/service/strPagedApi";
import type { CustomOption } from "../config";

const api = "/project-board-api/board/strapi/api/config-forms";
export const findFormListApi = (params: any, loadingOptions: CustomOption = {}) => {
  return GetStrapi({ params, url: api, loadingOptions });
};
export const findFormByIdApi = (id: string, loadingOptions: CustomOption = {}) => {
  return GetOneStrapi({ id, url: api, loadingOptions });
};
export const createFormApi = (data: any, loadingOptions: CustomOption = {}) => {
  return PostStrapi({ url: api, data, loadingOptions });
};
export const updateFormApi = (id: string, data: any, loadingOptions: CustomOption = {}) => {
  return PutStrapi({ id, url: api, data, loadingOptions });
};


