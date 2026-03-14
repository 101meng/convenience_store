package com.lin101.convenience_store.data.api

import com.lin101.convenience_store.data.model.*
import retrofit2.http.*

interface StoreApiService {

    // 【核心修改】：套上 BaseResponse 包装盒
    @GET("api/categories")
    suspend fun getCategories(): BaseResponse<List<Category>>

    @GET("api/products")
    suspend fun getProducts(@Query("categoryId") categoryId: Int? = null): BaseResponse<List<Product>>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") productId: Int): BaseResponse<Product>

    // ====== 下面的代码保持你原来的样子即可 ======
    @GET("api/auth/sendCode")
    suspend fun sendCode(@Query("phone") phone: String): ApiResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/user/update")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UpdateProfileResponse

    // 购物车部分 (之前已加)
    @GET("api/cart/list")
    suspend fun getCartList(@Query("userId") userId: Int): BaseResponse<List<CartItem>>

    @PUT("api/cart/update")
    suspend fun updateCartQuantity(@Body request: CartUpdateReq): BaseResponse<Any>

    @DELETE("api/cart/remove")
    suspend fun removeCartItem(@Query("cartId") cartId: Int): BaseResponse<Any>

    @POST("api/cart/add")
    suspend fun addToCart(@Body request: CartAddReq): BaseResponse<Any>

    // ================= 订单模块 =================
    @POST("api/order/submit")
    suspend fun submitOrder(@Body request: OrderModels.OrderSubmitReq): BaseResponse<String>
}