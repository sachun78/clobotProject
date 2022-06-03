package kr.co.clobot.robot.common

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.lge.robot.platform.data.*

import com.lge.robot.platform.management.monitoring.MonitoringEventListener
import com.lge.robot.platform.management.monitoring.MonitoringManager
import com.lge.robot.platform.management.monitoring.MonitoringResultListener
import com.lge.robot.platform.navigation.navigation.NavigationManager
import com.lge.robot.platform.navigation.navigation.NavigationResultListener
import com.lge.robot.platform.power.IPowerModeListener
import com.lge.robot.platform.power.IPowerStateListener
import com.lge.robot.platform.power.PowerManager
import kr.co.clobot.robot.common.main.data.BatteryEvent
import kr.co.clobot.robot.common.main.data.NaviError
import kr.co.clobot.robot.common.main.managers.MonitoringManagerInstance
import kr.co.clobot.robot.common.main.managers.NavigationManagerInstance
import kr.co.clobot.robot.common.main.managers.PowerManagerInstance
import kr.co.clobot.robot.common.main.model.NavigationMessage


class RobotEventService : Service() {
    companion object {
        const val TAG: String = "RobotEventService"
    }

    private lateinit var mNavigationManage: NavigationManager
    private lateinit var mPowerManager: PowerManager
    private lateinit var mMonitoringManager: MonitoringManager

    override fun onCreate() {
        super.onCreate()
        EventBus.instance
        mNavigationManage = NavigationManagerInstance.instance.createNavigationManager(this)
        mPowerManager = PowerManagerInstance.instance.createPowerManager(this)
        mMonitoringManager = MonitoringManagerInstance.instance.createMonitoringManager()
        mNavigationManage.setListener(mResultListener)
        mPowerManager.setListener(mPowerModeListener, mPowerStateListener)
        mMonitoringManager.setEventListener(mMonitoringEventListener)
        mMonitoringManager.setResultListener(mMonitoringResultListener)
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "RobotEventService onDestroy");
        super.onDestroy()
    }

    var mPowerModeListener = object : IPowerModeListener {
        override fun onPowerModeChanged(mode: Int) {
            Log.d(TAG, "power mode chang done");
            if (mode != -1 || mode != -2) {
                EventBus.instance.sendEvent(EventBus.POWER_TOPIC_MODE, mode)
            }
        }

        override fun onReturnPowerMode(currentMode: Int) {
            Log.d(TAG, "current power mode");
            EventBus.instance.sendEvent(EventBus.POWER_TOPIC_MODE, currentMode)
        }
    }
    var mPowerStateListener = object : IPowerStateListener {
        override fun onPowerStateChanged(state: Int) {
            TODO("Not yet implemented")
        }
    }

    var mResultListener = object : NavigationResultListener {
        override fun onNaviActionInfo(naviActionInfo: NaviActionInfo?) {
            super.onNaviActionInfo(naviActionInfo)
            naviActionInfo?.let { EventBus.instance.sendEvent(EventBus.NAVI_TOPIC, it) }
        }

        override fun onSLAM3DResult(slamPos: SLAM3DPos?) {
            super.onSLAM3DResult(slamPos)
            Log.d(TAG, "onSLAM3DResult" + slamPos)
            slamPos?.let { EventBus.instance.sendEvent(EventBus.NAVI_TOPIC, it) }
        }

        override fun onNaviStatus2Result(naviStatus: NaviStatus2?) {
            super.onNaviStatus2Result(naviStatus)
            naviStatus?.let { EventBus.instance.sendEvent(EventBus.NAVI_TOPIC, it) }
        }

        override fun onNavigationEvent(navigationMessageType: Int, actionStatus: ActionStatus?) {
            super.onNavigationEvent(navigationMessageType, actionStatus)
            navigationMessageType.let {
                EventBus.instance.sendEvent(
                    EventBus.NAVI_TOPIC,
                    NavigationMessage(navigationMessageType)
                )
            }
        }

        override fun onError(errorCode: Int, errorMsg: String?) {
            super.onError(errorCode, errorMsg)
            var naviError = errorMsg?.let { NaviError(errorCode, it) }
            naviError?.let {
                EventBus.instance.sendEvent(
                    EventBus.NAVI_TOPIC,
                    naviError
                )
            }
        }
    }

    var mMonitoringResultListener = object : MonitoringResultListener {
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
            var batteyInfo: BatteryEvent = BatteryEvent()
            batteyInfo.apply {
                type = event?.ordinal
                batteryData = battery
            }
            batteyInfo?.let { EventBus.instance.sendEvent(EventBus.BATTERY_TOPIC, it) }
        }

        override fun onError(error: RobotError?) {
            error?.let { EventBus.instance.sendEvent(EventBus.ERROR_TOPIC, it) }
        }

        override fun onComponentEvent(p0: MonitoringEventListener.ComponentType?, p1: String?) {
            TODO("Not using now")
        }

        override fun onEmergencyEvent(p0: SensorStatus?) {
            TODO("Not using now")
        }
    }
}