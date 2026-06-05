# 墨香书阁 / InkRead — 接口文档

> 这是使用AI生成的API接口文档，辅助后端开发者使用，请勿用于其他用途。
> 
> 如果在阅读本文档时发现错误，欢迎提出建议，感谢您的阅读。
> 
> 版本：v1.0 | 日期：2026-06-02 | 用途：开发教程用

---

## 目录

- [一、基础规范](#一基础规范)
- [二、用户模块](#二用户模块)
- [三、分类模块](#三分类模块)
- [四、图书模块](#四图书模块)
- [五、购物车模块](#五购物车模块)
- [六、订单模块](#六订单模块)
- [七、用户管理模块（管理员）](#七用户管理模块管理员)
- [八、仪表盘模块（管理员）](#八仪表盘模块管理员)
- [九、公告模块](#九公告模块)
- [十、错误码速查表](#十错误码速查表)

---

## 一、基础规范

### 1.1 根路径

```
http://localhost:8080
```

### 1.2 统一响应格式

所有接口返回以下 JSON 结构：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | 状态码（完全兼容 HTTP 状态码） |
| `message` | String | 提示信息 |
| `data` | Object / Array / null | 业务数据，无数据时为 `null` |

### 1.3 统一分页格式

分页接口的 `data` 字段格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10,
    "records": []
  }
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `total` | Long | 总记录数 |
| `page` | Integer | 当前页码（从 1 开始） |
| `size` | Integer | 每页条数 |
| `pages` | Integer | 总页数 |
| `records` | Array | 当前页数据列表 |

### 1.4 鉴权说明

| 接口类别 | 鉴权要求 | Token 携带方式 |
|----------|----------|:---:|
| 公开接口 | 无需登录 | — |
| 用户接口 | 需登录（任意角色） | 请求头 `Authorization: Bearer <token>` |
| 管理员接口 | 需登录 + `admin` 角色 | 请求头 `Authorization: Bearer <token>` |

> 未登录访问需鉴权接口 → 返回 `401`；已登录但角色不足 → 返回 `403`。

### 1.5 HTTP 方法约定

| 方法 | 用途 | 幂等性 |
|:---:|------|:---:|
| `GET` | 查询资源 | ✅ 是 |
| `POST` | 新增资源 / 执行操作 | ❌ 否 |
| `PUT` | 修改资源 / 状态变更 | ✅ 是 |
| `DELETE` | 删除资源 | ✅ 是 |

### 1.6 命名约定

- URL 全部小写，单词间用短横线 `-` 连接（如 `order-item`）
- 参数名和字段名采用小驼峰 `camelCase`
- 数据库字段采用下划线 `snake_case`

---

## 二、用户模块

---

### 2.1 用户注册

> **公开接口** — 无需登录

| 属性 | 值 |
|------|-----|
| **URL** | `/api/users/register` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `username` | String | ✅ | 用户名，4-20 位，仅允许字母、数字、下划线 |
| `password` | String | ✅ | 密码，最少 6 位 |
| `confirmPassword` | String | ✅ | 确认密码，必须与 password 一致 |
| `nickname` | String | ❌ | 昵称，不传则默认等于用户名 |
| `phone` | String | ❌ | 手机号，11 位数字 |

**请求示例：**

```json
{
  "username": "zhangsan",
  "password": "123456",
  "confirmPassword": "123456",
  "nickname": "张三",
  "phone": "13800138000"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "张三",
    "createdAt": "2026-06-02 15:30:00"
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 用户名已存在 | 400 | 用户名已存在 |
| 两次密码不一致 | 400 | 两次输入的密码不一致 |
| 用户名格式不符 | 400 | 用户名为4-20位字母、数字或下划线 |
| 密码过短 | 400 | 密码不能少于6位 |

---

### 2.2 用户登录

> **公开接口** — 无需登录

| 属性 | 值 |
|------|-----|
| **URL** | `/api/users/login` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `username` | String | ✅ | 用户名 |
| `password` | String | ✅ | 密码 |
| `rememberMe` | Boolean | ❌ | 是否记住我，默认 `false`；`true` 时 Token 有效期 30 天 |

**请求示例：**

```json
{
  "username": "zhangsan",
  "password": "123456",
  "rememberMe": true
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user": {
      "id": 1,
      "username": "zhangsan",
      "nickname": "张三",
      "role": "user",
      "phone": "13800138000"
    }
  }
}
```

**错误场景：**

| 场景       | code | message       |
|----------|:----:|---------------|
| 账号不存在    | 401  | 账号不存在，请注册账号   |
| 用户名或密码错误 | 402  | 用户名或密码错误      |
| 账号已被禁用   | 403  | 账号已被禁用，请联系管理员 |

---

### 2.3 获取个人信息

> **需登录** — 普通用户 / 管理员

| 属性 | 值 |
|------|-----|
| **URL** | `/api/users/profile` |
| **方法** | `GET` |
| **请求头** | `Authorization: Bearer <token>` |

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "张三",
    "phone": "13800138000",
    "role": "user",
    "status": 1,
    "createdAt": "2026-05-15 10:00:00",
    "updatedAt": "2026-05-15 10:00:00"
  }
}
```

---

### 2.4 修改个人信息

> **需登录** — 普通用户 / 管理员

| 属性 | 值 |
|------|-----|
| **URL** | `/api/users/profile` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**请求体（全部可选，传什么改什么）：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `nickname` | String | ❌ | 新昵称 |
| `phone` | String | ❌ | 新手机号 |

**请求示例：**

```json
{
  "nickname": "张三丰",
  "phone": "13900139000"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "修改成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "张三丰",
    "phone": "13900139000",
    "role": "user"
  }
}
```

---

### 2.5 修改密码

> **需登录** — 普通用户 / 管理员

| 属性 | 值 |
|------|-----|
| **URL** | `/api/users/password` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `oldPassword` | String | ✅ | 原密码 |
| `newPassword` | String | ✅ | 新密码，最少 6 位 |
| `confirmPassword` | String | ✅ | 确认新密码 |

**请求示例：**

```json
{
  "oldPassword": "123456",
  "newPassword": "654321",
  "confirmPassword": "654321"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "密码修改成功，请重新登录",
  "data": null
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 原密码错误 | 400 | 原密码错误 |
| 两次新密码不一致 | 400 | 两次输入的密码不一致 |

---

## 三、分类模块

---

### 3.1 新增分类

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/categories` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `name` | String | ✅ | 分类名称，唯一，最长 50 字符 |
| `description` | String | ❌ | 分类描述 |
| `sortOrder` | Integer | ❌ | 排序号，默认 0，越小越靠前 |

**请求示例：**

```json
{
  "name": "计算机科学",
  "description": "编程、算法、计算机基础类图书",
  "sortOrder": 1
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "id": 1,
    "name": "计算机科学",
    "description": "编程、算法、计算机基础类图书",
    "sortOrder": 1,
    "status": 1,
    "createdAt": "2026-06-02 16:00:00"
  }
}
```

---

### 3.2 后台分类列表

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/categories` |
| **方法** | `GET` |
| **请求头** | `Authorization: Bearer <token>` |

> 后台列表返回**所有分类**（含禁用），并附带每个分类下的图书数量。

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "计算机科学",
      "description": "编程、算法、计算机基础类图书",
      "sortOrder": 1,
      "status": 1,
      "bookCount": 15,
      "createdAt": "2026-05-01 10:00:00"
    },
    {
      "id": 2,
      "name": "文学小说",
      "description": null,
      "sortOrder": 2,
      "status": 0,
      "bookCount": 8,
      "createdAt": "2026-05-02 11:00:00"
    }
  ]
}
```

---

### 3.3 前台分类列表

> **公开接口** — 无需登录

| 属性 | 值 |
|------|-----|
| **URL** | `/api/categories` |
| **方法** | `GET` |

> 前台**仅返回启用中的分类**（`status = 1`），不含图书数量。

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "name": "计算机科学",
      "description": "编程、算法、计算机基础类图书",
      "sortOrder": 1
    },
    {
      "id": 3,
      "name": "人文社科",
      "description": "历史、哲学、社会学",
      "sortOrder": 3
    }
  ]
}
```

---

### 3.4 编辑分类

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/categories/{id}` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 分类 ID |

**请求体（全部可选，传什么改什么）：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `name` | String | ❌ | 分类名称 |
| `description` | String | ❌ | 分类描述 |
| `sortOrder` | Integer | ❌ | 排序号 |

**请求示例：**

```json
{
  "name": "计算机与编程",
  "sortOrder": 0
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "修改成功",
  "data": {
    "id": 1,
    "name": "计算机与编程",
    "description": "编程、算法、计算机基础类图书",
    "sortOrder": 0,
    "status": 1
  }
}
```

---

### 3.5 删除分类

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/categories/{id}` |
| **方法** | `DELETE` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 分类 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 分类不存在 | 404 | 分类不存在 |
| 分类下还有图书 | 400 | 该分类下存在 15 本图书，无法删除 |

---

### 3.6 修改分类状态（启用/禁用）

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/categories/{id}/status` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 分类 ID |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `status` | Integer | ✅ | `1` 启用 / `0` 禁用 |

**请求示例：**

```json
{
  "status": 0
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "状态更新成功",
  "data": {
    "id": 1,
    "name": "计算机科学",
    "status": 0
  }
}
```

---

## 四、图书模块

---

### 4.1 新增图书

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/books` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `title` | String | ✅ | 书名，最长 200 字符 |
| `author` | String | ✅ | 作者 |
| `isbn` | String | ❌ | ISBN 号 |
| `categoryId` | Long | ✅ | 所属分类 ID |
| `price` | BigDecimal | ✅ | 价格（元），保留两位小数 |
| `stock` | Integer | ✅ | 库存数量，≥ 0 |
| `publisher` | String | ❌ | 出版社 |
| `publishDate` | String | ❌ | 出版日期，格式 `yyyy-MM-dd` |
| `description` | String | ❌ | 图书简介 |
| `coverUrl` | String | ❌ | 封面图片 URL（需先通过 4.2 接口上传获得） |

**请求示例：**

```json
{
  "title": "Java 编程思想",
  "author": "Bruce Eckel",
  "isbn": "978-7-111-21382-6",
  "categoryId": 1,
  "price": 99.00,
  "stock": 50,
  "publisher": "机械工业出版社",
  "publishDate": "2025-01-01",
  "description": "Java 经典入门书籍，全面讲解 Java 语言核心概念。",
  "coverUrl": "https://oss-cn-hangzhou.aliyuncs.com/bucket/covers/java-think.jpg"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "新增成功",
  "data": {
    "id": 1,
    "title": "Java 编程思想",
    "author": "Bruce Eckel",
    "isbn": "978-7-111-21382-6",
    "categoryId": 1,
    "categoryName": "计算机科学",
    "price": 99.00,
    "stock": 50,
    "publisher": "机械工业出版社",
    "publishDate": "2025-01-01",
    "description": "Java 经典入门书籍，全面讲解 Java 语言核心概念。",
    "coverUrl": "https://oss-cn-hangzhou.aliyuncs.com/bucket/covers/java-think.jpg",
    "status": 1,
    "createdAt": "2026-06-02 16:30:00"
  }
}
```

---

### 4.2 上传图书封面（OSS）

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/books/upload` |
| **方法** | `POST` |
| **Content-Type** | `multipart/form-data` |
| **请求头** | `Authorization: Bearer <token>` |

**表单参数：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `file` | File | ✅ | 图片文件 |

**文件限制：**

| 限制项 | 值 |
|--------|-----|
| 允许格式 | `.jpg` `.jpeg` `.png` `.webp` |
| 最大大小 | 2 MB |
| 上传位置 | 阿里云 OSS，目录 `covers/` 下 |
| 文件命名 | UUID + 原扩展名（防止重名） |

**成功响应：**

```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "url": "https://oss-cn-hangzhou.aliyuncs.com/inkread-bucket/covers/a1b2c3d4-e5f6-7890-abcd-ef1234567890.jpg"
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 未选择文件 | 400 | 请选择上传文件 |
| 格式不支持 | 400 | 仅支持 jpg、png、webp 格式的图片 |
| 文件过大 | 400 | 图片大小不能超过 2MB |

---

### 4.3 后台图书列表

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/books` |
| **方法** | `GET` |
| **请求头** | `Authorization: Bearer <token>` |

**查询参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | ❌ | 1 | 页码 |
| `size` | Integer | ❌ | 10 | 每页条数 |
| `keyword` | String | ❌ | — | 书名或作者模糊搜索 |
| `categoryId` | Long | ❌ | — | 按分类筛选 |
| `status` | Integer | ❌ | — | 按状态筛选：`1` 上架 / `0` 下架 |

> 后台列表返回**所有图书**（含下架的），每条记录含分类名称。

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "page": 1,
    "size": 10,
    "pages": 10,
    "records": [
      {
        "id": 1,
        "title": "Java 编程思想",
        "author": "Bruce Eckel",
        "coverUrl": "https://oss-cn-hangzhou.aliyuncs.com/inkread-bucket/covers/java-think.jpg",
        "categoryId": 1,
        "categoryName": "计算机科学",
        "price": 99.00,
        "stock": 50,
        "status": 1,
        "createdAt": "2026-06-01 10:00:00"
      }
    ]
  }
}
```

---

### 4.4 前台图书列表

> **公开接口** — 无需登录

| 属性 | 值 |
|------|-----|
| **URL** | `/api/books` |
| **方法** | `GET` |

**查询参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | ❌ | 1 | 页码 |
| `size` | Integer | ❌ | 12 | 每页条数 |
| `keyword` | String | ❌ | — | 书名或作者模糊搜索 |
| `categoryId` | Long | ❌ | — | 按分类筛选（传入则只查该分类） |
| `sort` | String | ❌ | `latest` | 排序方式：`latest` 上架时间降序 / `price-asc` 价格升序 / `price-desc` 价格降序 |

> 前台**仅返回上架图书**（`status=1` 且 `is_deleted=0`），所属分类**仅限启用状态**。

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 85,
    "page": 1,
    "size": 12,
    "pages": 8,
    "records": [
      {
        "id": 1,
        "title": "Java 编程思想",
        "author": "Bruce Eckel",
        "coverUrl": "https://oss-cn-hangzhou.aliyuncs.com/inkread-bucket/covers/java-think.jpg",
        "categoryName": "计算机科学",
        "price": 99.00,
        "stock": 50
      }
    ]
  }
}
```

---

### 4.5 图书详情

> **公开接口** — 无需登录

| 属性 | 值 |
|------|-----|
| **URL** | `/api/books/{id}` |
| **方法** | `GET` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 图书 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "Java 编程思想",
    "author": "Bruce Eckel",
    "isbn": "978-7-111-21382-6",
    "categoryId": 1,
    "categoryName": "计算机科学",
    "price": 99.00,
    "stock": 50,
    "publisher": "机械工业出版社",
    "publishDate": "2025-01-01",
    "description": "Java 经典入门书籍，全面讲解 Java 语言核心概念。",
    "coverUrl": "https://oss-cn-hangzhou.aliyuncs.com/inkread-bucket/covers/java-think.jpg",
    "status": 1,
    "createdAt": "2026-06-01 10:00:00"
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 图书不存在或已下架 | 404 | 图书不存在 |

---

### 4.6 编辑图书

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/books/{id}` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 图书 ID |

**请求体（全部可选，传什么改什么）：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `title` | String | ❌ | 书名 |
| `author` | String | ❌ | 作者 |
| `isbn` | String | ❌ | ISBN |
| `categoryId` | Long | ❌ | 分类 ID |
| `price` | BigDecimal | ❌ | 价格 |
| `stock` | Integer | ❌ | 库存 |
| `publisher` | String | ❌ | 出版社 |
| `publishDate` | String | ❌ | 出版日期 |
| `description` | String | ❌ | 简介 |
| `coverUrl` | String | ❌ | 封面 URL（变更时先上传获取新 URL） |

**请求示例：**

```json
{
  "price": 89.00,
  "stock": 100,
  "description": "Java 经典入门书籍（2025 修订版），全面讲解 Java 语言核心概念。"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "修改成功",
  "data": {
    "id": 1,
    "title": "Java 编程思想",
    "price": 89.00,
    "stock": 100,
    "description": "Java 经典入门书籍（2025 修订版），全面讲解 Java 语言核心概念。"
  }
}
```

---

### 4.7 删除图书

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/books/{id}` |
| **方法** | `DELETE` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 图书 ID |

> 实现方式：**软删除**（`is_deleted` 设为 `1`），不物理删除数据。

**成功响应：**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 图书不存在 | 404 | 图书不存在 |

---

### 4.8 图书上下架

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/books/{id}/shelf` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 图书 ID |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `status` | Integer | ✅ | `1` 上架 / `0` 下架 |

**请求示例：**

```json
{
  "status": 0
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "已下架",
  "data": {
    "id": 1,
    "title": "Java 编程思想",
    "status": 0
  }
}
```

---

## 五、购物车模块

> 所有购物车接口均 **需登录**（普通用户 / 管理员）。

---

### 5.1 加入购物车

| 属性 | 值 |
|------|-----|
| **URL** | `/api/cart` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `bookId` | Long | ✅ | 图书 ID |
| `quantity` | Integer | ❌ | 数量，默认 1 |

**请求示例：**

```json
{
  "bookId": 1,
  "quantity": 2
}
```

**业务逻辑：**

- 同一用户对同一图书重复加入，数量累加而非新增记录
- 累加后的数量不得超过该图书的当前库存

**成功响应：**

```json
{
  "code": 200,
  "message": "已加入购物车",
  "data": {
    "id": 1,
    "bookId": 1,
    "bookTitle": "Java 编程思想",
    "coverUrl": "https://oss-cn-hangzhou.aliyuncs.com/inkread-bucket/covers/java-think.jpg",
    "price": 99.00,
    "quantity": 2,
    "subtotal": 198.00
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 图书不存在或下架 | 400 | 图书不存在或已下架 |
| 库存不足 | 400 | 库存不足，当前库存为 0 |

---

### 5.2 购物车列表

| 属性 | 值 |
|------|-----|
| **URL** | `/api/cart` |
| **方法** | `GET` |
| **请求头** | `Authorization: Bearer <token>` |

> 返回当前登录用户购物车中所有商品，三表联查（cart + book + user），按加入时间倒序。

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "bookId": 1,
      "bookTitle": "Java 编程思想",
      "coverUrl": "https://oss-cn-hangzhou.aliyuncs.com/inkread-bucket/covers/java-think.jpg",
      "price": 99.00,
      "stock": 50,
      "quantity": 2,
      "subtotal": 198.00,
      "selected": true,
      "createdAt": "2026-06-02 17:00:00"
    },
    {
      "id": 2,
      "bookId": 3,
      "bookTitle": "深入理解 Java 虚拟机",
      "coverUrl": "https://oss-cn-hangzhou.aliyuncs.com/inkread-bucket/covers/jvm.jpg",
      "price": 79.00,
      "stock": 30,
      "quantity": 1,
      "subtotal": 79.00,
      "selected": false,
      "createdAt": "2026-06-02 16:30:00"
    }
  ]
}
```

> `selected` 字段由前端管理，下单时前端传入选中的购物车项 ID 列表。

---

### 5.3 修改购物车数量

| 属性 | 值 |
|------|-----|
| **URL** | `/api/cart/{id}` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 购物车项 ID |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `quantity` | Integer | ✅ | 新数量，范围 1 ~ 当前库存 |

**请求示例：**

```json
{
  "quantity": 5
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "数量已更新",
  "data": {
    "id": 1,
    "quantity": 5,
    "subtotal": 495.00
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 购物车项不存在或不属于该用户 | 404 | 购物车项不存在 |
| 数量超过库存 | 400 | 库存不足，当前库存为 50 |

---

### 5.4 删除购物车项（单个）

| 属性 | 值 |
|------|-----|
| **URL** | `/api/cart/{id}` |
| **方法** | `DELETE` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 购物车项 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "已移除",
  "data": null
}
```

---

### 5.5 批量删除购物车项

| 属性 | 值 |
|------|-----|
| **URL** | `/api/cart` |
| **方法** | `DELETE` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `ids` | Long[] | ✅ | 购物车项 ID 列表 |

**请求示例：**

```json
{
  "ids": [1, 2, 3]
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "已移除 3 项",
  "data": null
}
```

---

## 六、订单模块

---

### 6.1 下单

> **需登录** — 普通用户 / 管理员

| 属性 | 值 |
|------|-----|
| **URL** | `/api/orders` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `cartIds` | Long[] | ✅ | 要结算的购物车项 ID 列表（必须属于当前用户） |
| `receiverName` | String | ✅ | 收货人姓名 |
| `receiverPhone` | String | ✅ | 收货人手机号 |
| `receiverAddress` | String | ✅ | 收货地址 |

**请求示例：**

```json
{
  "cartIds": [1, 3],
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "receiverAddress": "浙江省杭州市西湖区文三路 123 号"
}
```

**业务逻辑（需在同一事务中执行）：**

1. 校验 `cartIds` 是否都属于当前用户
2. 校验每个商品的库存是否充足
3. 生成订单号（格式：`INK` + `yyyyMMddHHmmss` + 4 位随机数字）
4. 写入 `orders` 表
5. 遍历商品写入 `order_item` 表
6. 扣减库存（`book.stock = book.stock - quantity`）
7. 删除已下单的购物车记录
8. 以上全部在一个数据库事务中

**成功响应：**

```json
{
  "code": 200,
  "message": "下单成功",
  "data": {
    "id": 1,
    "orderNo": "INK202606021730001234",
    "totalAmount": 277.00,
    "status": "待发货",
    "itemCount": 2,
    "createdAt": "2026-06-02 17:30:00"
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 购物车 ID 列表为空 | 400 | 请选择要结算的商品 |
| 购物车项不属于当前用户 | 400 | 存在无效的商品项 |
| 某商品库存在下单时已不足 | 400 | 《Java 编程思想》库存不足，当前库存为 2 |
| 收货信息不完整 | 400 | 请填写完整的收货信息 |

---

### 6.2 用户订单列表

> **需登录** — 普通用户 / 管理员

| 属性 | 值 |
|------|-----|
| **URL** | `/api/orders` |
| **方法** | `GET` |
| **请求头** | `Authorization: Bearer <token>` |

**查询参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | ❌ | 1 | 页码 |
| `size` | Integer | ❌ | 10 | 每页条数 |
| `status` | String | ❌ | — | 筛选状态：`待发货` / `已发货` / `已完成` / `已取消` |

> 仅返回当前登录用户的订单，按创建时间倒序。

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 5,
    "page": 1,
    "size": 10,
    "pages": 1,
    "records": [
      {
        "id": 1,
        "orderNo": "INK202606021730001234",
        "totalAmount": 277.00,
        "status": "待发货",
        "itemCount": 2,
        "createdAt": "2026-06-02 17:30:00"
      }
    ]
  }
}
```

---

### 6.3 订单详情

> **需登录** — 普通用户（只能看自己的订单）/ 管理员（可看所有）

| 属性 | 值 |
|------|-----|
| **URL** | `/api/orders/{id}` |
| **方法** | `GET` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 订单 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "orderNo": "INK202606021730001234",
    "userId": 1,
    "username": "zhangsan",
    "totalAmount": 277.00,
    "status": "待发货",
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "receiverAddress": "浙江省杭州市西湖区文三路 123 号",
    "logisticsCompany": null,
    "logisticsNo": null,
    "items": [
      {
        "id": 1,
        "bookId": 1,
        "bookTitle": "Java 编程思想",
        "bookCover": "https://oss-cn-hangzhou.aliyuncs.com/inkread-bucket/covers/java-think.jpg",
        "price": 99.00,
        "quantity": 2,
        "subtotal": 198.00
      },
      {
        "id": 2,
        "bookId": 3,
        "bookTitle": "深入理解 Java 虚拟机",
        "bookCover": "https://oss-cn-hangzhou.aliyuncs.com/inkread-bucket/covers/jvm.jpg",
        "price": 79.00,
        "quantity": 1,
        "subtotal": 79.00
      }
    ],
    "createdAt": "2026-06-02 17:30:00",
    "updatedAt": "2026-06-02 17:30:00"
  }
}
```

> 普通用户访问他人订单 → 返回 `403`。

---

### 6.4 取消订单

> **需登录** — 普通用户（只能取消自己的）/ 管理员

| 属性 | 值 |
|------|-----|
| **URL** | `/api/orders/{id}/cancel` |
| **方法** | `PUT` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 订单 ID |

**业务逻辑：**

1. 校验订单是否属于当前用户（管理员不受限）
2. 校验订单状态为"待发货"
3. 状态改为"已取消"
4. 回退库存

**成功响应：**

```json
{
  "code": 200,
  "message": "订单已取消",
  "data": {
    "id": 1,
    "orderNo": "INK202606021730001234",
    "status": "已取消"
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 订单不属于当前用户 | 403 | 无权操作此订单 |
| 订单状态不是"待发货" | 400 | 当前订单状态不允许取消 |

---

### 6.5 确认收货

> **需登录** — 普通用户（只能确认自己的）

| 属性 | 值 |
|------|-----|
| **URL** | `/api/orders/{id}/confirm` |
| **方法** | `PUT` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 订单 ID |

**业务逻辑：**

1. 校验订单属于当前用户
2. 校验订单状态为"已发货"
3. 状态改为"已完成"

**成功响应：**

```json
{
  "code": 200,
  "message": "已确认收货",
  "data": {
    "id": 1,
    "orderNo": "INK202606021730001234",
    "status": "已完成"
  }
}
```

---

### 6.6 管理员订单列表

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/orders` |
| **方法** | `GET` |
| **请求头** | `Authorization: Bearer <token>` |

**查询参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | ❌ | 1 | 页码 |
| `size` | Integer | ❌ | 10 | 每页条数 |
| `status` | String | ❌ | — | 筛选状态 |
| `keyword` | String | ❌ | — | 按订单号或用户名模糊搜索 |

> 返回所有用户的订单。

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 25,
    "page": 1,
    "size": 10,
    "pages": 3,
    "records": [
      {
        "id": 1,
        "orderNo": "INK202606021730001234",
        "username": "zhangsan",
        "totalAmount": 277.00,
        "status": "待发货",
        "itemCount": 2,
        "receiverName": "张三",
        "receiverPhone": "13800138000",
        "createdAt": "2026-06-02 17:30:00"
      }
    ]
  }
}
```

---

### 6.7 管理员发货

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/orders/{id}/ship` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 订单 ID |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `logisticsCompany` | String | ✅ | 物流公司名称 |
| `logisticsNo` | String | ✅ | 物流运单号 |

