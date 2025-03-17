# 麻将游戏后端服务

这是一个基于Spring Boot的麻将游戏后端服务。

## 技术栈

- Spring Boot 2.7.14
- MyBatis 2.3.1
- MySQL 8.0+
- JWT认证

## 环境要求

- JDK 8+
- MySQL 8.0+
- Maven 3.6+

## 数据库配置

1. 创建数据库：

   ```sql
   CREATE DATABASE IF NOT EXISTS mahjong DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. 初始化表结构：

## 配置说明

项目配置文件位于 `src/main/resources/application.yml`，主要配置项包括：

- 服务器端口：8080
- 数据库连接信息：
  - URL：jdbc:mysql://localhost:3306/mahjong
  - 用户名：root
  - 密码：200451
- JWT配置：
  - 密钥：mahjong-game-secret-key-for-jwt-token-generation-and-validation
  - 过期时间：24小时

## 启动项目

1. 确保MySQL服务已启动，并已创建好数据库和表结构
2. 使用Maven构建项目：

   ```bash
   mvn clean package
   ```
3. 运行项目：

   ```bash
   java -jar target/hello-0.0.1-SNAPSHOT.jar
   ```

   或者使用Maven直接运行：
   ```bash
   mvn spring-boot:run
   ```

## API接口

### 用户相关接口

- 游客登录：`POST /api/users/guest`
- 检查昵称是否可用：`GET /api/users/check-nickname/{nickname}`
- 注册新用户：`POST /api/users`
  - 请求体：`{"nickname": "用户昵称"}`
- 用户登录：`POST /api/users/login`
  - 请求体：`{"id": 用户ID}`
