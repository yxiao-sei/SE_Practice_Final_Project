import { defineStore } from "pinia";

export const useCachePermissionRoutesStore = defineStore("permissionRoutes", {
  state: () => ({
    permissionRoutes: [],
  }),
  getters: {},
  actions: {
    setPermissionRoutes(payload: any) {
      this.permissionRoutes = payload;
    },
  }
});
