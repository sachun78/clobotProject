package com.lge.support.second.application.data.robot

class NavigationMessage {

    private var _msg: Int = 0

    val currentMsg: Int
        get() = _msg

    constructor(msg: Int) {
        _msg = msg
    }
}