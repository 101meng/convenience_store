package com.lin101.convenience_store.ui.ai

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import com.lin101.convenience_store.data.model.AiModels
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

class AiPlannerViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application

    // 用户的输入
    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput.asStateFlow()

    // 聊天历史记录 (简单起见，这里只存当前这轮对话的记录：0为AI问候，1为用户发，2为AI回)
    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(isAi = true, text = "Hi there! ✨ Tell me your scenario (e.g., 'Late night coding' or 'Post-workout snack') and I'll tailor a Bento box for you!")
    ))
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory.asStateFlow()

    // 是否正在思考中
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 提示事件流
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    fun updateInput(text: String) {
        _userInput.value = text
    }

    fun submitPrompt() {
        val prompt = _userInput.value.trim()
        if (prompt.isEmpty()) return

        viewModelScope.launch {
            // 1. 把用户的话上屏，清空输入框，显示 Loading
            val currentChat = _chatHistory.value.toMutableList()
            currentChat.add(ChatMessage(isAi = false, text = prompt))
            _chatHistory.value = currentChat
            _userInput.value = ""
            _isLoading.value = true

            try {
                // 2. 请求后端 AI 接口
                val req = AiModels.AiPlannerReq(prompt)
                val response = ApiClient.storeService.getAiRecommendation(req)

                if (response.code == 200 && response.data != null) {
                    // 3. 把 AI 的回复和商品上屏
                    val aiReply = ChatMessage(
                        isAi = true,
                        text = response.data.aiMessage,
                        products = response.data.recommendedProducts
                    )
                    currentChat.add(aiReply)
                    _chatHistory.value = currentChat
                } else {
                    _uiEvent.emit("AI is a bit sleepy right now. Try again!")
                }
            } catch (e: Exception) {
                _uiEvent.emit("Network connection lost.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // 将 AI 推荐的所有商品一键加入真实购物车
    fun addAllToCart(products: List<Product>?) {
        if (products.isNullOrEmpty()) return

        viewModelScope.launch {
            try {
                val prefs = context.dataStore.data.first()
                val userId = prefs[UserPreferences.USER_ID_KEY]

                if (userId == null) {
                    _uiEvent.emit("Please log in first")
                    return@launch
                }

                // 遍历商品，逐个调用你现有的加入购物车接口
                for (product in products) {
                    val req = CartAddReq(userId, product.productId, 1)
                    ApiClient.storeService.addToCart(req)
                }

                _uiEvent.emit("Magic! 🛒 Added ${products.size} items to your cart!")
            } catch (e: Exception) {
                _uiEvent.emit("Failed to add items.")
            }
        }
    }
}

// 辅助数据类：用于在 UI 渲染聊天气泡
data class ChatMessage(
    val isAi: Boolean,
    val text: String,
    val products: List<Product>? = null
)