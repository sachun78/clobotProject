package com.lge.support.second.application.main.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lge.robot.platform.data.NaviActionInfo
import com.lge.robot.platform.data.NaviStatus2
import com.lge.robot.platform.data.SLAM3DPos

class NavigationModel : ViewModel() {
    private val _slamPos = MutableLiveData<SLAM3DPos>()
    private val _naviActionInfo = MutableLiveData<NaviActionInfo>()
    private val _naviStatus = MutableLiveData<NaviStatus2>()
    private val _naviPowerMode = MutableLiveData<Int>()
    private val _x = MutableLiveData<String>()
    private val _y = MutableLiveData<String>()
    private val _z = MutableLiveData<String>()
    private val _degree = MutableLiveData<String>()

    val currentSlamPos: LiveData<SLAM3DPos>
        get() = _slamPos

    val currentNaviStatus: LiveData<NaviStatus2>
        get() = _naviStatus

    val currentNaviPowerMode: LiveData<Int>
        get() = _naviPowerMode

    val currentNaviActionInfo: LiveData<NaviActionInfo>
        get() = _naviActionInfo

    val currentX: LiveData<String>
        get() = _x

    val currentY: LiveData<String>
        get() = _y

    val currentZ: LiveData<String>
        get() = _z

    val currentDegree: LiveData<String>
        get() = _degree


    fun updateSlam(currentPos: SLAM3DPos) {
        _slamPos.value = currentPos
        _x.value = currentPos.robotPos.x.toString()
        _y.value = currentPos.robotPos.y.toString()
        _z.value = currentPos.robotPos.z.toString()
        _degree.value = currentPos.robotPos.deg.toString()
    }

    fun updateNaviStaus(currentNaviStatus: NaviStatus2) {
        _naviStatus.value = currentNaviStatus
        currentNaviStatus.toSimpleString()
    }

    fun updatePowerMode(currentPowerMode: Int) {
        _naviPowerMode.value = currentPowerMode
    }

    fun updateNaviActionInfo(currentNaviActionInfo: NaviActionInfo) {
        _naviActionInfo.value = currentNaviActionInfo
//        when (currentNaviActionInfo.actionStatus) {
//            NaviActionInfo.ACTION_ID_EMPTY -> {
//                println("action empty")
//            }
//            else -> {
//                println(currentNaviActionInfo.actionStatus)
//            }
//        }
//
//        when (currentNaviActionInfo.motionStatus) {
//            NaviActionInfo.MOTION_STATUS_COMPLETE -> {
//                println("motion complete")
//            }
//            else -> {
//                println(currentNaviActionInfo.motionStatus)
//            }
//        }
    }
}