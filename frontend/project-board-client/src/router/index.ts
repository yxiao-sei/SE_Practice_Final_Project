import { createRouter, createWebHashHistory, type RouteRecordRaw } from "vue-router";

export const tabBarList = [
  {
    path: '/projects',
    name: '课题项目一览',
    component: () => import('@/views/projects.vue'),
    meta: {
      isMenu: true,
      iconName: 'icon-a-wenjianjia1'
    }
  },
  // {
  //   path: '/my-projects',
  //   name: '我的项目',
  //   component: () => import('@/views/my-projects.vue'),
  //   meta: {
  //     isMenu: true,
  //     iconName: "icon-a-jiantieban2"
  //   }
  // },
  {
    path: '/personal',
    name: '个人中心',
    component: () => import('@/views/personal.vue'),
    meta: {
      isMenu: true,
      iconName: "icon-jianli"
    }
  }]

export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: '首页',
    redirect: '/projects',
    component: () => import('@/views/home.vue'),
    children: tabBarList
  },
  {
    path: '/application',
    name: '课题报名',
    component: () => import('@/views/application.vue')
  },
  {
    path: '/project-detail',
    name: '课题详情',
    component: () => import('@/views/project-detail.vue')
  },
  {
    path: '/application-detail',
    name: '课题报名详情',
    component: () => import('@/views/application-detail.vue')
  },
  {
    path: '/subscription',
    name: '订阅设置',
    component: () => import('@/views/subscription.vue')
  },
  {
    path: '/information',
    name: '个人简历',
    component: () => import('@/views/information.vue')
  },
  {
    path: '/appointment',
    name: '面试预约',
    component: () => import('@/views/appointment.vue')
  },
  {
    path: '/option',
    name: '选择项目课题',
    component: () => import('@/views/option.vue')
  },
  {
    path: '/new-appointment',
    name: '新增预约',
    component: () => import('@/views/new-appointment.vue')
  },
  // {
  //   path: '/appointment-detail',
  //   name: '面试预约详情',
  //   component: () => import('@/views/appointment-detail.vue')
  // }
];
const router = createRouter({
  history: createWebHashHistory(),
  routes,
});
export default router;
