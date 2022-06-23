package com.lge.support.second.application.data.chatbot.dto

data class DataDto(
    val channel_id: String,
    val dialogue_id: String,
    val domain_id: String,
    val in_str: String,
    val in_type: String,
    val life_cycle: Int,
    val log_level: String,
    val result: Result,
    val session_id: String,
    val timestamp: String
)