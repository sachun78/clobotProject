package com.lge.support.second.application

import android.app.Application
import com.lge.support.second.application.data.chatbot.ChatbotApi
import com.lge.support.second.application.database.CommonDatabase
import com.lge.support.second.application.repository.ChatbotRepository
import com.lge.support.second.application.repository.RobotRepository

class MainApplication : Application() {
    //val mCommonDatabase by lazy { CommonDatabase.getInstance(this) }

    companion object {
        private val chatbotService = ChatbotApi.instance

        val mRobotRepo by lazy {
            RobotRepository()
        }
        val mChatbotRepo by lazy {
            ChatbotRepository(chatbotService)
        }
    }


    override fun onCreate() {
        super.onCreate()

    }

    override fun onTerminate() {
        super.onTerminate()

    }
}