package com.itsmejunaidali.speechtotextsample.ui.speechtotext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Created by Junaid Ali on 16,March,2021
 */
class SpeechToTextViewModel: ViewModel() {

    private val _speechInput: MutableLiveData<String> = MutableLiveData()
    val speechInput: LiveData<String> = _speechInput

    private val _speechStateLabel: MutableLiveData<String> = MutableLiveData()
    val speechStateLabel: LiveData<String> = _speechStateLabel

    fun setSpeechInput(speechInput: String) {
        _speechInput.postValue(speechInput)
    }

    fun setSpeechStateLabel(label: String) {
        _speechStateLabel.postValue(label)
    }

}