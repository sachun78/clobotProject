package com.lge.support.second.application.main.model

import androidx.lifecycle.*
import com.example.googlecloudmanager.GoogleSTT
import com.example.googlecloudmanager.common.Resource as R2

import com.lge.support.second.application.main.data.chatbot.ChatRequest
import com.lge.support.second.application.main.data.chatbot.ChatbotData
import com.lge.support.second.application.main.repository.ChatbotRepository
import com.lge.support.second.application.main.util.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChatbotViewModel(private val repository: ChatbotRepository) : ViewModel() {
    private val _queryResult: MutableLiveData<ChatbotData> = MutableLiveData<ChatbotData>()
    val queryResult: LiveData<ChatbotData> get() = _queryResult
    private val _speechEnd: MutableLiveData<Boolean> = MutableLiveData()
    val speechEnd: LiveData<Boolean> get() = _speechEnd

    fun getResponse(in_str: String) {
        val domain_id = "seoul_mmca"
        val in_str = in_str
        val request = ChatRequest(domain_id, in_str)

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

    // TODO(H, change R2 state)
    fun speechResponse() {
        println("speechResponse")
        GoogleSTT.speechToTextFlow().onEach { result ->
            when (result) {
                is R2.Success -> {
                    println("get success")
                    _speechEnd.value = result.data
                    getResponse(GoogleSTT.text)
                }
                is R2.Error -> {
                    println("get error")
                    _speechEnd.value = false
                }
                is R2.Loading -> {
                    println("get loading")
                    _speechEnd.value = false
                }
            }
        }.launchIn(viewModelScope)
    }

    class Factory constructor(private val repository: ChatbotRepository) :
        ViewModelProvider.Factory { // factory pattern
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ChatbotViewModel::class.java)) {
                ChatbotViewModel(this.repository) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}