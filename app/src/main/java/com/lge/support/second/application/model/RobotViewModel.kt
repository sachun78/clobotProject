package com.lge.support.second.application.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.lge.robot.platform.EventIndex
import com.lge.robot.platform.data.*
import com.lge.robot.platform.error.ErrorStatusBean
import com.lge.robot.platform.navigation.NavigationMessageType
import com.lge.robot.platform.util.poi.data.POI
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.data.robot.MoveState
import com.lge.support.second.application.data.robot.NaviError
import com.lge.support.second.application.data.robot.NaviErrorStatus
import com.lge.support.second.application.data.robot.NavigationMessage
import com.lge.support.second.application.repository.RobotRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import kotlin.random.Random


class RobotViewModel(
    private val robotRepository: RobotRepository, application: Application
) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "RobotViewModel"
    }

    // private val workManager = WorkManager.getInstance(application)
    private var mTimerTask: Timer
    var currentJob: Job? = null

    val mActionStatus = MutableLiveData<ActionStatus>()
    val mNaviStatus2 = MutableLiveData<NaviStatus2>()
    val mSLAM3DPos = MutableLiveData<SLAM3DPos>()
    val mNaviActionInfo = MutableLiveData<NaviActionInfo>()

    val mPowerMode = MutableLiveData<Int>()
    val mBatteryData = MutableLiveData<Battery>()

    val isGkrProcessing = MutableLiveData(false)
    private val _isMoving = MutableLiveData(false)
    val isMoving: LiveData<Boolean> = _isMoving

    private val _moveState = MutableLiveData<MoveState>()
    val moveState: LiveData<MoveState> get() = _moveState

    private val _emergency = MutableLiveData(false)
    val emergency: LiveData<Boolean> get() = _emergency

    private val _isSchedule = MutableLiveData(false)
    val isSchedule: LiveData<Boolean> = _isSchedule

    private val _isScheduleWait = MutableLiveData(false)
    val isScheduleWait: LiveData<Boolean> = _isScheduleWait

    private val _isDocking = MutableLiveData(false)
    val isDocking: LiveData<Boolean> = _isDocking

    private val _isDocent1 = MutableLiveData(false)
    val isDocent1: LiveData<Boolean> = _isDocent1

    private val _robotError = MutableLiveData<RobotError>()
    val robotError: LiveData<RobotError> = _robotError

    val batterySOC = MutableLiveData<Int>()

    val pois = RobotRepository.mPoiManager.getAllPoi()
    private var cruiseCount = 0

    private var gkr_try_count = 0

    override fun onCleared() {
        super.onCleared()
        mTimerTask.cancel()
    }

    /**
     * register a robot data callbacks
     *
     */
    init {
        robotRepository.powerMangerCallback.onEach { powerMode ->
            mPowerMode.value = powerMode as Int
        }.launchIn(viewModelScope)

        robotRepository.navigationManagerCallback.onEach { naviData ->
            when (naviData) {
                is ActionStatus -> {
                    mActionStatus.value = naviData
                    checkNaviActionStatus(naviData)
                }
                is NaviStatus2 -> mNaviStatus2.value = naviData
                is SLAM3DPos -> {
                    mSLAM3DPos.value = naviData
                }
                is NaviActionInfo -> {
                    mNaviActionInfo.value = naviData
                }
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
                SensorStatus.EmergencyStatusCode.EMERGENCY_PRESS -> {
                    _emergency.value = true
                }
                SensorStatus.EmergencyStatusCode.EMERGENCY_RELEASE -> {
                    println("1, EMERGENCY RELEASED")
                    _emergency.value = false
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
                    if (data.soc < 50) {
                        Log.i(TAG, "battery is lower than 50% detected.")
                    }
                }
                is RobotError -> {
                    _robotError.value = data
                    checkRobotError(data)
                }
                else -> println("monitoring else, $data")
            }
        }.launchIn(viewModelScope)

        mTimerTask = kotlin.concurrent.timer(period = 1000) {
            RobotRepository.mSensorManager.requestEmergencyStatus()
        }
    }

    fun docent1Request(page: MainActivity) {
        currentJob = docent1().onEach {
            when (it) {
                "moving" -> {
                    //TODO(상태알림, speak 위치이동)
//                    googleRepositiory.speak(
//                        MainActivity.mainContext(),
//                        "문화 해설 위치로 이동합니다. 저를 따라오세요."
//                    )
                }
                "move_done" -> {
                    page.changeFragment("docent")
                }
            }

        }.launchIn(viewModelScope)
    }

    private fun docent1(): Flow<String> = flow {
        // docking 상태가 아닌 home 위치 혹은 다른 위치에 있다고 가정.
        _isDocent1.value = true
        emit("moving")
        // 지점 이동
        pois?.get(3)?.let { it1 -> robotRepository.moveWithPoi(it1) }

        while (_isDocent1.value == true) {
            delay(100)
        }

        // page 전환
        emit("move_done")
    }

    private fun unDocking(): Flow<Boolean> = flow {
        Log.d(TAG, "unDocking - start");
        if (mNaviStatus2.value?.bootSeq != NaviStatus2._e_boot_seq.eBOOT_ON_DOCK) {
            println("undocking fail, reason : Robot is not on place Docking Station")
            return@flow
        }

        RobotRepository.mNavigationManager.doUndockingEx()
        RobotRepository.mNavigationManager.doRelativeRotationEx(180.0, 40.0)
        Log.d(TAG, "unDocking - end");
    }

    fun move(poi: POI) {
        robotRepository.moveWithPoi(poi)
    }

    fun onGkr() {
        robotRepository.findPosition()
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

        // 1. Move to Goal POI Goal
        if (pois != null && pois.size > 0) {
            pois[3].let { it1 -> robotRepository.moveWithPoi(it1) }
            emit(true)
        } else {
            emit(false)
        }
    }

    /**
     *  Test cruise Flow. for schedule and Entire docent
     *
     */

    private val cruise: Flow<Boolean> = flow {
        _isSchedule.value = true
        cruiseCount = 0

        while (true) {
            if (isMoving.value == false) {
                if (isScheduleWait.value == true) {
                    // 도착후 10초간 대기
                    //TODO(상태 알림, promote_n 요청)
                    delay(10 * 1000)
                    _isScheduleWait.value = false
                }

                // movo to random poi
                if (pois != null && pois.size > 0) {
                    pois[Random.nextInt(7)].let { it1 ->
                        robotRepository.moveWithPoi(it1)
                        cruiseCount++
                    }
                }

                if (cruiseCount == 5) {
                    break
                }
            }
            delay(100)
        }

        _isSchedule.value = true
    }

    /**
     * Robot Message Handlers.
     * @author heechan
     */

    private fun checkNaviActionStatus(status: ActionStatus) {
        when (status.motionStatus.geteStatus()) {
            NaviActionInfo.MOTION_STATUS_STOP_BY_PEOPLE,
            NaviActionInfo.MOTION_STATUS_STOP_BY_OBS,
            NaviActionInfo.MOTION_STATUS_PATH_FAIL_GOAL_OCCUPIED -> {
                //TODO(회피 상태값 변경)
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
                if (gkr_try_count < 3) {
                    robotRepository.findPosition()
                    gkr_try_count++
                }
            }
            ErrorReport._e_error_event.eERR_MAP_LOADING_FAIL.ordinal -> {
                // 네비 엔진에서 맵을 못찾을 경우
                println("slam Error 3")
            }
        }
    }

    private fun checkRobotError(data: RobotError) {
        Log.i(TAG, "checkRobotError: $data")

        val naviErrors = HashSet(NaviErrorStatus(data).naviErrors)
        if (naviErrors.isEmpty()) {
            return
        }

        for (error in naviErrors) {
            // check emergency
            Log.d(
                TAG,
                "code: " + error.code + " safety code: " + error.safetyCode + " ${error.subId}"
            )
            when (error.errorDevice) {
                ErrorStatusBean.TYPE.EMERGENCY -> {
                    val statusCode =
                        SensorStatus.EmergencyStatusCode.getStatus(
                            Integer.parseInt(
                                error.rawStatusCode,
                                10
                            )
                        )
                    when (statusCode) {
                        SensorStatus.EmergencyStatusCode.EMERGENCY_PRESS -> {
                            Log.d(TAG, "emergency pressed")
                            if (emergency.value == false) {
                                _emergency.value = true
                            }
                        }
                        SensorStatus.EmergencyStatusCode.EMERGENCY_RELEASE -> {
                            Log.d(TAG, "emergency released")
                            if (emergency.value == true) {
                                _emergency.value = false
                            }
                        }
                    }
                }
                ErrorStatusBean.TYPE.BUMPER -> {
                    println("BUMPER EVENT")
                }
                else -> Log.d(
                    TAG,
                    "NAVI DEVICE ERROR , WE NEED TO RESET NAVIGATION, DEVICE: ${error.errorDevice}"
                );
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
                _moveState.value = MoveState.MOVE_DONE
                if (isDocking.value == true) {
                    _isDocking.postValue(true)
                    RobotRepository.mNavigationManager.doDockingEx()
                }
                if (isSchedule.value == true) {
                    _isScheduleWait.value = true
                }

                if (isDocent1.value == true) {
                    _isDocent1.value = false
                }
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_MOVE_TO_GOAL_ACCEPTED -> {
                println("MOVE GOAL ACCEPTED")
                if (isDocking.value != true) {
                    _moveState.value = MoveState.MOVE_START
                }
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_DOCKING_ACCEPTED -> {
                println("DOCKING ACCEPTED")
                _isDocking.value = true
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_DOCKING_DONE -> {
                println("DOCKING SUCCESS")
                _isDocking.value = false
            }
            // TODO(Failure인 경우 처리 필요확인)

            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_UNDOCKING_ACCEPTED -> {
                println("UNDOCKING Accepted")
                _isDocking.value = false
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_UNDOCKING_DONE -> {
                println("UNDOCKING SUCCESS")
            }
        }
    }

    class Factory(
        private val robotRepository: RobotRepository,
        private val application: Application
    ) :
        ViewModelProvider.Factory { // factory pattern
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RobotViewModel::class.java)) {
                return RobotViewModel(
                    this.robotRepository,
                    this.application
                ) as T
            }
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}