import { GetStrapi, GetOneStrapi, PostStrapi, PutStrapi, DeleteStrapi } from "./strapi";
import type { CustomOption } from "./config";
const url = "/project-board-api/board/strapi/api/administrators";
export const findAdministratorListApi = (params: any, loadingOptions: CustomOption = {}) => {
  return GetStrapi({ params, url, loadingOptions });
};
export const findAdministratorByIdApi = (id: string, loadingOptions: CustomOption = {}) => {
  return GetOneStrapi({ id, url, loadingOptions });
};
export const createAdministratorApi = (data: any, loadingOptions: CustomOption = {}) => {
  return PostStrapi({ url: url, data, loadingOptions });
};
export const updateAdministratorApi = (id: string, data: any, loadingOptions: CustomOption = {}) => {
  return PutStrapi({ id, url, data, loadingOptions });
};
export const deleteAdministratorApi = (id: string, loadingOptions: CustomOption = {}) => {
  return DeleteStrapi({ url, id, loadingOptions });
};
