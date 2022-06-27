package com.lge.support.second.application.managers.mqtt

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lge.support.second.application.data.mqtt.*
import java.lang.RuntimeException
import java.lang.reflect.Type
import kotlin.reflect.typeOf

class MessageConnecter: Mqttv5Client() {

    private val HEARTBEAT_PERIOD_SEC = 8 // heartbeat 이벤트 주기
    private val HEARTBEAT_START_DELAY_SEC = 3
    private val MONITORING_PERIOD_SEC = 1 // monitoring data 이벤트 주기
    private val MONITORING_START_DELAY_SEC = 0
    private val SERVER_TIMEOUT_COUNT = 1
    private val mServerTimeToLeaveCount = 0

    private val properties: MqttProperties = MqttProperties()

    init {
        setId("88")
        initFunc()
    }

    private fun initFunc() {
        if (!connectServer()) {
            Log.e(TAG, "Connection fail")
            return
        }
    }

    fun connectServer(): Boolean {
        try {
            open( getTopicId(), properties.ip, properties.port, properties.id, properties.passwd)
        } catch (e: RuntimeException) {
            Log.e(TAG, "failed to open mqtt connection: ${e.message}")
            return false
        }
        return true
    }

    override fun onReceive(cmd: String?, message: MqttMessage?) {
        Log.d("hjbae", "onReceive: ${message?.getPayload()}")
    }

    override fun onResponse(cmd: String?, message: MqttMessage?) {
        val type = object : TypeToken<Request<StartCtrl>>() {}.type
        var tmpResult: Request<StartCtrl> = Gson().fromJson(message?.getPayload(), type)

        Log.d("hjbae", "getPayload: ${message?.getPayload()}")
        Log.d("hjbae", "$tmpResult")

        publish(tmpResult.replyTo, tmpResult.request)
    }

    companion object {
        private const val TAG = "messageConn"
    }
}