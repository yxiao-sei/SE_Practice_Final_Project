import SystemUserPage from "@/config-page/SystemUser.json";
import Projects from "@/config-page/Projects.json";
import Student from "@/config-page/Student.json";
import Dictionary from "@/config-page/Dictionary.json";
import Members from "@/config-page/Members.json";
import Applications from "@/config-page/Applications.json";
import Appointment from "@/config-page/Appointment.json";
import RecruitRequirements from "@/config-page/RecruitRequirements.json";
import { GetStrapi, GetOneStrapi, PostStrapi, PutStrapi } from "@/service/strPagedApi";
import type { CustomOption } from "../config";

const api = "/project-board-api/board/strapi/api/config-pages";

const pageConfig = {
  SystemUser: SystemUserPage,
  Projects: Projects,
  Dictionary: Dictionary,
  Members: Members,
  Applications: Applications,
  Appointment: Appointment,
  Student: Student,
  RecruitRequirements: RecruitRequirements
}
export const findPageListApi = (params: any, loadingOptions: CustomOption = {}) => {
  const pageName = params.filters.name.$eq as keyof typeof pageConfig
  if (pageConfig[pageName]) return Promise.resolve({ result: [pageConfig[pageName]] })
  return GetStrapi({ params, url: api, loadingOptions });
};
export const findPageByIdApi = (id: string, loadingOptions: CustomOption = {}) => {
  return GetOneStrapi({ id, url: api, loadingOptions });
};
export const createPageApi = (data: any, loadingOptions: CustomOption = {}) => {
  return PostStrapi({ url: api, data, loadingOptions });
};
export const updatePageApi = (id: string, data: any, loadingOptions: CustomOption = {}) => {
  return PutStrapi({ id, url: api, data, loadingOptions });
};


