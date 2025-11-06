import { RouterView } from "vue-router";

export const routes = [
  {
    path: "/setting",
    component: RouterView,
    name: "Setting",
    meta: {
      icon: "icon-shezhi",
      menuName: "系统设置",
      isSub: true,
      order: 88,
      roles: [{ key: 'ROLE_SETTINGS', value: 'read' }]
    },
    children: [
      {
        path: "/setting/dictionary",
        component: () => import("@/views/permission/dictionary/dictionary.vue"),
        name: "Dictionary",
        meta: {
          menuName: "字典库",
          target: "/setting",
          isMenu: true,
          roles: [{ key: 'ROLE_SETTINGS', value: 'read' }]
        },
      }],
  }]