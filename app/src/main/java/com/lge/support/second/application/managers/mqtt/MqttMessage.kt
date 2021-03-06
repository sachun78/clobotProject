package com.lge.support.second.application.managers.mqtt

import com.google.gson.Gson
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import java.lang.reflect.Type

class MqttMessage(private val data: Mqtt5Publish?) {
//    operator fun <R> get(clazz: Class<R>?): R? {
//        return if (null == data) null else Gson().fromJson(data.payloadAsBytes.toString(), clazz)
//    }

    val topic: String?
        get() = data?.topic?.toString()

    fun getPayload(): String? {
        return data?.payloadAsBytes?.let { String(it) }
    }

    companion object {
        fun of(data: Mqtt5Publish?): MqttMessage {
            return MqttMessage(data)
        }
    }
}