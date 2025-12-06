# 🧠 NexusBrain - Enterprise Team RAG Knowledge Base

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-green)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-0.8.x-blue)](https://spring.io/projects/spring-ai)
[![Architecture](https://img.shields.io/badge/Architecture-Modular%20Monolith-purple)](https://github.com/YourUsername/NexusBrain)

> **NexusBrain** 是一个基于 **模块化单体 (Modular Monolith)** 架构的企业级知识库系统。它摒弃了传统的 SaaS 租户模式，采用更灵活的 **“团队/群组 (Team-based)”** 协作模型，结合 **RAG (检索增强生成)** 技术，允许用户在私有群或协作群中上传文档，并与 AI 进行基于文档的智能对话。

---

## 🏗️ 系统架构 (Architecture)

本项目采用 Maven 多模块架构，物理上打包为一个 Jar 包（便于部署），逻辑上通过模块强制解耦（便于维护和未来拆分微服务）。

### 模块划分

| 模块名称 (ArtifactId) | 模块类型 | 核心职责 (Role) | 依赖关系 |
| :--- | :--- | :--- | :--- |
| **`nexus-backend`** | Root (Pom) | **管家**。管理 Spring Boot, Cloud, AI 等所有依赖版本，不含代码。 | 父工程 |
| **`nexus-common`** | 通用 (Jar) | **语言规范**。定义全局响应 `Result<T>`、异常体系、`BaseEntity`、工具类。 | 被所有模块依赖 |
| **`nexus-infrastructure`** | 基建 (Jar) | **技术底座**。集成 Redis, MinIO, RabbitMQ, DeepSeek Client, MyBatis-Plus 配置。 | 被业务模块依赖 |
| **`nexus-module-system`** | 业务 (Jar) | **系统核心**。负责用户认证、RBAC 权限、**团队/群组管理** (`sys_team`, `sys_team_member`)。 | 依赖 Common, Infra |
| **`nexus-module-knowledge`**| 业务 (Jar) | **写入链路**。负责文件上传、MinIO 存储、发送“解析文档”消息到 MQ。 | 依赖 Common, Infra |
| **`nexus-module-rag`** | 业务 (Jar) | **读取/计算链路**。监听 MQ 消息、文档切片、向量化 (Embedding)、向量检索、AI 对话。 | 依赖 Common, Infra |
| **`nexus-start`** | 启动 (Jar) | **入口**。包含启动类和配置文件，聚合上述所有模块，对外暴露 API。 | 依赖所有模块 |

---

## 🛠️ 技术栈 (Tech Stack)

### 核心框架
- **Java 17 (LTS)**: 企业级开发标准。
- **Spring Boot 3.2.x**: 支持最新的 AIGC 开发特性。
- **Spring AI 0.8.x**: Spring 官方 AI 框架，对接 DeepSeek/OpenAI，统一 Chat 和 Embedding 接口。

### 数据存储
- **MySQL 8.0**: 事务库。存储用户、**团队/群组**、权限、文件元数据。
- **PostgreSQL + pgvector**: 向量库。专用于存储文本向量 (Embedding)，构建 RAG 的核心索引。
- **MyBatis-Plus 3.5.5**: ORM 框架，简化 CRUD 操作。

### 中间件
- **RabbitMQ**: 异步解耦。处理耗时的文档解析任务（上传 -> MQ -> 异步解析），防止接口超时。
- **MinIO**: 对象存储。私有化部署的 S3 替代方案，存储原始 PDF/Word 文件。
- **Redis 7**: 缓存。用于 Token 存储、防重放、以及 AI 对话的上下文记忆 (Session History)。

### 工具链
- **Lombok**: 简化样板代码。
- **Hutool**: Java 工具包之王。
- **Knife4j**: 生成友好的接口文档。

---

## 🔄 核心业务流转 (Core Workflows)

### 1. 写入链路：文档上传与解析 (Write Path)
用户上传文档到指定团队群组，系统异步处理解析与向量化。

```mermaid
sequenceDiagram
    participant User as 用户
    participant API as Nexus-Start (Knowledge)
    participant MinIO as MinIO存储
    participant DB as MySQL
    participant MQ as RabbitMQ
    
    User->>API: 上传文档 (PDF/Word)
    API->>MinIO: 1. 存储原始文件
    API->>DB: 2. 记录文件元数据 (状态: 待解析)
    API->>MQ: 3. 发送解析消息 (DocId)
    API-->>User: 4. 返回 "上传成功" (异步处理)
