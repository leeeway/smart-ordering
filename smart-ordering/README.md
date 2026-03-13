# Smart Ordering (唐肆点膳)

## 项目简介
**Smart Ordering (唐肆点膳)** 是一个面向内部短距离场景的点餐系统，适用于公司、工作室或小型团队内部使用。系统以唐朝风格商品与文化元素为特色，支持快速点餐、库存管理及订单处理。

## 主要功能
- **商品订购系统**：在线浏览商品并下单。
- **多店铺支持**：支持多个虚拟店铺配置。
- **企业微信集成**：通过 WebHook 推送订单通知。
- **订单管理**：提供订单处理与状态展示。

## 技术栈
- **Java 11**
- **Spring Boot 2.6.6**
- **MyBatis** & **SQL Server**
- **Spring Cloud Kubernetes** (ConfigMap 动态配置)
- **Lombok** & **Gson**
- **Log4j2** (使用 Disruptor 提高异步日志性能)

## 项目结构
```
smart-ordering/
├── src/main/java/com/example/order/
│   ├── bean/order/          # 订单与商品实体类
│   ├── config/              # 数据库、Gson、缓存等配置
│   ├── controller/          # 订单控制层
│   ├── dao/                 # MyBatis Mapper 接口
│   ├── service/             # 业务逻辑层
│   ├── util/                # 工具类 (IP, Base64, Cookie)
│   └── SmartOrderingApplication.java # 启动类
├── src/main/resources/
│   ├── static/              # 静态网页资源 (lottery, order)
│   ├── application.yaml     # 应用基础配置
│   ├── bootstrap.yml        # Spring Cloud 引导配置
│   └── log4j2.xml           # 日志详细配置
├── sql/
│   └── schema.sql           # 数据库初始化脚本
└── pom.xml                  # Maven 依赖管理
```

## 快速开始

### 环境要求
- JDK 11+
- Maven 3.6+
- SQL Server

### 安装与运行

1. **配置数据库**
   在 `src/main/resources/application.yaml` 中配置 SQL Server 连接信息，并执行 `sql/schema.sql` 初始化表结构。

2. **编译打包**
   ```bash
   mvn clean package -DskipTests
   ```

3. **运行应用**
   ```bash
   java -jar target/smart-ordering-1.0-SNAPSHOT.jar
   ```


## 维护与开发

### 代码规范
- 使用 Lombok 简化 POJO。
- 遵循标准的 MVC 三层架构。
- 配置文件支持通过 Kubernetes ConfigMap 进行动态覆盖。

## License
[MIT License](LICENSE)


