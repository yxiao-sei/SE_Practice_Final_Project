import { GetStrapi, GetOneStrapi, PostStrapi, PutStrapi } from "@/service/strPagedApi";
import type { CustomOption } from "../config";
const url = "/project-board-api/board/strapi/api/departments";
export const findDepartmentsListApi = (params: any, loadingOptions: CustomOption = {}) => {
  return GetStrapi({ params, url, loadingOptions });
};
export const findDepartmentsByIdApi = (id: string, loadingOptions: CustomOption = {}) => {
  return GetOneStrapi({ id, url, loadingOptions });
};
export const createDepartmentsApi = (data: any, loadingOptions: CustomOption = {}) => {
  return PostStrapi({ url: url, data, loadingOptions });
};
export const updateDepartmentsApi = (id: string, data: any, loadingOptions: CustomOption = {}) => {
  return PutStrapi({ id, url, data, loadingOptions });
};
