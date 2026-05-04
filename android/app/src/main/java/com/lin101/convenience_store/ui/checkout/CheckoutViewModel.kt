package com.lin101.convenience_store.ui.checkout

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import com.lin101.convenience_store.data.model.OrderModels
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 结算页面的 ViewModel
 * 负责管理配送/自提状态、选中的门店、支付方式，以及动态获取地址和计算真实金额
 */
class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application

    // 状态管理：是否选择自提（默认 false，即配送）
    private val _isPickup = MutableStateFlow(false)
    val isPickup: StateFlow<Boolean> = _isPickup.asStateFlow()

    // 状态管理：当前选中的门店 ID（默认 1）
    private val _selectedStoreId = MutableStateFlow(1)
    val selectedStoreId: StateFlow<Int> = _selectedStoreId.asStateFlow()

    // 状态管理：当前选中的支付方式（默认微信支付）
    private val _selectedPayment = MutableStateFlow("WeChat Pay")
    val selectedPayment: StateFlow<String> = _selectedPayment.asStateFlow()

    // 结算总价（动态获取的真实数据流，初始值为 0.0）
    private val _subtotal = MutableStateFlow(0.0)
    val subtotal: StateFlow<Double> = _subtotal.asStateFlow()

    // 状态管理：用户的真实收货地址
    private val _deliveryAddress = MutableStateFlow("")
    val deliveryAddress: StateFlow<String> = _deliveryAddress.asStateFlow()

    // 提示信息事件流，用于在 UI 层弹出 Toast
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    init {
        // ViewModel 初始化时，拉取最新的购物车数据和用户地址
        loadInitData()
    }

    fun setPickup(isPickup: Boolean) {
        _isPickup.value = isPickup
    }

    fun setStore(storeId: Int) {
        _selectedStoreId.value = storeId
    }

    fun setPayment(payment: String) {
        _selectedPayment.value = payment
    }

    private fun loadInitData() {
        viewModelScope.launch {
            try {
                // 1. 从 DataStore 读取当前登录用户的 userId 和地址
                val prefs = context.dataStore.data.first()
                val userId = prefs[UserPreferences.USER_ID_KEY] ?: return@launch
                val address = prefs[UserPreferences.USER_ADDRESS_KEY] ?: ""

                // 2. 更新 UI 状态流中的真实地址
                _deliveryAddress.value = address

                // 3. 调用购物车接口，获取该用户目前购物车里的所有真实商品
                val response = ApiClient.storeService.getCartList(userId)

                // 4. 解析结果并计算总金额
                if (response.code == 200 && response.data != null) {
                    var realTotal = 0.0
                    for (item in response.data) {
                        realTotal += item.price * item.quantity
                    }
                    _subtotal.value = realTotal
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 核心业务：执行最终的下单网络请求
     * @param onSuccess 下单成功后的回调函数（用于让 UI 跳转回首页）
     */
    fun submitOrder(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // 1. 从 DataStore 读取用户 ID
                val prefs = context.dataStore.data.first()
                val userId = prefs[UserPreferences.USER_ID_KEY] ?: return@launch

                val currentAddress = _deliveryAddress.value

                // ==========================================
                // 【核心重构：防线拦截】：坚决拒绝空地址外卖！
                // ==========================================
                if (!_isPickup.value && currentAddress.isEmpty()) {
                    _uiEvent.emit("Please add a delivery address in Profile first!")
                    return@launch
                }

                // 2. 组装发给后端的复杂请求体
                val req = OrderModels.OrderSubmitReq(
                    userId = userId,
                    // 如果是自提才传门店 ID，否则传 null
                    storeId = if (_isPickup.value) _selectedStoreId.value else null,
                    // 根据布尔值转换为后端需要的字符串枚举
                    orderType = if (_isPickup.value) "pickup" else "shipping",
                    paymentMethod = _selectedPayment.value,
                    // 如果是自提则不需要收货地址，如果是配送则传入真实地址
                    deliveryAddress = if (_isPickup.value) null else currentAddress,
                    // 配送费逻辑：自提为 0，配送为 1.50
                    deliveryFee = if (_isPickup.value) 0.0 else 1.50
                )

                // 3. 发送真实的下单请求给后端
                val response = ApiClient.storeService.submitOrder(req)

                // 4. 处理后端返回的下单结果
                if (response.code == 200) {
                    _uiEvent.emit("Success! Order No: ${response.data}")
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