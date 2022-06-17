package com.lge.support.second.application.main.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.googlecloudmanager.domain.GoogleCloudRepository
import com.lge.robot.platform.data.*
import com.lge.robot.platform.navigation.NavigationMessageType
import com.lge.support.second.application.main.data.robot.BatteryEvent
import com.lge.support.second.application.main.data.robot.NaviError
import com.example.googlecloudmanager.common.Resource as R2

import com.lge.support.second.application.main.data.chatbot.ChatRequest
import com.lge.support.second.application.main.data.chatbot.ChatbotData
import com.lge.support.second.application.main.data.robot.NavigationMessage
import com.lge.support.second.application.main.repository.ChatbotRepository
import com.lge.support.second.application.main.repository.PageConfigRepo
import com.lge.support.second.application.main.repository.RobotRepository
import com.lge.support.second.application.main.util.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ChatbotRepository,
    private val googleRepositiory: GoogleCloudRepository,
    private val robotRepository: RobotRepository,
    private val pageConfigRepo: PageConfigRepo
) : ViewModel() {

    var ischatfirst: Boolean = false
    private val TAG = "ChatbotViewModel"
    private val _queryResult: MutableLiveData<ChatbotData> = MutableLiveData<ChatbotData>()
    val queryResult: LiveData<ChatbotData>
        get() = _queryResult

    private val _speechText: MutableLiveData<String> = MutableLiveData()
    val speechText: LiveData<String>
        get() = _speechText

    private val _sttTryCount: MutableLiveData<Int> = MutableLiveData(0)

    // ROBOT DATA
    val mNavigationMessageType = MutableLiveData<Int>()
    val mActionStatus = MutableLiveData<ActionStatus>()
    val mNaviStatus2 = MutableLiveData<NaviStatus2>()
    val mSLAM3DPos = MutableLiveData<SLAM3DPos>()
    val mNaviActionInfo = MutableLiveData<NaviActionInfo>()

    val mPowerMode = MutableLiveData<Int>()
    val mBatteryInfo = MutableLiveData<BatteryEvent>()

    val isGkrProcessing = MutableLiveData(false)
    val robotError = MutableLiveData<RobotError>()

    // robot data callback
    init {
        robotRepository.monitoringMangerCallback.onEach { data ->
            when (data) {
                is BatteryEvent -> {
                    mBatteryInfo.value = data
                    var batteryData = data.batteryData
                    when (data.type) {
                        null -> println("battery type null")
                    }
                }
                is RobotError -> robotError.value = data
                else -> println("monitoring else!")
            }
        }

        robotRepository.powerMangerCallback.onEach { powerMode ->
            mPowerMode.value = powerMode as Int
        }
        robotRepository.navigationManagerCallback.onEach { naviData ->
            when (naviData) {
                is ActionStatus -> mActionStatus.value = naviData
                is NaviStatus2 -> mNaviStatus2.value = naviData
                is SLAM3DPos -> {
                    mSLAM3DPos.value = naviData
                    if (naviData.slamStatus == SLAM3DPos.SLAMStatus.SLAM_STATUS_GKR_FAILED && !isGkrProcessing.value!!) {
//                        robotRepository.findPosition()
                    }
                }
                is NaviActionInfo -> mNaviActionInfo.value = naviData
                is NavigationMessage -> {
                    checkNaviMessage(naviData)
                }
                is NaviError -> {
                    checkNaviError(naviData)
                    println("else $naviData")
                }
            }
        }.launchIn(viewModelScope)
    }

    // PageConfig
    fun pageConfigUpdate() {
        viewModelScope.launch {
            pageConfigRepo.doUpdata()
        }
    }

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

    private fun unDocking(): Flow<Boolean> = flow {
        Log.d(TAG, "unDocking - start");
        if (mNaviStatus2.value?.bootSeq != NaviStatus2._e_boot_seq.eBOOT_ON_DOCK) {
            println("undocking fail, reason : Robot is not on place Docking Station")
            return@flow
        }
        RobotRepository.mNavigationManager.doUndockingEx()

        while (true) {
            if (mActionStatus.value?.geteActionStatus() == ActionStatus._e_action_status.eAction_Completed
                && mActionStatus.value?.actionInfo?.id == ActionInfo._e_action_id.eUnDocking
            ) {
                println("Catching Undocking Event Completed ")
                emit(true)
                break
            }
            delay(100)
        }

        RobotRepository.mNavigationManager.doRelativeRotationEx(180.0, 5.0)
        Log.d(TAG, "unDocking - end");
    }

    fun dockingRequest() {
        docking().onEach {
            when (it) {
                true -> {
                    println("docking~")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun docking(): Flow<Boolean> = flow {
        Log.d(TAG, "docking - start");

        // 1. Moveto Goal POI Goal
        val pois = RobotRepository.mPoiManager.getAllPoi()
        pois?.get(3)?.let { it1 -> robotRepository.moveWithPoi(it1) }

        while (true) {
            if (mActionStatus.value?.geteActionStatus() == ActionStatus._e_action_status.eAction_Completed) {
                println("Catching move to POI ChargerPOS Completed ")
                emit(true)
                break
            }
            delay(100)
        }

        RobotRepository.mNavigationManager.doDockingEx()

        while (true) {
            if (mActionStatus.value?.geteActionStatus() == ActionStatus._e_action_status.eAction_Completed
                && mActionStatus.value?.actionInfo?.id == ActionInfo._e_action_id.eDocking
            ) {
                println("Catching move to POI ChargerPOS Completed ")
                emit(true)
                break
            }
            delay(100)
        }
        Log.d(TAG, "docking - end");
    }

    // Use GoogleCloud API
    fun speak(_context: Context, text: String) {
        googleRepositiory.speak(_context, text)
    }

    fun stop() {
        googleRepositiory.stop()
    }

    // TODO(H, change R2 state)
    fun speechResponse() {
        if (ischatfirst == true) {
            googleRepositiory.ischatfirst = true
        }
        googleRepositiory.speachToText().onEach { result ->
            when (result) {
                is R2.Complete -> {
                    Log.i(TAG, "onSuccess")
                    _speechText.value = result.data
                    var translated: String
//                    GoogleTranslateV3.translate("Docent")

                    // TODO(translate result data when language is not KOREAN)
                    speechText.value?.let {
                        if (it == "") {
                            // retry 3회까지
                            return@onEach speechResponse()
                        }
                        getResponse(it)
                    }
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

    private fun checkNaviMessage(msg: NavigationMessage) {
        //DEBUGGING : print msg name
        //Log.d(TAG, "navi message by action" + EventIndex.convertToString(msg.currentMsg))
        when (msg.currentMsg) {
            NavigationMessageType.EXTERN_NAVI_EVENT_GKR_START -> {
                println("GKR Start")
                isGkrProcessing.value = true
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_GKR_END -> {
                println("GKR END")
                isGkrProcessing.value = false
            }
        }
    }

    class Factory(
        private val repository: ChatbotRepository,
        private val googleRepository: GoogleCloudRepository,
        private val robotRepository: RobotRepository,
        private val pageConfigRepo: PageConfigRepo
    ) :
        ViewModelProvider.Factory { // factory pattern
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(
                    this.repository,
                    this.googleRepository,
                    this.robotRepository,
                    this.pageConfigRepo
                ) as T
            }
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}