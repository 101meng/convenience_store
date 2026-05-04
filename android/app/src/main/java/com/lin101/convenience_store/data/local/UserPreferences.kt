package com.lin101.convenience_store.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.lin101.convenience_store.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val TOKEN_KEY = stringPreferencesKey("jwt_token")
        val USER_ID_KEY = intPreferencesKey("user_id")
        val USER_PHONE_KEY = stringPreferencesKey("user_phone")
        val USER_NICKNAME_KEY = stringPreferencesKey("user_nickname")
        val USER_AVATAR_KEY = stringPreferencesKey("user_avatar")
        val USER_ADDRESS_KEY = stringPreferencesKey("user_address")
        val USER_BALANCE_KEY: Preferences.Key<Double>
            get() = doublePreferencesKey("user_balance")

        val SHOPPING_MODE_KEY = stringPreferencesKey("shopping_mode")
        val CURRENT_LOCATION_NAME_KEY = stringPreferencesKey("current_location_name")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }

    // 【新增】：暴露用户的真实收货地址流
    val userAddressFlow: Flow<String> = context.dataStore.data.map { it[USER_ADDRESS_KEY] ?: "" }

    // 如果没数据，最底层的默认值退化为“自提”和“总店”
    val shoppingModeFlow: Flow<String> = context.dataStore.data.map {
        it[SHOPPING_MODE_KEY] ?: "pickup"
    }
    val currentLocationNameFlow: Flow<String> = context.dataStore.data.map {
        it[CURRENT_LOCATION_NAME_KEY] ?: "Market Street Flagship"
    }

    suspend fun saveAuthInfo(token: String, user: User) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = user.userId
            prefs[USER_PHONE_KEY] = user.phone
            prefs[USER_NICKNAME_KEY] = user.nickname
            prefs[USER_AVATAR_KEY] = user.avatarUrl ?: ""

            val safeAddress = user.address ?: ""
            prefs[USER_ADDRESS_KEY] = safeAddress
            prefs[USER_BALANCE_KEY] = user.balance ?: 0.0
            if (safeAddress.isEmpty()) {
                // 如果新用户没有地址，强制默认让他去最近的门店自提！
                prefs[SHOPPING_MODE_KEY] = "pickup"
                prefs[CURRENT_LOCATION_NAME_KEY] = "Market Street Flagship"
            } else {
                // 如果是老用户且有地址，默认给他送到家！
                prefs[SHOPPING_MODE_KEY] = "shipping"
                prefs[CURRENT_LOCATION_NAME_KEY] = safeAddress
            }
        }
    }

    suspend fun updateShoppingMode(mode: String, locationName: String) {
        context.dataStore.edit { prefs ->
            prefs[SHOPPING_MODE_KEY] = mode
            prefs[CURRENT_LOCATION_NAME_KEY] = locationName
        }
    }

    suspend fun clearAuthInfo() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }
}