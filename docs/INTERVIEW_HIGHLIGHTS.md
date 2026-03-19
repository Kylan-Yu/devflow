# Interview Highlights | 面试亮点总结

## 1. 30-Second Pitch | 30 秒项目介绍
**EN**  
DevFlow is a full-stack bilingual To-C community platform built with React and Spring Boot.  
It demonstrates practical backend modularization, read-heavy performance optimization, async event-driven notifications, and Docker-based reproducible local deployment.

**中文**  
DevFlow 是一个基于 React 与 Spring Boot 的中英双语 To-C 社区项目。  
它重点展示了后端模块化设计、高频读链路性能优化、异步事件通知链路，以及基于 Docker 的可复现本地部署能力。

## 2. What Recruiters Can Evaluate Quickly | 招聘方可快速评估点
**EN**
- Full-stack delivery from API to UI and docs
- Clear module boundaries and naming consistency
- Practical scalability decisions for a 300k DAU target mindset
- Portfolio-friendly tradeoffs: readable, explainable, maintainable

**中文**
- 从 API 到前端再到文档的完整交付能力
- 清晰模块边界与一致命名规范
- 面向 30 万 DAU 目标的可解释扩展思路
- 作品集友好取舍：可读、可讲、可维护

## 3. Key Technical Talking Points | 建议重点讲解技术点
**EN**
- Redis cache strategy on feed/post detail/unread/profile hot paths
- RabbitMQ async event flow for notifications
- WebSocket near-real-time unread update
- Cursor pagination for feed scalability
- Unified API response (`code/message/data/traceId`)

**中文**
- Feed/帖子详情/未读数/用户资料的 Redis 缓存策略
- RabbitMQ 异步通知事件链路
- WebSocket 近实时未读数更新
- 信息流游标分页扩展能力
- 统一接口响应模型（`code/message/data/traceId`）

## 4. Suggested 8-Minute Demo Flow | 建议 8 分钟演示路径
1. Show project architecture and module boundaries.
2. Run docker-compose and start apps quickly.
3. Login with demo account, browse latest/hot feed.
4. Open post detail and show interaction stats.
5. Trigger notification and show unread badge update.
6. Open Swagger and explain API grouping.
7. Summarize scalability design and future roadmap.

1. 展示整体架构与模块边界。  
2. 演示 docker-compose 启动与项目运行。  
3. 使用演示账号登录并浏览最新/热门信息流。  
4. 打开帖子详情并说明互动指标。  
5. 触发通知并展示未读角标变化。  
6. 打开 Swagger 说明 API 分组。  
7. 总结可扩展设计与后续路线。  

## 5. Resume Bullet Suggestions | 简历亮点写法建议
**EN**
- Built a bilingual full-stack community platform (React + Spring Boot) with modular monolith architecture.
- Designed Redis caching for high-frequency read endpoints and improved feed/detail access efficiency.
- Implemented async notification pipeline via RabbitMQ and real-time push via WebSocket.
- Delivered reproducible local deployment with Docker Compose and interview-ready documentation.

**中文**
- 搭建中英双语全栈社区平台（React + Spring Boot），采用模块化单体架构。  
- 为高频读接口设计 Redis 缓存策略，提升信息流与详情访问效率。  
- 实现基于 RabbitMQ 的异步通知链路与 WebSocket 实时推送。  
- 提供 Docker Compose 可复现联调环境与面试导向文档体系。  
