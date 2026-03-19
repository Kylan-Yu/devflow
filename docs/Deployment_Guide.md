# Deployment Guide | 部署指南

## 1. Prerequisites | 环境要求
**EN**
- Java 21
- Node.js 18+
- Docker Desktop (or Docker Engine + Compose)

**中文**
- Java 21
- Node.js 18+
- Docker Desktop（或 Docker Engine + Compose）

## 2. Local Quick Start | 本地快速启动
### 2.1 Start infrastructure
```bash
cd deploy
docker compose up -d mysql redis rabbitmq minio
```

### 2.2 Run backend API
```bash
cd apps/api
mvn spring-boot:run
```

### 2.3 Run web app
```bash
cd apps/web
npm install
npm run dev
```

### 2.4 Run admin app
```bash
cd apps/admin
npm install
npm run dev
```

## 3. One-Command Compose Flow | 一键 Compose 联调
```bash
cd deploy
docker compose up -d --build
```

Available services:
- API: `http://localhost:8080`
- Web (local dev): `http://localhost:5173`
- Admin (local dev): `http://localhost:5174`
- RabbitMQ Console: `http://localhost:15672`
- MinIO Console: `http://localhost:9001`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

## 4. Demo Accounts and Demo Data | 演示账号与演示数据
**EN**
- Seed data is initialized by Flyway migration.
- Demo accounts:
  - `alice@devflow.local`
  - `bob@devflow.local`
  - `carol@devflow.local`
  - `david@devflow.local`
- Password: `password`

**中文**
- 演示数据由 Flyway migration 初始化。
- 演示账号：
  - `alice@devflow.local`
  - `bob@devflow.local`
  - `carol@devflow.local`
  - `david@devflow.local`
- 密码：`password`

## 5. Verification Checklist | 验证清单
- Can access latest/hot feed endpoints
- Can open post detail page
- Can see unread count on notifications
- Can switch language in web/admin
- Swagger page is accessible

- 可以访问最新/热门流  
- 可以打开帖子详情  
- 可以看到通知未读数  
- 可以在 web/admin 切换语言  
- 可以访问 Swagger 页面  

## 6. Production Notes | 生产环境注意事项
**EN**
- Replace all default credentials and secrets.
- Configure external Redis/MySQL/RabbitMQ with managed services.
- Add HTTPS termination and proper CORS origin policy.

**中文**
- 替换默认账号、密码与密钥。  
- 使用外部托管版 Redis/MySQL/RabbitMQ。  
- 增加 HTTPS 终止与严格 CORS 策略。  
