import { type RouteRecordRaw } from 'vue-router';

export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    component: () => import('./Index.vue'),
    name: '登录',
    meta: {
      menuName: '登录',
      isMenu: false,
    },
    children: [],
  },
];
