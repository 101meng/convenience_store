package com.lin101.convenience_store.data.model

/**
 * 【新增文件】
 * 专门用来接收后端 Result<Map<String, Object>> 里面 data 层级的 token 和 user 信息
 */
data class AuthData(
    val token: String,
    val user: User
)