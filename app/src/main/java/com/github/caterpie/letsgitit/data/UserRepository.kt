package com.github.caterpie.letsgitit.data

import com.github.caterpie.letsgitit.data.local.UserPreferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class UserRepository(
    private val userPreferencesDataStore: UserPreferencesDataStore
) {

    suspend fun isUserLogin(): Flow<Boolean> {
        return userPreferencesDataStore.getUserPreferencesFlow()?.map {
            return@map it.userToken.isNotBlank()
        } ?: flow {
            this.emit(false)
        }
    }
}