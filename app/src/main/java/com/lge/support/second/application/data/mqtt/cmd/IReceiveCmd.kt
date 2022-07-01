package com.lge.support.second.application.data.mqtt.cmd

import com.lge.support.second.application.data.mqtt.ResponseData
import com.lge.support.second.application.managers.mqtt.MqttMessage

interface IReceiveCmd {
    fun setAgentId(id: String)
    fun process(msg: MqttMessage?): Boolean
    fun getTopic(): String
    fun getPayLoad(): Any
}