# Project Board 客户端（Project-Board-Client）

面向学生与评审用户的移动端/响应式客户端，基于 Vue 3 + Vite 构建。提供项目浏览、报名预约、个人信息管理等功能，与 LabLink 后端服务、Strapi 内容中心协同。

---

## 框架与技术栈说明

- **Vue 3 + TypeScript**：组合式 API，提升组件复用与类型安全。
- **Vite 6**：按需编译、极速热更新，兼容移动端调试。
- **Vant 4 + Tailwind CSS**：移动端 UI 组件库 + 原子化样式，适配多端。
- **Pinia + pinia-plugin-persistedstate**：状态管理与登录态持久化。
- **Vue Router 4**：页面路由；结合 `meta` 控制 tab 显示、缓存策略。
- **Axios + 请求工具集**：统一错误处理、Loading、请求取消。
- **PostCSS（pxtorem / preset-env）**：移动端自适应与现代 CSS 支持。

---

## 目录结构速览

```
frontend/project-board-client/
├─ src/
│  ├─ assets/          # 全局样式、图标与 Tailwind 基础样式
│  ├─ components/      # 移动端业务组件（项目卡片、搜索栏等）
│  ├─ router/          # 路由定义（含 tab 配置、导航守卫）
│  ├─ service/         # API 调用、axios 封装、Strapi 接口
│  ├─ stores/          # Pinia store（缓存信息、选项数据等）
│  ├─ views/           # 页面视图：项目、预约、订阅、个人中心
│  └─ hooks/           # 业务 hooks，如虚拟文件上传
├─ tailwind.config.js  # Tailwind 定制（色板、断点、插件）
├─ vite.config.js      # Vite 配置（别名、插件、自动导入）
├─ postcss.config.cjs  # REM/Autoprefixer 等 PostCSS 插件配置
└─ pnpm-workspace.yaml # 与其他前端模块共享依赖
```

---

## 环境准备

- Node.js ≥ 20（建议使用 LTS 版本，匹配 `@tsconfig/node22`）
- 包管理器推荐 **pnpm 8+**（项目锁定文件为 `pnpm-lock.yaml`）
- 推荐 IDE：VS Code（安装 Volar、ESLint、Tailwind CSS IntelliSense）
- 确保后端 LabLink 服务、Strapi、RabbitMQ 已按主 README 启动

---

## 安装依赖

```bash
# 进入项目目录
cd frontend/project-board-client

# 安装依赖（推荐）
pnpm install

# 或使用 npm
npm install
```

---

## 开发与调试

```bash
# 启动本地开发服务器（默认 http://localhost:5173/）
pnpm dev

# 或使用 npm
npm dev
```

调试提示：

- API 地址配置在 `src/service/config/index.ts`，按环境切换。
- Tailwind 自定义配置在 `tailwind.config.js` 与 `src/assets/main.css`。
- 若需要与真实移动设备调试，可通过 `vite --host` 或使用 `pnpm dev -- --host` 暴露内网地址。
- `src/stores/cacheInfo.ts` 中的持久化策略决定了登录态存储方式，开发时可适度缩短过期时间。

---

## 编译与发布

```bash
# 构建前执行类型检查 + 打包（默认 dist/）
pnpm build

# 仅构建（跳过类型检查）
pnpm build-only

# 产物预览（需先 build）
pnpm preview
```

构建产物位于 `dist/`，可与后端服务同域部署或通过 CDN 提供静态资源。  
如需区分环境，请在 `vite.config.js` 中扩展 `define` 或 `.env.production`。

---

## 常见问题

- **移动端 REM 适配异常**：确认 `postcss-pxtorem`、`meta viewport` 配置保持一致，并检查 `@vant/touch-emulator` 是否在桌面端启用。
- **请求被跨域拦截**：在开发环境中使用 `vite.config.js` 内的代理设置，或在后端允许对应的 CORS。
- **组件样式冲突**：Tailwind 与 Vant 并存时，建议使用 `:global` 或 BEM 命名避免覆盖。

---

如需补充功能设计、接口约定或性能优化文档，可在仓库中新增对应章节。祝开发顺利！
