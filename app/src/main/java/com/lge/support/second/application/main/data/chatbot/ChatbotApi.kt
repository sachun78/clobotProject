package com.lge.support.second.application.main.data.chatbot

import android.util.Log
import com.lge.support.second.application.main.data.chatbot.dto.ChatbotResponseDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatbotApi {
    @POST("/chat")
    suspend fun query(
        @Body parameters: ChatRequest,
    ): ChatbotResponseDto

    companion object {
        const val BASE_URL = "http://59.13.28.124:5201/"
        var chatbotService: ChatbotApi? = null

        fun getInstance(): ChatbotApi {
            if (chatbotService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                chatbotService = retrofit.create(ChatbotApi::class.java)
            }
            Log.i("ChatbotApi", "chat bot service created!")
            return chatbotService!!
        }
    }
}