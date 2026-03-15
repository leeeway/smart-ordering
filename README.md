# Smart Ordering (唐肆点膳) 🏮

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-11-orange.svg)](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.6.6-green.svg)](https://spring.io/projects/spring-boot)

**Smart Ordering (唐肆点膳)** 是一款专为公司、工作室或小型团队设计的内部点餐系统。它以独特的**盛唐文化**为视觉与文案主题，将日常点餐化作一场“坊间探店”之旅，既实用又富有情趣。

---

## ✨ 核心特性

- **🎭 沉浸式唐风体验**：从“锦鲤玉卷”到“凝霜琼酪”，全案采用唐朝风格命名的商品与店铺，支持高度自定义的 UI 视觉（头像、背景图）。
- **🏬 多店铺模式**：支持配置多个虚拟店铺，如“御馔海焙坊”、“长安甜馥肆”等，满足不同餐饮类型的需求。
- **💼 企业协同集成**：深度集成企业微信，订单通过 WebHook 实时推送至群聊，并支持 @所有人 提醒及订单自动拆单。
- **📦 库存管理**：内置基础库存扣减机制，防止超卖。
- **🕒 动态看板**：首页展示最近订单轮播，增强团队互动氛围。
- **☁️ 云原生架构**：支持 Spring Cloud Kubernetes 动态配置（ConfigMap），轻松适配容器化部署。

---

## 🛠️ 技术栈

| 领域 | 技术 |
| :--- | :--- |
| **后端** | Java 11, Spring Boot 2.6.6, Spring Cloud Kubernetes, MyBatis |
| **数据库** | SQL Server (Druid 连接池) |
| **日志** | Log4j2 + Disruptor (高性能异步日志) |
| **工具** | Lombok, Gson, OpenFeign, Actuator |
| **静态资源** | HTML5, CSS3, JavaScript |

---

## 🚀 快速部署

### 方式一：Docker Compose (推荐)

最简单快捷的启动方式，包含应用与 SQL Server 数据库：

1. **克隆仓库**
   ```bash
   git clone https://github.com/leeway/smart-ordering.git
   cd smart-ordering/smart-ordering
   ```

2. **启动容器**
   ```bash
   docker-compose up -d
   ```

3. **访问系统**
   - 首页：[http://localhost:8081/order.html](http://localhost:8081/order.html)
   - 管理接口：[http://localhost:8081/actuator/health](http://localhost:8081/actuator/health)

### 方式二：本地 Maven 运行

1. **初始化数据库**
   - 在 SQL Server 中创建数据库 `shop_db`。
   - 执行 `sql/schema.sql` (注：脚本目前为 MySQL 语法，SQL Server 环境下请根据注释微调)。

2. **修改配置**
   在 `src/main/resources/application.yaml` 中配置连接信息。

3. **编译并运行**
   ```bash
   mvn clean package -DskipTests
   java -jar target/smart-ordering-1.0-SNAPSHOT.jar
   ```

---

## ⚙️ 配置说明

系统主要通过 `application.yaml` 进行配置，关键项如下：

- `order.enabled`: 订单功能总开关。
- `order.webhook-url`: 企业微信 WebHook 地址。
- `order.shops`: 店铺及商品列表，支持图片 URL 和动态价格。
- `spring.datasource`: 数据库连接配置。

---

## 🤝 贡献指南

我们欢迎任何形式的贡献，包括但不限于：
- 提交 Bug 或新功能建议 (Issues)。
- 改进代码或文档 (Pull Request)。
- 创作更多有趣的唐风文案或静态资源。

---

## 📜 许可证

本项目基于 **[MIT License](LICENSE)** 开源。

---

> **“锦书已达，珍馐将至。”** —— 祝您的团队用餐愉快！🍵
