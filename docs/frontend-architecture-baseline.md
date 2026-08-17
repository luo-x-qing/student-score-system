# 前端架构与版本基线

## 1. 文档信息

| 项目 | 内容 |
| --- | --- |
| 适用模块 | `student-score-frontend` |
| 当前阶段 | Vue 空应用基线 |
| 基线日期 | 2026-08-17 |
| 版本来源 | `package.json`、`package-lock.json` 和本机验证结果 |

本文档记录学生成绩处理系统前端已经落地的架构与版本。需求中已选定但尚未安装的依赖单独列为规划项，不视为当前实现。

## 2. 当前架构

前端采用 Vue 3 单页应用架构，由 Vite 提供开发服务器和生产构建能力。前端与 Spring Boot 后端保持独立目录、独立依赖和独立构建：

```text
student-score-system/
├── student-score-backend/   # Spring Boot 后端
└── student-score-frontend/  # Vue 前端
```

当前应用入口为 `index.html` 和 `src/main.js`，页面使用 Vue 单文件组件。前端依赖由 npm 管理，锁文件版本为 3。

## 3. 版本基线

### 3.1 运行与构建环境

| 组件 | 已验证版本 | 用途 |
| --- | --- | --- |
| Node.js | 24.5.0 | JavaScript 运行环境 |
| npm | 11.5.2 | 依赖和脚本管理 |
| Vite | 7.3.6 | 开发服务器与生产构建 |
| `@vitejs/plugin-vue` | 6.0.8 | Vite 的 Vue 单文件组件支持 |

### 3.2 应用框架

| 组件 | `package.json` 声明 | 当前锁定版本 | 用途 |
| --- | --- | --- | --- |
| Vue | `^3.5.0` | 3.5.41 | 前端视图框架 |
| `@vue/compiler-sfc` | `^3.5.0` | 3.5.41 | Vue 单文件组件编译器 |

Vue 与 `@vue/compiler-sfc` 应保持相同版本，避免开发环境和构建环境的组件编译行为不一致。

## 4. 已验证命令

在 `student-score-frontend` 目录执行：

```powershell
npm install
npm run dev
npm run build
```

验证结果：

- `npm run dev` 可启动 Vite 7.3.6，默认地址为 `http://localhost:5173/`。
- `npm run build` 可完成生产构建，产物写入 `dist/`。
- `node_modules/` 和 `dist/` 已由仓库根目录的 `.gitignore` 忽略。

## 5. 兼容性说明

HBuilderX 创建的 Vue 3 模板最初使用 Vue 3.2.8、Vite 2.5.3 和 `@vitejs/plugin-vue` 1.6.0。Vite 2.5.3 无法在本项目的 Node.js 24.5.0 环境中正常加载 `vite.config.js`，会报告：

```text
Error: config must export or return an object.
```

项目已将构建链升级到本文件记录的版本，解决该兼容性问题。后续不得仅根据 HBuilderX 模板名称判断版本，应以 `package-lock.json` 中的实际安装版本为准。

## 6. 规划中的前端依赖

根据项目需求，后续阶段计划引入以下能力，但截至本基线日期尚未安装：

| 组件 | 计划用途 | 引入时机 |
| --- | --- | --- |
| Vue Router | 页面路由 | 建立应用页面骨架时 |
| Element Plus | 表格、表单、弹窗和分页 | 开始业务页面开发时 |
| Axios | 调用后端 API | 配置后端健康检查和 API 层时 |
| ECharts | 饼图和柱状图 | 开发成绩统计页面时 |

引入这些依赖时，应在本文档补充声明版本、实际锁定版本、用途和验证结果。

## 7. 版本维护规则

1. `package.json` 记录允许安装的版本范围，`package-lock.json` 记录可复现安装的实际版本。
2. 提交依赖变更时必须同时提交 `package.json` 和 `package-lock.json`。
3. 升级 Vue 时同步检查 `@vue/compiler-sfc`，升级 Vite 时同步检查 `@vitejs/plugin-vue`。
4. 依赖升级后至少执行一次 `npm run build`；涉及开发服务器或代理配置时，同时验证 `npm run dev`。
5. 本文档只记录经过安装和验证的版本；仅存在于需求中的技术选型放在“规划中的前端依赖”。
