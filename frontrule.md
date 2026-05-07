# 前端开发规范（Vue3 + Element Plus + Axios + Vuex + ECharts）

## 一、通用原则

- 严格遵循 **SOLID、DRY、KISS、YAGNI** 原则
- 遵循 **OWASP 前端安全最佳实践**（如 XSS 防护、CSRF 防护、输入校验）
- 采用 **分层架构设计**，确保职责分离
- 代码生成过程中，不生成 node_modules、dist 等无关文件

---

## 二、技术栈规范

- **框架**：Vue3（Composition API）
- **UI组件库**：Element Plus
- **状态管理**：Vuex
- **请求库**：Axios
- **数据可视化**：ECharts
- **图标库**：Element Plus 内置图标、SVG 图标库
- **构建工具**：Vite
- **代码规范**：ESLint + Prettier
- **包管理**：npm 或 pnpm，禁止混用

---

## 三、项目结构规范

```
src/
  api/           // 所有后端接口请求封装
  assets/        // 静态资源（图片、样式、字体等）
  components/    // 通用组件
  views/         // 页面级组件
  router/        // 路由配置
  store/         // Vuex 状态管理
  utils/         // 工具函数
  styles/        // 全局样式
  icons/         // 图标资源
  App.vue        // 根组件
  main.js        // 入口文件
```

### 3.1 项目布局规范

- 管理系统推荐采用**顶部栏 + 侧边栏 + 主显示区**的经典三栏布局，提升用户导航效率和操作体验。
- **顶部栏（Header）**：用于展示系统名称、全局操作（如用户信息、通知、设置、退出登录等）。
- **侧边栏（Sidebar）**：用于模块/菜单导航，支持多级菜单，建议可折叠，选中高亮，图标与文字结合。
- **主显示区（Main Content）**：用于展示各业务页面内容，需自适应窗口大小，支持滚动。
- 布局组件建议统一封装，如 `Layout.vue`，并在 `App.vue` 或 `views/layout/` 目录下维护。
- 响应式设计：侧边栏在窄屏下自动收起或变为抽屉式，顶部栏内容自适应收缩。
- 侧边栏菜单数据建议与路由配置解耦，便于动态权限控制和菜单渲染。
- 主显示区支持多标签页（Tab）切换时，需保证标签页缓存与销毁机制合理，避免内存泄漏。
- 布局样式建议参考 Element Plus 官方管理后台模板或 Ant Design Pro 等主流设计风格，保持简洁、统一、美观。
- 禁止在布局组件中直接写业务逻辑，业务逻辑应在各自页面组件中实现。

**示意结构：**

```
App.vue
 └─ Layout.vue
     ├─ HeaderBar.vue   // 顶部栏
     ├─ SideBar.vue     // 侧边栏
     └─ MainContent.vue // 主显示区（router-view）
```

---

## 四、代码规范

### 1. 命名规范

- 组件名、文件名：`UpperCamelCase`（如 `UserList.vue`）
- 变量、方法名：`lowerCamelCase`（如 `getUserList`）
- 常量：`UPPER_SNAKE_CASE`（如 `API_BASE_URL`）
- 路由命名：简洁明了，使用英文小写加中划线（如 `user-list`）

### 2. 目录与文件

- 每个页面一个独立目录，包含页面组件和相关子组件
- 通用组件放在 `components/`，禁止与页面组件混放
- API 封装统一放在 `api/`，每个模块一个文件
- 工具函数统一放在 `utils/`

### 3. 代码风格

- 使用 ESLint + Prettier 自动格式化，禁止手动调整缩进
- 统一使用单引号 `'`，禁止混用单双引号
- 组件 props、emit 必须声明类型
- 禁止在业务代码中直接操作 DOM，需通过 Vue 方式
- 禁止直接在组件中写请求，必须通过 `api/` 封装

### 4. 注释规范

- 组件、方法、复杂逻辑必须添加注释，采用 JSDoc 风格
- 计划待完成的任务需添加 `// TODO`
- 存在潜在缺陷的逻辑需添加 `// FIXME`

---

## 五、组件开发规范

- 组件必须具备良好的复用性和独立性
- 组件 props 必须定义默认值和类型
- 组件事件命名采用 `update:xxx` 或 `onXxx` 风格
- 禁止在组件内直接调用 Vuex，需通过 props 或 emit 传递
- 组件样式采用 `scoped`，全局样式放在 `styles/`

---

## 六、状态管理（Vuex）规范

- 每个模块单独一个 store 文件，统一在 `store/index.js` 注册
- 状态命名简洁明了，避免歧义
- 禁止直接修改 state，必须通过 mutation
- 异步操作统一放在 action
- 禁止在组件中直接操作 store，需通过 mapState、mapActions 等辅助函数

---

## 七、接口请求（Axios）规范

- 所有请求统一封装在 `api/` 目录
- 请求和响应拦截器统一处理 token、错误提示等
- 接口地址、超时时间等配置统一管理
- 禁止在组件中直接写 axios 请求
- 响应数据结构需与后端约定，统一处理异常和 loading 状态

---

## 八、路由与权限

- 路由配置统一在 `router/` 目录
- 路由懒加载，提升性能
- 需根据用户权限动态生成路由
- 路由守卫统一处理登录态、权限校验

---

## 九、数据可视化（ECharts）规范

- 图表组件统一封装，禁止在页面直接初始化 ECharts
- 图表配置项与数据分离，便于维护
- 图表主题、颜色等风格统一配置

---

## 十、安全与性能

- 严格校验用户输入，防止 XSS、CSRF 等攻击
- 禁止直接拼接 HTML，需使用 v-html 时必须严格过滤
- 组件懒加载、图片懒加载，提升首屏性能
- 合理拆分 chunk，优化打包体积

---

## 十一、扩展性与可维护性

- 业务逻辑与视图分离，复杂逻辑抽离为 hooks 或 utils
- 关键业务逻辑需预留扩展点（如策略模式、插件机制）
- 日志、埋点等统一封装，禁止在业务代码中直接调用

---

## 十二、部署与环境

- 配置文件（如 API 地址）通过 `.env` 管理，禁止写死
- 区分开发、测试、生产环境
- 敏感信息禁止前端暴露

---

## 十三、常用第三方库建议

- 日期处理：`dayjs`
- 深拷贝：`lodash`
- 表单校验：`@vueuse/core` 或自定义 hooks

---

## 十四、文档与协作

- 代码变更需配套更新文档
- 组件、API、工具函数需有详细说明
- 采用 git 分支管理，feature/bugfix/release 规范命名

---

## 十五、跨域处理规范

- **开发环境下，统一通过 Vite 的代理（proxy）功能解决跨域问题，禁止在代码中直接修改接口地址以规避跨域。**
- 代理配置统一在 `vite.config.js` 文件中进行，示例配置如下：

```js
// vite.config.js 片段
export default {
  // ...其他配置
  server: {
    proxy: {
      '/api': {
        target: 'http://后端服务地址:端口', // 例如 http://localhost:8080
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api/, ''),
      },
    },
  },
}
```

- **接口请求统一以 `/api` 为前缀**，如 `/api/user/list`，便于代理和后期维护。
- 生产环境下，需由运维或网关统一处理跨域，前端不做跨域配置。
- 禁止在 axios 或 fetch 请求中使用 `withCredentials: true`，除非有明确需求并经安全评审。

---

如需详细代码示例或具体模板，可随时告知！
