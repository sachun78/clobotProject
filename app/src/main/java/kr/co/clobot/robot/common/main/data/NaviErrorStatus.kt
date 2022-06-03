package kr.co.clobot.robot.common.main.data

import android.text.TextUtils
import android.util.Log
import com.lge.robot.platform.data.DeviceStatus
import com.lge.robot.platform.data.RobotError
import com.lge.robot.platform.error.ErrorConfig
import kr.co.clobot.robot.common.main.data.RobotErrorConstant.getTypeErrorConfigs
import org.json.JSONArray
import org.json.JSONObject


class NaviErrorStatus {
    val TAG = NaviErrorStatus::class.java.simpleName
    var naviErrors = ArrayList<ErrorConfig>()

    constructor(naviDeviceStatus: RobotError?) {
        if (naviDeviceStatus != null) {
            updateNVDeviceStatus(naviDeviceStatus)
        }
    }

    fun updateNVDeviceStatus(naviStatus: RobotError): Boolean {
        val descriptions = naviStatus.description
        if (TextUtils.isEmpty(descriptions) == false) {
            return try {
                val nvJson = JSONObject(descriptions)
                val naviTypeErrorConfigs = getTypeErrorConfigs(ErrorConfig.TYPE_NAVI_ERR)
                for (device in RobotErrorConstant.instance.naviDevices) {
                    for (config in naviTypeErrorConfigs!!) {
                        if (config.getErrorDevice().toString() == device) {
                            checkNaviDeviceError(nvJson.getJSONArray(device), config)
                        }

                    }
                }
                true
            } catch (e: Exception) {
                Log.e(TAG, "updateNVDeviceStatus $e")
                e.printStackTrace()
                false
            }
        }
        Log.i(TAG, "NV device description is empty")
        return false
    }

    private fun checkNaviDeviceError(jsonStatesDevice: JSONArray, config: ErrorConfig) {
        var errorOccur = false
        val errorCode = 0
        val deviceStates = arrayOfNulls<DeviceStatus>(jsonStatesDevice.length())
        for (i in 0 until jsonStatesDevice.length()) {
            try {
                if (jsonStatesDevice[i] == "null") {
                    errorOccur = false
                } else {
                    val devJson = JSONObject(jsonStatesDevice.getString(i)).toString()
                    deviceStates[i] = DeviceStatus(devJson)
                    errorOccur = parsingErrorStatus(config, deviceStates[i], i)
                    if (errorOccur) {
                        Log.d(TAG, "Device error occur " + deviceStates[i].toString())
                    }
                }
                if (errorOccur) {
                    break
                }
            } catch (e: Exception) {
                Log.e(TAG, "get Navi Device Error is failed $e")
            }
        }
    }

    private fun parsingErrorStatus(
        config: ErrorConfig,
        devStatus: DeviceStatus?,
        subId: Int
    ): Boolean {
        try {
            if (isMatchStatus(config.getNaviStatus(), devStatus!!.status)
                && isMatchSafetyCode(config.getSafetyCode(), devStatus.safetyCode)
                && isMatchStatusCode(config.getStatusCode(), devStatus.statusCode)
            ) {
                val statusCode = devStatus.statusCode
                config.setRawStatusCode(String.format("%02X", statusCode and 0xFF))
                config.setSubId(subId)
                Log.d(TAG, "add error $config")
                naviErrors.add(config)
                return true
            }
        } catch (e: Exception) {
            Log.e(TAG, "error config parsing fail!. please check error config $e")
        }
        return false
    }

    private fun isMatchStatus(configStatus: String, status: Int): Boolean {
        if (configStatus == "") {
            return true
        } else if (configStatus == "+" && status > 0) {
            return true
        } else if (configStatus.toInt() == status) {
            Log.d(TAG, "isMatchStatus for ($configStatus,$status) return true")
            return true
        }
        Log.d(TAG, "isMatchStatus for ($configStatus,$status) return false")
        return false
    }

    private fun isMatchSafetyCode(configSafety: String, safetyCode: Int): Boolean {
        if (configSafety == "") {
            return true
        } else if (configSafety == "+" && safetyCode > 0) {
            return true
        } else if (configSafety.toInt() == safetyCode) {
            Log.d(TAG, "isMatchSafetyCode for ($configSafety,$safetyCode) return true")
            return true
        }
        Log.d(TAG, "isMatchSafetyCode for ($configSafety,$safetyCode) return false")
        return false
    }

    private fun isMatchStatusCode(configStatusCode: String, statusCode: Int): Boolean {
        if (configStatusCode == "") {
            return true
        } else if (configStatusCode == "+" && statusCode > 0) {
            return true
        } else if (configStatusCode.toInt() == statusCode) {
            Log.d(TAG, "isMatchStatusCode for ($configStatusCode,$statusCode) return true")
            return true
        }
        Log.d(TAG, "isMatchStatusCode for ($configStatusCode,$statusCode) return false")
        return false
    }

    fun getErrors(): ArrayList<ErrorConfig>? {
        return naviErrors
    }
}