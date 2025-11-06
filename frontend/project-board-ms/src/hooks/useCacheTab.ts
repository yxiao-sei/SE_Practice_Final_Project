/*
 * @Author: laladila1986@163.com
 * @Date: 2023-09-21 14:25:32
 * @LastEditors: laladila1986@163.com
 * @LastEditTime: 2023-10-30 11:24:22
 * @FilePath: \llbxt-web\src\hooks\useCacheTab.ts
 * @Description:
 * @Copyright: Copyright 2023, All Rights Reserved.
 */
import { ref } from 'vue';

export interface PageMenu {
  name: string
}


export type PageMenuProps = {
  pageMenus: PageMenu[]; // 页面菜单数组，每个菜单包含name和path属性
  defaultValue: string; // 默认选中的菜单项的path属性值，用于初始化选中项和session缓存值
  sessionKey: string; // session缓存的key值，用于存储当前选中的菜单项的path属性值
};

interface InitConfig {
  sessionKey: string;
  defaultValue: string;
  direct?: string;
}
export const useCacheTab = ({ sessionKey, defaultValue, direct }: InitConfig) => {
  const activeTab = ref('');

  const uuKey = sessionKey;

  if (direct) {
    activeTab.value = direct;
  } else if (sessionStorage.getItem(uuKey)) {
    activeTab.value = sessionStorage.getItem(uuKey) as string;
  } else {
    activeTab.value = defaultValue;
  }

  window.document.title = activeTab.value;

  const setSessionCache = () => {
    sessionStorage.setItem(uuKey, activeTab.value);
    window.document.title = activeTab.value;
  };

  return {
    setSessionCache,
    activeTab,
  };
};
