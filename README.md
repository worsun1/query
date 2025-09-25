# Query Service

该项目提供一个使用 Spring Boot + MyBatis 实现的查询服务，支持 HTTP 和 gRPC 两种访问方式，从 MySQL 数据库中查询客户信息。

## 功能概览

- **REST API**：
  - `GET /api/v1/customers/{id}` 按 ID 查询客户。
  - `GET /api/v1/customers?name=...&email=...` 根据姓名/邮箱进行模糊查询（姓名支持模糊匹配，邮箱精确匹配）。
- **gRPC API**：
  - `GetCustomer`：按 ID 查询客户。
  - `SearchCustomers`：按姓名或邮箱筛选客户。

## 数据库设计

数据库名可自定义（示例为 `query_service`），包含一张 `customers` 表：

| 字段       | 类型          | 说明           |
|------------|---------------|----------------|
| `id`       | BIGINT        | 主键，自增     |
| `name`     | VARCHAR(100)  | 客户姓名       |
| `email`    | VARCHAR(255)  | 邮箱，唯一约束 |
| `created_at` | DATETIME    | 创建时间，默认当前时间 |

项目提供的 `schema.sql` 可在启动时自动初始化表结构及一些示例数据。

## 配置方式

在 `src/main/resources/application.yaml` 中配置 MySQL 连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/query_service
    username: your_username
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

根据需要调整连接信息，并确保数据库中存在 `query_service`（或其他自定义名称）。

## 启动项目

```bash
mvn spring-boot:run
```

- REST 服务默认监听在 `http://localhost:8080`
- gRPC 服务默认监听在 `localhost:9090`

## 构建 Docker 镜像

项目提供 `scripts/build-image.sh` 用于一键打包镜像，该脚本会先执行 Maven 打包，再根据 `Dockerfile` 构建镜像：

```bash
scripts/build-image.sh
```

- 默认镜像名为 `query-service:latest`，可通过环境变量 `IMAGE_NAME`、`IMAGE_TAG` 自定义。
- 如果本地没有安装 Maven，可在执行脚本前设置 `MVN_CMD="./mvnw"` 等自定义命令。
- 构建完成后可使用 `docker run -p 8080:8080 -p 9090:9090 query-service:latest` 启动容器。

## gRPC 接口说明

Proto 文件位于 `src/main/proto/customer.proto`，可使用任意支持 gRPC 的客户端调用。例如使用 `grpcurl`：

```bash
grpcurl -plaintext -d '{"id":1}' localhost:9090 query.CustomerService/GetCustomer
```

## 运行测试

```bash
mvn test
```

## 依赖版本

- Spring Boot 3.2.5
- MyBatis Spring Boot Starter 3.0.3
- gRPC Server Spring Boot Starter 3.0.0
