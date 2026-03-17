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
    }

    val tokenFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    suspend fun saveAuthInfo(token: String, user: User) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = user.userId
            prefs[USER_PHONE_KEY] = user.phone
            prefs[USER_NICKNAME_KEY] = user.nickname
            prefs[USER_AVATAR_KEY] = user.avatarUrl ?: ""
            prefs[USER_ADDRESS_KEY] = user.address ?: ""
            prefs[USER_BALANCE_KEY] = user.balance ?: 0.0
        }
    }

    suspend fun clearAuthInfo() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}