package com.lin101.convenience_store.ui.checkout

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import com.lin101.convenience_store.data.model.CartItem
import com.lin101.convenience_store.data.model.OrderModels
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application
    private val userPreferences = UserPreferences(context)

    // 购物模式（pickup / shipping）
    private val _shoppingMode = MutableStateFlow("pickup")
    val shoppingMode: StateFlow<String> = _shoppingMode.asStateFlow()

    // 当前门店 ID
    private val _storeId = MutableStateFlow<Int?>(null)
    val storeId: StateFlow<Int?> = _storeId.asStateFlow()

    // 当前门店名称
    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName.asStateFlow()

    // 用户收货地址
    private val _deliveryAddress = MutableStateFlow("")
    val deliveryAddress: StateFlow<String> = _deliveryAddress.asStateFlow()

    // 购物车商品列表
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // 商品小计（不含配送费）
    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()

    // 支付方式 code（wechat / alipay / apple_pay）
    private val _selectedPayment = MutableStateFlow("wechat")
    val selectedPayment: StateFlow<String> = _selectedPayment.asStateFlow()

    // UI 提示事件
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    init {
        // 监听购物模式变化
        viewModelScope.launch {
            userPreferences.shoppingModeFlow.collect { mode ->
                _shoppingMode.value = mode
            }
        }

        // 监听门店 ID 变化
        viewModelScope.launch {
            userPreferences.currentStoreIdFlow.collect { newStoreId ->
                _storeId.value = newStoreId
                loadStoreName(newStoreId)
                fetchCartList()
            }
        }

        // 监听门店名称变化
        viewModelScope.launch {
            userPreferences.currentStoreNameFlow.collect { name ->
                if (name.isNotEmpty()) _storeName.value = name
            }
        }

        // 监听用户收货地址变化
        viewModelScope.launch {
            userPreferences.userAddressFlow.collect { address ->
                _deliveryAddress.value = address
            }
        }

        // 初始加载购物车
        viewModelScope.launch {
            fetchCartList()
        }
    }

    private suspend fun loadStoreName(storeId: Int) {
        try {
            val storesResp = ApiClient.storeService.getStores()
            if (storesResp.code == 200 && storesResp.data != null) {
                val name = storesResp.data.find { it.storeId == storeId }?.storeName ?: ""
                if (name.isNotEmpty()) _storeName.value = name
            }
        } catch (e: Exception) {
            Log.e("CheckoutViewModel", "Failed to load store name", e)
        }
    }

    private fun fetchCartList() {
        viewModelScope.launch {
            try {
                val prefs = context.dataStore.data.first()
                val userId = prefs[UserPreferences.USER_ID_KEY] ?: return@launch

                val response = ApiClient.storeService.getCartList(userId)
                if (response.code == 200 && response.data != null) {
                    _cartItems.value = response.data
                    calculateSubtotal()
                } else {
                    Log.e("CheckoutViewModel", "getCartList failed: code=${response.code}, message=${response.message}")
                    _cartItems.value = emptyList()
                    _subtotal.value = 0.0
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _cartItems.value = emptyList()
                _subtotal.value = 0.0
            }
        }
    }

    private fun calculateSubtotal() {
        val total = _cartItems.value.sumOf { it.price * it.quantity }
        _subtotal.value = total
    }

    fun selectPayment(code: String) {
        _selectedPayment.value = code
    }

    fun submitOrder(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val prefs = context.dataStore.data.first()
                val userId = prefs[UserPreferences.USER_ID_KEY] ?: return@launch
                val currentAddress = _deliveryAddress.value
                val currentMode = _shoppingMode.value
                val currentStoreId = _storeId.value
                val currentPaymentCode = _selectedPayment.value

                // 外卖模式且地址为空时拦截
                if (currentMode == "shipping" && currentAddress.isEmpty()) {
                    _uiEvent.emit("Please add a delivery address in Profile first!")
                    return@launch
                }

                val req = OrderModels.OrderSubmitReq(
                    userId = userId,
                    storeId = if (currentMode == "pickup") currentStoreId else null,
                    orderType = currentMode,
                    paymentMethod = currentPaymentCode,   // 传递 code，如 "wechat"
                    deliveryAddress = if (currentMode == "pickup") null else currentAddress,
                    deliveryFee = if (currentMode == "pickup") 0.0 else 1.50
                )

                val response = ApiClient.storeService.submitOrder(req)
                if (response.code == 200) {
                    _uiEvent.emit("Success! Order No: ${response.data}")
                    // 下单成功，清空本地购物车
                    _cartItems.value = emptyList()
                    _subtotal.value = 0.0
                    onSuccess()
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