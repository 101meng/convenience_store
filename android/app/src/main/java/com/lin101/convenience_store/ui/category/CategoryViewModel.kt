package com.lin101.convenience_store.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import com.lin101.convenience_store.data.model.Category
import com.lin101.convenience_store.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application

    private val userPreferences = UserPreferences(application)

    private val _storeId = MutableStateFlow<Int?>(null)
    val storeId: StateFlow<Int?> = _storeId.asStateFlow()

    private val _storeName = MutableStateFlow("")
    val storeName: StateFlow<String> = _storeName.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow(1)
    val selectedCategoryId: StateFlow<Int> = _selectedCategoryId.asStateFlow()

    private val _filteredProducts = MutableStateFlow<List<Product>>(emptyList())
    val filteredProducts: StateFlow<List<Product>> = _filteredProducts.asStateFlow()

    init {
        loadStore()
        fetchCategories()
        viewModelScope.launch {
            userPreferences.currentStoreIdFlow.collect { newStoreId ->
                if (newStoreId != null && newStoreId != _storeId.value) {
                    _storeId.value = newStoreId
                    fetchProductsByCategory(_selectedCategoryId.value)
                }
            }
        }
    }

    private fun loadStore() {
        viewModelScope.launch {
            try {
                val prefs = context.dataStore.data.first()
                val id = prefs[UserPreferences.CURRENT_STORE_ID_KEY]
                _storeId.value = id
                if (id != null) {
                    try {
                        val storesResp = ApiClient.storeService.getStores()
                        if (storesResp.code == 200 && storesResp.data != null) {
                            _storeName.value = storesResp.data.find { it.storeId == id }?.storeName ?: ""
                        }
                    } catch (_: Exception) {}
                }
            } catch (_: Exception) {}
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            try {
                val response = ApiClient.storeService.getCategories()
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