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
import com.lge.support.second.application.data.robot.*
import com.lge.support.second.application.repository.RobotRepository
import com.lge.support.second.application.util.LEDState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import kotlin.random.Random


class RobotViewModel(
    private val robotRepository: RobotRepository, application: Application
) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "RobotViewModel"
    }

    private var mTimerTask: Timer

    val mActionStatus = MutableLiveData<ActionStatus>()
    val mNaviStatus2 = MutableLiveData<NaviStatus2>()
    val mSLAM3DPos = MutableLiveData<SLAM3DPos>()
    val mNaviActionInfo = MutableLiveData<NaviActionInfo>()

    val mPowerMode = MutableLiveData<Int>()
    val mBatteryData = MutableLiveData<Battery>()

    private val _isMoving = MutableLiveData(false)
    val isMoving: LiveData<Boolean> = _isMoving

    private val _moveState = MutableLiveData(MoveState.STAY)
    val moveState: LiveData<MoveState> get() = _moveState

    private val _robotState = MutableLiveData(RobotState.BOOT)
    val robotState: LiveData<RobotState> get() = _robotState

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
                is NaviStatus2 -> {
                    if (robotState.value == RobotState.INIT
                        && naviData.bootSeq == NaviStatus2._e_boot_seq.eBOOT_ON_DOCK
                    ) {
                        _robotState.value = RobotState.CHARGING
                    } else if (robotState.value != RobotState.INIT
                        && naviData.bootSeq == NaviStatus2._e_boot_seq.eBOOT_OPERATION
                        && mActionStatus.value?.geteActionStatus() == ActionStatus._e_action_status.eAction_Ready
                    ) {
                        _robotState.value = RobotState.INIT
                    }
                    mNaviStatus2.value = naviData
                }
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
                }
            }
        }.launchIn(viewModelScope)

        robotRepository.sensorManagerCallback.onEach { sensorState ->
            when (sensorState) {
                SensorStatus.EmergencyStatusCode.EMERGENCY_PRESS -> {
                    if (robotState.value != RobotState.EMERGENCY)
                        _robotState.value = RobotState.EMERGENCY
                }
                SensorStatus.EmergencyStatusCode.EMERGENCY_RELEASE -> {
                    if (robotState.value == RobotState.EMERGENCY) {
                        println("1, EMERGENCY RELEASED")
                        _robotState.value = RobotState.EMERGENCY_RELEASE
                    }
                }
            }
        }.launchIn(viewModelScope)

        robotRepository.monitoringMangerCallback.onEach { data ->
            when (data) {
                is Battery -> {
                    mBatteryData.value = data
                    Log.i(TAG, "battery: $data")
                    if (data.soc != batterySOC.value) {
                        batterySOC.value = data.soc
                    }
                    if (data.soc <= 30) {
                        Log.e(TAG, "battery is lower than 30% detected.")
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
            RobotRepository.mMonitoringManager.batteryStatus
        }
    }

    fun docentRequest(page: MainActivity, docentId: Int) {
        docent(docentId).onEach {
            when (it) {
                "move_done" -> {
                    page.changeFragment("move-arrive_1")
                }
                "move_done2" -> {
                    page.changeFragment("move-arrive_1")
                }
                "move_done3" -> {
                    page.changeFragment("move-arrive_1")
                }
            }

        }.launchIn(viewModelScope)
    }

    private fun docent(docentId: Int): Flow<String> = flow {
        // docking ????????? ?????? home ?????? ?????? ?????? ????????? ????????? ??????.
        if (robotState.value != RobotState.INIT) {
            return@flow
        }
        _isDocent1.value = true
        // ?????? ??????
        pois?.get(docentId)?.let { it1 -> robotRepository.moveWithPoi(it1) }

        while (_isDocent1.value == true) {
            // TODO(Check EMERGENCY)
            if (robotState.value == RobotState.EMERGENCY) {
                _isDocent1.value = false
                return@flow
            }
            delay(100)
        }
        // page ??????
        when (docentId) {
            0 -> emit("move_done")
            2 -> emit("move_done2")
            3 -> emit("move_done3")
            else -> emit("move_done")
        }
    }

    fun initialized() {
        robotRepository.initialized()
    }

    fun activation() {
        robotRepository.activation()
    }

    fun move(poi: POI) {
        robotRepository.moveWithPoi(poi)
    }

    fun stop() {
        robotRepository.stop()
    }

    fun pause() {
        robotRepository.pause()
    }

    fun resume() {
        robotRepository.resume()
    }

    fun onGkr() {
        robotRepository.findPosition()
    }

    fun setLed(state: LEDState = LEDState.NORMAL) {
        robotRepository.setLed(state)
    }

    fun dockingRequest() {
        Log.d(TAG, "docking - start");
        _isDocking.value = true
        // 1. Move to Goal POI Goal
        RobotRepository.mPoiManager.getHomeCharger()
            .let { it1 -> robotRepository.moveWithPoi(it1) }
    }

    fun undockingRequest() {
        viewModelScope.launch {
            Log.d(TAG, "unDocking - start")
            delay(500)
            if (mNaviStatus2.value?.bootSeq != NaviStatus2._e_boot_seq.eBOOT_ON_DOCK) {
                println("undocking fail, reason : Robot is not on place Docking Station")
                return@launch
            }

            RobotRepository.mNavigationManager.doUndockingEx()
            RobotRepository.mNavigationManager.doRelativeRotationEx(180.0, 40.0)
            Log.d(TAG, "unDocking - end");
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
                    // ????????? 10?????? ??????
                    //TODO(?????? ??????, promote_n ??????)
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
                //TODO(?????? ????????? ??????)
            }
        }
    }

    private fun checkNaviError(data: NaviError) {
        when (data.errorId) {
            ErrorReport._e_error_event.eERR_SLAM.ordinal -> {
                Log.e(TAG, "ERROR SLAM, $data")

            }
            ErrorReport._e_error_event.eERR_SLAM_NON_OPERATION_AREA.ordinal -> {
                //????????? ??? ????????? ?????? ?????????
                Log.e(TAG, "ERROR SLAM_NON_OPERATION_AREA, $data")
                if (_robotState.value == RobotState.BOOT && gkr_try_count < 3) {
                    onGkr()
                    gkr_try_count++
                } else {
                    _robotState.value = RobotState.BOOTFAIL
                }
            }
            ErrorReport._e_error_event.eERR_MAP_LOADING_FAIL.ordinal -> {
                // ?????? ???????????? ?????? ????????? ??????
                Log.e(TAG, "ERROR MAP_LOADING_FAIL, $data")
                _robotState.value = RobotState.BOOTFAIL
            }
            ErrorReport._e_error_event.eERR_ACTION.ordinal -> {
                Log.e(TAG, "ERROR checkNaviError : ACTION, $data")
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
                            if (robotState.value != RobotState.EMERGENCY) {
                                _robotState.value = RobotState.EMERGENCY
                            }
                        }
                        SensorStatus.EmergencyStatusCode.EMERGENCY_RELEASE -> {
                            Log.d(TAG, "emergency released")
                            if (robotState.value == RobotState.EMERGENCY) {
                                _robotState.value = RobotState.EMERGENCY_RELEASE
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

    fun recoverFromEmergency() {
        activation()
        setLed(LEDState.NORMAL)
        _robotState.value = RobotState.INIT
    }

    private fun checkNaviMessage(msg: NavigationMessage) {
        //DEBUGGING : print msg name
        Log.d(TAG, "navi message by action " + EventIndex.convertToString(msg.currentMsg))
        when (msg.currentMsg) {
            NavigationMessageType.EXTERN_NAVI_EVENT_GKR_START -> {
                println("GKR Start")
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_GKR_END -> {
                println("GKR END")
                robotRepository.initialized()
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_MOVE_TO_GOAL_DONE -> {
                println("MOVE GOAL DONE")
                _moveState.value = MoveState.MOVE_DONE
                if (isDocking.value == true) {
                    _isDocking.value = true
                    println("DOCKING REQ")
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
                    if (isDocent1.value == true) {
                        _moveState.value = MoveState.DOCENT_MOVE
                    } else {
                        _moveState.value = MoveState.MOVE_START
                    }
                } else {
                    _moveState.value = MoveState.DOCKING_MOVE
                }
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_DOCKING_DONE -> {
                println("DOCKING SUCCESS")
                _isDocking.value = false
            }

            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_UNDOCKING_ACCEPTED -> {
                println("UNDOCKING Accepted")
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_ACTION_UNDOCKING_DONE -> {
                Log.d(TAG, "UNDOCKING SUCCESS")
                RobotRepository.mNavigationManager.doRelativeRotationEx(180.0, 40.0)
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_INITIALIZED -> {
                Log.d(TAG, "NAVI INITIALIZED!")
                if (_robotState.value == RobotState.BOOT || _robotState.value == RobotState.BOOTFAIL) {
                    _robotState.value = RobotState.INIT
                }
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