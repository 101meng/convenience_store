package com.lin101.convenience_store.ui.product

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import com.lin101.convenience_store.data.model.CartAddReq
import com.lin101.convenience_store.data.model.Product
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * 商品详情页的 ViewModel
 * 升级为 AndroidViewModel 以便读取本地 DataStore 中的登录信息
 */
class ProductDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application

    // 购买数量状态，默认为 1
    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    // 预留的商品详细信息状态
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    // 提示事件流
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    // 加载真实的单件商品数据
    fun loadProductDetail(productId: Int) {
        viewModelScope.launch {
            try {
                val response = ApiClient.storeService.getProductById(productId)
                if (response.code == 200 && response.data != null) {
                    _product.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit("Failed to load details. Please check network.")
            }
        }
    }

    // 增加数量
    fun increaseQuantity() {
        if (_quantity.value < 99) _quantity.value += 1
    }

    // 减少数量
    fun decreaseQuantity() {
        if (_quantity.value > 1) _quantity.value -= 1
    }

    /**
     * 【核心升级】接入真实的后端加入购物车接口
     */
    fun addToCart() {
        viewModelScope.launch {
            val currentProduct = _product.value ?: return@launch
            try {
                // 1. 从 DataStore 获取当前登录用户的 ID
                val prefs = context.dataStore.data.first()
                val userId = prefs[UserPreferences.USER_ID_KEY]

                // 未登录情况拦截
                if (userId == null) {
                    _uiEvent.emit("Please log in first")
                    return@launch
                }

                // 2. 组装请求参数
                val request = CartAddReq(
                    userId = userId,
                    productId = currentProduct.productId,
                    quantity = _quantity.value
                )

                // 3. 发送真实网络请求给后端
                val response = ApiClient.storeService.addToCart(request)

                // 4. 解析后端返回的状态码
                if (response.code == 200) {
                    // 动态拼接商品名称，提示更加人性化
                    _uiEvent.emit("Added ${_quantity.value} ${currentProduct.name} to cart!")
                } else {
                    _uiEvent.emit(response.message ?: "Failed to add")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit("Network error, please try again.")
            }
        }
    }
}