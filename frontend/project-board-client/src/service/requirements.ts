import { GetStrapi, GetOneStrapi, PostStrapi, PutStrapi, DeleteStrapi } from "./strapi";
import type { CustomOption } from "./config";
const url = "/project-board-api/board/strapi/api/project-recruit-requirements";
export const findRequirementsListApi = (params: any, loadingOptions: CustomOption = {}) => {
  return GetStrapi({ params, url, loadingOptions });
};
export const findRequirementByIdApi = (id: string, loadingOptions: CustomOption = {}) => {
  return GetOneStrapi({ id, url, loadingOptions });
};
export const createRequirementApi = (data: any, loadingOptions: CustomOption = {}) => {
  return PostStrapi({ url: url, data, loadingOptions });
};
export const updateRequirementApi = (id: string, data: any, loadingOptions: CustomOption = {}) => {
  return PutStrapi({ id, url, data, loadingOptions });
};
export const deleteRequirementApi = (id: string, loadingOptions: CustomOption = {}) => {
  return DeleteStrapi({ url, id, loadingOptions });
};
