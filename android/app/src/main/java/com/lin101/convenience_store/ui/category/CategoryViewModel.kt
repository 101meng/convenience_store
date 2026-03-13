package com.lin101.convenience_store.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.model.Category
import com.lin101.convenience_store.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow(1)
    val selectedCategoryId: StateFlow<Int> = _selectedCategoryId.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    init {
        // ViewModel 初始化时，立刻发起网络请求获取分类数据
        fetchCategories()
    }

    private fun fetchCategories() {
        // 开启协程，在后台线程请求网络
        viewModelScope.launch {
            try {
                // 1. 请求分类列表
                val categoryList = ApiClient.storeService.getCategories()
                _categories.value = categoryList

                // 2. 如果有数据，默认选中第一个分类，并请求它的商品
                if (categoryList.isNotEmpty()) {
                    val firstId = categoryList[0].categoryId
                    _selectedCategoryId.value = firstId
                    fetchProductsByCategory(firstId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // TODO: 可以在这里处理网络错误，比如弹个 Toast
            }
        }
    }

    // 暴露给 UI 点击侧边栏时调用的方法
    fun selectCategory(categoryId: Int) {
        _selectedCategoryId.value = categoryId
        fetchProductsByCategory(categoryId)
    }

    private fun fetchProductsByCategory(categoryId: Int) {
        viewModelScope.launch {
            try {
                // 从后端请求指定 ID 的商品列表
                val productList = ApiClient.storeService.getProducts(categoryId)
                _filteredProducts.value = productList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}