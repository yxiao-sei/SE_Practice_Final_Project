import { type RouteRecordRaw } from 'vue-router';

export const routes: RouteRecordRaw[] = [
  {
    path: '/:pathMatch(.*)',
    name: 'page404',
    component: () => import('./404.vue'),
    meta: {
      title: 'page404',
      noCache: true,
    },
  },
];
