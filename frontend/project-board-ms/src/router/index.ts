import {
  createRouter,
  createWebHashHistory,
  type RouteRecordRaw,
} from "vue-router";
interface ImportRoute {
  [key: string]: {
    routes: RouteRecordRaw[];
  };
}
// 静态路由导入
const constantFiles: ImportRoute = import.meta.glob(
  "@/views/constant/*/index.ts",
  { eager: true }
);
let constantModules: RouteRecordRaw[] = [];
Object.keys(constantFiles).forEach((key: string) => {
  if (constantFiles[key].routes) {
    constantModules = constantModules.concat(constantFiles[key].routes);
  }
});
// 静态路由导入
const permissionFiles: ImportRoute = import.meta.glob(
  "@/router/routes/*.ts",
  { eager: true }
);
let permissionModules: RouteRecordRaw[] = [];
Object.keys(permissionFiles).forEach((key: string) => {
  if (permissionFiles[key].routes) {
    permissionModules = permissionModules.concat(permissionFiles[key].routes);
  }
});

export const asyncRoutes: Array<RouteRecordRaw> = [
  {
    path: '/layout',
    name: '系统首页',
    component: () => import('@/layout/Index.vue'),
    children: [...permissionModules],
  },
];

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: constantModules,
});

export default router;
