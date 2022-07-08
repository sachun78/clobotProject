package com.lge.support.second.application.model

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import com.example.googlecloudmanager.common.Language
import com.example.googlecloudmanager.domain.GoogleCloudRepository
import com.lge.support.second.application.data.chatbot.ChatRequest
import com.lge.support.second.application.data.chatbot.ChatbotData
import com.lge.support.second.application.data.chatbot.dto.ChatbotResponseDto
import com.lge.support.second.application.data.pageConfig.PageInfoItem
import com.lge.support.second.application.managers.robot.worker.ScheduleWorker
import com.lge.support.second.application.repository.ChatbotRepository
import com.lge.support.second.application.repository.SceneConfigRepo
import com.lge.support.second.application.util.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import com.example.googlecloudmanager.common.Resource as R2

class MainViewModel(
    private val repository: ChatbotRepository,
    private val googleRepositiory: GoogleCloudRepository,
    private val sceneConfigRepo: SceneConfigRepo,
    application: Application
) : AndroidViewModel(application) {
    var ischatfirst: Boolean = true    ///////chat페이지 처음 진입하는 것인지 여부//////
    private val TAG = "MainViewModel"
    private val _queryResult: MutableLiveData<ChatbotData?> = MutableLiveData<ChatbotData?>()
    val queryResult: LiveData<ChatbotData?> get() = _queryResult

    private val _breakData: MutableLiveData<ChatbotResponseDto?> =
        MutableLiveData<ChatbotResponseDto?>()
    val breakData: LiveData<ChatbotResponseDto?> get() = _breakData

    private val _speechText: MutableLiveData<String> = MutableLiveData()
    val speechText: LiveData<String> get() = _speechText

    private val _speechStatus: MutableLiveData<String> = MutableLiveData()
    val speechStatus: LiveData<String> get() = _speechStatus

    private val _currentPage: MutableLiveData<String> = MutableLiveData()
    val currentPage: LiveData<String> get() = _currentPage

    private val _currentPageInfo: MutableLiveData<PageInfoItem> = MutableLiveData()
    val currentPageInfo: LiveData<PageInfoItem> get() = _currentPageInfo

    private val workManager = WorkManager.getInstance(application)

    init {
        cancelSchedule()
    }

    fun updatePageInfo(pageId: String) {
        _currentPageInfo.value = sceneConfigRepo.getPageInfo(pageId)
    }

    suspend fun breakChat() {
        repository.breakChat().onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _breakData.value = result.data!!
                }
                is Resource.Error -> {
                }
                is Resource.Loading -> {
                }
            }
        }.launchIn(viewModelScope).join()
    }

    // Use Chatbot
    suspend fun getResponse(
        in_str: String,
        in_type: String? = null,
        raw_str: String = "",
        changePage: Boolean = true
    ) {
        val domain_id = "seoul_mmca"
        val request = ChatRequest(
            domain_id,
            in_str,
            in_type = in_type ?: "query",
            parameters = ChatRequest.ChatRequestParameter(
                lang = googleRepositiory.language.toLocalString(),
                raw_str = raw_str
            )
        )

        val job = repository(request).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _queryResult.value = result.data!!
                    if (!changePage) return@onEach

                    if (result.data.customCode.page_id != "") {
                        if (_currentPage.value != result.data.customCode.page_id) {
                            _currentPage.value = result.data.customCode.page_id
                        }
                    } else if (result.data.template_id != "") {
                        if (_currentPage.value != result.data.template_id) {
                            _currentPage.value = result.data.template_id
                        }
                    } else {
                        _currentPage.value = ""
                    }
                }
                is Resource.Error -> {}
                is Resource.Loading -> {}
            }
        }.launchIn(viewModelScope)

        //chatbot request.. async
        job.join()
    }

    fun resetCurrentPage() {
        _currentPage.postValue("")
    }

    // Use GoogleCloud API
    fun ttsSpeak(_context: Context, text: String) {
        googleRepositiory.speak(_context, text)
    }

    fun ttsStop() {
        googleRepositiory.stop()
    }

    fun speechStop() {
        googleRepositiory.speechStop()
    }

    fun setLanguage(lang: Language) {
        googleRepositiory.language = lang
    }

    // TODO(H, change R2 state)
    fun speechResponse() {
//        if (ischatfirst) {
//            googleRepositiory.ischatfirst = false
//        }
        googleRepositiory.speachToText().onEach { result ->
            when (result) {
                is R2.Complete -> {
                    Log.i(TAG, "onSuccess")
                    _speechText.value = result.data
                    var translated: String

                    // TODO(translate result data when language is not KOREAN)
                    speechText.value?.let {
                        getResponse(it)
                    }

                    if (googleRepositiory.language != Language.Korean) {
                        translated = googleRepositiory.translate(speechText.value!!)
                        getResponse(translated)
                    }
                }
                is R2.Loading -> {
                    Log.i(TAG, "onLoading : ${result.data}")
                    _speechStatus.value = result.data
                }
                is R2.Listen -> {
                    Log.i(TAG, "onListen ${result.data}")
                    _speechText.value = result.data
                }
                is R2.Error -> {
                    Log.i(TAG, "onError")
                    _speechText.value = ""
                }
            }
        }.launchIn(viewModelScope)
    }

    internal fun enrollSchedule() {
        val workRequest = OneTimeWorkRequestBuilder<ScheduleWorker>().build()
        workManager.enqueueUniqueWork(
            "schedule",
            ExistingWorkPolicy.KEEP, workRequest
        )
    }

    internal fun cancelSchedule() {
        workManager.cancelUniqueWork("schedule")
    }

    fun getCurrPageInfo(): PageInfoItem? {
        return sceneConfigRepo.getCurrPageInfo()
    }

    class Factory(
        private val repository: ChatbotRepository,
        private val googleRepository: GoogleCloudRepository,
        private val sceneConfigRepo: SceneConfigRepo,
        private val application: Application
    ) :
        ViewModelProvider.Factory { // factory pattern
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(
                    this.repository,
                    this.googleRepository,
                    this.sceneConfigRepo,
                    this.application
                ) as T
            }
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}