# DevFlow / 开发者内容社区平台（中英双语项目总览）

## 1. Project Positioning / 项目定位

**English**  
DevFlow is a scalable To-C web application for developers to share technical posts, discover content, interact with others, and receive real-time notifications.  
This project is designed as a public portfolio project for remote job applications in Europe and Australia. It focuses on product thinking, engineering quality, scalability, bilingual support, and production-like architecture.

**中文**  
DevFlow 是一个面向开发者用户的 To C Web 应用，支持技术内容发布、内容发现、互动、实时通知等能力。  
这个项目主要用于远程求职时展示公开作品，重点体现产品思维、工程质量、可扩展性、中英双语能力，以及接近生产环境的架构设计。

---

## 2. Core Goals / 核心目标

**English**
- Demonstrate strong full-stack capability beyond traditional To-B systems
- Show product-oriented To-C experience: feed, interaction, moderation, notification
- Support **peak DAU around 300,000**
- Support **Chinese and English bilingual UI switching**
- Keep the first version achievable by Codex with clear phases

**中文**
- 展示不止于传统 To B 系统的全栈能力
- 体现面向用户产品的 To C 能力：信息流、互动、审核、通知
- 支持**30 万日活峰值级别**的设计目标
- 支持**中英双语界面切换**
- 首版必须适合通过 Codex 分阶段落地开发

---

## 3. Target Users / 目标用户

**English**
- Developers and engineering learners
- Job seekers sharing interview experiences
- Engineers posting project showcases and technical notes
- Small communities discussing backend, frontend, cloud, AI, career topics

**中文**
- 开发者与工程技术学习者
- 分享面试经验的求职者
- 发布项目展示与技术笔记的工程师
- 讨论后端、前端、云原生、AI、职业发展的垂直社区用户

---

## 4. MVP Scope / MVP 范围

**English**
The MVP includes:
- User registration and login
- Profile and follow system
- Post creation and feed browsing
- Like, comment, favorite
- Search
- Notification center
- Admin moderation console
- Bilingual support
- Docker-based local deployment

**中文**
MVP 包含：
- 用户注册登录
- 个人主页与关注系统
- 帖子发布与信息流浏览
- 点赞、评论、收藏
- 搜索
- 通知中心
- 管理后台审核
- 中英双语支持
- 基于 Docker 的本地部署

---

## 5. Scalability Principle / 可扩展原则

**English**
The first implementation should prefer a **modular monolith**:
- simpler to deliver
- easier for Codex to complete
- easier to understand by interviewers
- can evolve into microservices later

Scalability is achieved through:
- stateless API services
- Redis cache
- RabbitMQ async events
- object storage for media
- read-optimized feed strategy
- pagination and index design
- optional future split of notification/search/feed services

**中文**
首版建议采用**模块化单体**：
- 更容易交付
- 更适合 Codex 开发
- 更容易被面试官快速理解
- 后续可自然拆分为微服务

扩展性主要通过以下手段实现：
- 无状态 API 服务
- Redis 缓存
- RabbitMQ 异步事件
- 对象存储承载媒体资源
- 面向读取优化的信息流策略
- 分页与索引设计
- 后续可拆分通知、搜索、Feed 等服务

---

## 6. Suggested Stack / 建议技术栈

**Backend / 后端**
- Java 21
- Spring Boot 3
- Spring Security
- JWT + Refresh Token
- MySQL 8
- Redis
- RabbitMQ
- WebSocket
- MinIO
- OpenAPI / Swagger
- Flyway
- Testcontainers

**Frontend / 前端**
- React
- TypeScript
- Vite
- Tailwind CSS
- Zustand or Redux Toolkit
- React Query
- react-i18next

**Infra / 部署**
- Docker Compose for local demo
- Nginx reverse proxy
- GitHub Actions (optional)
- Prometheus + Grafana (phase 2)
- Loki / ELK optional

---

## 7. Resume Value / 对简历的价值

**English**
This project can help position you as:
- Senior Java full-stack engineer
- Product-aware backend engineer
- To-C scalable platform engineer
- Engineer with bilingual project delivery ability

**中文**
这个项目可以帮助你在简历和 Git 仓库中塑造成：
- 高级 Java 全栈工程师
- 具备产品思维的后端工程师
- 具备 To C 高并发平台意识的工程师
- 具备中英双语交付能力的工程师
