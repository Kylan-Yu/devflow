# DevFlow

[English](./README_EN.md) | 简体中文

本仓库是一个**中英双语全栈开发者社区**，面向作品集展示和远程工程岗位面试。

## 项目价值
- 展示为300k+日活跃用户(DAU)设计的生产级工程能力。
- 覆盖完整用户旅程：注册、内容创作、社交互动、内容审核、实时通知。
- 采用模块化单体架构，可在需要时演进为微服务。
- 针对高并发（10000+并发请求）优化，包含原子计数器和分布式锁。

## 技术栈
- **前端：** React + TypeScript + Vite
- **后端：** Spring Boot 3 + Java 17 + Spring Security + Spring Data JPA + Flyway
- **基础设施：** MySQL + Redis + RabbitMQ + MinIO
- **实时：** WebSocket + JWT保护
- **工程化：** GitHub Actions CI、集成测试、双语文档

## 运行前提
- Java 17
- Maven 3.9+
- Node.js 20+ / npm 10+
- MySQL 8、Redis 7、RabbitMQ 3.13、MinIO；或直接使用 Docker Desktop + Docker Compose

## 仓库结构
```text
apps/api    Spring Boot 模块化单体后端
apps/web    React + TypeScript 用户端Web应用
apps/admin  React + TypeScript 管理端应用
deploy      Docker Compose 本地基础设施栈
docs        架构、部署、API和面试文档
```

## 快速启动
### 1）准备环境
- 在仓库根目录安装前端依赖：`npm ci`
- 启动基础设施服务：`cd deploy && docker compose up -d mysql redis rabbitmq minio`

### 2）启动后端API
```bash
mvn -f apps/api/pom.xml spring-boot:run
```

### 3）启动前端应用
```bash
# 用户端Web应用
npm run dev:web

# 管理员审核仪表板
npm run dev:admin
```

### 4）访问服务
- **API：** `http://localhost:8080`
- **Swagger UI：** `http://localhost:8080/swagger-ui/index.html`
- **Web：** `http://localhost:5173`
- **Admin：** `http://localhost:5174`
- **RabbitMQ 管理台：** `http://localhost:15672`
- **MinIO 控制台：** `http://localhost:9001`

### 5）默认账号
- **管理员：** `admin / Admin@123456`
- **种子用户：**
  - `alice@devflow.local`
  - `bob@devflow.local`
  - `carol@devflow.local`
  - `david@devflow.local`
- **密码：** `password`

## 核心功能
- **认证：** 注册、登录、刷新令牌、登出
- **用户资料：** 显示名称、简介、语言偏好、头像上传
- **社区信息流：** 最新信息流、热门信息流、分类过滤、游标分页
- **搜索：** 关键词和分类帖子搜索，支持可分享查询URL
- **内容工作流：** 创建、编辑、删除、详情查看、封面上传
- **社交互动：** 点赞、收藏、评论、关注/取消关注
- **通知：** 未读数、列表、标记已读、WebSocket推送
- **举报和审核：** 举报帖子/用户、审核举报、隐藏帖子、禁用用户
- **管理员审计轨迹：** 追踪审核操作，包含操作员、目标和时间戳
- **双语体验：** web端、管理端和文档均支持中英文

## 架构说明
**后端架构：**
- **模块化单体：** 在保持交付效率的同时保留清晰的模块边界
- **显式Redis缓存：** 高频读取接口使用显式缓存键名和短TTL
- **异步事件管道：** 互动副作用通过RabbitMQ解耦，再由WebSocket推送
- **游标分页：** 性能优化的分页，避免深度偏移问题

**性能优化：**
- **游标分页：** 避免深度分页中的N+1查询问题
- **多层缓存：** Redis + 应用级缓存用于热点路径
- **数据库索引：** 为高频查询模式优化的索引
- **异步处理：** 非阻塞通知管道

**高并发设计：**
- **原子计数器：** 独立计数器表避免热点行更新
- **分布式锁：** 基于Redis的关键操作锁
- **事件聚合：** 批量处理减少消息队列压力
- **连接池优化：** 数据库、Redis、RabbitMQ连接池调优

## 测试
### 后端
```bash
cd apps/api
mvn test
```

### 前端
```bash
# Web应用
cd apps/web
npm run test
npm run build

# 管理端应用
cd apps/admin
npm run test
npm run build
```

## CI 持续集成
- **工作流文件：** `.github/workflows/ci.yml`
- **自动执行：**
  - 后端测试（Java 17）
  - 前端测试与构建（Node 20）
  - 关键流程集成测试

## 高并发模式
测试10000并发请求：
```bash
# 使用高并发配置启动
java -jar -Dspring.profiles.active=high-concurrency apps/api/target/devflow-api.jar
```

**10000并发关键优化：**
- HikariCP：100个数据库连接
- Redis：50个活跃连接
- RabbitMQ：20个消费者，50个预取
- Tomcat：500个最大线程
- 热点更新的原子计数器
- 通知的事件聚合

## 作品集定位
**这个项目刻意保持"面试可讲清楚"的工程形态：**
- **清晰的模块边界：** 便于讨论关注点分离
- **真实的产品工作流：** 从注册到内容审核的完整用户旅程
- **务实的基础设施选择：** 单体vs微服务、缓存策略、异步处理
- **足够的讨论深度：** 缓存、异步管道、安全、治理和交付质量

**关键面试话题：**
- **为什么选择模块化单体而非微服务？**
- **如何处理缓存失效？**
- **解释你的游标分页实现。**
- **通知管道是如何工作的？**
- **你的性能优化策略是什么？**
- **如何确保数据一致性？**
- **系统如何处理10000并发请求？**

## 文档入口
- **架构说明：** `docs/ARCHITECTURE.md`
- **接口概览：** `docs/API_OVERVIEW.md`
- **面试重点：** `docs/INTERVIEW_HIGHLIGHTS.md`

---

面试演示建议：先讲架构和分层，再演示高并发优化、游标分页和实时通知三条核心链路。
