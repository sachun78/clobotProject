package com.lge.support.second.application.main.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.googlecloudmanager.GoogleTranslateV3
import com.example.googlecloudmanager.common.Language
import com.example.googlecloudmanager.domain.GoogleCloudRepository
import com.example.googlecloudmanager.common.Resource as R2

import com.lge.support.second.application.main.data.chatbot.ChatRequest
import com.lge.support.second.application.main.data.chatbot.ChatbotData
import com.lge.support.second.application.main.repository.ChatbotRepository
import com.lge.support.second.application.main.util.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChatbotViewModel(
    private val repository: ChatbotRepository,
    private val googleRepositiory: GoogleCloudRepository
) : ViewModel() {
    private val TAG = "ChatbotViewModel"
    private val _queryResult: MutableLiveData<ChatbotData> = MutableLiveData<ChatbotData>()
    val queryResult: LiveData<ChatbotData> get() = _queryResult
    private val _speechText: MutableLiveData<String> = MutableLiveData()
    val speechText: LiveData<String> get() = _speechText

    // Use Chatbot
    fun getResponse(in_str: String, in_type: String? = null) {
        val domain_id = "national_th"
        val in_str = in_str
        val request = ChatRequest(
            domain_id,
            in_str,
            in_type = in_type ?: "query",
//            parameters = ChatRequest.ChatRequestParameter(lang = if (GoogleSTT.getLanguage() == Language.Korean) "ko" else "en")
        )

        repository(request).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    println("get success")
                    _queryResult.value = result.data
                }
                is Resource.Error -> {
                    println("get error")
                    _queryResult.value = null
                }
                is Resource.Loading -> {
                    println("get loading")
                    _queryResult.value = null
                }
            }
        }.launchIn(viewModelScope)
    }

    // Use GoogleCloud API
    fun speak(_context: Context, text: String) {
        googleRepositiory.speak(_context, text)
    }

    // TODO(H, change R2 state)
    fun speechResponse() {
        googleRepositiory.speachToText().onEach { result ->
            when (result) {
                is R2.Complete -> {
                    Log.i(TAG, "onSuccess")
                    _speechText.value = result.data
                    var translated: String
//                    GoogleTranslateV3.translate("Docent")

                    // TODO(translate result data when language is not KOREAN)
                    speechText.value?.let { getResponse(it) }
//                    if (GoogleSTT.getLanguage() != Language.Korean) {
//                        translated = GoogleTranslateV3.translate(GoogleSTT.text)
//                        getResponse(translated)
//                    } else {
//
//                    }
                }
                is R2.Loading -> {
                    Log.i(TAG, "onLoading : ${result.data}")
                    // _speechText.value = result.data
                }
                is R2.Listen -> {
                    Log.i(TAG, "onListen")
                    _speechText.value = result.data
                }
                is R2.Error -> {
                    Log.i(TAG, "onError")
                    _speechText.value = ""
                }
            }
        }.launchIn(viewModelScope)
    }

    class Factory constructor(
        private val repository: ChatbotRepository,
        private val googleRepository: GoogleCloudRepository
    ) :
        ViewModelProvider.Factory { // factory pattern
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ChatbotViewModel::class.java)) {
                ChatbotViewModel(this.repository, this.googleRepository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}