# 万里教育平台后端服务 (Spring Boot)

这是万里教育平台的Spring Boot版本后端服务，从Node.js项目迁移而来。

## 项目概述

万里教育平台是一个综合性的在线教育管理系统，提供用户管理、课程管理、班级管理、选课管理、考勤管理等功能。

### 主要功能

- **用户管理**：用户注册、登录、权限管理
- **课程管理**：课程创建、编辑、发布
- **班级管理**：班级创建、学生管理
- **选课管理**：学生选课、退课
- **考勤管理**：签到、签退、考勤统计
- **认证授权**：JWT令牌、角色权限控制

## 技术栈

### 后端技术
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security 6**
- **Spring Data JPA**
- **Spring Cache**
- **Spring Validation**

### 数据库
- **PostgreSQL 15**
- **Redis 7**（缓存）

### 工具库
- **JWT** - JSON Web Token认证
- **MapStruct** - 对象映射
- **Swagger/OpenAPI 3** - API文档
- **Testcontainers** - 集成测试
- **Lombok** - 代码简化

### 构建工具
- **Maven 3.9+**

## 项目结构

```
wanli-backend-springboot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── wanli/
│   │   │           ├── WanliBackendApplication.java
│   │   │           ├── config/          # 配置类
│   │   │           ├── controller/      # 控制器
│   │   │           ├── service/         # 服务层
│   │   │           ├── repository/      # 数据访问层
│   │   │           ├── entity/          # 实体类
│   │   │           ├── dto/             # 数据传输对象
│   │   │           ├── mapper/          # 对象映射
│   │   │           ├── security/        # 安全配置
│   │   │           ├── exception/       # 异常处理
│   │   │           └── util/            # 工具类
│   │   └── resources/
│   │       ├── application.yml          # 主配置文件
│   │       ├── application-dev.yml      # 开发环境配置
│   │       ├── application-staging.yml  # 测试环境配置
│   │       ├── application-prod.yml     # 生产环境配置
│   │       └── db/migration/            # 数据库迁移脚本
│   └── test/                            # 测试代码
├── docker/                              # Docker配置
├── docs/                                # 项目文档
├── pom.xml                              # Maven配置
├── Dockerfile                           # Docker镜像构建
├── docker-compose.yml                   # 本地开发环境
└── README.md                            # 项目说明
```

## 快速开始

### 环境要求

- Java 17+
- Maven 3.9+
- PostgreSQL 15+
- Redis 7+
- Docker & Docker Compose（可选）

### 本地开发

#### 1. 克隆项目

```bash
git clone https://github.com/JamesWuVip/wanli-backend-springboot.git
cd wanli-backend-springboot
```

#### 2. 使用Docker Compose（推荐）

```bash
# 启动所有服务（包括数据库）
docker-compose up -d

# 查看日志
docker-compose logs -f wanli-backend

# 停止服务
docker-compose down
```

#### 3. 手动启动

**启动数据库服务**

```bash
# 启动PostgreSQL和Redis
docker-compose up -d postgres redis
```

**配置环境变量**

```bash
export SPRING_PROFILES_ACTIVE=dev
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/wanli_dev
export SPRING_DATASOURCE_USERNAME=wanli
export SPRING_DATASOURCE_PASSWORD=wanli123
export SPRING_REDIS_HOST=localhost
export SPRING_REDIS_PORT=6379
export SPRING_REDIS_PASSWORD=redis123
```

**启动应用**

```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/wanli-backend-*.jar
```

### 访问应用

- **应用地址**：http://localhost:8080
- **API文档**：http://localhost:8080/swagger-ui.html
- **健康检查**：http://localhost:8080/api/actuator/health

## 环境配置

### 开发环境 (dev)

```bash
export SPRING_PROFILES_ACTIVE=dev
```

### 测试环境 (staging)

```bash
export SPRING_PROFILES_ACTIVE=staging
```

### 生产环境 (prod)

```bash
export SPRING_PROFILES_ACTIVE=prod
```

## API 文档

项目集成了Swagger/OpenAPI 3，启动应用后可以通过以下地址访问API文档：

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 测试

```bash
# 运行所有测试
mvn test

# 运行单元测试
mvn test -Dtest="*UnitTest"

# 运行集成测试
mvn test -Dtest="*IntegrationTest"

# 生成测试报告
mvn test jacoco:report
```

## 部署

### Docker部署

```bash
# 构建镜像
docker build -t wanli-backend:latest .

# 运行容器
docker run -d \
  --name wanli-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  wanli-backend:latest
```

### 生产环境部署

1. 构建生产包
```bash
mvn clean package -Pprod
```

2. 部署到服务器
```bash
java -jar -Dspring.profiles.active=prod target/wanli-backend-*.jar
```

## 监控

项目集成了Spring Boot Actuator，提供以下监控端点：

- `/api/actuator/health` - 健康检查
- `/api/actuator/info` - 应用信息
- `/api/actuator/metrics` - 应用指标
- `/api/actuator/prometheus` - Prometheus指标

## 开发规范

请参考项目中的开发规范文档，包括：

- 代码规范
- 数据库设计规范
- API设计规范
- Git提交规范

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

- 项目维护者：JamesWu
- 邮箱：your.email@example.com
- 项目地址：https://github.com/JamesWuVip/wanli-backend-springboot

## 更新日志

查看 [CHANGELOG.md](CHANGELOG.md) 了解版本更新详情。