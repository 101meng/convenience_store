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

// 继承 AndroidViewModel，并通过构造函数接收 Application 实例
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // 实例化本地存储工具类
    private val userPreferences = UserPreferences(application)

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _code = MutableStateFlow("")
    val code: StateFlow<String> = _code.asStateFlow()

    private val _isCountingDown = MutableStateFlow(false)
    val isCountingDown: StateFlow<Boolean> = _isCountingDown.asStateFlow()

    private val _countdownSeconds = MutableStateFlow(60)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    fun updatePhone(input: String) {
        _phone.value = input.filter { it.isDigit() }.take(11)
    }

    fun updateCode(input: String) {
        _code.value = input.filter { it.isDigit() }.take(6)
    }

    fun sendVerificationCode() {
        if (_phone.value.length != 11 || _isCountingDown.value) return

        viewModelScope.launch {
            try {
                val response = ApiClient.storeService.sendCode(_phone.value)
                if (response.code == 200) {
                    _uiEvent.emit("验证码已发送")
                    startCountdown()
                } else {
                    _uiEvent.emit(response.message)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit("网络异常，请检查后端服务")
            }
        }
    }

    private fun startCountdown() {
        _isCountingDown.value = true
        _countdownSeconds.value = 60
        viewModelScope.launch {
            while (_countdownSeconds.value > 0) {
                delay(1000L)
                _countdownSeconds.value -= 1
            }
            _isCountingDown.value = false
        }
    }

    fun verifyAndLogin(onSuccess: () -> Unit) {
        if (_phone.value.length != 11 || _code.value.length != 6) return

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = LoginRequest(phone = _phone.value, code = _code.value)
                val response = ApiClient.storeService.login(request)

                if (response.code == 200 && response.token != null && response.user != null) {
                    // 修改 2：登录成功后，将后端返回的真实 Token 和 User 信息持久化到手机本地！
                    userPreferences.saveAuthInfo(response.token, response.user)

                    _uiEvent.emit("登录成功")
                    onSuccess()
                } else {
                    _uiEvent.emit(response.message ?: "登录失败")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit("网络异常，登录请求失败")
            } finally {
                _isLoading.value = false
            }
        }
    }
}