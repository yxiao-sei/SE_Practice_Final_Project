import { GetStrapi, GetOneStrapi, PostStrapi, PutStrapi, DeleteStrapi } from "./strapi";
import type { CustomOption } from "./config";

const api = "/project-board-api/board/strapi/api/projects";
export const findProjectsApi = (params: any, loadingOptions: CustomOption = {}) => {
  return GetStrapi({ params, url: api, loadingOptions });
};
export const findProjectByIdApi = (id: string, loadingOptions: CustomOption = {}) => {
  return GetOneStrapi({ id, url: api, loadingOptions });
};
export const createProjectApi = (data: any, loadingOptions: CustomOption = {}) => {
  return PostStrapi({ url: api, data, loadingOptions });
};
export const updateProjectApi = (id: string, data: any, loadingOptions: CustomOption = {}) => {
  return PutStrapi({ id, url: api, data, loadingOptions });
};
export const deleteProjectApi = (id: string, loadingOptions: CustomOption = {}) => {
  return DeleteStrapi({ url: api, id, loadingOptions });
};


