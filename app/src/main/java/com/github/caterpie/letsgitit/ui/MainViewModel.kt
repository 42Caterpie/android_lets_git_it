package com.github.caterpie.letsgitit.ui

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.*
import com.github.caterpie.letsgitit.data.ChallengeRepository
import kotlinx.coroutines.launch

class MainViewModel(private val challengeRepository: ChallengeRepository) : ViewModel() {
    private val mChallenge: MutableLiveData<Int> = MutableLiveData<Int>(365)
    val challenge: LiveData<Int> = mChallenge

    private val mOnGoing: MutableLiveData<Int> = MutableLiveData(0)
    val onGoing: LiveData<Int> = mOnGoing

    init {
        viewModelScope.launch {
            challengeRepository.getOnGoing().collect {
                mOnGoing.value = it
            }
        }
    }

    fun setChallengeEvent() : TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // 사용자가 Challenge 설정 값을 지운 경우에 공백이 넘어오게 됨, 해당 경우 365로 처리
                p0?.let {
                    mChallenge.value = if (it.toString().isNotBlank()) {
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