package com.lge.support.second.application

import android.app.Application
import com.lge.support.second.application.database.CommonDatabase
import com.lge.support.second.application.repository.PageConfigRepo
import com.lge.support.second.application.repository.RobotRepository

class MainApplication : Application() {
    val mCommonDatabase by lazy { CommonDatabase.getInstance(this) }
    val mPageConfigRepo by lazy {
        PageConfigRepo(mCommonDatabase!!.pageConfigDao(), mCommonDatabase!!.ttsConfigDao())
    }
    val mRobotRepo by lazy {
        RobotRepository()
    }

    override fun onCreate() {
        super.onCreate()

    }

    override fun onTerminate() {
        super.onTerminate()

    }
}