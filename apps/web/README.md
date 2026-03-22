# DevFlow Web

React + TypeScript + Vite user-facing application.
React + TypeScript + Vite 用户端应用。

## Implemented Pages | 已实现页面
- `/` lightweight bilingual home and navigation entry
- `/login` login page
- `/register` register page
- `/feed/latest` latest feed
- `/feed/hot` hot feed
- `/search` keyword and category search
- `/posts/new` create post with cover upload
- `/posts/:id` post detail with like, favorite, and comment interactions
- `/posts/:id/edit` edit post
- `/users/:id` profile page with follow / unfollow
- `/notifications` notification list and unread actions
- `/settings` profile settings for display name, bio, language, and avatar upload
- `/reports/me` personal report history page

## Feature Notes | 功能说明
- Session lifecycle includes login, logout, token refresh, and 401 retry handling
- User profile supports display name, bio, preferred language, and avatar upload
- Post workflow supports creation, editing, detail view, and cover image upload
- Search supports keyword and category filtering with shareable URL params
- Moderation workflow includes reporting posts or users and tracking report outcomes
- Real-time notification badge and list updates are powered by WebSocket
- UI copy is localized from the start with `en-US` and `zh-CN`

## i18n | 国际化
- Supported locales: `en-US`, `zh-CN`
- Category labels, button text, messages, and notification templates all support bilingual rendering

## Realtime | 实时能力
- Connects to `/ws/notifications?token=...` after login
- Updates unread badge and notification list in near real-time

## Related Files | 关键入口文件
- Router: `src/router/index.tsx`
- API client: `src/api/client.ts`
- i18n: `src/i18n`
- Shared styles: `src/styles/index.css`
