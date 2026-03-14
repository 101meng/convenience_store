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
        fetchCategories()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                // 请求后端接口
                val response = ApiClient.storeService.getCategories()
                // 【核心修改】：判断 code 是否为 200，并提取 data
                if (response.code == 200 && response.data != null) {
                    val categoryList = response.data
                    _categories.value = categoryList

                    if (categoryList.isNotEmpty()) {
                        val firstId = categoryList[0].categoryId
                        _selectedCategoryId.value = firstId
                        fetchProductsByCategory(firstId)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectCategory(categoryId: Int) {
        _selectedCategoryId.value = categoryId
        fetchProductsByCategory(categoryId)
    }

    private fun fetchProductsByCategory(categoryId: Int) {
        viewModelScope.launch {
            try {
                // 请求后端接口
                val response = ApiClient.storeService.getProducts(categoryId)
                // 【核心修改】：判断 code 并提取 data
                if (response.code == 200 && response.data != null) {
                    _filteredProducts.value = response.data
                } else {
                    _filteredProducts.value = emptyList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _filteredProducts.value = emptyList()
            }
        }
    }
}