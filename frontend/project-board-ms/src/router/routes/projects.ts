import { type RouteRecordRaw, RouterView } from "vue-router";

export const pageMenus = [
  {
    path: "/project-application",
    component: () => import("@/views/permission/application/List.vue"),
    name: "ProjectApplication",
    meta: {
      icon: "icon-shezhi",
      menuName: "学生应聘申请",
      order: 1,
      isMenu: false,
      target: "/project-list",
    },
  },
  {
    path: "/recruit-requirements",
    component: () => import("@/views/permission/recruit-requirements/List.vue"),
    name: "RecruitRequirements",
    meta: {
      icon: "icon-shezhi",
      menuName: "岗位需求",
      order: 1,
      isMenu: false,
      target: "/project-list",
    },
  },
  // {
  //   path: "/members",
  //   name: "Members",
  //   component: import("@/views/permission/projects/Members.vue"),
  //   meta: {
  //     menuName: "成员一览",
  //     isMenu: false,
  //     order: 1,
  //     target: "/project-list",
  //   },
  // },
  // {
  //   path: "/recruit",
  //   name: "Recruit",
  //   component: import("@/views/permission/projects/Recruit.vue"),
  //   meta: {
  //     menuName: "招聘申请",
  //     isMenu: false,
  //     order: 2,
  //     target: "/project-list",
  //   },
  // },
  // {
  //   path: "/division",
  //   name: "Division",
  //   component: import("@/views/permission/projects/Division.vue"),
  //   meta: {
  //     menuName: "成员分工管理",
  //     isMenu: false,
  //     order: 3,
  //     target: "/project-list",
  //   },
  // }
]

export const routes: RouteRecordRaw[] = [
  {
    path: "/project-list",
    name: "ProjectList",
    component: () => import("@/views/permission/projects/List.vue"),
    meta: {
      icon: "icon-a-wenjian1",
      menuName: "课题项目管理",
      isMenu: true,
      order: 10,
    }
  },
  {
    path: "/project-detail",
    name: "ProjectDetail",
    component: () => import("@/views/permission/projects/Detail.vue"),
    redirect: "/project-application",
    meta: {
      icon: "icon-Setting_13_",
      menuName: "详情",
      isMenu: false,
      target: "/project-list",
    },
    children: pageMenus
  }
]