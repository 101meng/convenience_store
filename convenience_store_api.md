## 一、用户端 API

### 1. 基础商城接口

#### 1.1 获取所有分类列表

- **接口名称**：获取商品分类
- **接口路径**：`/api/categories`
- **请求方式**：`GET`
- **请求参数**：无
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "categoryId": 1,
      "categoryName": "Fresh",
      "iconUrl": "icon_fresh.png"
    },
    {
      "categoryId": 2,
      "categoryName": "Snacks",
      "iconUrl": "icon_snacks.png"
    }
  ]
}
```

#### 1.2 根据分类 ID 获取商品列表

- **接口名称**：分类商品查询
- **接口路径**：`/api/products`
- **请求方式**：`GET`
- **请求参数 (Query)**：`categoryId`（可选，不传则返回所有商品）
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "productId": 1,
      "categoryId": 1,
      "name": "Zesty Avocado & Quinoa Bowl",
      "price": 12.50,
      "imageUrl": "bowl.jpg",
      "tag1": "Vegan",
      "tag2": "Gluten-Free",
      "tag3": "Organic"
    }
  ]
}
```

#### 1.3 获取商品详情

- **接口名称**：商品详情查询
- **接口路径**：`/api/products/{id}`
- **请求方式**：`GET`
- **请求参数 (Path)**：`id`（商品 ID）
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "productId": 1,
    "categoryId": 1,
    "name": "Zesty Avocado & Quinoa Bowl",
    "description": "A nutrient-packed powerhouse...",
    "price": 12.50,
    "originalPrice": 15.00,
    "isFlashSale": 0,
    "imageUrl": "bowl.jpg",
    "unit": "1份",
    "tag1": "Vegan"
  }
}
```

### 2. 认证接口

#### 2.1 发送验证码

- **接口名称**：发送登录验证码
- **接口路径**：`/api/auth/sendCode`
- **请求方式**：`GET`
- **请求参数 (Query)**：`phone`（手机号）
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "验证码发送成功",
  "data": null
}
```

- **失败响应 (JSON)**：

json

```
{
  "code": 500,
  "message": "验证码发送失败",
  "data": null
}
```

#### 2.2 登录与静默注册

- **接口名称**：手机号验证码登录（自动注册）
- **接口路径**：`/api/auth/login`
- **请求方式**：`POST`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "phone": "15839816471",
  "code": "123456"
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwicGhvbmUiOi...",
    "user": {
      "userId": 1,
      "phone": "15839816471",
      "nickname": "User_6471",
      "avatarUrl": "https://ui-avatars.com/api/?name=U&background=random",
      "balance": 0.00,
      "address": null
    }
  }
}
```

### 3. 购物车接口

#### 3.1 加入购物车

- **接口名称**：添加商品到购物车
- **接口路径**：`/api/cart/add`
- **请求方式**：`POST`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "userId": 1,
  "productId": 2,
  "quantity": 1
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "已成功加入购物车",
  "data": null
}
```

#### 3.2 获取购物车列表

- **接口名称**：用户购物车查询
- **接口路径**：`/api/cart/list`
- **请求方式**：`GET`
- **请求参数 (Query)**：`userId`（用户 ID）
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "cartId": 1,
      "productId": 2,
      "name": "Artisanal Cold Brew",
      "price": 4.50,
      "imageUrl": "coffee.jpg",
      "quantity": 2
    }
  ]
}
```

#### 3.3 更新购物车商品数量

- **接口名称**：修改购物车商品数量
- **接口路径**：`/api/cart/update`
- **请求方式**：`PUT`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "cartId": 1,
  "quantity": 3
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "商品数量已更新",
  "data": null
}
```

#### 3.4 移除购物车商品

