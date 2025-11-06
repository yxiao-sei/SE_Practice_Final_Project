import { GetStrapi, GetOneStrapi, PostStrapi, PutStrapi, DeleteStrapi } from "./strapi";
import type { CustomOption } from "./config";
const url = "/project-board-api/board/strapi/api/project-recruit-applications";
export const findApplicationsListApi = (params: any, loadingOptions: CustomOption = {}) => {
  return GetStrapi({ params, url, loadingOptions });
};
export const findApplicationByIdApi = (id: string, loadingOptions: CustomOption = {}) => {
  return GetOneStrapi({ id, url, loadingOptions });
};
export const createApplicationApi = (data: any, loadingOptions: CustomOption = {}) => {
  return PostStrapi({ url: url, data, loadingOptions });
};
export const updateApplicationApi = (id: string, data: any, loadingOptions: CustomOption = {}) => {
  return PutStrapi({ id, url, data, loadingOptions });
};
export const deleteApplicationApi = (id: string, loadingOptions: CustomOption = {}) => {
  return DeleteStrapi({ url, id, loadingOptions });
};
