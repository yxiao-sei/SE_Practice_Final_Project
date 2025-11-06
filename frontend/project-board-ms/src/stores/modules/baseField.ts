import { findDepartmentsListApi } from "@/service/api/department";
import { getOfficeRolesApi, getTeacherRoles, getStudentRoles } from "@/service/api/user";
import { defineStore } from "pinia";
import { authenticationMethods } from "../_baseFiledValueJson";
import { shallowRef } from "vue";
import { generateDepartmentTree } from "@/utils";

interface BaseObj {
  id: string | number;
  label: string;
  value: string | number;
  jsonData?: any;
}
// , "metadatasUnit"
const metadatasKeys = [
  "projectLabels",
  "metadataCategory",
  "projectOfDepartments",
  "memberRecruitmentStatus",
  "projectStatus",
  "metadataDirection",
  "xxxsList",
  "xwlbList",
  "userTypeList",
  "xqList",
  "postStatus"
];
interface BaseFieldState {
  [x: string]: any
}
export const useBaseFieldStore = defineStore("baseField", {
  state: (): BaseFieldState => ({
    roles: [],
    projectLabels: [],
    xxxsList: [],
    xqList: [],
    postStatus: [],
    xwlbList: [],
    userTypeList: [],
    projectOfDepartments: [],
    memberRecruitmentStatus: [],
    projectStatus: [],
    adminList: [],
    metadataCategories: [],
    reobtainKeys: [],
    metadataDirection: [],
    authenticationMethods,
    roleTeacherList: shallowRef([]),
    roleStudentList: shallowRef([]),
    roleDepartmentList: shallowRef([]),
    departmentTreeList: shallowRef([]),
    subDepartmentList: shallowRef([]),
  }),
  getters: {},
  actions: {
    dispatchRoleList() {
      if (this.roleDepartmentList.length && this.roleTeacherList.length && this.roleStudentList.length)
        return Promise.resolve({
          roleDepartmentList: this.roleDepartmentList,
          roleTeacherList: this.roleTeacherList,
          roleStudentList: this.roleStudentList,
        });
      return new Promise((resolve) => {
        const promiseArr = [getOfficeRolesApi(), getTeacherRoles(), getStudentRoles()];
        Promise.all(promiseArr).then(([resD, resO, resS]) => {
          const departmentRoles = resD.roles;
          this.roleDepartmentList = departmentRoles.map((role: any) => {
            const key = Object.keys(role)[0];
            return {
              label: role[key],
              value: key,
            };
          });
          const officeRoles = resO.roles;
          this.roleTeacherList = officeRoles.map((role: any) => {
            const key = Object.keys(role)[0];
            return {
              label: role[key],
              value: key,
            };
          });
          const studentRoles = resS.roles;
          this.roleStudentList = studentRoles.map((role: any) => {
            const key = Object.keys(role)[0];
            return {
              label: role[key],
              value: key,
            };
          });
          resolve({
            roleDepartmentList: this.roleDepartmentList,
            roleTeacherList: this.roleTeacherList,
            roleStudentList: this.roleStudentList,
          });
        });
      });
    },
    dispatchFetchDepartmentList(refresh = false) {
      if (this.departmentTreeList.length && !refresh) return;
      findDepartmentsListApi({ pagination: { pageSize: 10000, page: 1 } }).then(
        ({ result }) => {
          this.departmentTreeList = generateDepartmentTree(result);
          this.subDepartmentList = result;
        }
      );
    },
    fetchRoleList() {
      if (!this.roles.length || this.reobtainKeys.includes("roles")) {
        getOfficeRolesApi().then((res) => {
          this.roles = res.roles.map((role: any) => {
            const key = Object.keys(role)[0];
            return {
              label: role[key],
              value: key,
            };
          });
          this.reobtainKeys = this.reobtainKeys.filter((key: string) => key !== "roles"
          );
        });
      }
    },
    setFieldCached<T extends keyof BaseFieldState>(key: T, value: BaseFieldState[T]) {
      this[key] = value;
    },
    addReobtainKey(keys: string | string[]) {
      let targetKeys = [];
      if (Array.isArray(keys)) {
        targetKeys = (keys.includes("metadatas") ? [...metadatasKeys, ...keys] : keys).filter((key) => !this.reobtainKeys.includes(key));
      } else {
        targetKeys = (
          keys === "metadatas" ? [...metadatasKeys, keys] : [keys]
        ).filter((key) => !this.reobtainKeys.includes(key));
      }
      this.reobtainKeys = this.reobtainKeys.concat(targetKeys);
    },
    updateReobtainKeys(key: string) {
      this.reobtainKeys = this.reobtainKeys.filter((item: string) => item !== key);
    },
  },
});
