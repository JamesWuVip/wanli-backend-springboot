# 多阶段构建 Dockerfile
# 构建阶段
FROM maven:3.9.5-openjdk-17-slim AS builder

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 和源代码
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:17-jre-slim

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# 创建应用用户
RUN groupadd -r wanli && useradd -r -g wanli wanli

# 设置工作目录
WORKDIR /app

# 创建必要的目录
RUN mkdir -p logs uploads && \
    chown -R wanli:wanli /app

# 从构建阶段复制 JAR 文件
COPY --from=builder /app/target/wanli-backend-*.jar app.jar

# 设置文件权限
RUN chown wanli:wanli app.jar

# 切换到应用用户
USER wanli

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/actuator/health || exit 1

# JVM 参数优化
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:G1HeapRegionSize=16m -XX:+UseStringDeduplication -XX:+OptimizeStringConcat -Djava.security.egd=file:/dev/./urandom"

# 启动应用
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]