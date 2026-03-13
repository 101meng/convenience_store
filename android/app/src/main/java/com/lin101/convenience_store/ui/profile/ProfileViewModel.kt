package com.lin101.convenience_store.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lin101.convenience_store.data.local.UserPreferences
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    /**
     * 退出登录：清空本地保存的所有认证信息
     */
    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            // 调用我们之前写好的清空 DataStore 的方法
            userPreferences.clearAuthInfo()
            // 清空完毕后，通知 UI 层进行页面跳转
            onSuccess()
        }
    }
}