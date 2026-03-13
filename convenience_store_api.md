## 1. 获取所有分类列表

- **接口路径 (URL)**: `/api/categories`
- **请求方式 (Method)**: `GET`
- **请求参数**: 无
- **成功响应 (JSON)**:

json

```
[
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
```

## 2. 根据分类 ID 获取商品列表

- **接口路径 (URL)**: `/api/products`
- **请求方式 (Method)**: `GET`
- **请求参数 (Query)**: `categoryId` (例如：`/api/products?categoryId=1`)
- **成功响应 (JSON)**:

json

```
[
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
```

## 3. 发送验证码

- **接口路径 (URL)**: `/api/auth/sendCode`
- **请求方式 (Method)**: `GET`
- **请求参数 (Query)**: `phone` (例如：`/api/auth/sendCode?phone=15839816471`)
- **成功响应 (JSON)**:

json

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

json

```
{
  "phone": "15839816471",
  "code": "123456"
}
```

- **成功响应 (JSON)**:

json

```
{
  "code": 200,
  "message": "登录成功",
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
```

## 5. 更新用户个人资料

- **接口路径 (URL)**: `/api/user/update`
- **请求方式 (Method)**: `POST`
- **请求头 (Headers)**: `Content-Type: application/json`
- **请求参数 (Body)**:

json

```
{
  "phone": "15839816471",
  "nickname": "Alex Johnson",
  "address": "123 Convenience St, Apt 4B, Metro City, 10001"
}
```
