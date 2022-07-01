package com.lge.support.second.application.managers.mqtt

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lge.support.second.application.data.mqtt.*
import com.lge.support.second.application.data.mqtt.cmd.IReceiveCmd
import com.lge.support.second.application.data.mqtt.cmd.StopCtrlCmd
import java.lang.RuntimeException

class MessageConnecter: Mqttv5Client() {

    private val HEARTBEAT_PERIOD_SEC = 8 // heartbeat 이벤트 주기
    private val HEARTBEAT_START_DELAY_SEC = 3
    private val MONITORING_PERIOD_SEC = 1 // monitoring data 이벤트 주기
    private val MONITORING_START_DELAY_SEC = 0
    private val SERVER_TIMEOUT_COUNT = 1
    private val mServerTimeToLeaveCount = 0

    private val properties: MqttProperties = MqttProperties()
    private lateinit var mReceiveCmdMap: HashMap<String, IReceiveCmd>

    init {
        setId("88")
        initFunc()
    }

    private fun initFunc() {
        if (!connectServer()) {
            Log.e(TAG, "Connection fail")
            return
        }
        if (!registerCmd()) {
            Log.e(TAG, "registerCmd fail")
            return
        }
    }

    private fun connectServer(): Boolean {
        try {
            open( getTopicId(), properties.ip, properties.port, properties.id, properties.passwd)
        } catch (e: RuntimeException) {
            Log.e(TAG, "failed to open mqtt connection: ${e.message}")
            return false
        }
        return true
    }

    private fun registerCmd(): Boolean {
        mReceiveCmdMap = HashMap()
        mReceiveCmdMap[MqttCmdEnum.STOP_CMD.code] = StopCtrlCmd()

        return true
    }

    override fun onReceive(cmd: String?, message: MqttMessage?) {
        Log.d("hjbae", "onReceive: ${message?.getPayload()}")
    }

    override fun onResponse(cmd: String?, message: MqttMessage?) {

        val curCmd: IReceiveCmd? = mReceiveCmdMap[cmd]
        if (curCmd?.process(message) == true) {
            publish(curCmd.getTopic(), curCmd.getPayLoad())
        }
    }

    companion object {
        private const val TAG = "messageConn"
    }
}