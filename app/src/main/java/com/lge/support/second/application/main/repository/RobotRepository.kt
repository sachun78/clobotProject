package com.lge.support.second.application.main.repository

import android.util.Log
import com.lge.robot.platform.data.*
import com.lge.robot.platform.device.led.LedManager
import com.lge.robot.platform.device.sensor.SensorManager
import com.lge.robot.platform.device.sensor.SensorResultListener
import com.lge.robot.platform.management.monitoring.MonitoringEventListener
import com.lge.robot.platform.management.monitoring.MonitoringManager
import com.lge.robot.platform.management.monitoring.MonitoringResultListener
import com.lge.robot.platform.navigation.navigation.NavigationResultListener
import com.lge.robot.platform.power.IPowerModeListener
import com.lge.robot.platform.power.IPowerStateListener
import com.lge.robot.platform.util.poi.data.POI
import com.lge.support.second.application.MainActivity.Companion.mainContext
import com.lge.support.second.application.main.data.robot.NavigationMessage

import com.lge.support.second.application.main.managers.robot.NavigationManagerInstance
import com.lge.support.second.application.main.managers.robot.PowerManagerInstance
import com.lge.support.second.application.main.managers.robot.PoiDbManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class RobotRepository {
    val TAG = "RobotRepository"

    companion object {
        val mNavigationManager =
            NavigationManagerInstance.instance.createNavigationManager(mainContext())
        val mPowerManager = PowerManagerInstance.instance.createPowerManager(mainContext())
        val mMonitoringManager = MonitoringManager()
        val mPoiManager = PoiDbManager(mainContext())
        val mSensorManager = SensorManager()
        val mLedManager: LedManager = LedManager(mainContext())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val sensorManagerCallback: Flow<Any?> = callbackFlow {
        val sensorListener = object : SensorResultListener {
            override fun onError(p0: Int, p1: String?) {
                Log.e(TAG, "RobotError: $p0, $p1");
            }

            override fun onStatus(
                sensor: SensorResultListener.Sensor?,
                sensorStatuses: Array<out SensorStatus>?
            ) {
                if (sensor == null || sensorStatuses == null) {
                    Log.e(TAG, "sensor is$sensor, sensorStatuses is $sensorStatuses")
                    return
                }
                for (sensorStatus in sensorStatuses) {
                    sensorStatus.setSensorType(sensor)
                }

                for (sensorStatus in sensorStatuses) {
                    if (SensorStatus.Status.NOT_RECEIVED == sensorStatus.status) {
                        continue
                    }
                    when (sensor) {
                        SensorResultListener.Sensor.EMERGENCY -> {
                            val eCode =
                                SensorStatus.EmergencyStatusCode.getStatus(sensorStatus.statusCode)
                            when (eCode) {
                                SensorStatus.EmergencyStatusCode.NOT_DEFINED -> {}
                                SensorStatus.EmergencyStatusCode.EMERGENCY_PRESS -> {
                                    sendBlocking(SensorStatus.EmergencyStatusCode.EMERGENCY_PRESS)
                                }
                                SensorStatus.EmergencyStatusCode.EMERGENCY_RELEASE -> {
                                    sendBlocking(SensorStatus.EmergencyStatusCode.EMERGENCY_RELEASE)
                                }
                                SensorStatus.EmergencyStatusCode.NORMAL -> {
                                    sendBlocking(SensorStatus.EmergencyStatusCode.NORMAL)
                                }
                            }
                        }
                        else -> {}
                    }
                }
            }

            override fun onData(sensor: SensorResultListener.Sensor, data: Any) {
                when (sensor) {
                    SensorResultListener.Sensor.IMU -> {
                        var imuData = data as IMUData
                        Log.d(TAG, imuData.toString())
                    }
                    SensorResultListener.Sensor.BUMPER -> {
                        var bumperData = data as BumperData
                        Log.d(TAG, bumperData.toString())
                    }
                    SensorResultListener.Sensor.LIDAR -> {
                        var lidatData = data as LidarData
                        Log.d(TAG, lidatData.toString())
                    }
                    SensorResultListener.Sensor.SENSOR3D -> {
                        var sensor3DData = data as Sensor3DData
                        Log.d(TAG, sensor3DData.toString())
                    }
                    SensorResultListener.Sensor.SONAR -> {
                        var sonarData = data as MultiSonarData
                        Log.d(TAG, sonarData.toString())
                    }
                    else -> {
                        Log.d(TAG, "... sensor status -> $sensor & $data")
                    }

                }
            }

            override fun onResponse(p0: SensorResultListener.Sensor?, p1: Int, p2: Int) {
                TODO("Not yet implemented")
            }

            override fun onControlResponse(
                p0: SensorResultListener.Sensor?,
                p1: Int,
                p2: Int,
                p3: LaserDiagnosisData?
            ) {
                TODO("Not yet implemented")
            }
        }

        mSensorManager.setResultListener(sensorListener)

        awaitClose { }
    }

    val powerMangerCallback: Flow<Any?> = callbackFlow {
        var modeListener = object : IPowerModeListener {
            override fun onPowerModeChanged(mode: Int) {
                Log.d(TAG, "power mode chang done $mode");
                if (mode != -1 || mode != -2) {
                    sendBlocking(mode)
                }
            }

            override fun onReturnPowerMode(currentMode: Int) {
                Log.d(TAG, "current power mode, $currentMode");
                sendBlocking(currentMode)
            }
        }
        var stateListener = IPowerStateListener { TODO("Not yet implemented") }

        mPowerManager.setListener(modeListener, stateListener)
        awaitClose {}
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val monitoringMangerCallback: Flow<Any?> = callbackFlow {
        val eventListener = object : MonitoringEventListener {
            override fun onBatteryEvent(
                event: MonitoringEventListener.EventType?,
                battery: Battery?
            ) {
                val batteryData = battery as Battery

                Log.i(TAG, "onBatteryEvent: $batteryData");
                sendBlocking(batteryData)
            }

            override fun onError(error: RobotError?) {
                Log.e(TAG, "RobotError: ${error.toString()}");
                sendBlocking(error)
            }

            override fun onComponentEvent(p0: MonitoringEventListener.ComponentType?, p1: String?) {
                println("ComponentEvent")
            }

            override fun onEmergencyEvent(state: SensorStatus?) {
                println("EmergencyEvent $state")
            }
        }

        println("call monitoringManager setEventListener")
        mMonitoringManager.setEventListener(eventListener)

        val resultListener = object : MonitoringResultListener {
            override fun onError(p0: Int, p1: String?) {
                TODO("Not yet implemented")
            }

            override fun onResult(type: MonitoringResultListener.MonitoringType?, data: Any?) {
                data?.let {
                    when (type) {
                        MonitoringResultListener.MonitoringType.BATTERY_STATUS -> {
                            //when you call getBatteryStatus API
                            val batteryData = data as Battery
                            sendBlocking(batteryData)
                        }
                        else -> {
                            println("else monitoring result!")
                        }
                    }
                }
            }
        }

        println("call monitoringManager setResultListener")
        mMonitoringManager.setResultListener(resultListener)

        awaitClose { }
    }

    val navigationManagerCallback: Flow<Any?> = callbackFlow {
        val listener = object : NavigationResultListener {
            override fun onActionStatusResult(actionStatus: ActionStatus?) {
                super.onActionStatusResult(actionStatus)
                Log.d(TAG, "$actionStatus")
                sendBlocking(actionStatus)
            }

            override fun onNaviActionInfo(naviActionInfo: NaviActionInfo?) {
                super.onNaviActionInfo(naviActionInfo)
                Log.d(TAG, "onNaviActionInfo $naviActionInfo")
                sendBlocking(naviActionInfo)
            }

            override fun onSLAM3DResult(slamPos: SLAM3DPos?) {
                super.onSLAM3DResult(slamPos)
//                Log.d(TAG, "onSLAM3DResult $slamPos")
                sendBlocking(slamPos)
            }

            override fun onNaviStatus2Result(naviStatus: NaviStatus2?) {
                super.onNaviStatus2Result(naviStatus)
                Log.d(TAG, "onStatus2Result $naviStatus")
                sendBlocking(naviStatus)
            }

            override fun onNavigationEvent(
                navigationMessageType: Int,
                actionStatus: ActionStatus?
            ) {
                super.onNavigationEvent(navigationMessageType, actionStatus)
                Log.d(TAG, "onNavigationEvent $navigationMessageType status: $actionStatus")
                sendBlocking(NavigationMessage(navigationMessageType))
            }

            override fun onError(errorCode: Int, errorMsg: String?) {
                super.onError(errorCode, errorMsg)
                Log.e(TAG, "NaviError $errorMsg")
                sendBlocking(errorMsg)
            }

            override fun onAccept(navigationMessageType: Int, naviAcceptInfo: NaviAcceptInfo?) {
                super.onAccept(navigationMessageType, naviAcceptInfo)
                val msg = NavigationMessage(navigationMessageType)
                Log.d(TAG, "onAccept $msg")
                sendBlocking(msg)
            }
        }

        mNavigationManager.setListener(listener)

        awaitClose {}
    }

    fun findPosition() {
        mNavigationManager.requestGKR("CLOBOT_IPARK", "F7")
    }

    fun moveWithPoi(poi: POI) {
        val mInitPos = mPoiManager.getInitPosition()
        val pos = PosXYZDeg(
            poi.positionX.toDouble(), poi.positionY.toDouble(),
            poi.positionZ.toDouble(), poi.theta.toDouble()
        )
        mNavigationManager.doMoveToGoalEx(
            mInitPos?.buildingIndex, mInitPos?.floorIndex,
            pos, 1.0, 3.0
        )
    }

    private fun setLed() {
        mLedManager.selectColorWithMode(255, 1, 10, 5, 180)
    }

    init {
        mMonitoringManager.batteryStatus
        mPowerManager.robotActivation()
        setLed()
    }
}