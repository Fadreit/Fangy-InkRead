<div align="center">

<p>
  <img src="./inkread-front/public/inkread-logo.svg" width="160" alt="墨香书阁Logo">
</p>

<img src="https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white" alt="Java 17">
<img src="https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 3">
<img src="https://img.shields.io/badge/Vue-3.x-4FC08D?logo=vuedotjs&logoColor=white" alt="Vue 3">
<img src="https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white" alt="MySQL 8">
<img src="https://img.shields.io/badge/MyBatis-3.x-FF0000?logo=mybatis&logoColor=white" alt="MyBatis">
<img src="https://img.shields.io/badge/license-MIT-green" alt="License">

</div>

# 📚 墨香书阁 (InkRead)

> 一个基于 Spring Boot 3 + Vue 3 的在线书店全栈项目，前后端分离架构，其中前端代码由AI创建。

**InkRead** InkRead 是一个全栈图书管理商城项目，采用 Spring Boot 3 和 Vue 3 框架构建。该项目主要帮助Java初学者熟悉web开发，包括 RESTful API 设计、RBAC 身份验证、云存储解决方案等。项目配有完整的API接口文档，以便帮助开发者进行开发。

---

## ✨ 功能特性 (Features)

### 📖 公开功能 (Public)
- 图书分类浏览与图书列表
- 图书详情查看（含封面图片）
- 图书搜索与分页
- 公告查看

### 👤 用户功能 (User)
- 注册 / 登录（JWT + Sa-Token）
- 个人资料管理与密码修改
- 购物车（添加 / 修改数量 / 删除）
- 下单购买（订单快照机制）
- 订单历史查询（分页 + 状态筛选）

### 🛠️ 管理后台 (Admin)
- 分类管理（CRUD + 软删除）
- 图书管理（CRUD + OSS 封面上传 + 上下架）
- 订单管理（发货 / 分页查询）
- 用户管理（状态切换 / 分页查询）
- 仪表盘（用户数、订单数、销售额统计）

---

## 🏗️ 技术栈 (Tech Stack)

| 层级 | 技术 | 说明                          |
|---|---|-----------------------------|
| **后端框架** | Spring Boot 3.x | REST API 服务                 |
| **前端框架** | Vue 3 + Vite | 单页应用                        |
| **数据库** | MySQL 8.0 | 关系型数据库           |
| **ORM** | MyBatis | 手写 XML Mapper       |
| **认证授权** | Sa-Token + JWT |
| **文件存储** | 阿里云 OSS | 图书封面图片上传                    |
| **构建工具** | Maven (wrapper) | Maven3                      |

---

## 📁 项目结构

```
Fangy-InkRead/
├── inkread-boot/                  # 后端 Spring Boot 项目
│   ├── src/main/java/com/fadreit/inkreadboot/
│   │   ├── controller/            # REST 控制器（公开 + 用户 + 管理端）
│   │   ├── service/               # 业务逻辑层
│   │   ├── mapper/                # MyBatis Mapper 接口
│   │   ├── entity/                # 数据库实体类
│   │   ├── dto/                   # 请求 / 响应 DTO
│   │   ├── config/                # 配置类
│   │   ├── common/                # 统一响应 Result<T>、全局异常处理
│   │   └── util/                  # 工具类
│   └── src/main/resources/
│       ├── mapper/                # MyBatis XML Mapper 文件
│       ├── application-sample.yaml  # 配置文件模板
│       └── application.yaml         # 本地配置
├── inkread-front/                 # 前端 Vue 3 项目
└── docs/                          # 项目文档
        ├── db/                      # 数据库语句
```

---

## 🚀 快速开始 (Quick Start)

### 环境要求

- **JDK** 17
- **MySQL** 8
- **Node.js** 18+（前端）
- **Maven** 3

### 1. 克隆项目

```bash
git clone https://github.com/Fadreit/Fangy-InkRead.git
cd Fangy-InkRead
```

### 2. 配置数据库

创建 MySQL 数据库并导入表结构，表结构语句放在 `docs/db/` 中：

```sql
CREATE DATABASE `inkread-db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置后端

复制配置模板并填入你的信息：

```bash
cp inkread-boot/src/main/resources/application-sample.yaml \
   inkread-boot/src/main/resources/application.yaml
```

编辑 `application.yaml`，修改以下配置：
- 数据库连接信息（URL、用户名、密码）
- 阿里云 OSS 凭证（AccessKey、Bucket 等）

### 4. 启动后端

```bash
cd inkread-boot
./mvnw spring-boot:run
```

后端启动后访问：
- API 服务：`http://localhost:8080`

### 5. 启动前端

```bash
cd inkread-front
npm install
npm run dev
```

前端开发服务器默认运行在 `http://localhost:5173`。

---

## 🗄️ 数据库设计

项目包含 7 张核心数据表：

```
user → category → book → cart → orders → order_item -> announcement
```

| 表名           | 说明                          |
|--------------|-----------------------------|
| `user`       | 用户表（含角色字段，支持软删除）            |
| `category`   | 图书分类表（支持软删除）                |
| `book`       | 图书表（含 OSS 封面 URL，支持软删除/上下架） |
| `cart`       | 购物车（用户-图书关联）                |
| `orders`     | 订单表（含收件信息、金额、状态）            |
| `order_item` | 订单明细（订单快照，存储下单时的书名/封面/价格）   |
| `announce`   | 公告表（标题、内容）                  |

---

## 🔌 API 概览

共 **37 个接口**，分为三组：

| 分组 | 数量 | 路径前缀 | 说明 |
|---|---|---|---|
| 公开接口 | 6 | `/api/books`, `/api/categories` | 无需登录 |
| 用户接口 | 13 | `/api/cart`, `/api/orders`, `/api/users` | 需要登录 |
| 管理接口 | 18 | `/api/admin/**` | 需要 admin 角色 |

---

## 📝 开发计划以及更新日志

 ### ver 1.0 ----- 2026.6.3
- [x] 项目脚手架搭建
- [x] 公开接口（分类列表、图书列表/详情）
- [x] 用户模块（注册、登录、个人中心）
- [x] 管理端 CRUD + OSS 上传
- [x] 购物车 + 下单流程
- [x] 用户管理 + 仪表盘
- [x] 前端页面开发（AI）

### ver 1.1 ----- 2026.6.6
- [x] 增加公告模块
- [x] 修复部分bug
- [x] 前端代码更正

### ver 1.2 ----- 2026.6.7
- [x] 添加留言功能
- [x] 更新API文档
---

## 🤝 贡献

这是一个个人学习项目，欢迎提出建议和改进意见！

1. Fork 本仓库
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📄 许可证

本项目基于 MIT License 开源。

---

<div align="center">
  <sub>Built with ❤️ by Fadreit</sub>
</div>
