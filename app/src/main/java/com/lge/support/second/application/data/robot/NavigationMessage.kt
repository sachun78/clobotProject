package com.lge.support.second.application.data.robot

import com.lge.robot.platform.data.ActionStatus
import com.lge.robot.platform.data.NaviStatus

class NavigationMessage(msg: Int, actionStatus: ActionStatus?) {

    private var _msg: Int = msg
    private var _status: ActionStatus? = actionStatus

    val currentMsg: Int
        get() = _msg
    val currentStatus: ActionStatus?
        get() = _status
}