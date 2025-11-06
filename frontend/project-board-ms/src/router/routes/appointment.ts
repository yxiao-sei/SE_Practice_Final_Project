import { RouterView } from "vue-router";

export const routes = [
  {
    path: "/project-appointment",
    component: () => import("@/views/permission/appointment/List.vue"),
    name: "Appointment",
    meta: {
      icon: "icon-shezhi",
      menuName: "面试预约",
      order: 20,
      isMenu: true,
    },
  }]