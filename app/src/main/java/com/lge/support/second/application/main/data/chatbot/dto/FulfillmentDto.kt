package com.lge.support.second.application.main.data.chatbot.dto

data class FulfillmentDto(
    val custom_code: CustomCode,
    val emotion: String,
    val messages: List<Message>,
    val response_status: String,
    val response_type: String,
    val speech: List<String>,
    val template_id: String
)