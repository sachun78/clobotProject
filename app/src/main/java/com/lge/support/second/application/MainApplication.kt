package com.lge.support.second.application

import android.app.Application
import androidx.work.Configuration
import com.lge.support.second.application.data.chatbot.ChatbotApi
import com.lge.support.second.application.database.CommonDatabase
import com.lge.support.second.application.repository.ChatbotRepository
import com.lge.support.second.application.repository.RobotRepository
import com.lge.support.second.application.repository.SceneConfigRepo

class MainApplication : Application(), Configuration.Provider {
    private val mCommonDatabase by lazy { CommonDatabase.getInstance(this) }
    val mPageInfoRepo by lazy { SceneConfigRepo.getInstance(mCommonDatabase.pageInfoDao()) }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
            .build()

    init {
        instance = this
    }
    
    companion object {
        lateinit var instance: MainApplication
        private val chatbotService = ChatbotApi.instance

        val mRobotRepo by lazy {
            RobotRepository()
        }

        val mChatbotRepo by lazy {
            ChatbotRepository(chatbotService)
        }

        fun appContext(): MainApplication {
            return instance
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onTerminate() {
        super.onTerminate()
    }
}