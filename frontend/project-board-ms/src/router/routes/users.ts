import { type RouteRecordRaw, RouterView } from "vue-router";
export const routes: RouteRecordRaw[] = [
  {
    path: "/user",
    component: RouterView,
    name: "UserMS",
    redirect: "/system-users",
    meta: {
      icon: "icon-Setting_13_",
      menuName: "用户管理",
      isSub: true,
      order: 77,
      roles: [{ key: 'ROLE_USER', value: 'read' }]
    },
    children: [
      {
        path: "/students",
        component: () => import("@/views/permission/user/Students.vue"),
        name: "Students",
        meta: {
          menuName: "学生",
          isMenu: true,
          target: '/user',
          roles: [{ key: 'ROLE_USER', value: 'read' }]
        },
      },
      {
        path: "/teachers",
        component: () => import("@/views/permission/user/Teachers.vue"),
        name: "Teachers",
        meta: {
          menuName: "教师",
          isMenu: true,
          target: '/user',
          roles: [{ key: 'ROLE_USER', value: 'read' }]
        },
      },
      {
        path: "/system",
        component: () => import("@/views/permission/user/System.vue"),
        name: "SystemUser",
        meta: {
          menuName: "系统管理员",
          isMenu: true,
          target: '/user',
          roles: [{ key: 'ROLE_USER', value: 'read' }]
        },
      }],
  },
];