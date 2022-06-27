package com.lge.support.second.application.data.mqtt

data class Camera(
    val host: String,
    val port: Int,
    val auth: String
)

data class StartCtrl(
    val userId: String,
    val interval: Int,
    val camera: List<Camera>
)

data class Request<T>(
    val replyTo: String = "",
    val correlationId: String = "",
    val request: T
)