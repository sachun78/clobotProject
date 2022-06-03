package kr.co.clobot.robot.common.main.model

class NavigationMessage {

    private var _msg: Int = 0

    val currentMsg: Int
        get() = _msg

    constructor(msg: Int) {
        _msg = msg
    }

}