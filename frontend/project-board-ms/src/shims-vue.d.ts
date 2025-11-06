/*
 * @Author: lanseliuyiya laladila1986@163.com
 * @Date: 2022-12-02 18:09:06
 * @LastEditors: lanseliuyiya laladila1986@163.com
 * @LastEditTime: 2024-12-15 19:36:36
 * @FilePath: /project-board-ms/src/shims-vue.d.ts
 * @Description:
 *
 * Copyright (c) 2022 by lanseliuyiya laladila1986@163.com, All Rights Reserved.
 */

declare module '*.vue' {
  import { App, defineComponent } from 'vue';

  const component: ReturnType<typeof defineComponent> & {
    install(app: App): void;
  };
  export default component;
}
declare global {
  interface Window {
    Public: any;
  }
}
