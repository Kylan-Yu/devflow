# 架构设计与 30 万日活目标 / Architecture & 300k DAU Design

## 1. Architecture Choice / 架构选择

**English**
Use a **modular monolith** first:
- `auth`
- `user`
- `post`
- `interaction`
- `feed`
- `notification`
- `admin`
- `common`

This gives:
- faster delivery
- lower complexity
- better code readability
- easier future service split

**中文**
首版采用**模块化单体**：
- `auth`
- `user`
- `post`
- `interaction`
- `feed`
- `notification`
- `admin`
- `common`

优势：
- 交付更快
- 复杂度更低
- 代码可读性更好
- 后续更容易拆分微服务

---

## 2. Peak DAU Target / 峰值日活目标

**English**
Target: **300,000 DAU peak-capable design**

This does not mean full enterprise internet-company scale, but the architecture should show that:
- read traffic is much higher than write traffic
- cache is used aggressively
- heavy tasks are async
- feed and notification access paths are optimized
- bottlenecks are understood and documented

**中文**
目标：**具备承载 30 万日活峰值的架构设计能力**

这并不意味着首版就做到大厂完整规模，而是要体现：
- 读流量显著高于写流量
- 缓存使用充分
- 重任务异步化
- Feed 与通知访问链路做了专门优化
- 关键瓶颈有明确认知并写入文档

---

## 3. High-Level Architecture / 高层架构

**English**
Client → Nginx → React apps / Spring Boot API  
API → MySQL / Redis / RabbitMQ / MinIO  
Realtime → WebSocket Gateway inside API  
Admin → same backend with RBAC separation

**中文**
客户端 → Nginx → React 前端 / Spring Boot API  
API → MySQL / Redis / RabbitMQ / MinIO  
实时能力 → API 内嵌 WebSocket 网关  
管理后台 → 与主后端共享服务，通过 RBAC 区分权限

---

## 4. Scalability Strategy / 扩展策略

### 4.1 Stateless API / 无状态 API
**English**
Run multiple backend instances behind Nginx.

**中文**
后端 API 保持无状态，可在 Nginx 后方横向扩容多个实例。

### 4.2 Redis Cache / Redis 缓存
**English**
Use Redis for:
- hot feed cache
- post detail cache
- user profile cache
- unread notification count
- rate limit / anti-abuse counters

**中文**
Redis 用于：
- 热门 Feed 缓存
- 帖子详情缓存
- 用户资料缓存
- 通知未读数
- 限流与风控计数

### 4.3 RabbitMQ Async Events / RabbitMQ 异步事件
**English**
Async event examples:
- post liked
- post commented
- user followed
- notification created
- score refresh triggered

**中文**
异步事件场景包括：
- 帖子被点赞
- 帖子被评论
- 用户被关注
- 创建通知
- 触发热度重算

### 4.4 MySQL Read Efficiency / MySQL 读优化
**English**
- proper composite indexes
- cursor pagination
- denormalized counters
- avoid heavy join on hot path

**中文**
- 合理的复合索引
- 游标分页
- 计数字段反范式化
- 热路径避免复杂 Join

### 4.5 Media Handling / 媒体处理
**English**
Store images in MinIO. Store only metadata and URLs in database.

**中文**
图片放 MinIO，数据库只存元数据与访问 URL。

---

## 5. Suggested Non-Functional Targets / 建议非功能指标

**English**
- API p95 < 300ms for common read endpoints under demo-scale load
- login p95 < 500ms
- feed page cache hit ratio > 70% in hot periods
- notification unread count near real-time
- zero single point in app layer
- graceful degradation for non-core real-time features

**中文**
- 常见读接口在演示压测下 p95 < 300ms
- 登录接口 p95 < 500ms
- 热点时期 Feed 页缓存命中率 > 70%
- 通知未读数接近实时
- 应用层避免单点
- 非核心实时能力支持降级

---

## 6. Future Service Split Plan / 后续拆分方向

**English**
When scaling further, split in this order:
1. notification service
2. feed/ranking service
3. search service
4. media service

**中文**
如果后续继续放大规模，优先按以下顺序拆分：
1. 通知服务
2. Feed / 排序服务
3. 搜索服务
4. 媒体服务
