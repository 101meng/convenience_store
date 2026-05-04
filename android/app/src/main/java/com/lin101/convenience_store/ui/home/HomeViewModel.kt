package com.lin101.convenience_store.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.model.Banner
import com.lin101.convenience_store.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    val shoppingMode: StateFlow<String> = userPreferences.shoppingModeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "pickup")

    val currentLocationName: StateFlow<String> = userPreferences.currentLocationNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Market Street Flagship")

    // 【新增】：获取真实的用户收货地址
    val userAddress: StateFlow<String> = userPreferences.userAddressFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _banners = MutableStateFlow<List<Banner>>(emptyList())
    val banners: StateFlow<List<Banner>> = _banners.asStateFlow()

    private val _flashSales = MutableStateFlow<List<Product>>(emptyList())
    val flashSales: StateFlow<List<Product>> = _flashSales.asStateFlow()

    private val _newArrivals = MutableStateFlow<List<Product>>(emptyList())
    val newArrivals: StateFlow<List<Product>> = _newArrivals.asStateFlow()

    init {
        fetchHomeData()
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
                    android.util.Log.d("BannerDebug", "首个Banner的URL是: ${_banners.value.firstOrNull()?.imageUrl}")
                    _flashSales.value = response.data.flashSales ?: emptyList()
                    _newArrivals.value = response.data.newArrivals ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Failed to fetch home data from server", e)
            }
        }
    }
}