package com.lin101.convenience_store.data.api

import com.lin101.convenience_store.data.model.ApiResponse
import com.lin101.convenience_store.data.model.AuthData
import com.lin101.convenience_store.data.model.BaseResponse
import com.lin101.convenience_store.data.model.CartAddReq
import com.lin101.convenience_store.data.model.CartItem
import com.lin101.convenience_store.data.model.CartUpdateReq
import com.lin101.convenience_store.data.model.Category
import com.lin101.convenience_store.data.model.HomeData
import com.lin101.convenience_store.data.model.LoginRequest
import com.lin101.convenience_store.data.model.OrderModels
import com.lin101.convenience_store.data.model.Product
import com.lin101.convenience_store.data.model.UpdateProfileRequest
import com.lin101.convenience_store.data.model.UpdateProfileResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreApiService {

    @GET("api/categories")
    suspend fun getCategories(): BaseResponse<List<Category>>

    @GET("api/products")
    suspend fun getProducts(@Query("categoryId") categoryId: Int? = null): BaseResponse<List<Product>>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") productId: Int): BaseResponse<Product>

    @GET("api/auth/sendCode")
    suspend fun sendCode(@Query("phone") phone: String): ApiResponse

    // 【修改点】：使用 BaseResponse 包裹刚才新建的 AuthData
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<AuthData>

    @POST("api/user/update")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UpdateProfileResponse

    // 购物车部分
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

    @GET("api/order/list")
    suspend fun getOrderList(@Query("userId") userId: Int): BaseResponse<List<OrderModels.OrderVO>>

    @GET("api/home/index")
    suspend fun getHomeData(): BaseResponse<HomeData>
}