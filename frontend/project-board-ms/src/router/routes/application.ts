import { RouterView } from "vue-router";

export const routes = [
  {
    path: "/project-recruit-application",
    component: () => import("@/views/permission/application/List.vue"),
    name: "Application",
    meta: {
      icon: "icon-shezhi",
      menuName: "学生应聘申请",
      order: 20,
      isMenu: true,
    },
  }]