package com.lge.support.second.application.main.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.googlecloudmanager.common.Language
import com.example.googlecloudmanager.domain.GoogleCloudRepository
import com.lge.robot.platform.EventIndex
import com.lge.robot.platform.data.*
import com.lge.robot.platform.navigation.NavigationMessageType
import com.lge.robot.platform.util.poi.data.POI
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
import java.util.*
import kotlin.random.Random

class MainViewModel(
    private val repository: ChatbotRepository,
    private val googleRepositiory: GoogleCloudRepository,
    private val robotRepository: RobotRepository,
    private val pageConfigRepo: PageConfigRepo
) : ViewModel() {

    var ischatfirst: Boolean = false    ///////chat페이지 처음 진입하는 것인지 여부//////
    private val TAG = "MainViewModel"
    private val _queryResult: MutableLiveData<ChatbotData> = MutableLiveData<ChatbotData>()
    val queryResult: LiveData<ChatbotData>
        get() = _queryResult

    private val _speechText: MutableLiveData<String> = MutableLiveData()
    val speechText: LiveData<String>
        get() = _speechText

    private var mTimerTask: Timer

    // ROBOT DATA
    val mNavigationMessageType = MutableLiveData<Int>()
    val mActionStatus = MutableLiveData<ActionStatus>()
    val mNaviStatus2 = MutableLiveData<NaviStatus2>()
    val mSLAM3DPos = MutableLiveData<SLAM3DPos>()
    val mNaviActionInfo = MutableLiveData<NaviActionInfo>()

    val mPowerMode = MutableLiveData<Int>()
    val mBatteryData = MutableLiveData<Battery>()

    val isGkrProcessing = MutableLiveData(false)
    private val _isMoving = MutableLiveData(false)
    val isMoving: LiveData<Boolean> = _isMoving

    private val _emergency = MutableLiveData(false)
    val emergency: LiveData<Boolean> = _emergency

    private val _isSchedule = MutableLiveData(false)
    val isSchedule: LiveData<Boolean> = _isSchedule

    private val _isScheduleWait = MutableLiveData(false)
    val isScheduleWait: LiveData<Boolean> = _isScheduleWait

    private val _isUnDocking = MutableLiveData(false)
    val isUnDocking: LiveData<Boolean> = _isUnDocking

    private val _isDocking = MutableLiveData(false)
    val isDocking: LiveData<Boolean> = _isDocking

    private val _robotError = MutableLiveData<RobotError>()
    val robotError: LiveData<RobotError> = _robotError

    val batterySOC = MutableLiveData<Int>()

    val pois = RobotRepository.mPoiManager.getAllPoi()
    private var cruiseCount = 0

    override fun onCleared() {

        super.onCleared()
        mTimerTask.cancel()
    }

    // robot data callback
    init {

        robotRepository.powerMangerCallback.onEach { powerMode ->
            mPowerMode.value = powerMode as Int
        }.launchIn(viewModelScope)

        robotRepository.navigationManagerCallback.onEach { naviData ->
            when (naviData) {
                is ActionStatus -> mActionStatus.value = naviData
                is NaviStatus2 -> mNaviStatus2.value = naviData
                is SLAM3DPos -> {
                    mSLAM3DPos.value = naviData
                    if (naviData.slamStatus == SLAM3DPos.SLAMStatus.SLAM_STATUS_GKR_FAILED && !isGkrProcessing.value!!) {
                        isGkrProcessing.value = true
                        robotRepository.findPosition()
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

        robotRepository.sensorManagerCallback.onEach { sensorState ->
            when (sensorState) {
                SensorStatus.EmergencyStatusCode.NORMAL -> {
//                    println("Emergency Normal get")
                }
                SensorStatus.EmergencyStatusCode.EMERGENCY_PRESS -> {
                    println("Emergency Press get")
                    _emergency.value = true
                }
                SensorStatus.EmergencyStatusCode.EMERGENCY_RELEASE -> {
                    println("Emergency Release get")
                    _emergency.value = false
                    RobotRepository.mPowerManager.robotActivation()
                }
            }
        }.launchIn(viewModelScope)

        robotRepository.monitoringMangerCallback.onEach { data ->
            when (data) {
                is Battery -> {
                    mBatteryData.value = data
                    if (data.soc != batterySOC.value) {
                        batterySOC.value = data.soc
                    }
                    Log.d(TAG, "$data")
                }
                is RobotError -> {
                    _robotError.value = data
                }
                else -> println("monitoring else!")
            }
        }.launchIn(viewModelScope)

        mTimerTask = kotlin.concurrent.timer(period = 1000) {
            RobotRepository.mSensorManager.requestEmergencyStatus()
            RobotRepository.mMonitoringManager.batteryStatus
        }
    }

    // PageConfig
    fun pageConfigUpdate() {
        viewModelScope.launch {
            pageConfigRepo.doUpdata()
        }
    }

    // Use Chatbot
    fun getResponse(in_str: String, in_type: String? = null) {
        val domain_id = "seoul_mmca"
        val in_str = in_str
        val request = ChatRequest(
            domain_id,
            in_str,
            in_type = in_type ?: "query",
            parameters = ChatRequest.ChatRequestParameter(lang = googleRepositiory.language.toLocalString())
        )

        repository(request).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _queryResult.value = result.data
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

    private fun unDocking(): Flow<Boolean> = flow {
        Log.d(TAG, "unDocking - start");
        if (mNaviStatus2.value?.bootSeq != NaviStatus2._e_boot_seq.eBOOT_ON_DOCK) {
            println("undocking fail, reason : Robot is not on place Docking Station")
            return@flow
        }

        RobotRepository.mNavigationManager.doUndockingEx()
        RobotRepository.mNavigationManager.doRelativeRotationEx(180.0)
        Log.d(TAG, "unDocking - end");
    }

    fun move(poi: POI) {
        robotRepository.moveWithPoi(poi)
    }

    fun dockingRequest() {
        docking().launchIn(viewModelScope)
    }

    fun undockingRequest() {
        unDocking().launchIn(viewModelScope)
    }

    private fun docking(): Flow<Boolean> = flow {
        Log.d(TAG, "docking - start");
        _isDocking.value = true

        // 1. Moveto Goal POI Goal
        pois?.get(3)?.let { it1 -> robotRepository.moveWithPoi(it1) }
        emit(true)
    }

    fun cruise_request() {
        cruise.launchIn(viewModelScope)
    }

    val cruise: Flow<Boolean> = flow {
        _isSchedule.value = true
        cruiseCount = 0

        while (true) {
            if (isMoving.value == false) {
                if (isScheduleWait.value == true) {
                    // 도착후 10초간 대기
                    getResponse("promote_n")
                    delay(10 * 1000)
                    _isScheduleWait.value = false
                }

                // movo to random poi
                pois?.get(Random.nextInt(7))?.let { it1 ->
                    robotRepository.moveWithPoi(it1)
                    cruiseCount++
                }

                if (cruiseCount == 5) {
                    break
                }
            }
            delay(100)
        }

        _isSchedule.value = true
    }

    // Use GoogleCloud API
    fun speak(_context: Context, text: String) {
        googleRepositiory.speak(_context, text)
    }

    fun stop() {
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
        if (ischatfirst) {
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
//                        if (it == "") {
//                            // retry 3회까지
//                            return@onEach speechResponse()
//                        }
                        getResponse(it)
                    }

                    if (googleRepositiory.language != Language.Korean) {
                        translated = googleRepositiory.translate(speechText.value!!)
                        getResponse(translated)
                    } else {

                    }
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
        println("chackNaviError: $data")
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
        Log.d(TAG, "navi message by action " + EventIndex.convertToString(msg.currentMsg))
        when (msg.currentMsg) {
            NavigationMessageType.EXTERN_NAVI_EVENT_GKR_START -> {
                println("GKR Start")
                isGkrProcessing.value = true
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_GKR_END -> {
                println("GKR END")
                isGkrProcessing.value = false
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_MOVE_TO_GOAL_DONE -> {
                println("MOVE GOAL DONE")
                _isMoving.value = false
                if (isDocking.value == true) {
                    _isDocking.postValue(true)
                    RobotRepository.mNavigationManager.doDockingEx()
                }
                if (isSchedule.value == true) {
                    _isScheduleWait.value = true
                }
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_MOVE_TO_GOAL_ACCEPTED -> {
                println("MOVE GOAL ACCEPTED")
                if (isDocking.value != true) {
                    _isMoving.value = true
                }
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_DOCKING_ACCEPTED -> {
                println("DOCKING ACCEPTED")
                _isDocking.value = true
                _isUnDocking.value = false
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_DOCKING_DONE -> {
                println("DOCKING SUCCESS")
                _isDocking.value = false
            }
            // TODO(Failure 인 경우 처리 필요확인)

            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_UNDOCKING_ACCEPTED -> {
                println("UNDOCKING Accepted")
                _isUnDocking.value = true
                _isDocking.value = false
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_UNDOCKING_DONE -> {
                println("UNDOCKING SUCCESS")
                _isUnDocking.value = false
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