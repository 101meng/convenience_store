package com.lin101.convenience_store.ui.cart

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import com.lin101.convenience_store.data.model.CartItem
import com.lin101.convenience_store.data.model.CartUpdateReq
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 购物车的 ViewModel
 * 负责与后端交互、管理购物车数据流、以及动态计算总价
 */

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val userPreferences = UserPreferences(context)

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    private val _storeId = MutableStateFlow<Int?>(null)
    val storeId: StateFlow<Int?> = _storeId.asStateFlow()

    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName.asStateFlow()

    private val _shoppingMode = MutableStateFlow("pickup")
    val shoppingMode: StateFlow<String> = _shoppingMode.asStateFlow()

    init {
        viewModelScope.launch {
            // 监听购物模式变化
            userPreferences.shoppingModeFlow.collect { mode ->
                _shoppingMode.value = mode
            }
        }

        viewModelScope.launch {
            // 监听门店 ID 变化
            userPreferences.currentStoreIdFlow.collect { newStoreId ->
                _storeId.value = newStoreId
                // 门店变化后，重新加载门店名称并刷新购物车列表
                loadStoreName(newStoreId)
                fetchCartList()
            }
        }
    }

    private suspend fun loadStoreName(storeId: Int) {
        try {
            val storesResp = ApiClient.storeService.getStores()
            if (storesResp.code == 200 && storesResp.data != null) {
                val name = storesResp.data.find { it.storeId == storeId }?.storeName ?: ""
                _storeName.value = name
            }
        } catch (e: Exception) {
            Log.e("CartViewModel", "Failed to load store name", e)
        }
    }

    fun fetchCartList() {
        viewModelScope.launch {
            try {
                val prefs = context.dataStore.data.first()
                val userId = prefs[UserPreferences.USER_ID_KEY] ?: return@launch
                val currentStoreId = _storeId.value ?: 1
                Log.d("CartViewModel", "fetchCartList: userId=$userId, storeId=$currentStoreId")

                val response = ApiClient.storeService.getCartList()
                if (response.code == 200 && response.data != null) {
                    _cartItems.value = response.data
                    calculateTotal()
                } else {
                    Log.e("CartViewModel", "getCartList failed: code=${response.code}, message=${response.message}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateQuantity(cartId: Int, newQuantity: Int) {
        if (newQuantity < 1) return

        viewModelScope.launch {
            try {
                val updatedList = _cartItems.value.map {
                    if (it.cartId == cartId) it.copy(quantity = newQuantity) else it
                }
                _cartItems.value = updatedList
                calculateTotal()

                ApiClient.storeService.updateCartQuantity(CartUpdateReq(cartId, newQuantity))
            } catch (e: Exception) {
                fetchCartList()
            }
        }
    }

    fun removeItem(cartId: Int) {
        viewModelScope.launch {
            try {
                _cartItems.value = _cartItems.value.filter { it.cartId != cartId }
                calculateTotal()
                ApiClient.storeService.removeCartItem(cartId)
            } catch (e: Exception) {
                fetchCartList()
            }
        }
    }

    private fun calculateTotal() {
        var total = 0.0
        for (item in _cartItems.value) {
            total += item.price * item.quantity
        }
        _totalPrice.value = total
    }
}
