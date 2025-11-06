import { createApp } from 'vue'
import router from './router'
import App from "./App.vue"
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import '@vant/touch-emulator'
import 'vant/es/toast/style'
import 'vant/es/notify/style'
import 'vant/es/dialog/style'
import 'vant/es/popup/style'
import './assets/iconfont'
import './assets/main.css'
import SvgIcon from './components/SvgIcon.vue'

const app = createApp(App);
app.use(createPinia().use(piniaPluginPersistedstate));
app.use(router).mount('#app')
app.component('SvgIcon', SvgIcon);
router.beforeEach((to) => {
  document.title = to.name as any
});
