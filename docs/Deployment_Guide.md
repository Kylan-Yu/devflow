# Deployment Guide

## Prerequisites | 环境要求
**EN**
- Java 17
- Node.js 18+
- Docker Desktop or Docker Engine with Compose

**中文**
- Java 17
- Node.js 18+
- Docker Desktop 或 Docker Engine + Compose

## Local Quick Start | 本地快速启动
### 1. Install frontend dependencies
```bash
npm ci
```

### 2. Start infrastructure
```bash
cd deploy
docker compose up -d mysql redis rabbitmq minio
```

### 3. Run backend API
```bash
mvn -f apps/api/pom.xml spring-boot:run
```

### 4. Run web app
```bash
npm run dev:web
```

### 5. Run admin app
```bash
npm run dev:admin
```

## Local URLs | 本地地址
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Web: `http://localhost:5173`
- Admin: `http://localhost:5174`
- RabbitMQ Console: `http://localhost:15672`
- MinIO API: `http://localhost:9000`
- MinIO Console: `http://localhost:9001`

## Demo Accounts | 演示账号
**EN**
- Admin bootstrap account: `admin / Admin@123456`
- Seed users:
  - `alice@devflow.local`
  - `bob@devflow.local`
  - `carol@devflow.local`
  - `david@devflow.local`
- Seed user password: `password`

**中文**
- 默认管理员账号：`admin / Admin@123456`
- 预置用户账号：
  - `alice@devflow.local`
  - `bob@devflow.local`
  - `carol@devflow.local`
  - `david@devflow.local`
- 预置用户密码：`password`

## Verification Checklist | 启动后验证清单
- Can register or login from the web app
- Can browse latest and hot feed
- Can open a post detail page
- Can edit profile settings and upload an avatar
- Can create a post with a cover image
- Can receive notification unread updates
- Can login to the admin dashboard and moderate a user or post

- 可以从用户端注册或登录
- 可以浏览最新流和热门流
- 可以打开帖子详情页
- 可以编辑资料并上传头像
- 可以发布带封面的帖子
- 可以看到通知未读数更新
- 可以登录管理后台并治理用户或帖子

## Production Notes | 生产化注意事项
**EN**
- Replace all default credentials and secrets before any public deployment.
- Move MySQL, Redis, RabbitMQ, and object storage to managed infrastructure.
- Add HTTPS termination, secure CORS origin allowlists, and production secrets management.

**中文**
- 在任何公开部署前替换默认账号、密码和密钥。
- 将 MySQL、Redis、RabbitMQ 和对象存储切换为托管基础设施。
- 增加 HTTPS 终止、严格的 CORS 白名单和生产环境密钥管理。
