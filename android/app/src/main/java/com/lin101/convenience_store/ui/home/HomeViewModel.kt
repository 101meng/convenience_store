package com.lin101.convenience_store.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import com.lin101.convenience_store.data.model.Banner
import com.lin101.convenience_store.data.model.Product
import com.lin101.convenience_store.data.model.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * 首页 ViewModel
 * 增加了购物车角标数量的拉取逻辑
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val context = application

    val shoppingMode: StateFlow<String> = userPreferences.shoppingModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "pickup")

    val currentLocationName: StateFlow<String> = userPreferences.currentLocationNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Market Street Flagship")

    val userAddress: StateFlow<String> = userPreferences.userAddressFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    // 【新增】：购物车商品总数状态流
    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _flashSales = MutableStateFlow<List<Product>>(emptyList())
    val flashSales: StateFlow<List<Product>> = _flashSales.asStateFlow()

    private val _newArrivals = MutableStateFlow<List<Product>>(emptyList())
    val newArrivals: StateFlow<List<Product>> = _newArrivals.asStateFlow()

    init {
        fetchHomeData()
        fetchCartCount()
        fetchStores()
    }
    // 新增门店状态流
    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()

    // 在 fetchHomeData() 或 init 中调用
    private fun fetchStores() {
        viewModelScope.launch {
            try {
                val response = ApiClient.storeService.getStores()
                if (response.code == 200 && response.data != null) {
                    _stores.value = response.data
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "获取门店失败", e)
            }
        }
    }
    /**
     * 【新增】：从后端拉取真实的购物车商品总数
     */
    fun fetchCartCount() {
        viewModelScope.launch {
            try {
                // 从本地读取当前用户的 ID
                val prefs = context.dataStore.data.first()
                val userId = prefs[UserPreferences.USER_ID_KEY] ?: return@launch

                // 调用购物车列表接口
                val response = ApiClient.storeService.getCartList(userId)
                if (response.code == 200 && response.data != null) {
                    // 计算所有商品的总件数 (quantity 之和)
                    val total = response.data.sumOf { it.quantity }
                    _cartItemCount.value = total
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to fetch cart count", e)
            }
        }
    }

    fun updateDeliveryMode(mode: String, locationName: String) {
        viewModelScope.launch {
            userPreferences.updateShoppingMode(mode, locationName)
        }
    }

    private fun fetchHomeData() {
        viewModelScope.launch {
            try {
                val response = ApiClient.storeService.getHomeData()
                if (response.code.toInt() == 200 && response.data != null) {
                    _banners.value = response.data.banners ?: emptyList()
                    _flashSales.value = response.data.flashSales ?: emptyList()
                    _newArrivals.value = response.data.newArrivals ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to fetch home data", e)
            }
        }
    }
}