**请求示例：**

```json
{
  "logisticsCompany": "顺丰速运",
  "logisticsNo": "SF1234567890"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "发货成功",
  "data": {
    "id": 1,
    "orderNo": "INK202606021730001234",
    "status": "已发货",
    "logisticsCompany": "顺丰速运",
    "logisticsNo": "SF1234567890"
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 订单状态不是"待发货" | 400 | 当前订单状态不允许发货 |

---

## 七、用户管理模块（管理员）

---

### 7.1 用户列表

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/users` |
| **方法** | `GET` |
| **请求头** | `Authorization: Bearer <token>` |

**查询参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | ❌ | 1 | 页码 |
| `size` | Integer | ❌ | 10 | 每页条数 |
| `keyword` | String | ❌ | — | 按用户名或昵称模糊搜索 |

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 30,
    "page": 1,
    "size": 10,
    "pages": 3,
    "records": [
      {
        "id": 1,
        "username": "zhangsan",
        "nickname": "张三",
        "phone": "13800138000",
        "role": "user",
        "status": 1,
        "orderCount": 3,
        "createdAt": "2026-05-15 10:00:00"
      }
    ]
  }
}
```

---

### 7.2 用户详情

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/users/{id}` |
| **方法** | `GET` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 用户 ID |

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "nickname": "张三",
    "phone": "13800138000",
    "role": "user",
    "status": 1,
    "orderCount": 3,
    "createdAt": "2026-05-15 10:00:00",
    "updatedAt": "2026-06-01 12:00:00"
  }
}
```

---

### 7.3 禁用/启用用户

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/users/{id}/status` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 用户 ID |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `status` | Integer | ✅ | `1` 启用 / `0` 禁用 |

