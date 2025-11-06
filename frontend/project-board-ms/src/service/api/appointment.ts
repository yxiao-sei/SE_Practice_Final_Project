import { GetStrapi, GetOneStrapi, PostStrapi, PutStrapi, DeleteStrapi } from "@/service/strPagedApi";
import type { CustomOption } from "../config";

const api = "/project-board-api/board/strapi/api/project-recruit-appointments";
export const findAppointmentsApi = (params: any, loadingOptions: CustomOption = {}) => {
  return GetStrapi({ params, url: api, loadingOptions });
};
export const findAppointmentByIdApi = (id: string, loadingOptions: CustomOption = {}) => {
  return GetOneStrapi({ id, url: api, loadingOptions });
};
export const createAppointmentApi = (data: any, loadingOptions: CustomOption = {}) => {
  return PostStrapi({ url: api, data, loadingOptions });
};
export const updateAppointmentApi = (id: string, data: any, loadingOptions: CustomOption = {}) => {
  return PutStrapi({ id, url: api, data, loadingOptions });
};
export const deleteAppointmentApi = (id: string, loadingOptions: CustomOption = {}) => {
  return DeleteStrapi({ url: api, id, loadingOptions });
};


