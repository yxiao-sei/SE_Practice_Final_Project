import { GetStrapi, GetOneStrapi, PostStrapi, PutStrapi, DeleteStrapi } from "@/service/strPagedApi";
import type { CustomOption } from "../config";

const api = "/project-board-api/board/strapi/api/metadatas";
export const findMetaDataListApi = (params: any, loadingOptions: CustomOption = {}) => {
  return GetStrapi({ params, url: api, loadingOptions });
};
export const findMetaDataByIdApi = (id: string, loadingOptions: CustomOption = {}) => {
  return GetOneStrapi({ id, url: api, loadingOptions });
};
export const createMetaDataApi = (data: any, loadingOptions: CustomOption = {}) => {
  return PostStrapi({ url: api, data, loadingOptions });
};
export const updateMetaDataApi = (id: string, data: any, loadingOptions: CustomOption = {}) => {
  return PutStrapi({ id, url: api, data, loadingOptions });
};
export const deleteMetaDataApi = (id: string, loadingOptions: CustomOption = {}) => {
  return DeleteStrapi({ url: api, id, loadingOptions });
};


