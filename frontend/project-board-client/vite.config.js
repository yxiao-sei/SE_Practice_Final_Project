import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { VantResolver } from '@vant/auto-import-resolver';

// https://vite.dev/config/
export default defineConfig({
  base: './',
  plugins: [
    vue(),
    vueJsx(),
    vueDevTools(),
    AutoImport({
      resolvers: [VantResolver()],
    }),
    Components({
      resolvers: [VantResolver()],
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      '~': fileURLToPath(new URL('./src/components', import.meta.url)),
    },
  },
  server: {
    cors: true, // 默认启用并允许任何源
    open: false,
    port: 8091,
    proxy: {
      '/project-board-api/board/': {
        // target: 'http://demosite.imwork.net',
        // target: "https://yjsdata.ecnu.edu.cn",
        target: "https://seioa.ecnu.edu.cn",
        // target: "http://192.168.1.7:1367",
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/plms-api/, ""),
      },
    },
  },
})
