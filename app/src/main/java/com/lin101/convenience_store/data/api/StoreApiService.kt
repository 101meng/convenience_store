package com.lin101.convenience_store.data.api

import com.lin101.convenience_store.data.model.ApiResponse
import com.lin101.convenience_store.data.model.Category
import com.lin101.convenience_store.data.model.LoginRequest
import com.lin101.convenience_store.data.model.LoginResponse
import com.lin101.convenience_store.data.model.Product
import com.lin101.convenience_store.data.model.UpdateProfileRequest
import com.lin101.convenience_store.data.model.UpdateProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface StoreApiService {
    // 对应后端的 @GetMapping("/categories")
    @GET("api/categories")
    suspend fun getCategories(): List<Category>

    // 对应后端的 @GetMapping("/products")
    @GET("api/products")
    suspend fun getProducts(@Query("categoryId") categoryId: Int? = null): List<Product>

    // 发送验证码
    @GET("api/auth/sendCode")
    suspend fun sendCode(@Query("phone") phone: String): ApiResponse

    // 登录验证
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // --- 新增：用户相关接口 ---

    // 更新用户资料
    @POST("api/user/update")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): UpdateProfileResponse
}