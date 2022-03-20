package com.github.caterpie.letsgitit.ui

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.*
import com.github.caterpie.letsgitit.data.ChallengeRepository
import kotlinx.coroutines.launch
import java.lang.Integer.min

class MainViewModel(private val challengeRepository: ChallengeRepository) : ViewModel() {
    private val _challenge: MutableLiveData<Int> = MutableLiveData<Int>(365)
    val challenge: LiveData<Int> = _challenge

    private val _onGoing: MutableLiveData<Int> = MutableLiveData(0)
    val onGoing: LiveData<Int> = _onGoing

    init {
        viewModelScope.launch {
            challengeRepository.getOnGoing().collect {
                _onGoing.value = it
            }
        }
    }

    fun getProgressOnGoing() : Int {
        return min(onGoing.value ?: 0, challenge.value ?: 365)
    }

    fun setChallengeEvent() : TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 사용자가 Challenge 설정 값을 지운 경우에 공백이 넘어오게 됨, 해당 경우 365로 처리
                p0?.let {
                    _challenge.value = if (it.toString().isNotBlank()) {
                        it.toString().toInt()
                    } else {
                        365
                    }
                }
            }
        }
    }
}

class MainViewModelFactory(private val challengeRepository: ChallengeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(challengeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}