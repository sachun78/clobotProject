package com.lge.support.second.application.data.mqtt

data class Request<T>(
    val replyTo: String = "",
    val correlationId: String = "",
    val request: T?
)