- **接口名称**：删除购物车商品
- **接口路径**：`/api/cart/remove`
- **请求方式**：`DELETE`
- **请求参数 (Query)**：`cartId`（购物车项 ID）
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "商品已移出购物车",
  "data": null
}
```

### 4. 订单接口

#### 4.1 提交订单

- **接口名称**：创建订单
- **接口路径**：`/api/order/submit`
- **请求方式**：`POST`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "userId": 1,
  "storeId": null,
  "orderType": "shipping",
  "paymentMethod": "WeChat Pay",
  "deliveryAddress": "Central Park West, NY 10025",
  "deliveryFee": 1.50
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "订单提交成功，即将跳转",
  "data": "ORD-2026-A1B2C3D4"
}
```

- **失败响应 (JSON)**（购物车为空）：

json

```
{
  "code": 500,
  "message": "购物车为空",
  "data": null
}
```

#### 4.2 获取历史订单列表

- **接口名称**：用户订单查询
- **接口路径**：`/api/order/list`
- **请求方式**：`GET`
- **请求参数 (Query)**：`userId`（用户 ID）
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "orderId": 1,
      "orderSn": "ORD-2023-084",
      "actualAmount": 32.40,
      "status": "COMPLETED",
      "orderType": "shipping",
      "createdAt": "15 Oct 2026, 10:30 AM",
      "items": [
        {
          "productId": 2,
          "quantity": 1,
          "imageUrl": "coffee.jpg"
        }
      ]
    }
  ]
}
```

### 5. 用户信息接口

#### 5.1 更新用户个人资料

- **接口名称**：修改用户信息
- **接口路径**：`/api/user/update`
- **请求方式**：`POST`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "phone": "15839816471",
  "nickname": "Alex Johnson",
  "address": "123 Convenience St, Apt 4B, Metro City, 10001"
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "个人资料更新成功",
  "data": {
    "phone": "15839816471",
    "nickname": "Alex Johnson",
    "address": "123 Convenience St, Apt 4B, Metro City, 10001"
  }
}
```

### 6. AI 智能服务接口

#### 6.1 AI 商品推荐

- **接口名称**：AI 场景化商品推荐
- **接口路径**：`/api/ai/planner`
- **请求方式**：`POST`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "prompt": "推荐适合野餐的健康食品组合"
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "reply": "野餐推荐：牛油果藜麦碗（素食）+ 手工冷萃咖啡 + 抹茶提拉米苏...",
    "products": [
      {
        "productId": 1,
        "name": "Zesty Avocado & Quinoa Bowl",
        "price": 12.50
      }
    ]
  }
}
```

#### 6.2 AI 饮食营养分析

- **接口名称**：购物车商品营养分析
- **接口路径**：`/api/ai/dietitian`
- **请求方式**：`POST`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "cartItems": [
    {
      "productId": 1,
      "name": "Zesty Avocado & Quinoa Bowl",
      "quantity": 1
    }
  ]
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "calorie": 320,
    "protein": 12.5,
    "carbs": 45.2,
    "fat": 10.1,
    "suggestion": "该组合膳食纤维充足，可搭配低糖饮品更佳"
  }
}
```

### 7. 首页聚合接口

#### 7.1 获取首页数据

- **接口名称**：首页聚合数据查询
- **接口路径**：`/api/home/index`
- **请求方式**：`GET`
- **请求参数**：无
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "banners": [
      {
        "id": 1,
        "imageUrl": "https://xxx.com/banner1.jpg",
        "linkUrl": "category/1"
      }
    ],
    "flashSales": [
      {
        "productId": 2,
        "name": "Artisanal Cold Brew",
        "price": 4.50,
        "originalPrice": 9.00,
        "isFlashSale": 1,
        "flashSaleEndTime": "2026-03-18T20:41:32"
      }
    ],
    "newArrivals": [
      {
        "productId": 29,
        "name": "Matcha Tiramisu",
        "price": 6.79
      }
    ]
  }
}
```

## 二、管理员端 API

### 1. 仪表盘（Dashboard）接口

#### 1.1 获取仪表盘统计数据

- **接口名称**：仪表盘核心统计
- **接口路径**：`/api/admin/dashboard/stats`
- **请求方式**：`GET`
- **请求参数**：无
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalRevenue": "12589.60",
    "revenueGrowth": 12.5,
    "totalOrders": 896,
    "ordersGrowth": 8.2,
    "newUsers": 521,
    "activeBanners": 4
  }
}
```

