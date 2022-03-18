package com.github.caterpie.letsgitit.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private const val USER_PREFERENCES_NAME = "user_preferences"
val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

object UserPreferencesDataStore {

    data class UserPreferences(
        val userToken: String
    )

    private var dataStore: DataStore<Preferences>? = null

    private object PreferencesKeys {
        val USER_TOKEN = stringPreferencesKey("user_token")
    }

    fun create(dataStore: DataStore<Preferences>) {
        this.dataStore = dataStore
    }

    suspend fun getUserPreferencesFlow(): Flow<UserPreferences>? {
        return dataStore?.data?.
            catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }?.map { preferences ->
                val userToken = preferences[PreferencesKeys.USER_TOKEN] ?: ""
                UserPreferences(userToken)
            }
    }

    suspend fun setUserPreferences(userPreferences: UserPreferences) {
        dataStore?.edit { preferences ->
            userPreferences.run {
                preferences[PreferencesKeys.USER_TOKEN] = userToken
            }
        }
    }

}