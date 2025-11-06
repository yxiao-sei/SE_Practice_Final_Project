export * as asyncChildRoute from "./index";
import Dynamic from "./Dynamic.vue";
import { defineComponent } from "vue";
import ChildEl from "./RootView.vue";
import { deepClone } from "../utils";
import { RouterView } from "vue-router";

const RootView = defineComponent({
  components: {
    RouterView,
  },
  setup() {},
  template: `
      <router-view v-slot="{ Component, route }">
        <keep-alive>
          <component v-if="route.meta.keepAlive" :is="Component" :key="route.path"></component>
        </keep-alive>
        <component v-if="!route.meta?.keepAlive" :key="route.path" :is="Component"></component>
      </router-view>
      `,
});

export const generateDynamicRoute = (route: any) => {
  const copyRoute = deepClone(route);
  ChildEl.__name = copyRoute.name as string;
  copyRoute.component = ChildEl;
  const baseName = `${route.name}_root`;
  return {
    path: `${copyRoute.path}/dynamic`,
    name: baseName,
    redirect: copyRoute.path,
    component: RootView,
    meta: { ...route.meta, isRoot: true },
    children: [
      {
        ...copyRoute,
        meta: {
          ...route.meta,
          target: baseName
        },
      },
      {
        path: `${route.path}/:name`,
        component: Dynamic,
        name: `${route.name}Root_dynamic`,
        meta: {
          ...route.meta,
          target: baseName,
          isMenu: false,
        },
      },
    ],
  };
};
