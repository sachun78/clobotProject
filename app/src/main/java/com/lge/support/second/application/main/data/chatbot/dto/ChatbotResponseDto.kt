package com.lge.support.second.application.main.data.chatbot.dto

import com.lge.support.second.application.main.data.chatbot.ChatbotData


data class ChatbotResponseDto(
    val data: DataDto,
    val resCode: Int,
) {
    fun serialize(): ChatbotData {
        println(this)

        return ChatbotData(
            data = data,
            resCode = resCode
        )
    }
}