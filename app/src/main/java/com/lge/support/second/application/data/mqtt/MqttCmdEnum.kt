package com.lge.support.second.application.data.mqtt

enum class MqttCmdEnum(val code: String) {
    STOP_CMD("stopControl"),
    MOVETO_CMD("moveTo"),
    SPEECH_CMD("speech"),
    COMEBACK_CMD("comeback"),
    DRIVE_CMD("drive");
}