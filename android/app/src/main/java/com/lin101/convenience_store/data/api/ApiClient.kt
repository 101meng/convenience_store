package com.lin101.convenience_store.data.api

import android.content.Context
import com.lin101.convenience_store.data.local.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 模拟器访问本地后端的专属 IP
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // 预留一个变量，用于接管 Context
    private var userPreferences: UserPreferences? = null

    // 【新增】：供入口程序调用，把大环境 Context 传进来
    fun init(context: Context) {
        userPreferences = UserPreferences(context.applicationContext)
    }

    // 【核心改造】：打造一个带“拦截器”的网络客户端
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                // 1. 拿到原始的请求
                val originalRequest = chain.request()

                // 2. 阻塞式地从 DataStore 中瞬间读取当前的 Token
                val token = runBlocking {
                    userPreferences?.tokenFlow?.first()
                }

                // 3. 开始改造请求头
                val requestBuilder = originalRequest.newBuilder()
                if (!token.isNullOrEmpty()) {
                    // 标准的 JWT 携带格式：Bearer(空格)你的Token
                    requestBuilder.header("Authorization", "Bearer $token")
                }

                // 4. 把加了 Token 的请求发射出去！
                chain.proceed(requestBuilder.build())
            }
            .build()
    }

    // 把改造好的 okHttpClient 喂给 Retrofit
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // 使用自定义的 Client
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val storeService: StoreApiService by lazy {
        retrofit.create(StoreApiService::class.java)
    }
}