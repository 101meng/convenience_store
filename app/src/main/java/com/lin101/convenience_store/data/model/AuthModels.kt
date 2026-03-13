package com.lin101.convenience_store.data.model

// 对应后端的 User 实体类
data class User(
    val userId: Int,
    val phone: String,
    val nickname: String,
    val avatarUrl: String,
    val balance: Double,
    val address: String?
)

// 对应发送验证码的返回格式
data class ApiResponse(
    val code: Int,
    val message: String
)

// 对应登录请求的参数格式
data class LoginRequest(
    val phone: String,
    val code: String
)

// 对应登录成功的返回格式
data class LoginResponse(
    val code: Int,
    val message: String,
    val token: String?,
    val user: User?
)

// 更新资料的请求体
data class UpdateProfileRequest(
    val phone: String,
    val nickname: String,
    val address: String
)

// 更新资料的响应体
data class UpdateProfileResponse(
    val code: Int,
    val message: String,
    val user: User? // 复用你之前写好的 User 数据类
)