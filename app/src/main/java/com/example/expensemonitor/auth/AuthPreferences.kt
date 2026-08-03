package com.example.expensemonitor.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val REMEMBER_ME = booleanPreferencesKey("remember_me")
        val MPIN = stringPreferencesKey("mpin")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val IS_FIRST_TIME = booleanPreferencesKey("is_first_time")
    }

    val isRememberMeEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[REMEMBER_ME] ?: false
    }

    val mpin: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[MPIN] ?: ""
    }

    val isBiometricEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BIOMETRIC_ENABLED] ?: false
    }

    val isFirstTime: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_FIRST_TIME] ?: true
    }

    suspend fun setRememberMe(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[REMEMBER_ME] = enabled
        }
    }

    suspend fun saveMpin(mpin: String) {
        context.dataStore.edit { prefs ->
            prefs[MPIN] = mpin
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BIOMETRIC_ENABLED] = enabled
        }
    }

    suspend fun setFirstTime(isFirstTime: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_FIRST_TIME] = isFirstTime
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
