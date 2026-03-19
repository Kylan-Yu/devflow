# Codex 开发执行计划 / Codex Execution Plan

## Phase 0 / 阶段 0：仓库初始化

**Goal / 目标**
Create project skeleton and standards.

**Tasks / 任务**
- monorepo structure
- backend project init
- web project init
- admin project init
- shared i18n conventions
- docker-compose skeleton
- README template
- branch strategy
- lint/format config

**Deliverables / 交付物**
- runnable empty apps
- base routes
- env templates
- initial docs

---

## Phase 1 / 阶段 1：认证 + 用户体系

**Tasks**
- register/login/refresh/logout
- user profile APIs
- avatar upload
- language preference
- frontend auth pages
- bilingual navbar/footer
- admin login

**Acceptance**
- user can register/login/logout
- UI can switch Chinese/English
- admin can login

---

## Phase 2 / 阶段 2：帖子 + Feed 核心

**Tasks**
- create/edit/delete post
- post detail page
- category/tag management
- latest feed
- hot feed basic scoring
- profile page with posts
- web editor

**Acceptance**
- users can publish content
- users can browse latest/hot feed
- bilingual UI complete for these pages

---

## Phase 3 / 阶段 3：互动 + 通知

**Tasks**
- like/favorite/comment
- follow/unfollow
- notification event publishing via RabbitMQ
- notification persistence
- unread badge
- WebSocket push

**Acceptance**
- interactions produce notification records
- receiver sees unread badge update
- core real-time push works

---

## Phase 4 / 阶段 4：后台审核 + 举报

**Tasks**
- report workflow
- content moderation pages
- user status management
- admin dashboard
- audit logs

**Acceptance**
- admin can review reports and moderate content
- admin actions are logged

---

## Phase 5 / 阶段 5：性能优化 + 展示完善

**Tasks**
- Redis cache
- key SQL/index optimization
- seed demo data
- screenshot/GIF generation
- OpenAPI export
- architecture diagram
- bilingual docs complete

**Acceptance**
- demo load is stable
- docs are interview-ready
- GitHub presentation quality is high

---

## Codex Prompting Principle / Codex 提示原则

**English**
When asking Codex to build modules:
- give one bounded task at a time
- provide acceptance criteria
- specify file paths
- ask it not to rewrite unrelated files
- request bilingual comments for critical docs and config
- keep generated code aligned with existing naming style

**中文**
让 Codex 开发时要注意：
- 一次只下发一个边界清晰的任务
- 明确验收标准
- 指定文件路径
- 要求不要改动无关文件
- 关键文档与配置要求双语注释
- 命名风格保持一致

---

## Suggested Repo Structure / 建议仓库结构

```text
devflow/
  apps/
    api/
    web/
    admin/
  docs/
    00_Project_Overview.md
    01_Function_List_MVP.md
    02_Architecture_and_Scalability.md
    03_Data_Model_Outline.md
    04_API_and_Module_Boundaries.md
    05_Codex_Execution_Plan.md
  deploy/
    docker-compose.yml
    nginx/
  scripts/
  .github/
```
