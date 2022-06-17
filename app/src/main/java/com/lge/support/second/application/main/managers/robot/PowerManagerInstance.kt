package com.lge.support.second.application.main.managers.robot

import android.content.Context
import com.lge.robot.platform.power.PowerManager


object PowerManagerInstance {
    val instance = PowerManagerInstance

    private lateinit var mPowerManager: PowerManager
    private var initalized: Boolean = false

    fun createPowerManager(context: Context): PowerManager {
        mPowerManager = PowerManager(context)
        initalized = true
        return mPowerManager
    }

    fun getPowerManager(): PowerManager {
        return mPowerManager
    }

}


