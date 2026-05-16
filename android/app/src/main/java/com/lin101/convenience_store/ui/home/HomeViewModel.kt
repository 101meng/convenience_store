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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val context = application

    // 购物模式与位置名称（这里位置名称实际是门店名，由 UserPreferences 存储）
    val shoppingMode: StateFlow<String> = userPreferences.shoppingModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "pickup")

    // 当前门店名称（从 UserPreferences 读取，以保证持久化）
    val currentStoreName: StateFlow<String> = userPreferences.currentStoreNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Select Store")

    val userAddress: StateFlow<String> = userPreferences.userAddressFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    // 购物车数量
    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount: StateFlow<Int> = _cartItemCount.asStateFlow()

    // 首页数据
    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _flashSales = MutableStateFlow<List<Product>>(emptyList())
    val flashSales: StateFlow<List<Product>> = _flashSales.asStateFlow()

    private val _newArrivals = MutableStateFlow<List<Product>>(emptyList())
    val newArrivals: StateFlow<List<Product>> = _newArrivals.asStateFlow()

    init {
        fetchHomeData()
        fetchCartCount()
    }

    /**
     * 公开方法：刷新首页数据（当门店切换后调用）
     */
    fun fetchHomeData() {
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

    /**
     * 刷新购物车数量
     */
    fun fetchCartCount() {
        viewModelScope.launch {
            try {
                val prefs = context.dataStore.data.first()
                val userId = prefs[UserPreferences.USER_ID_KEY] ?: return@launch
                val response = ApiClient.storeService.getCartList(userId)
                if (response.code == 200 && response.data != null) {
                    val total = response.data.sumOf { it.quantity }
                    _cartItemCount.value = total
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to fetch cart count", e)
            }
        }
    }

    /**
     * 更新购物模式及当前门店名称（用于顶部显示）
     * 注意：门店 ID 的更新由 StoreViewModel 负责，这里只保存模式和相关显示名称
     */
    fun updateDeliveryMode(mode: String, storeName: String) {
        viewModelScope.launch {
            userPreferences.updateShoppingMode(mode, storeName)
        }
    }
}