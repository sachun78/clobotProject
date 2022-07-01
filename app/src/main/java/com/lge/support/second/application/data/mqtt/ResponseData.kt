package com.lge.support.second.application.data.mqtt

data class ResponseData<T>(
    var correlationId: String = "",
    var status: Int = 0,
    var message: String = "",
    var respose: T
)