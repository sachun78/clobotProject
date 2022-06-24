package com.lge.support.second.application.data.chatbot

import com.lge.support.second.application.data.chatbot.dto.CustomCode
import com.lge.support.second.application.data.chatbot.dto.Message

data class ChatbotData(
    val template_id: String,
    val response_status: String,
    val response_type: String,
    val emotion: String,
    val messages: List<Message>,
    val speech: List<String>,
    val customCode: CustomCode,
    val in_str: String
)
