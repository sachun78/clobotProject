package com.lge.support.second.application.model


import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.googlecloudmanager.common.Language
import com.example.googlecloudmanager.domain.GoogleCloudRepository
import com.lge.robot.platform.data.ActionStatus
import com.lge.robot.platform.data.ErrorReport
import com.lge.robot.platform.data.NaviActionInfo
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.data.chatbot.ChatRequest
import com.lge.support.second.application.data.chatbot.ChatbotData
import com.lge.support.second.application.data.robot.NaviError
import com.lge.support.second.application.database.pageConfig.PageConfig
import com.lge.support.second.application.repository.ChatbotRepository
import com.lge.support.second.application.repository.PageConfigRepo
import com.lge.support.second.application.util.Resource
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import com.example.googlecloudmanager.common.Resource as R2

class MainViewModel(
    private val repository: ChatbotRepository,
    private val googleRepositiory: GoogleCloudRepository,
    private val pageConfigRepo: PageConfigRepo
) : ViewModel() {
    var ischatfirst: Boolean = true    ///////chat페이지 처음 진입하는 것인지 여부//////
    private val TAG = "MainViewModel"
    private val _queryResult: MutableLiveData<ChatbotData?> = MutableLiveData<ChatbotData?>()
    val queryResult: LiveData<ChatbotData?> get() = _queryResult

    private val _speechText: MutableLiveData<String> = MutableLiveData()
    val speechText: LiveData<String> get() = _speechText

    private val _speechStatus: MutableLiveData<String> = MutableLiveData()
    val speechStatus: LiveData<String> get() = _speechStatus

    // page config
    val pageConfgs = pageConfigRepo.getAllPageConfig().asLiveData()
    fun currPageConfig(id: Int) = pageConfigRepo.getPageConfigById(id).asLiveData()
    fun insertAllConfig(data: List<PageConfig>) {
        Log.i("hjbae", "viewModel:  insertAllConfig ")
        viewModelScope.launch {
            pageConfigRepo.insertAllPageConfig(data)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            pageConfigRepo.deleteAll()
        }
    }

    // Use Chatbot
    fun getResponse(in_str: String, in_type: String? = null, raw_str: String = "") {
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

        repository(request).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _queryResult.value = result.data!!
                }
                is Resource.Error -> {
                    _queryResult.value = null
                }
                is Resource.Loading -> {
                    _queryResult.value = null
                }
            }
        }.launchIn(viewModelScope)
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
//                    _speechText.value = result.data
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

    private fun checkNaviActionStatus(status: ActionStatus) {
        when (status.motionStatus.geteStatus()) {
            NaviActionInfo.MOTION_STATUS_AVODING_OBS,
            NaviActionInfo.MOTION_STATUS_STOP_BY_OBS,
            NaviActionInfo.MOTION_STATUS_PATH_FAIL_GOAL_OCCUPIED -> {
                googleRepositiory.speak(
                    MainActivity.mainContext(),
                    "조심하세요. 제가 지나갈 수 있도록 옆으로 비켜주세요"
                )
            }
        }
    }

    private fun checkNaviError(data: NaviError) {
        when (data.errorId) {
            ErrorReport._e_error_event.eERR_SLAM.ordinal -> {
                println("slam Error 1")
            }
            ErrorReport._e_error_event.eERR_SLAM_NON_OPERATION_AREA.ordinal -> {
                //로봇을 맵 밖으로 강제 이동시
                println("slam Error 2")
                //or you can re start gkr
            }
            ErrorReport._e_error_event.eERR_MAP_LOADING_FAIL.ordinal -> {
                // 네비 엔진에서 맵을 못찾을 경우
                println("slam Error 3")
            }
        }
    }


    class Factory(
        private val repository: ChatbotRepository,
        private val googleRepository: GoogleCloudRepository,
        private val pageConfigRepo: PageConfigRepo
    ) :
        ViewModelProvider.Factory { // factory pattern
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(
                    this.repository,
                    this.googleRepository,
                    this.pageConfigRepo
                ) as T
            }
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}