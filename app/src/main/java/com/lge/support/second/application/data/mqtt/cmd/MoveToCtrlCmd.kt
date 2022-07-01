package com.lge.support.second.application.data.mqtt.cmd

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lge.support.second.application.MainApplication
import com.lge.support.second.application.data.mqtt.Request
import com.lge.support.second.application.data.mqtt.ResponseData
import com.lge.support.second.application.managers.mqtt.MqttMessage
import java.io.Serializable

data class MoveToRequest(
    var markerName: String = "",
    var markerId: String = "",
    var x: Float = 0f,
    var y: Float = 0f,
    var degree: Float = 0f,
    var speed: Float = 0f
)

class MoveToResponse: Serializable

class MoveToCtrlCmd: IReceiveCmd {
    private lateinit var mTopic: String
    private lateinit var mResponse: ResponseData<MoveToResponse>

    override fun setAgentId(id: String) {
        TODO("Not yet implemented")
    }

    override fun process(msg: MqttMessage?): Boolean {

        val type = object : TypeToken<Request<MoveToRequest>>() {}.type
        var mRequest: Request<MoveToRequest> = Gson().fromJson(msg?.getPayload(), type)

        Log.d("hjbae", "getPayload: ${msg?.getPayload()}")
        Log.d("hjbae", "$mRequest")

        MainApplication().mRobotRepo.moveToPos(
            mRequest.request?.x!!.toDouble(),
            mRequest.request?.y!!.toDouble(),
            7.0,
            mRequest.request?.degree!!.toDouble()
        )

        mTopic =  mRequest.replyTo
        mResponse = ResponseData<MoveToResponse>("", 0, "", MoveToResponse())

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