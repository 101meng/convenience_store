package com.lin101.convenience_store.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.model.Banner
import com.lin101.convenience_store.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    // 1. 轮播图状态流
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    // 2. 秒杀商品状态流
    private val _flashSales = MutableStateFlow<List<Product>>(emptyList())
    val flashSales: StateFlow<List<Product>> = _flashSales.asStateFlow()

    // 3. 新品推荐状态流
    private val _newArrivals = MutableStateFlow<List<Product>>(emptyList())
    val newArrivals: StateFlow<List<Product>> = _newArrivals.asStateFlow()

    init {
        // ViewModel 初始化时，立刻向服务器发起请求
        fetchHomeData()
    }

    private fun fetchHomeData() {
        viewModelScope.launch {
            try {
                // 向后端的 /api/home/index 接口发起一次性聚合请求
                val response = ApiClient.storeService.getHomeData()

                // 校验请求成功 (200) 且数据不为空
                if (response.code.toInt() == 200 && response.data != null) {
                    // 将后端返回的 JSON 数据分别塞进三个不同的状态流中
                    // Compose 监听到这三个流的变化，就会瞬间点亮屏幕上对应的三个 UI 模块
                    // 使用 ?: emptyList() 进行绝对的空指针防御
                    _banners.value = response.data.banners ?: emptyList()
                    _flashSales.value = response.data.flashSales ?: emptyList()
                    _newArrivals.value = response.data.newArrivals ?: emptyList()
                }
            } catch (e: Exception) {
                // 如果后端没开、或者网络断了，在这里捕获异常，防止 App 崩溃
                Log.e("HomeViewModel", "Failed to fetch home data from server", e)
            }
        }
    }
}