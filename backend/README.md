# 酒店管理系统后端

基于 Spring Boot 3.5.0 + Java 17 + MyBatis Plus 开发的酒店管理系统后端。

## 技术栈

- **框架**：Spring Boot 3.5.0
- **Java版本**：JDK 17
- **数据库**：MySQL 8.0+
- **ORM框架**：MyBatis Plus 3.5.5
- **安全框架**：JWT (jjwt 0.11.5)
- **API文档**：SpringDoc OpenAPI 2.5.0
- **工具库**：Hutool 5.8.13, Lombok 1.18.28

## 项目结构

```
backend/
├── src/
│   └── main/
│       ├── java/com/hotel/
│       │   ├── HotelManagementApplication.java    # 启动类
│       │   ├── common/                         # 公共类
│       │   │   ├── BusinessException.java        # 业务异常类
│       │   │   ├── ResultCodeConstant.java      # 响应码常量
│       │   │   └── RestResult.java            # 统一响应类
│       │   ├── config/                         # 配置类
│       │   │   ├── MybatisPlusConfig.java      # MyBatis Plus配置
│       │   │   └── OpenApiConfig.java         # OpenAPI配置
│       │   ├── controller/                     # 控制器层
│       │   │   ├── AuthController.java         # 用户认证
│       │   │   ├── OrderController.java        # 订单管理
│       │   │   ├── RoomController.java         # 房间管理
│       │   │   ├── RoomStatusController.java   # 房态可视化
│       │   │   ├── RoomTypeController.java     # 房型管理
│       │   │   └── StatisticsController.java   # 数据统计
│       │   ├── dto/                            # 数据传输对象
│       │   │   ├── LoginDTO.java
│       │   │   ├── OrderDTO.java
│       │   │   ├── RoomDTO.java
│       │   │   ├── RoomStatusDTO.java
│       │   │   └── RoomTypeDTO.java
│       │   ├── entity/                         # 实体类
│       │   │   ├── OrdersDO.java
│       │   │   ├── RoomDO.java
│       │   │   ├── RoomTypeDO.java
│       │   │   └── SysUserDO.java
│       │   ├── exception/                      # 异常处理
│       │   │   └── GlobalExceptionAdvice.java   # 全局异常处理器
│       │   ├── mapper/                         # 数据访问层
│       │   │   ├── OrdersMapper.java
│       │   │   ├── RoomMapper.java
│       │   │   ├── RoomTypeMapper.java
│       │   │   └── SysUserMapper.java
│       │   ├── query/                          # 查询对象
│       │   │   ├── BaseQuery.java
│       │   │   ├── OrderQuery.java
│       │   │   ├── RoomQuery.java
│       │   │   └── RoomTypeQuery.java
│       │   ├── service/                        # 服务层
│       │   │   ├── impl/                      # 服务实现
│       │   │   ├── OrderService.java
│       │   │   ├── RoomService.java
│       │   │   ├── RoomStatusService.java
│       │   │   ├── RoomTypeService.java
│       │   │   ├── StatisticsService.java
│       │   │   └── SysUserService.java
│       │   └── util/                           # 工具类
│       │       └── JwtUtil.java               # JWT工具类
│       └── resources/
│           ├── application.yml                  # 配置文件
│           └── schema.sql                      # 数据库初始化脚本
└── pom.xml                                     # Maven配置文件
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

1. 创建数据库：
```bash
mysql -u root -p < src/main/resources/schema.sql
```

或者在MySQL客户端中执行 `src/main/resources/schema.sql` 文件中的SQL语句。

2. 修改配置文件 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hotel_management
    username: root
    password: your_password
```

### 3. 运行项目

使用Maven编译并运行：
```bash
mvn clean package
java -jar target/hotel-management-1.0.0.jar
```

或者在IDE中直接运行 `HotelManagementApplication.java` 主类。

### 4. 访问API文档

项目启动后，访问 Swagger UI：
```
http://localhost:8080/swagger-ui.html
```

