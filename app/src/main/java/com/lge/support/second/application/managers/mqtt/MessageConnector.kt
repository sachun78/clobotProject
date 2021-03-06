package com.lge.support.second.application.managers.mqtt

import android.util.Log
import com.lge.support.second.application.data.mqtt.*
import com.lge.support.second.application.data.mqtt.cmd.IReceiveCmd
import com.lge.support.second.application.data.mqtt.cmd.MoveToCtrlCmd
import com.lge.support.second.application.data.mqtt.cmd.StopCtrlCmd
import java.lang.RuntimeException

class MessageConnector private constructor(): Mqttv5Client() {

    private val HEARTBEAT_PERIOD_SEC = 8 // heartbeat 이벤트 주기
    private val HEARTBEAT_START_DELAY_SEC = 3
    private val MONITORING_PERIOD_SEC = 1 // monitoring data 이벤트 주기
    private val MONITORING_START_DELAY_SEC = 0
    private val SERVER_TIMEOUT_COUNT = 1
    private val mServerTimeToLeaveCount = 0

    private val properties: MqttProperties = MqttProperties()
    private lateinit var mReceiveCmdMap: HashMap<String, IReceiveCmd>

    companion object {
        private const val TAG = "messageConn"

        @Volatile private var instance: MessageConnector? = null

        @JvmStatic fun getInstance(): MessageConnector =
            instance ?: synchronized(this) {
                instance ?: MessageConnector().also {
                    instance = it
                }
            }
    }

    init {
        setId("88")
        //initFunc()
    }

    fun initFunc() {
        if (!connectServer()) {
            Log.e(TAG, "Connection fail")
            return
        }
        if (!registerCmd()) {
            Log.e(TAG, "registerCmd fail")
            return
        }

        Log.e("hjbae", "initFunc call")
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
        mReceiveCmdMap[MqttCmdEnum.MOVETO_CMD.code] = MoveToCtrlCmd()

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
}