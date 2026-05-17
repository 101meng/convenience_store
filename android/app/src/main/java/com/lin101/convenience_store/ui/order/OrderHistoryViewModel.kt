package com.lin101.convenience_store.ui.order

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import com.lin101.convenience_store.data.model.OrderModels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OrderHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application

    private val _orders = MutableStateFlow<List<OrderModels.OrderVO>>(emptyList())
    val orders: StateFlow<List<OrderModels.OrderVO>> = _orders.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchOrders()
    }

    private fun fetchOrders() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val prefs = context.dataStore.data.first()
                if (prefs[UserPreferences.USER_ID_KEY] == null) {
                    _orders.value = emptyList()
                    return@launch
                }

                val response = ApiClient.storeService.getOrderList()
                if (response.code == 200 && response.data != null) {
                    _orders.value = response.data.map { order ->
                        order.copy(status = order.status?.lowercase())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun payOrder(orderId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.storeService.payOrder(orderId)
                if (response.code == 200) {
                    fetchOrders() // 支付成功，重新拉取最新订单状态
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun receiveOrder(orderId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.storeService.receiveOrder(orderId)
                if (response.code == 200) {
                    fetchOrders() // 收货成功，重新拉取最新订单状态
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
