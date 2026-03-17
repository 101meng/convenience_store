## 1. 获取所有分类列表

- **接口路径 (URL)**: `/api/categories`

- **请求方式 (Method)**: `GET`

- **请求参数**: 无

- **成功响应 (JSON)**:

JSON

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

## 2. 根据分类 ID 获取商品列表

- **接口路径 (URL)**: `/api/products`

- **请求方式 (Method)**: `GET`

- **请求参数 (Query)**: `categoryId` (例如：`/api/products?categoryId=1`)

- **成功响应 (JSON)**:

JSON

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

## 3. 发送验证码

- **接口路径 (URL)**: `/api/auth/sendCode`

- **请求方式 (Method)**: `GET`

- **请求参数 (Query)**: `phone` (例如：`/api/auth/sendCode?phone=15839816471`)

- **成功响应 (JSON)**:

JSON

```
{
  "code": 200,
  "message": "验证码发送成功"
}
```

## 4. 登录与静默注册

- **接口路径 (URL)**: `/api/auth/login`

- **请求方式 (Method)**: `POST`

- **请求头 (Headers)**: `Content-Type: application/json`

- **请求参数 (Body)**:

JSON

```
{
  "phone": "15839816471",
  "code": "123456"
}
```

- **成功响应 (JSON)**:

JSON

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

## 5. 更新用户个人资料

- **接口路径 (URL)**: `/api/user/update`

- **请求方式 (Method)**: `POST`

- **请求头 (Headers)**: `Content-Type: application/json`

- **请求参数 (Body)**:

JSON

```
{
  "phone": "15839816471",
  "nickname": "Alex Johnson",
  "address": "123 Convenience St, Apt 4B, Metro City, 10001"
}
```

- **成功响应 (JSON)**:

JSON

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

---

## 6. 获取商品详情

- **接口路径 (URL)**: `/api/products/{id}`

- **请求方式 (Method)**: `GET`

- **请求参数 (Path)**: `id` (例如：`/api/products/1`)

- **成功响应 (JSON)**:

JSON

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

## 7. 获取首页聚合数据

- **接口路径 (URL)**: `/api/home/index`

- **请求方式 (Method)**: `GET`

- **请求参数**: 无

- **成功响应 (JSON)**:

JSON

```
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "banners": [
      {
        "id": 1,
        "imageUrl": "https://...",
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

## 8. 加入购物车

- **接口路径 (URL)**: `/api/cart/add`

- **请求方式 (Method)**: `POST`

- **请求参数 (Body)**:

JSON

```
{
  "userId": 1,
  "productId": 2,
  "quantity": 1
}
```

- **成功响应 (JSON)**:

JSON

```
{
  "code": 200,
  "message": "已成功加入购物车",
  "data": null
}
```

## 9. 获取购物车列表

- **接口路径 (URL)**: `/api/cart/list`

- **请求方式 (Method)**: `GET`

- **请求参数 (Query)**: `userId` (例如：`/api/cart/list?userId=1`)

- **成功响应 (JSON)**:

JSON

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

## 10. 更新购物车商品数量

- **接口路径 (URL)**: `/api/cart/update`

- **请求方式 (Method)**: `PUT`

- **请求参数 (Body)**:

JSON

```
{
  "cartId": 1,
  "quantity": 3
}
```

- **成功响应 (JSON)**:

JSON

```
{
  "code": 200,
  "message": "商品数量已更新",
  "data": null
}
```

## 11. 移除购物车商品

- **接口路径 (URL)**: `/api/cart/remove`

- **请求方式 (Method)**: `DELETE`

- **请求参数 (Query)**: `cartId` (例如：`/api/cart/remove?cartId=1`)

- **成功响应 (JSON)**:

JSON

```
{
  "code": 200,
  "message": "商品已移出购物车",
  "data": null
}
```

## 12. 提交订单

- **接口路径 (URL)**: `/api/order/submit`

- **请求方式 (Method)**: `POST`

- **请求参数 (Body)**:

JSON

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

- **成功响应 (JSON)**:

JSON

```
{
  "code": 200,
  "message": "订单提交成功，即将跳转",
  "data": "ORD-2026-A1B2C3D4"
}
```

## 13. 获取历史订单列表

- **接口路径 (URL)**: `/api/order/list`

- **请求方式 (Method)**: `GET`

- **请求参数 (Query)**: `userId` (例如：`/api/order/list?userId=1`)

- **成功响应 (JSON)**:

JSON

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
