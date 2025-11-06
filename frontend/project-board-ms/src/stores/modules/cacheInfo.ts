import { type User } from "@/model/user";
import { defineStore } from "pinia";

export const useCacheUserInfoStore = defineStore("cacheInfo", {
  state: () => ({
    // eslint-disable-next-line @typescript-eslint/consistent-type-assertions
    userInfo: {} as User,
    token: { access_token: "" },
  }),
  getters: {
    access_token(state) {
      return state.token?.access_token;
    },
    isDepartmentUser: (state) => {
      return (
        state.userInfo.roles &&
        state.userInfo.roles.includes("_YJSYXT_DEPARTMENT_")
      );
    },
    userDepartmentName(state) {
      return state.userInfo.departmentName;
    },
    authorityFilterParams(state) {
      if (state.userInfo?.roles.includes('ROLE_SEI_ADMIN') || state.userInfo?.roles.includes('ROLE_SEI_SYSTEM_ADMIN')) {
        return {};
      }
      return {
        leadingTeacherNumber: {
          "$eq": state.userInfo.username
        }
      };
    },
    username: (state) => state.userInfo.username,
    realName: (state) => state.userInfo.realName,
  },
  actions: {
    setUserInfo(payload: any) {
      this.userInfo = payload;
    },
    setToken(payload: any) {
      this.token = payload;
    },
  },
  persist: {
    // 存储key
    key: "_store_user",
    // 存储源
    storage: sessionStorage,
    // 按需存储
    paths: ["userInfo", "token"],
  },
});
