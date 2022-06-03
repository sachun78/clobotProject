package kr.co.clobot.robot.common.main.managers

import android.content.Context
import com.lge.robot.platform.power.PowerManager


object PowerManagerInstance {
    val instance = PowerManagerInstance

    private lateinit var mPowerManager : PowerManager


    fun createPowerManager(context: Context): PowerManager {
        mPowerManager = PowerManager(context)
        return mPowerManager
    }

    fun getPowerManager(): PowerManager {
        return mPowerManager
    }

}