#### 1.2 获取仪表盘图表数据

- **接口名称**：仪表盘趋势图表
- **接口路径**：`/api/admin/dashboard/charts`
- **请求方式**：`GET`
- **请求参数 (Query)**：`days`（可选，默认 7，支持 7/30）
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "revenueTrend": {
      "dates": ["03-10", "03-11", "03-12"],
      "revenues": [1258.30, 1896.50, 1569.80]
    },
    "salesDistribution": [
      {
        "name": "Fresh",
        "value": 120
      },
      {
        "name": "Snacks",
        "value": 89
      }
    ]
  }
}
```

### 2. 轮播图（Banner）接口

#### 2.1 获取所有轮播图

- **接口名称**：轮播图列表查询
- **接口路径**：`/api/admin/banners`
- **请求方式**：`GET`
- **请求参数**：无
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "imageUrl": "https://xxx.com/banner1.jpg",
      "linkUrl": "category/1",
      "isActive": 1,
      "sort": 1
    }
  ]
}
```

#### 2.2 添加轮播图

- **接口名称**：新增轮播图
- **接口路径**：`/api/admin/banners`
- **请求方式**：`POST`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "imageUrl": "https://xxx.com/banner2.jpg",
  "linkUrl": "category/2",
  "isActive": 1,
  "sort": 2
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

#### 2.3 修改轮播图

- **接口名称**：更新轮播图
- **接口路径**：`/api/admin/banners`
- **请求方式**：`PUT`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "id": 1,
  "imageUrl": "https://xxx.com/banner1_new.jpg",
  "linkUrl": "category/1",
  "isActive": 1,
  "sort": 1
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

#### 2.4 修改轮播图状态

- **接口名称**：更新轮播图激活状态
- **接口路径**：`/api/admin/banners/status`
- **请求方式**：`PUT`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "id": 1,
  "isActive": 0
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

#### 2.5 删除轮播图

- **接口名称**：删除轮播图
- **接口路径**：`/api/admin/banners/{id}`
- **请求方式**：`DELETE`
- **请求参数 (Path)**：`id`（轮播图 ID）
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 3. 商品管理接口

#### 3.1 分页查询商品

- **接口名称**：商品分页查询

- **接口路径**：`/api/admin/products`

- **请求方式**：`GET`

- **请求参数 (Query)**：
  
  - `current`：当前页（默认 1）
  - `size`：页大小（默认 10）
  - `categoryId`：分类 ID（可选）
  - `keyword`：关键词（名称 / 描述，可选）

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "productId": 1,
        "categoryId": 1,
        "name": "Zesty Avocado & Quinoa Bowl",
        "price": 12.50,
        "imageUrl": "bowl.jpg"
      }
    ],
    "total": 120,
    "current": 1,
    "size": 10
  }
}
```

#### 3.2 添加商品

- **接口名称**：新增商品
- **接口路径**：`/api/admin/products`
- **请求方式**：`POST`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "categoryId": 1,
  "name": "New Product",
  "description": "Product Description",
  "price": 9.90,
  "originalPrice": 12.90,
  "isFlashSale": 0,
  "imageUrl": "new_product.jpg",
  "unit": "1份",
  "tag1": "Healthy"
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

#### 3.3 修改商品

- **接口名称**：更新商品
- **接口路径**：`/api/admin/products`
- **请求方式**：`PUT`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "productId": 1,
  "categoryId": 1,
  "name": "Updated Product Name",
  "price": 10.90,
  "imageUrl": "updated.jpg"
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

#### 3.4 删除商品

- **接口名称**：删除商品
- **接口路径**：`/api/admin/products/{id}`
- **请求方式**：`DELETE`
- **请求参数 (Path)**：`id`（商品 ID）
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 4. 用户管理接口

#### 4.1 分页查询用户

- **接口名称**：用户分页查询

- **接口路径**：`/api/admin/users`

- **请求方式**：`GET`

- **请求参数 (Query)**：
  
  - `current`：当前页（默认 1）
  - `size`：页大小（默认 10）
  - `keyword`：关键词（昵称 / 手机号，可选）

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "userId": 1,
        "phone": "15839816471",
        "nickname": "Alex Johnson",
        "avatarUrl": "https://ui-avatars.com/api/?name=A&background=random",
        "balance": 100.00,
        "address": "123 Convenience St"
      }
    ],
    "total": 521,
    "current": 1,
    "size": 10
  }
}
```

