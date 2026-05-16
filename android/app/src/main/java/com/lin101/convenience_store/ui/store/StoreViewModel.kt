package com.lin101.convenience_store.ui.store

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import com.lin101.convenience_store.data.model.Store
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class StoreViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val context = application

    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()

    private val _currentStoreId = MutableStateFlow<Int?>(null)
    val currentStoreId: StateFlow<Int?> = _currentStoreId.asStateFlow()

    private val _currentStoreName = MutableStateFlow("Select Store")
    val currentStoreName: StateFlow<String> = _currentStoreName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPersistedStore()
        fetchStores()  // 自动加载门店列表
    }

    private fun loadPersistedStore() {
        viewModelScope.launch {
            val prefs = context.dataStore.data.first()
            val savedId = prefs[UserPreferences.CURRENT_STORE_ID_KEY]
            val savedName = prefs[UserPreferences.CURRENT_STORE_NAME_KEY]
            _currentStoreId.value = savedId
            if (!savedName.isNullOrEmpty()) {
                _currentStoreName.value = savedName
            }
        }
    }

    /**
     * 公开方法：手动加载门店列表（用于刷新）
     */
    fun loadStores() {
        fetchStores()
    }

    private fun fetchStores() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiClient.storeService.getStores()
                if (response.code == 200 && response.data != null) {
                    _stores.value = response.data
                    val currentId = _currentStoreId.value
                    if (currentId != null) {
                        val store = response.data.find { it.storeId == currentId }
                        if (store != null) {
                            _currentStoreName.value = store.storeName
                        }
                    } else if (response.data.isNotEmpty()) {
                        // 没有选中门店时默认选中第一个
                        val first = response.data.first()
                        selectStore(first.storeId, first.storeName)
                    }
                }
            } catch (e: Exception) {
                Log.e("StoreViewModel", "Failed to fetch stores", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 选择门店：同时更新内存中的 StateFlow 和 DataStore 持久化
     */
    fun selectStore(storeId: Int, storeName: String) {
        _currentStoreId.value = storeId
        _currentStoreName.value = storeName
        ApiClient.syncUpdateStoreId(storeId)
        viewModelScope.launch {
            userPreferences.updateStore(storeId, storeName)
        }
    }

    fun refresh() {
        fetchStores()
    }
}