package com.lin101.convenience_store.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val context = application

    private val _nickname = MutableStateFlow("Loading...")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _avatarUrl = MutableStateFlow("")
    val avatarUrl: StateFlow<String> = _avatarUrl.asStateFlow()

    // 【新增】暴露给 UI 的余额状态流
    private val _balance = MutableStateFlow(0.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            context.dataStore.data.collect { prefs ->
                _nickname.value = prefs[UserPreferences.USER_NICKNAME_KEY] ?: "User"
                _phone.value = prefs[UserPreferences.USER_PHONE_KEY] ?: "No Phone Number"
                _avatarUrl.value = prefs[UserPreferences.USER_AVATAR_KEY] ?: ""
                _balance.value = prefs[UserPreferences.USER_BALANCE_KEY] ?: 0.0
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userPreferences.clearAuthInfo()
            onSuccess()
        }
    }
}