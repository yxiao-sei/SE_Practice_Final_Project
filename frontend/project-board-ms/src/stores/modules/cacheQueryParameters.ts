import { defineStore } from 'pinia';

export const useCacheQueryParameters = defineStore('store_cache_parameters', {
  state: () => ({
    params: {},
  }),
  getters: {},
  actions: {
    async setParams(query: any, route: { path: string; targetPath: string }) {
      const { path, targetPath } = route;
      if (targetPath) {
        this.params = { [targetPath]: this.params?.[targetPath] || {}, [path]: query };
      } else {
        this.params = { [path]: query };
      }
    },
    getParams(path: string) {
      return this.params?.[path] || {};
    },
  },
  persist: {
    // 存储key
    key: '_store_cache_parameters',
    // 存储源
    storage: sessionStorage,
    // 按需存储
    paths: ['params'],
  },
});
