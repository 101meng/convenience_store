package com.lin101.convenience_store.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.model.Product
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {

    // 购买数量状态，默认为 1
    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    // 预留的商品详细信息状态
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product.asStateFlow()

    // Toast 提示事件流
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    // 初始化时加载商品数据 (目前用假数据占位)
    fun loadProductDetail(productId: Int) {
        // TODO: 后续替换为调用 ApiClient.storeService.getProductDetail(productId)
        _product.value = Product(
            productId = productId,
            categoryId = 1,
            name = "Zesty Avocado & Quinoa Bowl",
            price = 12.50,
            imageUrl = "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?q=80&w=800&auto=format&fit=crop",
            tag1 = "Vegan",
            tag2 = "Gluten-Free",
            tag3 = "Organic"
        )
    }

    // 增加数量
    fun increaseQuantity() {
        if (_quantity.value < 99) {
            _quantity.value += 1
        }
    }

    // 减少数量
    fun decreaseQuantity() {
        if (_quantity.value > 1) {
            _quantity.value -= 1
        }
    }

    // 加入购物车逻辑
    fun addToCart() {
        viewModelScope.launch {
            val currentProduct = _product.value
            if (currentProduct != null) {
                // TODO: 后续这里调用后端的 /api/cart/add 接口，将商品ID和数量存入数据库
                println("Added to Cart: ${currentProduct.name}, Qty: ${_quantity.value}")
                _uiEvent.emit("Successfully added ${_quantity.value} item(s) to cart!")
            }
        }
    }
}