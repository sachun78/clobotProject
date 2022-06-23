package com.lge.support.second.application.managers.mqtt

import android.util.Log
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
    private var mId: String? = null
    private var mClientId: String? = null
    private var mTopicId: String? = null
    private var mClient: Mqtt5AsyncClient? = null
    private var mTopicResponse: String? = null

    private val subscribeTopics: MutableSet<String> = HashSet()
    private val topicPattern = Pattern.compile("\\/(\\w+)\\/(\\w+)")
    private val charset = Charset.forName("UTF-8")

    fun getTopicId(): String {
        return mTopicId ?: ""
    }

//    private val waitMap: MutableMap<String, Pending> = HashMap()

//    inner class Pending(val id: String, val cmd: String, val waitSec: Long?) {
//        private var timeout: Long? = null
//        private var result: ResponseMsg? = null
//        fun isTimeout(): Boolean {
//            return if (null == timeout) false else timeout <= System.currentTimeMillis()
//        }
//
//        @Throws(InterruptedException::class)
//        operator fun <R> get(clazz: Class<R>?): R? {
//            val res: ResponseMsg? = get()
//            return if (null == res) null else res.getResponse(clazz)
//        }
//
//        fun set(result: ResponseMsg?) {
//            this.result = result
//            synchronized(this) { this.notify() }
//        }
//
//        @Throws(InterruptedException::class)
//        fun get(): ResponseMsg? {
//            if (null == result) {
//                synchronized(this) {
//                    if (null == waitSec) {
//                        this.wait()
//                    } else {
//                        this.wait(waitSec * 1000)
//                    }
//                }
//            }
//            return result
//        }
//
//        init {
//            if (null != waitSec) {
//                timeout = System.currentTimeMillis() + waitSec * 1000
//            }
//        }
//    }

    fun setId(id: String) {
        mTopicId = "Agent$id"
        mClientId = "agent$id"
        mId = id
    }

    val topicResponse: String
        get() {
            mTopicResponse = String.format("/%s/response", mTopicId)
            return mTopicResponse!!
        }

    fun isSelf(name: String): Boolean {
        return mTopicId == name || mClientId == name
    }

    @JvmOverloads
    fun open(
        id: String,
        ip: String,
        port: Int,
        mqttConnId: String?,
        mqttConnPassword: String,
        distributed: Boolean = false
    ) {
        //this.id = id;
        //addSubscribeTopic(createSubscribeAll(id, distributed));
        addSubscribeTopic(createSubscribeAll(id))

        val client = MqttClient.builder()
            .identifier(mClientId!!)
            .serverHost(ip)
            .serverPort(port)
            .useMqttVersion5() //.sslWithDefaultConfig()
            .automaticReconnect()
            .initialDelay(1, TimeUnit.SECONDS)
            .maxDelay(10, TimeUnit.SECONDS)
            .applyAutomaticReconnect()
            .addConnectedListener { ctx: MqttClientConnectedContext? ->
                handleConnected()
                mClient!!.publishes(MqttGlobalPublishFilter.ALL, this)
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
        if (null != mClient) {
            for (it in subscribeTopics) {
                unsubscribe(it)
            }
            mClient!!.disconnect()
        }
    }

//    fun <T> send(target: String, cmd: String, data: T, waitSec: Long): Pending {
//        return send(target, cmd, data, waitSec, MqttQos.EXACTLY_ONCE)
//    }
//
//    fun <T> send(target: String, cmd: String, data: T, waitSec: Long, qos: MqttQos?): Pending {
//        val topic = String.format("/%s/%s", target, cmd)
//        val msgId = UUID.randomUUID().toString()
//        val payload: Request<*> = Request.of(topicResponse, msgId, data)
//        Log.d(TAG,"<< [$clientId] SENT: target[$target] cmd[$cmd]")
//        Log.d(TAG, "<< [" + clientId + "] SENT: payload[" + CmmUtils.toJsonString(payload) + "]")
//        push<Any>(topic, payload, qos)
//        Log.d(TAG,"<< [$clientId] PUSHED: target[$target] cmd[$cmd]")
//        return putWait(msgId, cmd, waitSec)
//    }
//
//    fun <T> push(topic: String, payload: T) {
//        push(topic, payload, MqttQos.AT_LEAST_ONCE)
//    }
//
//    fun <T> push(topic: String, payload: T, qos: MqttQos?) {
//        if (null == client) {
//            Log.e(TAG, "client not found.")
//            return
//        }
//        Log.i(TAG,"STATUS =" + client!!.state + ", HOST = " + client!!.config.serverHost + ", topic = " + topic)
//        if (MqttClientState.CONNECTED != client!!.state) {
//            throw RuntimeException("[$clientId] MQTT 서버에 접속안됨")
//        }
//        val data: String = CmmUtils.toJsonString(payload)
//        client!!.publishWith()
//            .topic(topic)
//            .qos(qos!!)
//            .payload(data.toByteArray(charset))
//            .send()
//    }

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
        if (null == mClient) return
        for (topic in subscribeTopics) {
            subscribe(topic)
        }
    }

    private fun subscribe(topic: String) {
        Log.d(TAG,"[" + mClientId + "] subscribing: topic[" + topic + "] state[" + mClient!!.state + "]")
        mClient!!.toAsync()
            .subscribeWith()
            .topicFilter(topic)
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
        Log.d(TAG,"[" + mClientId + "] unsubscribing: topic[" + topic + "] state[" + mClient!!.state + "]")
        mClient!!.toAsync()
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
        subscribeTopics.add(topic)
    }

    private fun createSubscribeAll(id: String, distributed: Boolean): String {
        return createSubscribeTopic(createSubscribeAll(id), distributed)
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
            if ("response" == cmd) {
//                val recvData: ResponseMsg = CmmUtils.toJsonObject(
//                    payload.payloadAsBytes,
//                    ResponseMsg::class.java
//                )
//                val token = popWait(recvData.getCorrelationId())
//                if (null == token) {
//                    Log.w(TAG,"[" + mClientId + "] invalid response(not exist request): correlationId[" + recvData.getCorrelationId() + "]")
//                    return
//                }
//                token.set(recvData)
//                onResponse(token.cmd, MqttMessage.of(payload))
                //onResponse(cmd, MqttMessage.of(payload))
            } else {
                //onReceive(cmd, MqttMessage.of(payload))
            }
        } catch (e: Exception) {
            Log.d(TAG, "[" + mClientId + "] mqttServer Failed:" + e.message)
        }
    }

//    @Synchronized
//    protected fun popWait(id: String): Pending? {
//        val ret = waitMap[id] ?: return null
//        waitMap.remove(id)
//        return ret
//    }
//
//    @Synchronized
//    protected fun putWait(id: String, value: String?, timeoutSec: Long?): Pending {
//        val ret: Pending = Pending(id, value, timeoutSec)
//        waitMap[id] = ret
//        return ret
//    }
//
//    protected fun getWait(id: String): Pending? {
//        return waitMap[id]
//    }

    protected abstract fun onReceive(cmd: String?, message: MqttMessage?)
    protected abstract fun onResponse(cmd: String?, message: MqttMessage?)

    companion object {
        private const val TAG = "mqtt5Client"
    }
}