#### 4.2 添加用户

- **接口名称**：新增用户
- **接口路径**：`/api/admin/users`
- **请求方式**：`POST`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "phone": "13800138000",
  "nickname": "New User",
  "balance": 50.00,
  "address": "456 Main St"
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

#### 4.3 给用户充值

- **接口名称**：用户余额充值

- **接口路径**：`/api/admin/users/{id}/recharge`

- **请求方式**：`PUT`

- **请求头**：`Content-Type: application/json`

- **请求参数**：
  
  - Path：`id`（用户 ID）
  - Body：

json

```
{
  "amount": 100.00
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 5. 订单管理接口

#### 5.1 分页查询订单

- **接口名称**：订单分页查询

- **接口路径**：`/api/admin/orders`

- **请求方式**：`GET`

- **请求参数 (Query)**：
  
  - `current`：当前页（默认 1）
  - `size`：页大小（默认 10）
  - `status`：订单状态（pending/delivering/completed，可选，All 则查全部）
  - `startDate`：开始日期（可选，格式：yyyy-MM-dd）
  - `endDate`：结束日期（可选，格式：yyyy-MM-dd）

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "orderId": 1,
        "orderSn": "ORD-2026-A1B2C3D4",
        "actualAmount": 32.40,
        "status": "COMPLETED",
        "orderType": "shipping",
        "deliveryAddress": "Central Park West, NY 10025",
        "createdAt": "15 Oct 2026, 10:30 AM",
        "items": [
          {
            "productId": 2,
            "quantity": 1,
            "imageUrl": "coffee.jpg"
          }
        ]
      }
    ],
    "total": 896,
    "current": 1,
    "size": 10
  }
}
```

#### 5.2 处理订单（更新状态）

- **接口名称**：更新订单状态
- **接口路径**：`/api/admin/orders/{id}/process`
- **请求方式**：`PUT`
- **请求参数 (Path)**：`id`（订单 ID）
- **逻辑说明**：pending → delivering → completed
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

### 6. 分类管理接口

#### 6.1 添加分类

- **接口名称**：新增商品分类
- **接口路径**：`/api/admin/categories`
- **请求方式**：`POST`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "categoryName": "Drinks",
  "iconUrl": "icon_drinks.png"
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

#### 6.2 删除分类

- **接口名称**：删除商品分类
- **接口路径**：`/api/admin/categories/{id}`
- **请求方式**：`DELETE`
- **请求参数 (Path)**：`id`（分类 ID）
- **逻辑说明**：分类下有商品则删除失败
- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": null
}
```

- **失败响应 (JSON)**（分类有商品）：

json

```
{
  "code": 500,
  "message": "Category not empty",
  "data": null
}
```

### 7. 管理员 AI 聊天接口

#### 7.1 管理员 AI 助手

- **接口名称**：管理员 AI 智能问答
- **接口路径**：`/api/admin/ai/chat`
- **请求方式**：`POST`
- **请求头**：`Content-Type: application/json`
- **请求参数 (Body)**：

json

```
{
  "prompt": "分析近7天的销售数据，给出优化建议"
}
```

- **成功响应 (JSON)**：

json

```
{
  "code": 200,
  "message": "操作成功",
  "data": "近7天生鲜类销售增长12%，零食类下降5%，建议增加生鲜促销活动，优化零食品类结构..."
}
```
