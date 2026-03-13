package com.lin101.convenience_store.ui.profile

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.api.ApiClient
import com.lin101.convenience_store.data.local.UserPreferences
import com.lin101.convenience_store.data.local.dataStore
import com.lin101.convenience_store.data.model.UpdateProfileRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val context = application

    // UI 状态流
    private val _nickname = MutableStateFlow("")
    val nickname: StateFlow<String> = _nickname.asStateFlow()

    private val _phone = MutableStateFlow("")
    val phone: StateFlow<String> = _phone.asStateFlow()

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 用于发送 Toast 提示的事件流
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    init {
        loadUserData()
    }

    /**
     * 初始化时从 DataStore 加载本地存储的用户信息
     */
    private fun loadUserData() {
        viewModelScope.launch {
            // 收集 DataStore 中的数据
            context.dataStore.data.collect { prefs ->
                _nickname.value = prefs[UserPreferences.USER_NICKNAME_KEY] ?: ""
                _phone.value = prefs[UserPreferences.USER_PHONE_KEY] ?: ""
                _address.value = prefs[UserPreferences.USER_ADDRESS_KEY] ?: ""
            }
        }
    }

    // 更新昵称状态
    fun updateNickname(newName: String) {
        _nickname.value = newName
    }

    // 更新地址状态
    fun updateAddress(newAddress: String) {
        _address.value = newAddress
    }

    /**
     * 提交保存修改
     */
    /**
     * 提交保存修改 (已接入真实接口)
     */
    fun saveChanges(onSuccess: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // 1. 组装请求参数（直接使用从 DataStore 里读取出来的 phone）
                val request = UpdateProfileRequest(
                    phone = _phone.value,
                    nickname = _nickname.value,
                    address = _address.value
                )

                // 2. 调用 Retrofit 接口发送给后端
                val response = ApiClient.storeService.updateProfile(request)

                // 3. 判断后端返回的状态码
                if (response.code == 200) {
                    // 后端数据库修改成功后，再同步更新手机本地的 DataStore 缓存
                    context.dataStore.edit { prefs ->
                        prefs[UserPreferences.USER_NICKNAME_KEY] = _nickname.value
                        prefs[UserPreferences.USER_ADDRESS_KEY] = _address.value
                    }

                    _uiEvent.emit("资料保存成功")
                    onSuccess() // 触发回调，返回上一页
                } else {
                    // 后端业务校验报错（如手机号找不到等）
                    _uiEvent.emit(response.message ?: "保存失败")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit("网络异常，请检查网络连接")
            } finally {
                _isLoading.value = false
            }
        }
    }
}