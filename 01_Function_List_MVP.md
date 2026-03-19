# DevFlow 功能清单 / Function List

## A. User & Auth / 用户与认证

### MVP
**English**
- Email registration
- Password login
- JWT access token + refresh token
- Forgot password by email code (can be mocked first)
- Logout
- Basic profile setup
- Avatar upload
- Language preference save (`zh-CN` / `en`)

**中文**
- 邮箱注册
- 密码登录
- JWT Access Token + Refresh Token
- 邮箱验证码找回密码（首版可先模拟）
- 退出登录
- 基础个人资料设置
- 头像上传
- 保存语言偏好（`zh-CN` / `en`）

### Future
- OAuth login (GitHub / Google)
- Device session management
- Login risk control

---

## B. Profile & Social Graph / 个人主页与社交关系

### MVP
**English**
- User profile page
- Follow / unfollow
- Followers / following list
- User post list
- Bio, skills, location, links

**中文**
- 用户主页
- 关注 / 取关
- 粉丝 / 关注列表
- 用户发帖列表
- 个人简介、技能、地区、外链

### Future
- Verified badges
- Personal portfolio cards
- Suggested users

---

## C. Post System / 帖子系统

### MVP
**English**
- Create text/image post
- Edit post
- Delete post (soft delete)
- Draft support (optional in MVP, recommended in phase 1.5)
- Tags
- Topic/category
- Post detail page
- Author page routing
- Markdown-lite or rich text editor

**中文**
- 发布图文帖子
- 编辑帖子
- 删除帖子（软删除）
- 草稿支持（MVP 可选，建议 1.5 阶段）
- 标签
- 主题分类
- 帖子详情页
- 作者主页跳转
- 轻量 Markdown 或富文本编辑器

### Future
- Code block syntax highlight
- Scheduled publishing
- Mention users
- Multi-image gallery
- Video preview

---

## D. Feed / 信息流

### MVP
**English**
- Latest feed
- Hot feed
- Following feed
- Infinite scroll / cursor pagination
- Feed item cache
- Trending tags

**中文**
- 最新流
- 热门流
- 关注流
- 无限滚动 / 游标分页
- Feed 项缓存
- 热门标签

### Ranking Inputs / 热度排序因素
**English**
- publish time
- like count
- comment count
- favorite count
- engagement decay over time

**中文**
- 发布时间
- 点赞数
- 评论数
- 收藏数
- 时间衰减后的互动得分

---

## E. Interaction / 互动能力

### MVP
**English**
- Like / unlike post
- Comment / delete own comment
- Favorite / unfavorite
- Share link copy
- Report content

**中文**
- 点赞 / 取消点赞
- 评论 / 删除自己的评论
- 收藏 / 取消收藏
- 复制分享链接
- 举报内容

### Future
- Nested comment
- Comment like
- Repost
- Quote post

---

## F. Search / 搜索

### MVP
**English**
- Search posts by title/content/tag
- Search users
- Search by category
- Search result highlight (simple)

**中文**
- 按标题 / 内容 / 标签搜索帖子
- 搜索用户
- 按分类搜索
- 搜索结果高亮（简版）

### Future
- Elasticsearch-based full-text search
- Typo tolerance
- Personalized search ranking

---

## G. Notification / 通知中心

### MVP
**English**
- Notifications for likes
- Notifications for comments
- Notifications for follows
- Notification list
- Mark as read
- Unread count badge
- Real-time push via WebSocket

**中文**
- 点赞通知
- 评论通知
- 关注通知
- 通知列表
- 标记已读
- 未读数角标
- 基于 WebSocket 的实时推送

### Future
- Email notification
- Notification preferences
- Batch digest

---

## H. Admin Console / 管理后台

### MVP
**English**
- Admin login
- Dashboard with key metrics
- User list / status control
- Post moderation
- Comment moderation
- Report review
- Category/tag management
- Operation audit log

**中文**
- 管理员登录
- 核心指标看板
- 用户列表 / 状态管理
- 帖子审核
- 评论审核
- 举报处理
- 分类 / 标签管理
- 操作审计日志

### Future
- Risk rules
- Sensitive word management
- Batch moderation
- Content quality scoring

---

## I. Internationalization / 国际化

### MVP
**English**
- All frontend text switchable between Chinese and English
- Language stored in user preference and local cache
- Backend response message codes mapped to frontend i18n
- Admin console also bilingual
- Seed data and demo content available in both languages

**中文**
- 前端所有文案支持中英文切换
- 语言偏好保存到用户设置和本地缓存
- 后端返回 message code，由前端映射 i18n 文案
- 管理后台也支持中英切换
- 初始化演示数据提供中英文样例

---

## J. Observability & Ops / 可观测与运维

### MVP
**English**
- Health check endpoint
- Request trace ID
- Structured logs
- API error code standard
- Docker Compose startup
- Basic metrics for API / DB / cache

**中文**
- 健康检查接口
- 请求 Trace ID
- 结构化日志
- API 错误码规范
- Docker Compose 一键启动
- API / 数据库 / 缓存基础指标

### Future
- Prometheus + Grafana
- Slow query analysis
- Alert rules
