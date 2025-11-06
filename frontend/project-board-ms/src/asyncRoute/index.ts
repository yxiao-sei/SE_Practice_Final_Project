import { isPermissions } from "./utils";
import { RouterView, type RouteRecordRaw, type Router } from "vue-router";
import { getUserApi } from "@/service/api/user";
import { findMenuListApi } from "@/service/api/menu-config";
import { useCachePermissionRoutesStore, useCacheUserInfoStore } from "@/stores";
import { defineComponent } from "vue";
import PageMenuBar from "../components/PageMenuBar/Index.vue";
import { generateDynamicRoute } from "./views";
const permissionRoutes: any[] = [];
// 判断路由权限
function hasPermission(roles: any[]) {
  if (!roles.length) return true;
  return roles.some((role) => {
    return isPermissions(role.key, role.value);
  });
}

function setChildren(router: RouteRecordRaw) {
  if (
    router.children &&
    router.children instanceof Array &&
    router.children.length
  ) {
    if (router.children && router.children.length) {
      router.children = generateRouters(router.children);
    } else {
      router.children = undefined;
    }
  }
}

//递归生成权限路由
function generateRouters(routes: RouteRecordRaw[]) {
  // @ts-ignore
  return routes.filter((router) => {
    if (router.meta && router.meta.roles) {
      if (hasPermission(router.meta.roles)) {
        setChildren(router);
        return true;
      }
    } else {
      setChildren(router);
      return true;
    }
  });
}

const findChildren = (
  route: RouteRecordRaw,
  remoteRoutes: RouteRecordRaw[]
) => {
  return remoteRoutes.filter(
    (i) => i.meta?.target && i.meta.target === route.name
  );
};

const findChildrenMenu = (
  route: RouteRecordRaw,
  remoteRoutes: RouteRecordRaw[]
) => {
  return remoteRoutes.filter(
    (i) => i.meta?.target && i.meta.target === route.name && i.meta?.isMenu
  );
};

const isLevelTowSub = (
  route: RouteRecordRaw,
  remoteRoutes: RouteRecordRaw[]
) => {
  const children = findChildren(route, remoteRoutes);
  if (!children?.length) return false;
  return children?.find((i) => i.meta?.isMenu && i.meta?.level === 3);
};

const isRoot = (route: RouteRecordRaw, remoteRoutes: RouteRecordRaw[]) => {
  const children = findChildrenMenu(route, remoteRoutes);
  return children && children.length;
};

const RootView = (route: RouteRecordRaw, routes: RouteRecordRaw[]) => {
  return defineComponent({
    name: route.name as string,
    components: {
      PageMenuBar,
      RouterView,
    },
    setup() { },
    template: `
      ${isLevelTowSub(route, routes)
        ? `<PageMenuBar class="page-menu-bar" target=${route.path} />`
        : ""
      }
      <router-view v-slot="{ Component, route }">
        <keep-alive>
          <component v-if="route.meta.keepAlive" :is="Component" :key="route.name"></component>
        </keep-alive>
        <component v-if="!route.meta?.keepAlive" :key="route.name" :is="Component"></component>
      </router-view>
      `,
  });
};

export const setRouteComponent = (routes: RouteRecordRaw[]) => {
  if (!routes || !routes.length) return [];
  return routes.map((r: RouteRecordRaw) => {
    if (isRoot(r, routes)) {
      r.component = RootView(r, routes);
    } else {
      // ChildEl.__name = r.name as string;
      // r.component = ChildEl;
      r = generateDynamicRoute(r);
    }
    return r;
  });
};

const mergeRoutes = (
  remoteRoutes: RouteRecordRaw[],
  localRoutes: RouteRecordRaw[]
) => {
  const routes = [...remoteRoutes, ...localRoutes];
  return [
    ...new Map(
      routes.map((route: RouteRecordRaw) => [route.path, route])
    ).values(),
  ];
};

