package com.lin101.convenience_store.ui.cart

import android.app.Application
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

    // 获取全局的 Context，用于读取 DataStore 里的用户登录信息
    private val context = application

    // 购物车商品列表状态流，UI 会监听它
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // 商品总价状态流，UI 底部结算栏会监听它
    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice: StateFlow<Double> = _totalPrice.asStateFlow()

    init {
        // ViewModel 创建时（即进入购物车页面时），立刻拉取真实数据
        fetchCartList()
    }

    /**
     * 获取真实购物车列表
     */
    fun fetchCartList() {
        viewModelScope.launch {
            try {
                // 1. 从本地 DataStore 中读取当前登录用户的 userId
                val prefs = context.dataStore.data.first()
                val userId = prefs[UserPreferences.USER_ID_KEY] ?: return@launch

                // 2. 发起网络请求获取真实数据
                val response = ApiClient.storeService.getCartList(userId)

                // 3. 判断状态码 200 (我们在 Kotlin 模型里定义的 code)
                if (response.code == 200 && response.data != null) {
                    _cartItems.value = response.data
                    calculateTotal() // 数据拿到了，重新计算一次总价
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 这里可以发个通知给 UI 提示网络错误
            }
        }
    }

    /**
     * 更新某个商品的数量 (+ 或 -)
     */
    fun updateQuantity(cartId: Int, newQuantity: Int) {
        if (newQuantity < 1) return // 数量不能小于 1，小于 1 应该走删除逻辑

        viewModelScope.launch {
            try {
                // 【体验优化 - 乐观更新】：先在本地立即更新 UI，让用户感觉不到网络延迟
                val updatedList = _cartItems.value.map {
                    if (it.cartId == cartId) it.copy(quantity = newQuantity) else it
                }
                _cartItems.value = updatedList
                calculateTotal()

                // 然后异步发送请求给后端同步数据库
                ApiClient.storeService.updateCartQuantity(CartUpdateReq(cartId, newQuantity))
            } catch (e: Exception) {
                // 如果网络请求失败了，说明数据库没更新成功，为了防止数据错乱，重新拉取一次列表覆盖本地
                fetchCartList()
            }
        }
    }

    /**
     * 从购物车移除商品 (滑动删除或点击垃圾桶)
     */
    fun removeItem(cartId: Int) {
        viewModelScope.launch {
            try {
                // 【体验优化 - 乐观更新】：先从本地列表中瞬间剔除，UI 立即消失
                _cartItems.value = _cartItems.value.filter { it.cartId != cartId }
                calculateTotal()

                // 通知后端彻底删除
                ApiClient.storeService.removeCartItem(cartId)
            } catch (e: Exception) {
                // 失败则回退状态
                fetchCartList()
            }
        }
    }

    /**
     * 内部方法：重新计算购物车商品总金额
     */
    private fun calculateTotal() {
        var total = 0.0
        for (item in _cartItems.value) {
            total += item.price * item.quantity
        }
        _totalPrice.value = total
    }
}