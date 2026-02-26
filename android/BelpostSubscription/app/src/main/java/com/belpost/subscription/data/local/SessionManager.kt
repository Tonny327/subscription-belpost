package com.belpost.subscription.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {

    private object Keys {
        val USER_ID = longPreferencesKey("user_id")
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val CART_TOKEN = stringPreferencesKey("cart_token")
        val CART_ID = longPreferencesKey("cart_id")
    }

    val userIdFlow: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[Keys.USER_ID]
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.USER_ID] != null
    }

    suspend fun saveSession(userId: Long, token: String? = null) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
            token?.let { prefs[Keys.AUTH_TOKEN] = it }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.AUTH_TOKEN)
        }
    }

    suspend fun getUserId(): Long? = context.dataStore.data.first()[Keys.USER_ID]

    suspend fun getOrCreateCartToken(): String {
        val token = context.dataStore.data.first()[Keys.CART_TOKEN]
        if (token.isNullOrBlank()) {
            val newToken = UUID.randomUUID().toString()
            context.dataStore.edit { prefs ->
                prefs[Keys.CART_TOKEN] = newToken
            }
            return newToken
        }
        return token
    }

    suspend fun saveCartId(cartId: Long) {
        context.dataStore.edit { prefs ->
            prefs[Keys.CART_ID] = cartId
        }
    }

    suspend fun getCartId(): Long? = context.dataStore.data.first()[Keys.CART_ID]
}