### 5. 默认账号

- 用户名：`admin`
- 密码：`123456`

## API接口文档

### 1. 用户认证

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户登录 | POST | /api/auth/login | 用户登录验证 |
| 用户退出 | POST | /api/auth/logout | 用户退出登录 |
| 获取当前用户信息 | GET | /api/auth/current | 获取当前登录用户信息 |

### 2. 房型管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 房型列表 | GET | /api/room-types | 获取所有房型列表 |
| 新增房型 | POST | /api/room-types | 添加新的房型 |
| 编辑房型 | PUT | /api/room-types/{id} | 修改房型信息 |
| 删除房型 | DELETE | /api/room-types/{id} | 删除指定房型 |

### 3. 房间管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 房间列表 | GET | /api/rooms | 获取所有房间列表 |
| 新增房间 | POST | /api/rooms | 添加新的房间 |
| 编辑房间 | PUT | /api/rooms/{id} | 修改房间信息 |
| 删除房间 | DELETE | /api/rooms/{id} | 删除指定房间 |

### 4. 订单管理

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 订单列表 | GET | /api/orders | 获取所有订单列表 |
| 新增订单 | POST | /api/orders | 创建新的订单 |
| 编辑订单 | PUT | /api/orders/{id} | 修改订单信息 |
| 删除订单 | DELETE | /api/orders/{id} | 删除指定订单 |
| 取消订单 | POST | /api/orders/{id}/cancel | 取消指定订单 |

### 5. 房态可视化

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取房态数据 | GET | /api/room-status | 获取所有房间的实时状态 |
| 更新房间状态 | PUT | /api/room-status/{id} | 快速更新房间状态 |

### 6. 数据统计

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取统计数据 | GET | /api/statistics | 获取酒店运营统计数据 |
| 导出统计数据 | GET | /api/statistics/export | 导出统计数据为Excel |

## 响应码说明

| 响应码 | 说明 |
|--------|------|
| 1 | 请求成功 |
| 0 | 请求失败 |
| 401 | 未授权（需登录） |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 开发规范

本项目严格遵循以下开发规范：

### 设计原则
- **SOLID**：单一职责、开闭原则、里氏替换、接口隔离、依赖倒置
- **DRY**：不要重复自己
- **KISS**：保持简单
- **YAGNI**：你不会需要它

### 分层架构
| 层级 | 职责 | 约束条件 |
|------|------|----------|
| Controller | 处理 HTTP 请求与响应 | - 禁止直接操作数据库<br>- 必须通过 Service 层调用 |
| Service | 业务逻辑实现，事务管理 | - 必须通过 Mapper 访问数据库 |
| Mapper | 数据持久化操作 | - 必须继承 BaseMapper |
| Entity | 数据库表结构映射对象 | - 用于数据库交互 |

### 代码风格
- 类名使用 `UpperCamelCase`（如 `UserServiceImpl`）
- 方法/变量名使用 `lowerCamelCase`（如 `saveUser`）
- 常量使用 `UPPER_SNAKE_CASE`（如 `MAX_LOGIN_ATTEMPTS`）
- 方法必须添加 Javadoc 格式注释
- 使用 SLF4J 记录日志

## 常见问题

### 1. 数据库连接失败

检查 `application.yml` 中的数据库连接信息是否正确，确保MySQL服务已启动。

### 2. 端口占用

如果8080端口被占用，可以在 `application.yml` 中修改端口号：
```yaml
server:
  port: 8081
```

### 3. 密码加密

用户密码使用BCrypt加密，可以使用以下方式生成加密密码：
```java
BCrypt.hashpw("your_password", BCrypt.gensalt());
```

## 待完成功能

- [ ] Excel导出功能
- [ ] 完善JWT认证拦截器
- [ ] 增加操作日志记录
- [ ] 数据权限控制
- [ ] 定时任务（如自动退房）

## 联系方式

如有问题，请联系项目维护团队。
