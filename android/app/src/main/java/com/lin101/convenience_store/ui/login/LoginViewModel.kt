package com.lin101.convenience_store.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.model.LoginRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    // 【新增】：60秒倒计时的状态流
    private val _countdown = MutableStateFlow(0)
    val countdown: StateFlow<Int> = _countdown.asStateFlow()

    fun updatePhone(newPhone: String) {
        if (newPhone.all { it.isDigit() } && newPhone.length <= 11) {
            _phone.value = newPhone
        }
    }

    fun updateCode(newCode: String) {
        if (newCode.length <= 6) {
            _code.value = newCode
        }
    }

    fun sendCode() {
        if (_phone.value.length != 11) {
            viewModelScope.launch { _uiEvent.emit("Please enter a valid 11-digit phone number") }
            return
        }

        // 【新增】：如果倒计时还在进行中，直接拦截，防止重复点击
        if (_countdown.value > 0) return

        viewModelScope.launch {
            try {
                val response = ApiClient.storeService.sendCode(_phone.value)
                _uiEvent.emit(response.message ?: "Code sent successfully!")

                // 【新增】：启动 60 秒倒计时协程
                _countdown.value = 60
                while (_countdown.value > 0) {
                    delay(1000) // 严格等待 1 秒
                    _countdown.value -= 1
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit("Network error, please try again.")
            }
        }
    }

    fun login(onSuccess: () -> Unit) {
        if (_phone.value.length != 11 || _code.value.isEmpty()) {
            viewModelScope.launch { _uiEvent.emit("Please complete phone and code") }
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = LoginRequest(phone = _phone.value, code = _code.value)
                val response = ApiClient.storeService.login(request)

                if (response.code == 200 && response.data != null) {
                    userPreferences.saveAuthInfo(response.data.token, response.data.user)
                    _uiEvent.emit("Login Successful!")
                    onSuccess()
                } else {
                    _uiEvent.emit(response.message ?: "Login failed")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit("Network error, check your connection.")
            } finally {
                _isLoading.value = false
            }
        }
    }
}