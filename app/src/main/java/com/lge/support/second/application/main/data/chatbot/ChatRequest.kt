package com.lge.support.second.application.main.data.chatbot

import com.google.gson.annotations.SerializedName

data class ChatRequest(
    @SerializedName("domain_id") var domain_id: String,
    @SerializedName("in_str") var in_str: String,
    @SerializedName("in_type") var in_type: String = "query",
    @SerializedName("channel_id") var channel_id: Int = 0,
    @SerializedName("session_id") var session_id: String = "clobot_test89",
    @SerializedName("parameters") var parameters: ChatRequestParameter = ChatRequestParameter()
) {
    data class ChatRequestParameter(
        @SerializedName("user_id") var user_id: String = "clobot_test89",
        @SerializedName("device_type") var device_type: String = "robot",
        @SerializedName("lang") var lang: String = "ko",
    )
}

