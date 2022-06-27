package com.lge.support.second.application.managers.mqtt

import android.util.Log
import com.google.gson.Gson
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.lifecycle.MqttClientConnectedContext
import com.hivemq.client.mqtt.lifecycle.MqttClientDisconnectedContext
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient
import com.hivemq.client.mqtt.mqtt5.lifecycle.Mqtt5ClientDisconnectedContext
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck
import com.hivemq.client.mqtt.mqtt5.message.unsubscribe.unsuback.Mqtt5UnsubAck
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer
import java.util.regex.Pattern

abstract class Mqttv5Client : Consumer<Mqtt5Publish> {
    private lateinit var mId: String
    private lateinit var mClientId: String
    private lateinit var mTopicId: String
    private lateinit var mTopicResponse: String
    private lateinit var mClient: Mqtt5AsyncClient

    private val subscribeTopics: MutableSet<String> = HashSet()
    private val topicPattern = Pattern.compile("\\/(\\w+)\\/(\\w+)")
    private val charset = Charset.forName("UTF-8")

    fun getTopicId(): String {
        return mTopicId ?: ""
    }

    fun setId(id: String) {
        mTopicId = "Agent$id"
        mClientId = "agent$id"
        mId = id
    }


    private fun isSelf(name: String): Boolean {
        return mTopicId == name || mClientId == name
    }

    fun open(id: String, ip: String, port: Int, mqttConnId: String?, mqttConnPassword: String ) {
        addSubscribeTopic(createSubscribeAll(id))

        val client = MqttClient.builder()
            .identifier(mClientId)
            .serverHost(ip)
            .serverPort(port)
            .useMqttVersion5()
            .automaticReconnect()
            .initialDelay(1, TimeUnit.SECONDS)
            .maxDelay(10, TimeUnit.SECONDS)
            .applyAutomaticReconnect()
            .addConnectedListener { ctx: MqttClientConnectedContext? ->
                handleConnected()
                mClient.publishes(MqttGlobalPublishFilter.ALL, this)
            }
            .addDisconnectedListener { ctx: MqttClientDisconnectedContext ->
                Log.d(TAG,"[" + mClientId + "] reconnect mqtt: source[" + ctx.source + "] attempt[" + ctx.reconnector.attempts + "]")
                val ctx5 = ctx as Mqtt5ClientDisconnectedContext
                ctx5.reconnector
                    .connectWith()
                    .simpleAuth()
                    .username(mqttConnId!!)
                    .password(mqttConnPassword.toByteArray())
                    .applySimpleAuth()
                    .applyConnect()
            }
            .buildAsync()

        client.connectWith()
            .cleanStart(false)
            .simpleAuth()
            .username(mqttConnId!!)
            .password(mqttConnPassword.toByteArray())
            .applySimpleAuth()
            .send()

        mClient = client
        Log.d(TAG, "[$mClientId] open: host[$ip] port[$port]")
        try {
            Thread.sleep(2000)
        } catch (ex: InterruptedException) {
            Thread.currentThread().interrupt()
        }
    }

    fun close() {
        for (it in subscribeTopics) {
            unsubscribe(it)
        }
        mClient.disconnect()
    }

    protected fun addSubscribe(topic: String, distributed: Boolean) {
        val realTopic = createSubscribeTopic(topic, distributed)
        addSubscribeTopic(realTopic)
    }

    fun subscribe(id: String, distributed: Boolean) {
        val topic = createSubscribeTopic(id, distributed)
        addSubscribeTopic(id)
        subscribe(topic)
    }

    fun unsubscribeAll(id: String, distributed: Boolean) {
        val topic = createSubscribeTopic(id, distributed)
        delSubscribeTopic(id)
        unsubscribe(topic)
    }

    private fun handleConnected() {
        Log.d(TAG, "[$mClientId] connected mqtt")
        for (topic in subscribeTopics) {
            subscribe(topic)
        }
    }

    fun <T> publish(topic: String, payload: T) {
        val data: String = Gson().toJson(payload)
        Log.d("hjbae", "publish: ${data}")

        mClient.publishWith()
            .topic(topic)
            .payload(data.toByteArray())
            .send()
            .whenComplete { _, throwable ->
                if (null != throwable) {
                    Log.e(TAG, "publish failed !!!")
                } else {
                    Log.d(TAG, "publish success : topic($topic}), payload(${data})")
                }
            }
    }

    private fun subscribe(topic: String) {
        Log.d(TAG,"[" + mClientId + "] subscribing: topic[" + topic + "] state[" + mClient.state + "]")
        mClient.toAsync()
            .subscribeWith()
            .topicFilter(topic)
            .noLocal(true)
            .qos(MqttQos.AT_LEAST_ONCE)
            .send()
            .whenComplete { subAck: Mqtt5SubAck?, subThrowable: Throwable? ->
                if (null != subThrowable) {
                    Log.e(TAG,"[$mClientId] subscribe failed: topic[$topic]")
                } else {
                    Log.d(TAG,"[$mClientId] subscribed: topic[$topic]")
                }
            }
    }

    private fun unsubscribe(topic: String) {
        Log.d(TAG,"[" + mClientId + "] unsubscribing: topic[" + topic + "] state[" + mClient.state + "]")
        mClient.toAsync()
            .unsubscribeWith()
            .topicFilter(topic)
            .send()
            .whenComplete { subAck: Mqtt5UnsubAck?, subThrowable: Throwable? ->
                if (null != subThrowable) {
                    Log.e(TAG,"[$mClientId] unsubscribe failed: topic[$topic]")
                } else {
                    Log.d(TAG,"[$mClientId] unsubscribed: topic[$topic]")
                }
            }
    }

    private fun addSubscribeTopic(topic: String) {
        subscribeTopics.add(topic)
    }

    private fun delSubscribeTopic(topic: String) {
        subscribeTopics.remove(topic)
    }

    private fun createSubscribeTopic(id: String, distributed: Boolean): String {
        return if (distributed) {
            String.format("\$queue/%s", id)
        } else {
            id
        }
    }

    private fun createSubscribeAll(id: String): String {
        return String.format("/%s/#", id)
    }

    override fun accept(payload: Mqtt5Publish) {
        try {
            val topic = payload.topic.toString()
            val matcher = topicPattern.matcher(topic)
            if (!(matcher.find() && isSelf(matcher.group(1)))) {
                Log.w(TAG,"[$mClientId] invalid topic: topic[$topic]")
                return
            }
            val cmd = matcher.group(2)
            Log.d(TAG, ">> [$mClientId] RECV: cmd[$cmd]")
            Log.d(TAG,">> [" + mClientId + "] RECV: payload[" + String(payload.payloadAsBytes) + "]")
            //var isResponse = String(payload.payloadAsBytes).indexOf(""""replyTo":"/$mTopicId/response"""")
            var isResponse = String(payload.payloadAsBytes).indexOf(""""replyTo"""")
            Log.d("hjbae", """"replyTo":"/$mTopicId/response"""")
            Log.d("hjbae", "$isResponse")
            if (isResponse != -1) {
                onResponse(cmd, MqttMessage.of(payload))
            } else {
                onReceive(cmd, MqttMessage.of(payload))
            }
        } catch (e: Exception) {
            Log.d(TAG, "[" + mClientId + "] mqttServer Failed:" + e.message)
        }
    }

    protected abstract fun onReceive(cmd: String?, message: MqttMessage?)
    protected abstract fun onResponse(cmd: String?, message: MqttMessage?)

    companion object {
        private const val TAG = "mqtt5Client"
    }
}