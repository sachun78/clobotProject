package com.lge.support.second.application.managers.mqtt

data class MqttProperties(
    val ip: String = "broker.hivemq.com",
    val port: Int = 1883,
    val id: String = "agent",
    val passwd: String = "9zmffhqht@"
)