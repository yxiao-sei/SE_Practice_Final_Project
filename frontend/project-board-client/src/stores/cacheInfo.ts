import { defineStore } from "pinia";
import { ref, computed } from "vue";

export interface User {
  // 定义 User 接口的具体字段
  token?: string;
  username?: string;
  realName?: string;
}

export const useUserInfo = defineStore('user-info', () => {
  const user = ref<any>();
  const loginTime = ref(0)
  const token = ref<{ access_token: string } | null>(null);
  const username = computed(() => user.value?.username);
  const realName = computed(() => user.value?.realName);

  const reset = () => {
    user.value = null;
    token.value = null;
  };

  const setUserInfo = (payload: User) => {
    user.value = payload;
  };

  const setToken = (payload: any) => {
    token.value = payload;
  };

  const setLoginTime = (payload:number) => {
    loginTime.value = payload;
  }

  return {
    token,
    username,
    realName,
    setUserInfo,
    reset,
    setToken,
    setLoginTime,
    loginTime,
    user
  };
}, {
  persist: {
    key: '_store_user',
    storage: sessionStorage,
    paths: ['user', 'token', 'loginTime']
  }
});
