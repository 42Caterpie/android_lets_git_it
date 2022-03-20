package com.github.caterpie.letsgitit.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChallengeRepository() {
    suspend fun getOnGoing(): Flow<Int> {
        return flow {
            for (i in 0..365) {
                delay(100)
                emit(i)
            }
        }
    }
}