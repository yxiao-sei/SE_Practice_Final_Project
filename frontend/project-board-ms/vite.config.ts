import vue from '@vitejs/plugin-vue';
import vueJsx from '@vitejs/plugin-vue-jsx';
import { readFileSync, writeFileSync } from 'fs';
import { URL, fileURLToPath } from 'node:url';
import { resolve } from 'path';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';
import Components from 'unplugin-vue-components/vite';
import { defineConfig } from 'vite';

// https://vitejs.dev/config/
export default defineConfig(({ command }) => {
  return {
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `
              @use "@/assets/styles/index.scss" as *;
            `,
        },
      },
    },
    plugins: [
      vue(),
      vueJsx(),
      Components({
        extensions: ['vue', 'ts', 'md'],
        directoryAsNamespace: true,
        include: [/\.vue$/, /\.vue\?vue/, /\.md$/],
        resolvers: [
          ElementPlusResolver({
            importStyle: false,
          }),
        ],
        dts: 'src/components.d.ts',
      }),
    ],
    resolve: {
      alias: {
        vue: 'vue/dist/vue.esm-bundler.js',
        '@': fileURLToPath(new URL('./src', import.meta.url)),
        '~': fileURLToPath(new URL('./src/components', import.meta.url)),
      },
    },
    server: {
      cors: true, // 默认启用并允许任何源
      open: false,
      host: '0.0.0.0',
      port: 5177,
      proxy: {
        '/project-board-api/board': {
          // target: "https://yjsdata.ecnu.edu.cn",
          target: "https://seioa.ecnu.edu.cn",
          // target: "http://192.168.1.7:1367",
          changeOrigin: true,
          // rewrite: (path) => path.replace(/^\/plms-api/, ""),
        },
        "/cfms-api": {
          target: "https://seioa.ecnu.edu.cn",
          changeOrigin: true,
        },
      },
    },
    base: './',
  };
});