**请求示例：**

```json
{
  "status": 0
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "用户已禁用",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "status": 0
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 不能操作自己 | 400 | 不能修改自己的状态 |

---

### 7.4 变更用户角色

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/users/{id}/role` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 用户 ID |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `role` | String | ✅ | `user` 或 `admin` |

**请求示例：**

```json
{
  "role": "admin"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "角色变更成功",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "role": "admin"
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 不能修改自己的角色 | 400 | 不能修改自己的角色 |

---

## 八、仪表盘模块（管理员）

---

### 8.1 后台仪表盘

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/dashboard` |
| **方法** | `GET` |
| **请求头** | `Authorization: Bearer <token>` |

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "bookCount": 120,
    "categoryCount": 8,
    "userCount": 35,
    "pendingOrderCount": 5,
    "recentOrders": [
      {
        "id": 10,
        "orderNo": "INK202606021730001234",
        "username": "zhangsan",
        "totalAmount": 277.00,
        "status": "待发货",
        "createdAt": "2026-06-02 17:30:00"
      },
      {
        "id": 9,
        "orderNo": "INK202606021500008765",
        "username": "lisi",
        "totalAmount": 99.00,
        "status": "待发货",
        "createdAt": "2026-06-02 15:00:00"
      }
    ]
  }
}
```

---

## 九、公告模块

---

### 9.1 前台公告列表

> **公开接口** — 无需登录

| 属性 | 值 |
|------|-----|
| **URL** | `/api/announcements` |
| **方法** | `GET` |

**查询参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | ❌ | 1 | 页码 |
| `size` | Integer | ❌ | 10 | 每页条数 |

> 仅返回已发布的公告（`status = 1` 且 `is_deleted = 0`），按创建时间倒序。

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 12,
    "page": 1,
    "size": 10,
    "pages": 2,
    "records": [
      {
        "id": 1,
        "title": "系统上线公告",
        "content": "墨香书阁于 2026 年 6 月 1 日正式上线，欢迎新老用户选购图书！",
        "createdAt": "2026-06-01 10:00:00",
        "updatedAt": "2026-06-01 10:00:00"
      },
      {
        "id": 2,
        "title": "618 年中大促",
        "content": "全场图书 8 折起，活动时间 6 月 15 日至 6 月 18 日，快来抢购吧！",
        "createdAt": "2026-06-05 09:00:00",
        "updatedAt": "2026-06-05 09:00:00"
      }
    ]
  }
}
```

