# Interview Highlights

## 30-Second Pitch | 30 秒项目介绍
**EN**  
DevFlow is a bilingual full-stack developer community platform built with React and Spring Boot.  
It demonstrates modular backend design, read-heavy performance optimization, async notification delivery, moderation tooling, and reproducible local deployment with Docker Compose.

**中文**  
DevFlow 是一个基于 React 和 Spring Boot 的中英双语开发者社区项目。  
它重点展示了模块化后端设计、读多写少场景下的性能优化、异步通知链路、治理后台，以及基于 Docker Compose 的可复现本地部署能力。

## What Recruiters Can Evaluate Quickly | 招聘方可以快速判断的点
**EN**
- End-to-end delivery from API to UI, admin tooling, CI, and documentation
- Clear module boundaries and professional naming conventions
- Practical architecture decisions for a 300k DAU target mindset
- Balanced engineering tradeoffs that are easy to explain in interviews

**中文**
- 从 API、前端、管理后台到 CI、文档的完整交付能力
- 清晰的模块边界和专业的命名风格
- 面向 300k DAU 目标思路的务实架构设计
- 便于在面试中讲清楚的工程取舍

## Talking Points Worth Emphasizing | 建议重点讲的技术点
**EN**
- JWT auth with refresh flow and protected WebSocket handshake
- Redis cache strategy for feed, post detail, unread count, and profile hot paths
- RabbitMQ async event flow for interaction-driven notifications
- MinIO-backed media uploads for avatars and post covers
- Search flow with cursor-friendly public query API
- Admin moderation workflow with report review and audit trail
- Unified API response model with stable message codes

**中文**
- 带 refresh flow 的 JWT 鉴权，以及受保护的 WebSocket 握手
- 面向 feed、帖子详情、未读数、用户资料等热点路径的 Redis 缓存设计
- 基于 RabbitMQ 的互动到通知异步链路
- 基于 MinIO 的头像和帖子封面上传能力
- 支持公开查询和分享 URL 的搜索链路
- 包含举报审核与审计日志的后台治理流程
- 统一响应模型和稳定的 message code 设计

## Suggested 8-Minute Demo Flow | 建议的 8 分钟演示路径
1. Open the root README and explain the monorepo structure.
2. Start Docker Compose and point out MySQL, Redis, RabbitMQ, and MinIO.
3. Login to the web app and show latest feed, search, and a post detail page.
4. Edit profile settings and upload an avatar.
5. Create a post with a cover image and show it on the feed.
6. Trigger a notification and show unread count updating in real time.
7. Submit a report from the web app, then review it from the admin app.
8. Open the admin audit trail and finish with Swagger UI plus architecture tradeoffs.

1. 打开根目录 README，先说明 monorepo 结构。  
2. 启动 Docker Compose，介绍 MySQL、Redis、RabbitMQ 和 MinIO。  
3. 登录用户端，展示最新流、搜索和帖子详情。  
4. 进入资料设置页，演示头像上传和资料编辑。  
5. 发布一篇带封面的帖子，并在信息流中查看展示效果。  
6. 触发一条通知，展示未读数实时更新。  
7. 在用户端提交举报，再到管理端完成审核。  
8. 打开管理员审计日志，最后用 Swagger UI 总结架构取舍。  

## Resume Bullet Suggestions | 简历亮点写法建议
**EN**
- Built a bilingual full-stack community platform with React, TypeScript, Spring Boot, and a modular monolith architecture.
- Designed Redis caching and cursor pagination for read-heavy feed and search endpoints.
- Implemented async notification delivery through RabbitMQ and JWT-protected WebSocket push.
- Added admin moderation, report review workflow, MinIO-backed media upload, CI workflow, and audit logging to make the project production-style and portfolio-ready.

**中文**
- 使用 React、TypeScript、Spring Boot 和模块化单体架构搭建中英双语全栈社区平台。  
- 为读多写少的信息流与搜索场景设计 Redis 缓存与游标分页方案。  
- 基于 RabbitMQ 实现异步通知链路，并通过 JWT 保护的 WebSocket 完成实时推送。  
- 补齐管理后台治理、举报审核、MinIO 媒体上传、CI 流程和审计日志，使项目更接近 production-style 作品集工程。  
