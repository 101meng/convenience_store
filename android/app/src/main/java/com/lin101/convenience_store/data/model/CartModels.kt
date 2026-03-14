package com.lin101.convenience_store.data.model

/**
 * 对应后端的 Result<T> 统一返回格式
 * 这样 Android 就可以无缝解析所有后端的接口了
 */
data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T? // data 可能是列表，也可能为空，所以用泛型 T?
)

/**
 * 对应后端的 CartVO (购物车视图对象)
 */
data class CartItem(
    val cartId: Int,
    val userId: Int,
    val productId: Int,
    val quantity: Int,
    val name: String,
    val price: Double,
    val imageUrl: String?
)

/**
 * 对应更新购物车数量的请求体
 */
data class CartUpdateReq(
    val cartId: Int,
    val quantity: Int
)

/**
 * 对应添加购物车的请求体
 */
data class CartAddReq(
    val userId: Int,
    val productId: Int,
    val quantity: Int
)