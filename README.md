本项目采用**“一端服务，多端接入”**的核心理念，构建包含移动端（Android）、Web 管理端和 Java 服务端的完整生态。各端通过 RESTful API 进行数据交互，并基于 Token 实现鉴权。

1. 📱 移动客户端（Android 端）
   全栈请求的完整生命周期。

第 1 步：Android 端发起请求 (Jetpack Compose + Retrofit + 协程)

动作： 首页 UI 渲染时，ViewModel 触发获取数据的逻辑。

技术： Kotlin 协程 (Coroutines) 在后台线程悄悄启动，Retrofit 库将你的请求打包成一个 HTTP 请求

第 2 步：后端网关接收与路由 (Spring Boot - Controller)

动作： 服务器上的 Tomcat 容器收到请求。Spring Boot 根据网址路径，将请求精准分发给 Controller 类里的对应方法。

技术： 使用 @RestController 和 @GetMapping 注解。

第 3 步：业务逻辑与查库 (Spring Boot - Service & MyBatisplus)

动作： Controller 把任务交给 Service 层处理（比如只查前 10 个商品）。

第 4 步：数据库响应 (MySQL)

动作： MySQL 执行查询， 数据找出来，返回给 Spring Boot。

第 5 步：数据序列化与返回 (Spring Boot - JSON)

动作： Spring Boot 把拿到的 Java List 对象，自动转换成轻量级的 JSON 格式

第 6 步：Android 端接收与刷新 UI (Gson + ViewModel + Jetpack Compose)

动作： Android 的 Retrofit 收到 JSON 后，通过 Gson 转换器自动变成 Kotlin 的 Product 数据类对象。

技术： ViewModel 更新自己的内部状态（State）。Jetpack Compose 一旦发现数据状态变了，瞬间触发“重组（Recomposition）”，把空荡荡的屏幕瞬间画满精美的商品卡片！
2. 💻 Web 管理端（前端后台）
管理端旨在提供高效、易用的后台数据运维与管理功能：

UI 框架： 采用 Vue 3 搭配 Element Plus 组件库，快速搭建现代化中后台界面。

网络请求： 封装 Axios 用于对接后端 Spring Boot 接口，统一处理请求头（注入 Token）和全局错误响应。

状态管理： 使用 Pinia 替代传统的 Vuex，负责全局存储管理员的身份信息、权限状态以及 鉴权 Token。

3. ⚙️ 服务端（后端 API）
   服务端作为整个系统的中央数据处理与中枢：

核心框架： 基于 Java + Spring Boot 构建，提供高可用、易扩展的后端服务。

接口规范： 全面采用 RESTful 风格 设计 API，确保接口语义清晰，方便多端（移动端、Web端）无缝统一接入。

数据持久化： 使用 MySQL 关系型数据库作为核心数据落地中心。
