# 中英双语实现指南 / Bilingual Implementation Guide

## 1. Frontend i18n / 前端国际化

**English**
Recommended library: `react-i18next`

**中文**
推荐库：`react-i18next`

### Language files / 语言文件
```text
apps/web/src/locales/en/common.json
apps/web/src/locales/zh/common.json
apps/admin/src/locales/en/common.json
apps/admin/src/locales/zh/common.json
```

### Example keys / 示例 key
- `nav.home`
- `nav.hot`
- `nav.following`
- `post.create`
- `post.publish`
- `notification.title`
- `admin.dashboard`

---

## 2. Backend Message Strategy / 后端消息策略

**English**
Do not hardcode Chinese/English business messages directly in backend responses.
Return:
- code
- messageCode
- data

Then frontend maps:
- `AUTH_LOGIN_SUCCESS`
- `POST_PUBLISHED`
- `COMMENT_CREATED`

**中文**
后端不要直接硬编码中文或英文业务提示。
建议返回：
- code
- messageCode
- data

再由前端映射：
- `AUTH_LOGIN_SUCCESS`
- `POST_PUBLISHED`
- `COMMENT_CREATED`

---

## 3. Database Localization Strategy / 数据库国际化策略

**English**
Use bilingual storage only for data that is truly user-facing and stable:
- category names
- system content templates

For user-generated posts/comments, keep original content only.

**中文**
只有真正面向用户展示且相对稳定的数据才建议双语存储：
- 分类名称
- 系统模板文案

用户生成的帖子/评论保持原文，不做双份存储。

---

## 4. UI Switching Rules / 界面切换规则

**English**
- guest users: use browser language + local storage
- logged-in users: user preference overrides browser language
- language switch should not require page refresh
- route paths do not need locale prefix in MVP

**中文**
- 未登录用户：浏览器语言 + 本地缓存
- 已登录用户：用户偏好优先于浏览器语言
- 切换语言不需要刷新页面
- MVP 阶段路由无需增加 locale 前缀

---

## 5. Documentation Rules / 文档规则

**English**
All key docs should be bilingual:
- README
- architecture
- deployment
- API usage
- interview highlights

**中文**
关键文档都应为双语：
- README
- 架构说明
- 部署文档
- API 使用说明
- 面试亮点说明
