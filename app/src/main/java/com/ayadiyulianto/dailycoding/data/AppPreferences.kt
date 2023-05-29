package com.ayadiyulianto.dailycoding.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ayadiyulianto.dailycoding.data.local.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val AUTH_USERID_KEY = stringPreferencesKey("auth_userid")
    private val AUTH_USERNAME_KEY = stringPreferencesKey("auth_username")
    private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")

    fun isLoggedIn(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            // IF token empty, user didn't log in
            preferences[AUTH_TOKEN_KEY]?.isNotEmpty() ?: false
        }
    }

    fun getAuthToken(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[AUTH_TOKEN_KEY] ?: ""
        }
    }

    suspend fun saveAuthUser(user: User) {
        dataStore.edit { preferences ->
            preferences[AUTH_USERID_KEY] = user.userId
            preferences[AUTH_USERNAME_KEY] = user.name
            preferences[AUTH_TOKEN_KEY] = user.token
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AppPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AppPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}