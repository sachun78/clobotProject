package com.lge.support.second.application.data.chatbot.dto

import com.lge.support.second.application.data.chatbot.ChatbotData


data class ChatbotResponseDto(
    val data: DataDto,
    val resCode: Int,
) {
    fun serialize(): ChatbotData {
        return ChatbotData(
            template_id = data.result.fulfillment.template_id,
            response_status = data.result.fulfillment.response_status,
            response_type = data.result.fulfillment.response_type,
            emotion = data.result.fulfillment.emotion,
            messages = data.result.fulfillment.messages,
            speech = data.result.fulfillment.speech,
            customCode = data.result.fulfillment.custom_code,
            in_str = data.in_str
        )
    }
}