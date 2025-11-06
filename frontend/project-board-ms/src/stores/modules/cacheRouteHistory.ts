import { defineStore } from "pinia";

export const useCacheRouteHistory = defineStore("store_cache_route_history", {
  state: () => ({
    routeHistory: <any[]>[],
  }),
  getters: {},
  actions: {
    addRouteHistory(
      route: { name: string; menuName: any; params: string; },
      clearable: boolean = false
    ) {
      if (clearable) {
        this.routeHistory = [route];
      } else {
        this.routeHistory.push(route);
      }
    },
    clearRouteHistoryData() {
      this.routeHistory = [];
    },
    removeRoute(index: number) {
      this.routeHistory = this.routeHistory.slice(0, index + 1);
    },
  },
  persist: {
    // 存储key
    key: "_store_cache_route_history",
    // 存储源
    storage: sessionStorage,
    // 按需存储
    paths: ["routeHistory"],
  },
});
