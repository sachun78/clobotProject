package com.lge.support.second.application

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.lge.robot.platform.LGRobotManager
import com.lge.robot.platform.data.*

import com.lge.robot.platform.management.monitoring.MonitoringEventListener
import com.lge.robot.platform.management.monitoring.MonitoringManager
import com.lge.robot.platform.management.monitoring.MonitoringResultListener
import com.lge.robot.platform.navigation.navigation.NavigationManager
import com.lge.robot.platform.navigation.navigation.NavigationResultListener
import com.lge.robot.platform.power.IPowerModeListener
import com.lge.robot.platform.power.IPowerStateListener
import com.lge.robot.platform.power.PowerManager
import com.lge.robot.platform.util.poi.data.POI
import com.lge.support.second.application.main.data.BatteryEvent
import com.lge.support.second.application.main.data.NaviError
import com.lge.support.second.application.main.managers.robot.MonitoringManagerInstance
import com.lge.support.second.application.main.managers.robot.NavigationManagerInstance
import com.lge.support.second.application.main.managers.robot.PowerManagerInstance
import com.lge.support.second.application.main.poi.PoiDbManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class RobotEventService : Service() {

    companion object {
        const val TAG: String = "RobotEventService"
        val mNavigationMessageType = MutableLiveData<Int>()
        val mActionStatus = MutableLiveData<ActionStatus>()
        val mNaviStatus2 = MutableLiveData<NaviStatus2>()
        val mSLAM3DPos = MutableLiveData<SLAM3DPos>()
        val mNaviActionInfo = MutableLiveData<NaviActionInfo>()

        private lateinit var mNavigationManager: NavigationManager
        private lateinit var mPowerManager: PowerManager
        private lateinit var mMonitoringManager: MonitoringManager
        private lateinit var mPoiManager: PoiDbManager

        fun unDocking(): Flow<Boolean> = flow {
            Log.d(TAG, "unDocking - start");
            if (mNaviStatus2.value?.bootSeq != NaviStatus2._e_boot_seq.eBOOT_ON_DOCK) {
                println("undocking fail, reason : Robot is not on place Docking Station")
                return@flow
            }
            mNavigationManager.doUndockingEx()

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

            mNavigationManager.doRelativeRotationEx(180.0, 5.0)

            Log.d(TAG, "unDocking - end");
        }

        fun docking(): Flow<Boolean> = flow {
            Log.d(TAG, "docking - start");

            // 1. Moveto Goal POI Goal
            val pois = mPoiManager.getAllPoi()
            pois?.get(3)?.let { it1 -> moveWithPoi(it1) }

            while (true) {
                if (mActionStatus.value?.geteActionStatus() == ActionStatus._e_action_status.eAction_Completed) {
                    println("Catching move to POI ChargerPOS Completed ")
                    emit(true)
                    break
                }
                delay(100)
            }

            mNavigationManager.doDockingEx()

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

        private fun moveWithPoi(poi: POI) {
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
    }

    override fun onCreate() {
        println("RobotEventService Created")
        super.onCreate()
        EventBus.instance
        mNavigationManager = NavigationManagerInstance.instance.createNavigationManager(this)
        mPowerManager = PowerManagerInstance.instance.createPowerManager(this)
        mMonitoringManager = MonitoringManagerInstance.instance.createMonitoringManager()
        mPoiManager = PoiDbManager(this@RobotEventService)
        mNavigationManager.setListener(mResultListener)
        mPowerManager.setListener(mPowerModeListener, mPowerStateListener)
        mMonitoringManager.setEventListener(mMonitoringEventListener)
        mMonitoringManager.setResultListener(mMonitoringResultListener)

//        mPowerManager.robotActivation()
//        mNavigationManager.requestInitialized()
//        mNavigationManager.requestEngineRunning()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "RobotEventService onDestroy");
//        mNavigationManager.doTerminate()
        super.onDestroy()
    }

    private var mPowerModeListener = object : IPowerModeListener {
        override fun onPowerModeChanged(mode: Int) {
            Log.d(TAG, "power mode chang done");
            if (mode != -1 || mode != -2) {
                EventBus.instance.sendEvent(EventBus.POWER_TOPIC_MODE, mode)
            }
        }

        override fun onReturnPowerMode(currentMode: Int) {
            Log.d(TAG, "current power mode, $currentMode");
            EventBus.instance.sendEvent(EventBus.POWER_TOPIC_MODE, currentMode)
        }
    }
    private var mPowerStateListener = IPowerStateListener { TODO("Not yet implemented") }

    private var mResultListener = object : NavigationResultListener {
        override fun onAccept(navigationMessageType: Int, naviAcceptInfo: NaviAcceptInfo?) {
            super.onAccept(navigationMessageType, naviAcceptInfo)
            Log.d(
                TAG,
                "onAccept navigationMessageType: $navigationMessageType naviAcceptInfo: $naviAcceptInfo"
            )
        }

        override fun onActionStatusResult(actionStatus: ActionStatus?) {
            super.onActionStatusResult(actionStatus)
            Log.d(TAG, "$actionStatus")
        }

        override fun onNaviActionInfo(naviActionInfo: NaviActionInfo?) {
            super.onNaviActionInfo(naviActionInfo)
            mNaviActionInfo.value = naviActionInfo
//            Log.d(TAG, "$naviActionInfo")
            if (naviActionInfo?.reason == 1) {
                println("reason: 1")
                mNavigationManager.requestInitialized()
                mNavigationManager.requestGKR("CLOBOT_IPARK", "F7")
            }
        }

        override fun onSLAM3DResult(slamPos: SLAM3DPos?) {
            super.onSLAM3DResult(slamPos)
            mSLAM3DPos.value = slamPos
            Log.d(TAG, "onSLAM3DResult" + slamPos)
        }

        override fun onNaviStatus2Result(naviStatus: NaviStatus2?) {
            super.onNaviStatus2Result(naviStatus)
            mNaviStatus2.value = naviStatus
            Log.d(TAG, "onStatus2Result" + naviStatus)
        }

        override fun onNavigationEvent(navigationMessageType: Int, actionStatus: ActionStatus?) {
            super.onNavigationEvent(navigationMessageType, actionStatus)
            Log.d(TAG, "onNavigationEvent $navigationMessageType status: $actionStatus")
            mActionStatus.value = actionStatus
            mNavigationMessageType.value = navigationMessageType
        }

        override fun onError(errorCode: Int, errorMsg: String?) {
            super.onError(errorCode, errorMsg)
            Log.d(TAG, "NaviError $errorMsg")
            var naviError = errorMsg?.let { NaviError(errorCode, it) }
            naviError?.let {
                EventBus.instance.sendEvent(
                    EventBus.NAVI_TOPIC,
                    naviError
                )
            }
        }
    }

    private var mMonitoringResultListener = object : MonitoringResultListener {
        override fun onResult(type: MonitoringResultListener.MonitoringType?, data: Any?) {
            data?.let {
                when (type) {
                    MonitoringResultListener.MonitoringType.BATTERY_STATUS -> {
                        //when you call getBatteryStatus API
                        var batteyInfo: BatteryEvent = BatteryEvent()
                        batteyInfo.apply {
                            batteryData = data as Battery
                        }
                        batteyInfo?.let { EventBus.instance.sendEvent(EventBus.BATTERY_TOPIC, it) }
                    }
                }
            }
        }

        override fun onError(p0: Int, p1: String?) {
            TODO("Not using now")
        }
    }

    var mMonitoringEventListener = object : MonitoringEventListener {
        override fun onBatteryEvent(event: MonitoringEventListener.EventType?, battery: Battery?) {
            var batteyInfo = BatteryEvent()
            batteyInfo.apply {
                type = event?.ordinal
                batteryData = battery
            }
            batteyInfo.let { EventBus.instance.sendEvent(EventBus.BATTERY_TOPIC, it) }
        }

        override fun onError(error: RobotError?) {
            error?.let { EventBus.instance.sendEvent(EventBus.ERROR_TOPIC, it) }
        }

        override fun onComponentEvent(p0: MonitoringEventListener.ComponentType?, p1: String?) {
            println("ComponentEvent")
        }

        override fun onEmergencyEvent(p0: SensorStatus?) {
            println("EmergencyEvent")
        }
    }
}