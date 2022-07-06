package com.lge.support.second.application.data.chatbot

import com.lge.support.second.application.data.chatbot.dto.ChatbotResponseDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatbotApi {
    @POST("/chat")
    suspend fun query(
        @Body parameters: ChatRequest,
    ): ChatbotResponseDto

    @POST("/chat")
    suspend fun breakChat(
        @Body parameters: ChatRequest,
    ): ChatbotResponseDto

    companion object {
        const val BASE_URL = "http://59.13.28.124:5201/"
        val instance: ChatbotApi by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(ChatbotApi::class.java)
        }
    }
}