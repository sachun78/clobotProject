package com.lge.support.second.application.main.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lge.robot.platform.data.Battery

class BatteryViewModel : ViewModel() {
    private val _battery = MutableLiveData<Battery>()

    fun updateBattery(battery: Battery) {
        _battery.value = battery
    }
}