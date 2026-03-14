package com.lin101.convenience_store.ui.checkout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import com.lin101.convenience_store.data.model.OrderModels
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application

    // 状态管理
    private val _isPickup = MutableStateFlow(false) // 默认配送 (false)
    val isPickup: StateFlow<Boolean> = _isPickup.asStateFlow()

    private val _selectedStoreId = MutableStateFlow(1) // 默认门店ID为1
    val selectedStoreId: StateFlow<Int> = _selectedStoreId.asStateFlow()

    private val _selectedPayment = MutableStateFlow("WeChat Pay") // 默认微信支付
    val selectedPayment: StateFlow<String> = _selectedPayment.asStateFlow()

    // 结算总价 (此处演示固定值，实际可再请求一次购物车获取)
    private val _subtotal = MutableStateFlow(32.40)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()

    // 提示信息流
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    // 状态更新方法
    fun setPickup(isPickup: Boolean) { _isPickup.value = isPickup }
    fun setStore(storeId: Int) { _selectedStoreId.value = storeId }
    fun setPayment(payment: String) { _selectedPayment.value = payment }

    // 执行下单网络请求
    fun submitOrder(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // 1. 从 DataStore 读取用户 ID 和地址
                val prefs = context.dataStore.data.first()
                val userId = prefs[UserPreferences.USER_ID_KEY] ?: return@launch
                var address = prefs[UserPreferences.USER_ADDRESS_KEY]
                if (address.isNullOrEmpty()) address = "Central Park West, NY 10025" // 默认占位地址

                // 2. 组装发给后端的请求体
                val req = OrderModels.OrderSubmitReq(
                    userId = userId,
                    storeId = if (_isPickup.value) _selectedStoreId.value else null,
                    orderType = if (_isPickup.value) "pickup" else "shipping",
                    paymentMethod = _selectedPayment.value,
                    deliveryAddress = if (_isPickup.value) null else address,
                    deliveryFee = if (_isPickup.value) 0.0 else 1.50
                )

                // 3. 发送真实请求
                val response = ApiClient.storeService.submitOrder(req)

                if (response.code == 200) {
                    _uiEvent.emit("Success! Order No: ${response.data}")
                    onSuccess() // 触发成功回调
                } else {
                    _uiEvent.emit(response.message ?: "Failed to place order")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit("Network Error, please try again.")
            }
        }
    }
}