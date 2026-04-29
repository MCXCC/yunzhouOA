# 云州OA - 前置依赖清单

## 一、开发环境

| 工具    | 版本     | 下载地址                                            |
| ------- | -------- | --------------------------------------------------- |
| JDK     | 21 (LTS) | https://www.oracle.com/java/technologies/downloads/ |
| Node.js | 20 (LTS) | https://nodejs.org/ (使用 nvm 管理)                 |
| Maven   | 3.9+     | https://maven.apache.org/download.cgi               |
| Git     | 2.x      | https://git-scm.com/downloads                       |

### nvm 安装 Node

```bash
nvm install 20
nvm use 20
```

---

## 二、数据库服务

### PostgreSQL 16（阿里云 RDS）

```
本地数据库软件: "D:\sql\bin\psql.exe"
地址: pgm-bp13ydmhvh1a3bz78o.pg.rds.aliyuncs.com:5432
数据库: yunzhou_dev
用户名: Chenlz
密码: Clz11053
```

---

## 三、中间件服务

### 1. Redis 7

**Windows 安装方式：**

- 下载：https://github.com/tporadowski/redis/releases
- 或使用 WSL2 安装 Linux 版本

```bash
# 默认配置
地址: localhost:6379
密码: (空)
数据库: 0
```

### 2. MinIO（对象存储）

**下载安装：**

- 下载：https://min.io/download
- Windows: 下载 minio.exe

```bash
# 启动命令
minio server D:\minio-data --console-address ":9001"

# 默认配置
地址: http://localhost:9000
用户名: minioadmin
密码: minioadmin
控制台: http://localhost:9001
```

### 3. Meilisearch（搜索引擎）

**下载安装：**

- 下载：https://www.meilisearch.com/docs/learn/getting_started/installation

```bash
# Windows 启动
meilisearch.exe --master-key masterKey

# 默认配置
地址: http://localhost:7700
API Key: masterKey
控制台: http://localhost:7700
```

### 4. Flowable（工作流引擎）

Flowable 使用 Spring Boot 内置集成，无需单独下载和部署。

**依赖配置（已添加到 pom.xml）：**

```xml
<dependency>
    <groupId>org.flowable</groupId>
    <artifactId>flowable-spring-boot-starter</artifactId>
    <version>7.0.1</version>
</dependency>
```

**默认配置：**

- 嵌入式部署，随应用启动
- 数据库表自动创建
- 默认使用项目的数据源（PostgreSQL）

### 5. 定时任务（Spring Boot 内置）

定时任务使用 Spring Boot 内置的 `@Scheduled` 注解实现，无需额外部署服务。

---

## 四、启动顺序

```
1. PostgreSQL  ← 已有（阿里云 RDS）
2. Redis
3. MinIO
4. Meilisearch
5. 后端服务 (mvn spring-boot:run)
6. 前端服务 (npm run dev)
```

---

## 五、端口占用清单

| 服务          | 端口 | 用途             |
| ------------- | ---- | ---------------- |
| PostgreSQL    | 5432 | 数据库（阿里云） |
| Redis         | 6379 | 缓存             |
| MinIO         | 9000 | 对象存储 API     |
| MinIO Console | 9001 | MinIO 控制台     |
| Meilisearch   | 7700 | 搜索引擎         |
| 后端服务      | 8080 | Spring Boot      |
| 前端服务      | 3000 | Vite Dev Server  |

---

## 六、快速启动脚本

### Windows (start-services.bat)

```bat
@echo off
echo 启动云州OA所需服务...

echo [1/3] 启动 Redis...
start "Redis" "C:\Program Files\Redis\redis-server.exe"

echo [2/3] 启动 MinIO...
start "MinIO" minio.exe server D:\minio-data --console-address ":9001"

echo [3/3] 启动 Meilisearch...
start "Meilisearch" meilisearch.exe --master-key masterKey

echo 所有服务已启动！
pause
```

---

## 七、配置修改说明

如需修改服务地址，编辑以下文件：

| 配置项      | 文件路径                                    |
| ----------- | ------------------------------------------- |
| 数据库      | console/src/main/resources/application.yaml |
| Redis       | console/src/main/resources/application.yaml |
| MinIO       | console/src/main/resources/application.yaml |
| Meilisearch | console/src/main/resources/application.yaml |
| Flowable    | console/src/main/resources/application.yaml |
| 前端代理    | web/vite.config.ts                          |
