package com.lge.support.second.application.main.repository

import android.util.Log
import com.lge.robot.platform.data.*
import com.lge.robot.platform.management.monitoring.MonitoringEventListener
import com.lge.robot.platform.management.monitoring.MonitoringResultListener
import com.lge.robot.platform.navigation.navigation.NavigationResultListener
import com.lge.robot.platform.power.IPowerModeListener
import com.lge.robot.platform.power.IPowerStateListener
import com.lge.robot.platform.util.poi.data.POI
import com.lge.support.second.application.MainActivity.Companion.mainContext
import com.lge.support.second.application.main.data.robot.BatteryEvent

import com.lge.support.second.application.main.managers.robot.MonitoringManagerInstance
import com.lge.support.second.application.main.managers.robot.NavigationManagerInstance
import com.lge.support.second.application.main.managers.robot.PowerManagerInstance
import com.lge.support.second.application.main.managers.robot.PoiDbManager
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
        val mMonitoringManager = MonitoringManagerInstance.instance.createMonitoringManager()
        val mPoiManager = PoiDbManager(mainContext())
    }

    val monitoringMangerCallback: Flow<Any?> = callbackFlow {
        var eventListener = object : MonitoringEventListener {
            override fun onBatteryEvent(
                event: MonitoringEventListener.EventType?,
                battery: Battery?
            ) {
                val batteryInfo = BatteryEvent()
                batteryInfo.apply {
                    type = event?.ordinal
                    batteryData = battery
                }
                sendBlocking(batteryInfo)
            }

            override fun onError(error: RobotError?) {
                Log.e(TAG, "$error");
                sendBlocking(error)
            }

            override fun onComponentEvent(p0: MonitoringEventListener.ComponentType?, p1: String?) {
                println("ComponentEvent")
            }

            override fun onEmergencyEvent(p0: SensorStatus?) {
                println("EmergencyEvent")
            }
        }
        var resultListener = object : MonitoringResultListener {
            override fun onResult(type: MonitoringResultListener.MonitoringType?, data: Any?) {
                data?.let {
                    when (type) {
                        MonitoringResultListener.MonitoringType.BATTERY_STATUS -> {
                            //when you call getBatteryStatus API
                            var batteryInfo: BatteryEvent = BatteryEvent()
                            batteryInfo.apply {
                                batteryData = data as Battery
                            }
                            sendBlocking(batteryInfo)
                        }
                    }
                }
            }

            override fun onError(p0: Int, p1: String?) {
                TODO("Not using now")
            }
        }

        mMonitoringManager.setEventListener(eventListener)
        mMonitoringManager.setResultListener(resultListener)

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

    val navigationManagerCallback: Flow<Any?> = callbackFlow {
        val listener = object : NavigationResultListener {
            override fun onActionStatusResult(actionStatus: ActionStatus?) {
                super.onActionStatusResult(actionStatus)
                sendBlocking(actionStatus)
                Log.d(TAG, "$actionStatus")
            }

            override fun onNaviActionInfo(naviActionInfo: NaviActionInfo?) {
                super.onNaviActionInfo(naviActionInfo)
                sendBlocking(naviActionInfo)
            }

            override fun onSLAM3DResult(slamPos: SLAM3DPos?) {
                super.onSLAM3DResult(slamPos)
                sendBlocking(slamPos)
                Log.d(TAG, "onSLAM3DResult" + slamPos)
            }

            override fun onNaviStatus2Result(naviStatus: NaviStatus2?) {
                super.onNaviStatus2Result(naviStatus)
                sendBlocking(naviStatus)
                Log.d(TAG, "onStatus2Result" + naviStatus)
            }

            override fun onNavigationEvent(
                navigationMessageType: Int,
                actionStatus: ActionStatus?
            ) {
                super.onNavigationEvent(navigationMessageType, actionStatus)
                sendBlocking(actionStatus)
                Log.d(TAG, "onNavigationEvent $navigationMessageType status: $actionStatus")
            }

            override fun onError(errorCode: Int, errorMsg: String?) {
                super.onError(errorCode, errorMsg)
                sendBlocking(errorMsg)
                Log.d(TAG, "NaviError $errorMsg")
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

    init {
        println("robot Repo init")
        mPowerManager.robotActivation()
    }
}