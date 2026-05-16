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
        val USER_BALANCE_KEY = doublePreferencesKey("user_balance")
        val SHOPPING_MODE_KEY = stringPreferencesKey("shopping_mode")
        val CURRENT_LOCATION_NAME_KEY = stringPreferencesKey("current_location_name")

        val CURRENT_STORE_ID_KEY = intPreferencesKey("current_store_id")
        // 新增：单独存储当前门店名称
        val CURRENT_STORE_NAME_KEY = stringPreferencesKey("current_store_name")
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val userAddressFlow: Flow<String> = context.dataStore.data.map { it[USER_ADDRESS_KEY] ?: "" }
    val shoppingModeFlow: Flow<String> = context.dataStore.data.map { it[SHOPPING_MODE_KEY] ?: "pickup" }

    val currentLocationNameFlow: Flow<String> = context.dataStore.data.map {
        it[CURRENT_LOCATION_NAME_KEY] ?: "Select Location"
    }

    val currentStoreIdFlow: Flow<Int> = context.dataStore.data.map {
        it[CURRENT_STORE_ID_KEY] ?: 1
    }

    // 新增：当前门店名称的 Flow
    val currentStoreNameFlow: Flow<String> = context.dataStore.data.map {
        it[CURRENT_STORE_NAME_KEY] ?: ""
    }

    suspend fun updateStoreId(storeId: Int) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_STORE_ID_KEY] = storeId
        }
    }

    // 新增：更新门店名称
    suspend fun updateStoreName(storeName: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_STORE_NAME_KEY] = storeName
        }
    }

    // 同时更新门店 ID 和名称
    suspend fun updateStore(storeId: Int, storeName: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENT_STORE_ID_KEY] = storeId
            prefs[CURRENT_STORE_NAME_KEY] = storeName
        }
    }

    suspend fun saveAuthInfo(token: String, user: User) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = user.userId
            prefs[USER_PHONE_KEY] = user.phone
            prefs[USER_NICKNAME_KEY] = user.nickname
            prefs[USER_AVATAR_KEY] = user.avatarUrl ?: ""
            prefs[USER_BALANCE_KEY] = user.balance ?: 0.0

            val safeAddress = user.address ?: ""
            prefs[USER_ADDRESS_KEY] = safeAddress

            if (safeAddress.isEmpty()) {
                prefs[SHOPPING_MODE_KEY] = "pickup"
                prefs[CURRENT_LOCATION_NAME_KEY] = "Main Store"
            } else {
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