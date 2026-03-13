package com.lin101.convenience_store.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.lin101.convenience_store.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 顶层扩展属性，确保全应用范围内 DataStore 只有一个实例（单例模式）
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    // 定义所有的 Key (键名)
    companion object {
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val USER_ID_KEY = intPreferencesKey("user_id")
        val USER_PHONE_KEY = stringPreferencesKey("user_phone")
        val USER_NICKNAME_KEY = stringPreferencesKey("user_nickname")
        val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
        val USER_ADDRESS_KEY = stringPreferencesKey("user_address")
    }

    /**
     * 将后端返回的 Token 和 User 对象存入本地
     */
    suspend fun saveAuthInfo(token: String, user: User) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = user.userId
            prefs[USER_PHONE_KEY] = user.phone
            prefs[USER_NICKNAME_KEY] = user.nickname
            prefs[USER_AVATAR_KEY] = user.avatarUrl
            // address 可能为空，赋一个默认空字符串
            prefs[USER_ADDRESS_KEY] = user.address ?: ""
        }
    }

    /**
     * 响应式获取 Token (返回 Flow)
     */
    val tokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    /**
     * 退出登录时调用：清空所有本地数据
     */
    suspend fun clearAuthInfo() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}