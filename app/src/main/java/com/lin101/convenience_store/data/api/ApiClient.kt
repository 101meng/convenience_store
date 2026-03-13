package com.lin101.convenience_store.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    /**
     * 【极其重要】：
     * 在 Android 模拟器中，localhost (127.0.0.1) 指的是模拟器这台虚拟手机自己！
     * 要想访问你电脑上的后端 8080 端口，必须使用 Android 官方指定的特殊 IP：10.0.2.2
     * （如果你用的是真机调试，这里要换成你电脑连接 WiFi 的局域网 IPv4 地址，比如 192.168.1.100）
     */
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // 自动将 JSON 转换为 Data Class
        .build()

    // 暴露 Service 供 ViewModel 调用
    val storeService: StoreApiService = retrofit.create(StoreApiService::class.java)
}