# DevFlow Web

React + TypeScript + Vite user-facing app.  
React + TypeScript + Vite 的用户端应用。

## Implemented Pages (Phase 1-3) | 已实现页面（阶段 1-3）
- `/login` login page
- `/register` register page
- `/feed/latest` latest feed
- `/feed/hot` hot feed
- `/posts/new` create post
- `/posts/:id` post detail + like/favorite/comment interactions
- `/posts/:id/edit` edit post
- `/users/:id` profile + follow/unfollow
- `/notifications` notification list + unread operations
- `/login` 登录页
- `/register` 注册页
- `/feed/latest` 最新流
- `/feed/hot` 热门流
- `/posts/new` 发布帖子
- `/posts/:id` 帖子详情与互动（点赞/收藏/评论）
- `/posts/:id/edit` 编辑帖子
- `/users/:id` 个人主页与关注/取关
- `/notifications` 通知列表与未读处理

## i18n
- Languages: `en-US` / `zh-CN`
- Notification text includes bilingual templates for like/comment/follow events.
- 语言支持：`en-US` / `zh-CN`
- 通知文案已包含点赞/评论/关注等双语模板。

## Realtime
- Connects WebSocket at `/ws/notifications` to update unread badge/list in near real-time.
- 通过 `/ws/notifications` 接收通知推送，近实时更新未读角标与通知列表。
