package com.lge.support.second.application.data.mqtt.cmd

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lge.support.second.application.data.mqtt.Request
import com.lge.support.second.application.data.mqtt.ResponseData
import com.lge.support.second.application.managers.mqtt.MqttMessage
import java.io.Serializable

class StopRequest: Serializable

class StopCtrlCmd: IReceiveCmd {
    private lateinit var mTopic: String
    private lateinit var mResponse: ResponseData<StopRequest>

    override fun setAgentId(id: String) {
        TODO("Not yet implemented")
    }

    override fun process(msg: MqttMessage?): Boolean {

        val type = object : TypeToken<Request<StopRequest>>() {}.type
        var mRequest: Request<StopRequest> = Gson().fromJson(msg?.getPayload(), type)

        Log.d("hjbae", "getPayload: ${msg?.getPayload()}")
        Log.d("hjbae", "$mRequest")

        mTopic =  mRequest.replyTo
        mResponse = ResponseData<StopRequest>("", 0, "", StopRequest())

        mResponse.correlationId = mRequest.correlationId
        mResponse.status = 0
        mResponse.message = ""

        return true
    }

    override fun getTopic(): String {
        return mTopic
    }

    override fun getPayLoad(): Any {
        return mResponse
    }
}