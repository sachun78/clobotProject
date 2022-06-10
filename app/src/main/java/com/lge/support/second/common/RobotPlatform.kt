package com.lge.support.second.application

import android.annotation.SuppressLint
import android.content.Context
import com.lge.robot.platform.LGRobotConnListener
import com.lge.robot.platform.LGRobotManager

object RobotPlatform {
    val instacne = RobotPlatform
    private var mLgRobotPlatform: LGRobotManager = LGRobotManager.getInstance()
    private var mIsConnected: Boolean = false

    fun connect(context: Context) {
        mLgRobotPlatform.initService(context, lgRobotConnListener);
    }

    val lgRobotConnListener = object : LGRobotConnListener {
        @SuppressLint("StaticFieldLeak")
        override fun onConnected() {
            mIsConnected = true
            //start service
        }

        override fun onDisconnected() {
            mIsConnected = false
            //end service
        }
    }
}