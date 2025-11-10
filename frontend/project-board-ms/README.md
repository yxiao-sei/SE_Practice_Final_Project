# Project Board 管理端（Project-Board-MS）

基于 Vue 3 + Vite 的项目进度/成员招募管理后台。项目聚焦「配置驱动」与「表单/看板」场景，可与 LabLink 系列后端微服务联动。

---

## 框架与技术栈说明

- **Vue 3 + TypeScript**：以组合式 API 组织业务逻辑。
- **Vite 4**：极快的开发服务器与按需构建能力。
- **Element Plus**：主力 UI 组件库，配合自定义主题变量。
- **Pinia + pinia-plugin-persistedstate**：状态管理与会话缓存。
- **Vue Router 4**：多级路由与权限控制，支持动态路由注入。
- **Axios + 自封装请求工具**：统一拦截、Loading/取消重复请求。
- **VXE-Table & ECharts**：复杂表格/导入导出能力与数据可视化。
- **HQIT plugin plus / utils**：集成弹窗、上下文菜单、权限路由等增强组件。

---

## 目录结构速览

```
frontend/project-board-ms/
├─ src/
│  ├─ assets/                # 全局样式、主题、图标
│  ├─ asyncRoute/            # 动态路由与配置化页面入口
│  ├─ components/            # 业务通用组件（导入导出、确认框等）
│  ├─ config-page/           # JSON 配置驱动的表单/表格
│  ├─ hooks/                 # 领域复用逻辑（上传、审批、缓存路由等）
│  ├─ layout/                # 布局与导航
│  ├─ router/                # 路由表与模块划分
│  ├─ service/               # 接口定义、axios 封装、分页工具
│  ├─ stores/                # Pinia store（权限、缓存、基础字段）
│  ├─ views/                 # 业务页面（项目、成员、需求等模块）
│  └─ vxTable/               # 表格能力扩展（renderer、modal、命令）
├─ public/                   # 静态资源
├─ vite.config.ts            # Vite 配置，含插件/别名
├─ vitest.config.ts          # 单元测试配置
└─ tsconfig*.json            # TypeScript 配置（应用、测试、节点）
```

---

## 环境准备

- Node.js ≥ 18（建议 18 LTS，确保兼容 `pnpm` 与 Vite）
- 包管理器推荐 **pnpm 8+**（仓库使用 `pnpm-lock.yaml`）  
  若未安装，执行 `npm install -g pnpm`
- 推荐 IDE：VS Code（安装 Volar、ESLint、Prettier）、WebStorm
- 后端服务：确保 LabLink 后端、Strapi、RabbitMQ 已按主仓库文档启动

---

## 安装依赖

```bash
# 进入项目目录
cd frontend/project-board-ms

# 安装依赖（推荐）
pnpm install

# 如需使用 npm
npm install
```

---

## 开发与调试

```bash
# 启动本地开发服务器（默认 http://localhost:5173/）
npm dev

# 如需使用 npm
npm dev
```

常见调试要点：

- `src/service/config/index.ts` 中定义基础 API 地址，按环境修改。
- `src/router/routes` 以及 `config-page/*.json` 控制页面结构与表单字段，支持配置驱动。
- 开发阶段建议开启浏览器 DevTools 的 `Preserve log`，便于排查接口重定向或权限问题。

---

## 编译与打包

```bash
# 标准构建（默认使用当前 mode，输出 dist/）
pnpm build

# 生产环境构建（等同 --mode=production）
pnpm build:prd

# UAT / 验收环境构建
pnpm build:uat

# 产物本地预览（需先 build）
pnpm preview
```

构建输出位于 `dist/`，可部署至 Nginx 或 LabLink 前端容器镜像。  
如需自定义环境变量，请在 `vite.config.ts` 中补充 `define` 或 `envPrefix`。

---

## 常见问题

- **依赖安装失败**：确认 Node 版本 ≥18，清理 `.pnpm-store` 或使用 `pnpm install --no-frozen-lockfile`。
- **Element Plus 样式异常**：确保引入 `src/assets/styles/index.scss`，并检查自定义主题变量是否被覆盖。
- **动态路由未加载**：检查 `stores/modules/permissionRoutes.ts` 与 `asyncRoute/index.ts` 的权限数据是否匹配后端返回。
- **打包后接口 404**：确认 `service/config` 中的 `baseURL` 已按部署环境调整，或通过反向代理处理。

---

如需更多说明（例如组件使用规范、国际化策略、CI/CD 集成），欢迎在仓库中新增相应文档或联系维护者。**祝开发顺利！**
