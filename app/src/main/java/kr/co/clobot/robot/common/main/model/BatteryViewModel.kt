package kr.co.clobot.robot.common.main.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lge.robot.platform.data.Battery

class BatteryViewModel : ViewModel() {
    private val _battery = MutableLiveData<Battery>()

    fun updateBattery(battery: Battery) {
        _battery.value = battery
    }
}