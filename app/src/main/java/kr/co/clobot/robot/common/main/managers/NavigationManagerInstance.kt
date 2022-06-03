package kr.co.clobot.robot.common.main.managers

import android.content.Context
import com.lge.robot.platform.navigation.navigation.NavigationManager


object NavigationManagerInstance {
    val instance = NavigationManagerInstance

    private lateinit var mNavigationManager : NavigationManager


    fun createNavigationManager(context: Context): NavigationManager {
        mNavigationManager = NavigationManager(context)
        return mNavigationManager
    }

    fun getNavigationManager(): NavigationManager {
        return mNavigationManager
    }

}
