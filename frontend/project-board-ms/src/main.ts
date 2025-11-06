import "@/assets/iconfont/index.js";
import "@/assets/theme/index.scss";
import router from "@/router";
import { useTable } from "@/vxTable";
import "@vue-office/docx/lib/index.css";
import "@vue-office/excel/lib/index.css";
import ElementPlus from "element-plus";
import initRem from "./rem";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import { createApp } from "vue";
import PageBreadcrumb from "~/pageBreadcrumb/Index.vue";
import SearchItem from "~/searchItem/Index.vue";
import SvgIcon from "~/svg-icon/Index.vue";
import App from "./App.vue";
import initAsyncRoutes from 'hqit-plugin-plus';
import { getUserApi } from "./service/api/user";
import { ElMessage } from "element-plus";
import { asyncRoutes } from "./router/index";
// import { watchAuthRouter } from "./asyncRoute";
const config = {
  router: router,
  asyncRouterMap: asyncRoutes,
  getUser: getUserApi,
  authKey: 'authorities',
  Message: ElMessage,
};
initRem(192);
const app = createApp(App);
app.use(createPinia().use(piniaPluginPersistedstate));
// watchAuthRouter(router);
app.component("SvgIcon", SvgIcon);
app.component("PageBreadcrumb", PageBreadcrumb);
app.component("SearchBlock", SearchItem);
app.use(ElementPlus);
app.use(useTable);
// app.use(ContextMenu)
app.use(initAsyncRoutes as any, config);
app.use(router);
app.mount("#app");