---

### 9.2 公告详情

> **公开接口** — 无需登录

| 属性 | 值 |
|------|-----|
| **URL** | `/api/announcements/{id}` |
| **方法** | `GET` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 公告 ID |

> 仅返回已发布的公告，草稿或已删除的公告返回 404。

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "系统上线公告",
    "content": "墨香书阁于 2026 年 6 月 1 日正式上线，欢迎新老用户选购图书！\n\n平台汇集了计算机科学、文学小说、人文社科等多个分类的优质图书，支持在线下单、物流跟踪等功能。",
    "status": 1,
    "createdAt": "2026-06-01 10:00:00",
    "updatedAt": "2026-06-05 14:00:00"
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 公告不存在（未发布/已删除） | 404 | 公告不存在 |

---

### 9.3 新增公告

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/announcements` |
| **方法** | `POST` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `title` | String | ✅ | 公告标题，最长 200 字符 |
| `content` | String | ✅ | 公告内容 |
| `status` | Integer | ❌ | `1` 发布 / `0` 草稿，默认 `0`（保存为草稿） |

**请求示例：**

```json
{
  "title": "系统上线公告",
  "content": "墨香书阁于 2026 年 6 月 1 日正式上线，欢迎新老用户选购图书！",
  "status": 1
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "发布成功",
  "data": {
    "id": 1,
    "title": "系统上线公告",
    "content": "墨香书阁于 2026 年 6 月 1 日正式上线，欢迎新老用户选购图书！",
    "status": 1,
    "createdAt": "2026-06-01 10:00:00",
    "updatedAt": "2026-06-01 10:00:00"
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 标题为空 | 400 | 公告标题不能为空 |
| 内容为空 | 400 | 公告内容不能为空 |

---

### 9.4 后台公告列表

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/announcements` |
| **方法** | `GET` |
| **请求头** | `Authorization: Bearer <token>` |

**查询参数：**

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | ❌ | 1 | 页码 |
| `size` | Integer | ❌ | 10 | 每页条数 |
| `keyword` | String | ❌ | — | 按标题模糊搜索 |
| `status` | Integer | ❌ | — | 按状态筛选：`1` 已发布 / `0` 草稿 |

> 后台列表返回所有公告（含草稿和已删除），按更新时间倒序。

**成功响应：**

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 20,
    "page": 1,
    "size": 10,
    "pages": 2,
    "records": [
      {
        "id": 3,
        "title": "618 年中大促",
        "content": "全场图书 8 折起，活动时间 6 月 15 日至 6 月 18 日。",
        "status": 1,
        "createdAt": "2026-06-05 09:00:00",
        "updatedAt": "2026-06-05 18:00:00"
      },
      {
        "id": 4,
        "title": "新书上架预告（草稿）",
        "content": "下周将上架一批新书...",
        "status": 0,
        "createdAt": "2026-06-05 17:00:00",
        "updatedAt": "2026-06-05 17:00:00"
      }
    ]
  }
}
```

---

### 9.5 编辑公告

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/announcements/{id}` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 公告 ID |

**请求体（全部可选，传什么改什么）：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `title` | String | ❌ | 公告标题 |
| `content` | String | ❌ | 公告内容 |
| `status` | Integer | ❌ | `1` 发布 / `0` 草稿 |

**请求示例：**

```json
{
  "title": "618 年中大促（更新）",
  "content": "全场图书 7 折起，活动时间延长至 6 月 20 日！"
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "修改成功",
  "data": {
    "id": 3,
    "title": "618 年中大促（更新）",
    "content": "全场图书 7 折起，活动时间延长至 6 月 20 日！",
    "status": 1,
    "createdAt": "2026-06-05 09:00:00",
    "updatedAt": "2026-06-05 19:00:00"
  }
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 公告不存在 | 404 | 公告不存在 |

---

### 9.6 删除公告

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/announcements/{id}` |
| **方法** | `DELETE` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 公告 ID |

> 实现方式：**软删除**（`is_deleted` 设为 `1`），不物理删除数据。

**成功响应：**

```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 公告不存在 | 404 | 公告不存在 |

---

### 9.7 发布/撤回公告

> **管理员接口** — 需 `admin` 角色

| 属性 | 值 |
|------|-----|
| **URL** | `/api/admin/announcements/{id}/status` |
| **方法** | `PUT` |
| **Content-Type** | `application/json` |
| **请求头** | `Authorization: Bearer <token>` |

**路径参数：**

| 参数 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 公告 ID |

**请求体：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `status` | Integer | ✅ | `1` 发布 / `0` 撤回为草稿 |

**请求示例：**

```json
{
  "status": 1
}
```

**成功响应：**

```json
{
  "code": 200,
  "message": "已发布",
  "data": {
    "id": 4,
    "title": "新书上架预告",
    "status": 1
  }
}
```

> 当 `status` 为 0 时，`message` 返回 `"已撤回"`。

**错误场景：**

| 场景 | code | message |
|------|:---:|------|
| 公告不存在 | 404 | 公告不存在 |

---

## 十、错误码速查表

| code | 含义 | 常见场景 |
|:---:|------|------|
| `200` | 成功 | 所有正常返回 |
| `400` | 请求参数错误 | 校验失败、业务规则不满足 |
| `401` | 未登录 | Token 缺失、过期或无效 |
| `403` | 无权限 | 角色不足、访问他人资源 |
| `404` | 资源不存在 | ID 对应的记录未找到 |
| `500` | 服务器内部错误 | 系统未知异常 |

---

## 附录 A：接口汇总表

| 序号 | URL | 方法 | 鉴权 | 所属模块 | 说明 |
|:---:|------|:---:|:---:|------|------|
| 1 | `/api/users/register` | POST | 公开 | 用户 | 注册 |
| 2 | `/api/users/login` | POST | 公开 | 用户 | 登录 |
| 3 | `/api/users/profile` | GET | 登录 | 用户 | 获取个人信息 |
| 4 | `/api/users/profile` | PUT | 登录 | 用户 | 修改个人信息 |
| 5 | `/api/users/password` | PUT | 登录 | 用户 | 修改密码 |
| 6 | `/api/categories` | GET | 公开 | 分类 | 前台分类列表 |
| 7 | `/api/admin/categories` | POST | 管理员 | 分类 | 新增分类 |
| 8 | `/api/admin/categories` | GET | 管理员 | 分类 | 后台分类列表 |
| 9 | `/api/admin/categories/{id}` | PUT | 管理员 | 分类 | 编辑分类 |
| 10 | `/api/admin/categories/{id}` | DELETE | 管理员 | 分类 | 删除分类 |
| 11 | `/api/admin/categories/{id}/status` | PUT | 管理员 | 分类 | 启用/禁用 |
| 12 | `/api/books` | GET | 公开 | 图书 | 前台图书列表 |
| 13 | `/api/books/{id}` | GET | 公开 | 图书 | 图书详情 |
| 14 | `/api/admin/books/upload` | POST | 管理员 | 图书 | 上传封面（OSS） |
| 15 | `/api/admin/books` | POST | 管理员 | 图书 | 新增图书 |
| 16 | `/api/admin/books` | GET | 管理员 | 图书 | 后台图书列表 |
| 17 | `/api/admin/books/{id}` | PUT | 管理员 | 图书 | 编辑图书 |
| 18 | `/api/admin/books/{id}` | DELETE | 管理员 | 图书 | 删除图书 |
| 19 | `/api/admin/books/{id}/shelf` | PUT | 管理员 | 图书 | 上下架 |
| 20 | `/api/cart` | POST | 登录 | 购物车 | 加入购物车 |
| 21 | `/api/cart` | GET | 登录 | 购物车 | 购物车列表 |
| 22 | `/api/cart/{id}` | PUT | 登录 | 购物车 | 修改数量 |
| 23 | `/api/cart/{id}` | DELETE | 登录 | 购物车 | 删除单个 |
| 24 | `/api/cart` | DELETE | 登录 | 购物车 | 批量删除 |
| 25 | `/api/orders` | POST | 登录 | 订单 | 下单 |
| 26 | `/api/orders` | GET | 登录 | 订单 | 用户订单列表 |
| 27 | `/api/orders/{id}` | GET | 登录 | 订单 | 订单详情 |
| 28 | `/api/orders/{id}/cancel` | PUT | 登录 | 订单 | 取消订单 |
| 29 | `/api/orders/{id}/confirm` | PUT | 登录 | 订单 | 确认收货 |
| 30 | `/api/admin/orders` | GET | 管理员 | 订单 | 管理员订单列表 |
| 31 | `/api/admin/orders/{id}` | GET | 管理员 | 订单 | 管理员订单详情 |
| 32 | `/api/admin/orders/{id}/ship` | PUT | 管理员 | 订单 | 发货 |
| 33 | `/api/admin/users` | GET | 管理员 | 用户管理 | 用户列表 |
| 34 | `/api/admin/users/{id}` | GET | 管理员 | 用户管理 | 用户详情 |
| 35 | `/api/admin/users/{id}/status` | PUT | 管理员 | 用户管理 | 禁用/启用 |
| 36 | `/api/admin/users/{id}/role` | PUT | 管理员 | 用户管理 | 角色变更 |
| 37 | `/api/admin/dashboard` | GET | 管理员 | 仪表盘 | 统计数据 |
| 38 | `/api/announcements` | GET | 公开 | 公告 | 前台公告列表 |
| 39 | `/api/announcements/{id}` | GET | 公开 | 公告 | 公告详情 |
| 40 | `/api/admin/announcements` | POST | 管理员 | 公告 | 新增公告 |
| 41 | `/api/admin/announcements` | GET | 管理员 | 公告 | 后台公告列表 |
| 42 | `/api/admin/announcements/{id}` | PUT | 管理员 | 公告 | 编辑公告 |
| 43 | `/api/admin/announcements/{id}` | DELETE | 管理员 | 公告 | 删除公告 |
| 44 | `/api/admin/announcements/{id}/status` | PUT | 管理员 | 公告 | 发布/撤回 |

> 📊 **接口统计**：公开 8 个 / 需登录 13 个 / 管理员 23 个 / 合计 44 个

---

## 附录 B：练习建议

按学习曲线依次实现：

```
第 1 步：公开接口（6 个）
  → 分类列表、图书列表、图书详情
  → 先把 Spring Boot + MyBatis + XML Mapper 跑通

第 2 步：用户模块（5 个）
  → 注册、登录、个人信息、修改密码
  → 接入 Sa-Token + JWT，学习鉴权体系

第 3 步：管理员图书/分类管理（11 个）
  → 分类 CRUD + 图书 CRUD + 封面上传 OSS
  → 手写多表联查 XML（book JOIN category）
  → OSS SDK 集成

第 4 步：购物车 + 订单（11 个）
  → 购物车三表联查 + 订单事务（多表同时写入）
  → 事务管理 @Transactional
  → 订单状态机逻辑

第 5 步：用户管理 + 仪表盘（4 个）
  → 聚合查询统计
  → 管理员权限校验
```

---

---

## 附录 C：数据库表结构

### C.0 表关系总览（ER 图）

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────┐
│    user      │       │       cart       │       │     book     │
├──────────────┤       ├──────────────────┤       ├──────────────┤
│ id        PK │──┐    │ id            PK │    ┌──│ id        PK │
│ username     │  └────│ user_id       FK │────┘  │ title        │
│ password     │       │ book_id       FK │       │ author       │
│ nickname     │       │ quantity        │       │ isbn         │
│ phone        │       │ created_at      │       │ category_id FK│──┐
│ role         │       │ updated_at      │       │ price        │  │
│ status       │       └──────────────────┘       │ stock        │  │
│ created_at   │                                   │ publisher    │  │
│ updated_at   │       ┌──────────────────┐       │ publish_date │  │
│ is_deleted   │       │   order_item     │       │ description  │  │
└──────────────┘       ├──────────────────┤       │ cover_url    │  │
       │               │ id            PK │       │ status       │  │
       │      ┌────────│ order_id      FK │──┐    │ created_at   │  │
       │      │        │ book_id       FK │──┤    │ updated_at   │  │
       │      │        │ book_title      │  │    │ is_deleted   │  │
       │      │        │ book_cover      │  │    └──────────────┘  │
       │      │        │ price           │  │                      │
       │      │        │ quantity        │  │    ┌──────────────┐  │
       │      │        │ subtotal        │  │    │   category   │  │
       │      │        └──────────────────┘  │    ├──────────────┤  │
       │      │                              │    │ id        PK │──┘
       │      │        ┌──────────────────┐  │    │ name         │
       │      │        │     orders       │  │    │ description  │
       │      └───────→│ id           PK │  │    │ sort_order   │
       │               │ order_no        │  │    │ status       │
       └──────────────→│ user_id      FK │──┘    │ created_at   │
                       │ total_amount    │       │ updated_at   │
                       │ status          │       │ is_deleted   │
                       │ receiver_name   │       └──────────────┘
                       │ receiver_phone  │
                       │ receiver_address│
                       │ logistics_company│
                       │ logistics_no    │
                       │ created_at      │
                       │ updated_at      │
                       └──────────────────┘
```

### 表关系说明

| 关系 | 类型 | 说明 |
|------|:---:|------|
| user ↔ cart | **一对多** | 一个用户有多条购物车记录 |
| book ↔ cart | **一对多** | 一本图书可出现在多个用户的购物车中 |
| user ↔ orders | **一对多** | 一个用户有多个订单 |
| orders ↔ order_item | **一对多** | 一个订单包含多个商品项 |
| book ↔ order_item | **一对多** | 一本图书可出现在多个订单项中 |
| category ↔ book | **一对多** | 一个分类下有多个图书 |

---

### C.1 user（用户表）

| 字段 | 类型 | 约束 | 默认值 | 说明 |
|------|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — | 用户 ID |
| `username` | VARCHAR(50) | NOT NULL, UNIQUE | — | 用户名 |
| `password` | VARCHAR(255) | NOT NULL | — | 加密后的密码（BCrypt） |
| `nickname` | VARCHAR(50) | NULL | NULL | 昵称 |
| `phone` | VARCHAR(20) | NULL | NULL | 手机号 |
| `role` | VARCHAR(20) | NOT NULL | `'user'` | 角色：`user` / `admin` |
| `status` | TINYINT | NOT NULL | `1` | 状态：`1` 启用 / `0` 禁用 |
| `created_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` | 注册时间 |
| `updated_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` ON UPDATE | 更新时间 |
| `is_deleted` | TINYINT(1) | NOT NULL | `0` | 软删除：`0` 正常 / `1` 已删除 |

**索引：**

| 索引名 | 字段 | 类型 |
|--------|------|:---:|
| `idx_user_username` | `username` | UNIQUE |
| `idx_user_role_status` | `role, status` | NORMAL |

**建表 SQL 提示：**

> 引擎 InnoDB，字符集 utf8mb4，主键自增从 1 开始。`password` 存储 BCrypt 加密结果（约 60 字符），预留 255 长度。

---

### C.2 category（图书分类表）

| 字段 | 类型 | 约束 | 默认值 | 说明 |
|------|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — | 分类 ID |
| `name` | VARCHAR(50) | NOT NULL, UNIQUE | — | 分类名称 |
| `description` | VARCHAR(255) | NULL | NULL | 分类描述 |
| `sort_order` | INT | NOT NULL | `0` | 排序号（越小越靠前） |
| `status` | TINYINT | NOT NULL | `1` | 状态：`1` 启用 / `0` 禁用 |
| `created_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` ON UPDATE | 更新时间 |
| `is_deleted` | TINYINT(1) | NOT NULL | `0` | 软删除 |

**索引：**

| 索引名 | 字段 | 类型 |
|--------|------|:---:|
| `idx_category_name` | `name` | UNIQUE |
| `idx_category_sort` | `sort_order` | NORMAL |

---

### C.3 book（图书表）

| 字段 | 类型 | 约束 | 默认值 | 说明 |
|------|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — | 图书 ID |
| `title` | VARCHAR(200) | NOT NULL | — | 书名 |
| `author` | VARCHAR(100) | NOT NULL | — | 作者 |
| `isbn` | VARCHAR(20) | NULL | NULL | ISBN 号 |
| `category_id` | BIGINT | NOT NULL, FK → category.id | — | 所属分类 ID |
| `price` | DECIMAL(10,2) | NOT NULL | — | 价格（元） |
| `stock` | INT | NOT NULL | `0` | 库存数量 |
| `publisher` | VARCHAR(100) | NULL | NULL | 出版社 |
| `publish_date` | DATE | NULL | NULL | 出版日期 |
| `description` | TEXT | NULL | NULL | 图书简介 |
| `cover_url` | VARCHAR(500) | NULL | NULL | 封面图片 URL（OSS） |
| `status` | TINYINT | NOT NULL | `1` | 上下架：`1` 上架 / `0` 下架 |
| `created_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` | 上架时间 |
| `updated_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` ON UPDATE | 更新时间 |
| `is_deleted` | TINYINT(1) | NOT NULL | `0` | 软删除 |

**索引：**

| 索引名 | 字段 | 类型 |
|--------|------|:---:|
| `idx_book_category` | `category_id` | NORMAL |
| `idx_book_title_author` | `title, author` | FULLTEXT（可选） |
| `idx_book_status_deleted` | `status, is_deleted` | NORMAL |

**外键说明：**

> `category_id` 在数据库层面可设为外键约束，也可仅在应用层维护。学习阶段建议**加外键**，便于理解数据完整性。

---

### C.4 cart（购物车表）

| 字段 | 类型 | 约束 | 默认值 | 说明 |
|------|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — | 购物车项 ID |
| `user_id` | BIGINT | NOT NULL, FK → user.id | — | 用户 ID |
| `book_id` | BIGINT | NOT NULL, FK → book.id | — | 图书 ID |
| `quantity` | INT | NOT NULL | `1` | 数量 |
| `created_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` | 加入时间 |
| `updated_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` ON UPDATE | 更新时间 |

**索引：**

| 索引名 | 字段 | 类型 |
|--------|------|:---:|
| `idx_cart_user` | `user_id` | NORMAL |
| `idx_cart_user_book` | `user_id, book_id` | **UNIQUE**（同一用户对同一图书只有一条记录，重复加入时更新数量） |

**业务约束说明：**

> `user_id + book_id` 联合唯一索引：同一用户对同一本书重复加入购物车时，执行 `UPDATE quantity = quantity + ?` 而非 `INSERT` 新记录。

---

### C.5 orders（订单表）

| 字段 | 类型 | 约束 | 默认值 | 说明 |
|------|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — | 订单 ID |
| `order_no` | VARCHAR(30) | NOT NULL, UNIQUE | — | 订单号（`INK` + 时间戳 + 随机数） |
| `user_id` | BIGINT | NOT NULL, FK → user.id | — | 下单用户 ID |
| `total_amount` | DECIMAL(10,2) | NOT NULL | — | 订单总金额 |
| `status` | VARCHAR(20) | NOT NULL | `'待发货'` | 订单状态：`待发货` / `已发货` / `已完成` / `已取消` |
| `receiver_name` | VARCHAR(50) | NOT NULL | — | 收货人姓名 |
| `receiver_phone` | VARCHAR(20) | NOT NULL | — | 收货人手机号 |
| `receiver_address` | VARCHAR(255) | NOT NULL | — | 收货地址 |
| `logistics_company` | VARCHAR(100) | NULL | NULL | 物流公司（发货时填写） |
| `logistics_no` | VARCHAR(100) | NULL | NULL | 物流运单号（发货时填写） |
| `created_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` | 下单时间 |
| `updated_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` ON UPDATE | 更新时间 |

**索引：**

| 索引名 | 字段 | 类型 |
|--------|------|:---:|
| `idx_orders_no` | `order_no` | UNIQUE |
| `idx_orders_user` | `user_id` | NORMAL |
| `idx_orders_status` | `status` | NORMAL |
| `idx_orders_user_status` | `user_id, status` | NORMAL |

**订单状态流转：**

```
待发货 ──→ 已发货 ──→ 已完成
  │           │
  └── 已取消   └──（不可取消）
```

---

### C.6 order_item（订单项表）

| 字段 | 类型 | 约束 | 默认值 | 说明 |
|------|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — | 订单项 ID |
| `order_id` | BIGINT | NOT NULL, FK → orders.id | — | 所属订单 ID |
| `book_id` | BIGINT | NOT NULL, FK → book.id | — | 图书 ID |
| `book_title` | VARCHAR(200) | NOT NULL | — | 图书书名快照（下单时的书名） |
| `book_cover` | VARCHAR(500) | NULL | NULL | 图书封面快照（下单时的封面 URL） |
| `price` | DECIMAL(10,2) | NOT NULL | — | 下单时的单价 |
| `quantity` | INT | NOT NULL | — | 购买数量 |
| `subtotal` | DECIMAL(10,2) | NOT NULL | — | 小计金额（price × quantity） |

> **设计说明**：`book_title`、`book_cover`、`price` 存储的是下单时的**快照**，而非关联实时查询。这样即使后续图书信息变更或删除，订单记录依然完整。

**索引：**

| 索引名 | 字段 | 类型 |
|--------|------|:---:|
| `idx_item_order` | `order_id` | NORMAL |
| `idx_item_book` | `book_id` | NORMAL |

---

### C.7 announcement（公告表）

| 字段 | 类型 | 约束 | 默认值 | 说明 |
|------|------|------|------|------|
| `id` | BIGINT | PK, AUTO_INCREMENT | — | 公告 ID |
| `title` | VARCHAR(200) | NOT NULL | — | 公告标题 |
| `content` | TEXT | NOT NULL | — | 公告内容 |
| `status` | TINYINT | NOT NULL | `0` | 状态：`1` 已发布 / `0` 草稿 |
| `created_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` | 创建时间 |
| `updated_at` | DATETIME | NOT NULL | `CURRENT_TIMESTAMP` ON UPDATE | 更新时间 |
| `is_deleted` | TINYINT(1) | NOT NULL | `0` | 软删除：`0` 正常 / `1` 已删除 |

**索引：**

| 索引名 | 字段 | 类型 |
|--------|------|:---:|
| `idx_announcement_status` | `status, is_deleted` | NORMAL |

> **说明**：公告表为独立表，无外键关联。前台仅展示 `status=1` 且 `is_deleted=0` 的公告，按创建时间倒序。

---

### C.8 多表联查对照表

> 以下列出开发过程中需要手写的主要多表联查 SQL 场景，用于指导 MyBatis XML Mapper 编写。

| 序号 | 场景 | SQL 关键字 | 涉及表 | 对应接口 |
|:---:|------|:---:|------|------|
| 1 | 后台图书列表（带分类名） | `LEFT JOIN` | book + category | `GET /api/admin/books` |
| 2 | 前台图书列表（带分类名，仅上架+启用分类） | `INNER JOIN` | book + category | `GET /api/books` |
| 3 | 购物车列表（带图书信息和用户信息） | `LEFT JOIN ×2` | cart + book + user | `GET /api/cart` |
| 4 | 订单详情（含订单项 + 图书快照） | `LEFT JOIN` | orders + order_item | `GET /api/orders/{id}` |
| 5 | 用户订单列表（含订单项数量统计） | `LEFT JOIN` + `COUNT` | orders + order_item | `GET /api/orders` |
| 6 | 管理员订单列表（带用户名） | `LEFT JOIN` | orders + user | `GET /api/admin/orders` |
| 7 | 后台分类列表（统计图书数） | `LEFT JOIN` + `COUNT` + `GROUP BY` | category + book | `GET /api/admin/categories` |
| 8 | 后台用户列表（统计订单数） | `LEFT JOIN` + `COUNT` + `GROUP BY` | user + orders | `GET /api/admin/users` |
| 9 | 仪表盘统计 | `COUNT` 聚合 | book + category + user + orders | `GET /api/admin/dashboard` |

---

### C.9 初始化数据建议

学习阶段建议手动插入测试数据，方便验证功能：

- **管理员账号**：`admin / admin123`（角色 `admin`，状态 `1`）
- **普通用户**：`test / test123`（角色 `user`，状态 `1`）
- **分类**：预置 5-8 个分类（计算机科学、文学小说、人文社科、经济管理、生活百科 等）
- **图书**：每个分类下 3-5 本，覆盖不同价格区间，库存 10-100
- **密码加密**：使用 BCryptPasswordEncoder 生成密文，不可存明文

---

> 接口文档版本：v1.2 | 共 44 个接口 | 7 张数据表 | 下一步：开始编码 🚀
