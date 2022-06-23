package com.lge.support.second.application

import android.app.Application
import com.lge.support.second.application.database.CommonDatabase
import com.lge.support.second.application.repository.PageConfigRepo

class MainApplication: Application() {
    val mCommonDatabase by lazy { CommonDatabase.getInstance(this) }
    val mPageConfigRepo by lazy {
        PageConfigRepo(mCommonDatabase!!.pageConfigDao(), mCommonDatabase!!.ttsConfigDao())
    }
}