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
import com.lin101.convenience_store.data.model.Store
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

    // ================= 鉴权模块 =================
    @GET("api/auth/sendCode")
    suspend fun sendCode(@Query("phone") phone: String): ApiResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<AuthData>

    @POST("api/user/update")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UpdateProfileResponse

    // ================= 商品与门店模块 =================
    @GET("api/categories")
    suspend fun getCategories(): BaseResponse<List<Category>>

    @GET("api/products")
    suspend fun getProducts(@Query("categoryId") categoryId: Int? = null): BaseResponse<List<Product>>

    @GET("api/products/{id}")
    suspend fun getProductById(@Path("id") id: Int): BaseResponse<Product>

    @GET("api/stores")
    suspend fun getStores(): BaseResponse<List<Store>>

    // ================= 购物车模块 =================
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

    // 【无需加 Header 参数，ApiClient 拦截器会自动加】
    @GET("api/home/index")
    suspend fun getHomeData(): BaseResponse<HomeData>

    @POST("api/order/pay")
    suspend fun payOrder(@Query("orderId") orderId: Int): BaseResponse<Any>

    @POST("api/order/receive")
    suspend fun receiveOrder(@Query("orderId") orderId: Int): BaseResponse<Any>

    // ================= AI 智能模块 =================
    @POST("api/ai/planner")
    suspend fun getAiRecommendation(@Body request: com.lin101.convenience_store.data.model.AiModels.AiPlannerReq): BaseResponse<com.lin101.convenience_store.data.model.AiModels.AiPlannerResp>

    @POST("api/ai/dietitian")
    suspend fun analyzeNutrition(@Body request: com.lin101.convenience_store.data.model.AiModels.AiDietitianReq): BaseResponse<com.lin101.convenience_store.data.model.AiModels.AiDietitianResp>
}