export const generateRouteRecord = async () => {
  const res: any = await getUserApi();
  if (res.code === 0) {
    // if(!/ROLE_SEI_SYSTEM_ADMIN/.test(res.result.details.roles)&&!/ROLE_SEI_ADMIN/.test(res.result.details.roles)&&!/ROLE_SEI_APPROVAL_ADMIN/.test(res.result.details.roles)){
    //   ElMessageBox({
    //     title: '提示',
    //     message: '您暂无权限访问管理后台，如有疑问请联系系统管理员。',
    //     showCancelButton: false,   // 移除取消按钮
    //     confirmButtonText: '确定',
    //     type: 'warning',
    //     closeOnClickModal: false,  // 禁止点击空白关闭
    //     closeOnPressEscape: false, // 禁止按ESC关闭
    //     showClose: false,          // 移除右上角的关闭按钮
    //   }).then(() => {
    //     // 点击确定后返回上一页面
    //     window.history.back();
    //   }).catch(() => {
    //     // 可选的错误处理（如果需要）
    //   });
    //   return
    // }
    useCacheUserInfoStore()?.setUserInfo({
      ...(res.result.details || {}),
      departmentWithRoles: res.result.departmentWithRoles,
    });
    const { result }: any = await findMenuListApi({
      pagination: { pageSize: 999 },
    });
    const remoteRoutes = result.map((r: any) => {
      return {
        meta: { ...r.meta, target: r.target, menuName: r.menuName },
        path: r.path,
        name: r.name,
        redirect: r.redirect,
        component: null,
      };
    });
    sessionStorage.setItem(
      "authorities",
      JSON.stringify(res.result.authorities)
    );
    const localRoutes = generateRouters(permissionRoutes);
    const _remoteRoutes = generateRouters(setRouteComponent(remoteRoutes));
    const routes = mergeRoutes(_remoteRoutes, localRoutes);
    return Promise.resolve(routes);
  }
};

const setRouters = async (router: Router) => {
  const routes: any = await generateRouteRecord();
  if (!routes) return;
  useCachePermissionRoutesStore()?.setPermissionRoutes(routes);
  const findFullRoute = (route: any): any => {
    const children = [
      ...(route.children || []),
      ...routes.filter((r: any) => r.meta.target === route.name),
    ];
    if (!children || children.length === 0) return { ...route, children: [] };
    return { ...route, children: children.map((r: any) => findFullRoute(r)) };
  };
  const addedKeys: string[] = [];
  routes.forEach((route: any) => {
    if (
      !addedKeys.includes(route.name as string) &&
      route.meta?.level === 1 &&
      !router.hasRoute(route.name as string)
    ) {
      const newRoute = findFullRoute(route);
      router.addRoute(route.meta.target || "Layout", newRoute);
      addedKeys.push(route.name as string);
    }
  });
};

const loginPath = "/login";

const getFirstMenu = (router: Router) => {
  try {
    const menuRoutes = router
      .getRoutes()
      .filter((i: any) => i.meta?.isMenu && i.meta?.level === 1)
      .sort((a: any, b: any) => {
        return a?.meta?.order - b?.meta?.order;
      });
    return menuRoutes[0]?.path;
  } catch (error) {
    return null;
  }
};

export const watchAuthRouter = (router: Router) => {
  let redirectTimes = 0;
  router.beforeEach(async (to, from) => {
    if (to.path == loginPath) {
      sessionStorage.removeItem("authorities");
      sessionStorage.removeItem("token");
      sessionStorage.removeItem("userInfo");
    }
    if (to && to.matched.length == 1 && redirectTimes < 1 || to.name === 'Layout') {
      redirectTimes++;
      await setRouters(router);
      if (to.name === 'Layout') {
        const firstMenuPath = getFirstMenu(router);
        if (firstMenuPath) return firstMenuPath;
      }
      return to.path;
    }
    redirectTimes = 0;
  });

  router.afterEach((to: any) => {
    document.title = to.meta && to.meta.menuName ? to.meta.menuName : to.name;
  });
};
