package com.lge.support.second.application.main.managers

import com.lge.robot.platform.management.monitoring.MonitoringManager

object MonitoringManagerInstance {
    val instance = MonitoringManagerInstance

    private lateinit var mMonitoringManager : MonitoringManager

    fun createMonitoringManager(): MonitoringManager {
        mMonitoringManager = MonitoringManager()
        return mMonitoringManager
    }

    fun getMonitoringManager(): MonitoringManager {
        return mMonitoringManager
    }

}
