package com.lin101.convenience_store.ui.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.model.AiModels
import com.lin101.convenience_store.data.model.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiDietitianViewModel : ViewModel() {

    private val _analysisResult = MutableStateFlow<AiModels.AiDietitianResp?>(null)
    val analysisResult: StateFlow<AiModels.AiDietitianResp?> = _analysisResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun analyzeCart(items: List<CartItem>) {
        if (items.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val req = AiModels.AiDietitianReq(items)
                val response = ApiClient.storeService.analyzeNutrition(req)
                if (response.code == 200) {
                    _analysisResult.value = response.data
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}