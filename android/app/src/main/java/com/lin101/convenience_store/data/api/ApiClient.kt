package com.lin101.convenience_store.data.api

import android.content.Context
import com.lin101.convenience_store.data.local.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 模拟器访问本地后端的专属 IP
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // 用于 OkHttp 拦截器的协程作用域
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var cachedToken: String? = null

    @Volatile
    private var cachedStoreId: Int? = null

    private var userPreferences: UserPreferences? = null

    fun init(context: Context) {
        userPreferences = UserPreferences(context.applicationContext)
        val prefs = userPreferences!!
        // 后台协程：先读取初始值，再持续监听变化
        scope.launch {
            try {
                cachedToken = prefs.tokenFlow.first()
            } catch (_: Exception) {}
            try {
                prefs.tokenFlow.collect { cachedToken = it }
            } catch (_: Exception) {}
        }
        scope.launch {
            try {
                cachedStoreId = prefs.currentStoreIdFlow.first()
            } catch (_: Exception) {}
            try {
                prefs.currentStoreIdFlow.collect { cachedStoreId = it }
            } catch (_: Exception) {}
        }
    }

    fun syncUpdateStoreId(storeId: Int) {
        cachedStoreId = storeId
    }

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()

                cachedToken?.let {
                    requestBuilder.header("Authorization", "Bearer $it")
                }

                cachedStoreId?.let {
                    android.util.Log.d("ApiClient", "Adding X-Store-Id: $it")
                    requestBuilder.header("X-Store-Id", it.toString())
                } ?: run {
                    android.util.Log.d("ApiClient", "cachedStoreId is null, no X-Store-Id header added")
                }

                val request = requestBuilder.build()
                android.util.Log.d("ApiClient", "Request URL: ${request.url}, Headers: ${request.headers}")
                chain.proceed(request)
            }
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val storeService: StoreApiService by lazy {
        retrofit.create(StoreApiService::class.java)
    }
}
