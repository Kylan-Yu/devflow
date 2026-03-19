# DevFlow Agent Instructions

## Project goal
Build a bilingual (Chinese/English) To-C developer community web app called DevFlow.
The system should be portfolio-ready, production-style, and suitable for remote job applications.

## Architecture
- Monorepo
- apps/api: Spring Boot 3 + Java 21
- apps/web: React + TypeScript + Vite
- apps/admin: React + TypeScript + Vite
- Prefer modular monolith for backend
- Use MySQL, Redis, RabbitMQ, MinIO in Docker Compose

## Key requirements
- Chinese/English bilingual support in docs and UI
- Peak-capable architecture targeting 300k DAU
- Clear modular boundaries
- Clean folder structure
- Do not rewrite unrelated files
- Add concise bilingual comments only where helpful
- Keep naming consistent and professional

## Coding rules
- Backend: layered architecture, DTO/VO separation, unified response model
- Frontend: componentized structure, route-based pages, i18n from the beginning
- Prefer maintainable code over overengineering
- Add README updates when major milestones are completed

## Delivery rules
- Before coding, summarize the plan
- Then implement only the requested scope
- After coding, list changed files and any manual follow-up steps