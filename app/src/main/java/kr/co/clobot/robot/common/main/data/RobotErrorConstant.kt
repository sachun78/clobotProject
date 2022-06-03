package kr.co.clobot.robot.common.main.data

import com.lge.robot.platform.EventIndex
import com.lge.robot.platform.data.bean.NavigationErrorBean
import com.lge.robot.platform.error.ErrorConfig
import com.lge.robot.platform.error.ErrorStatusBean
import java.util.*
import kotlin.collections.ArrayList

//this class will be move to Library
object RobotErrorConstant {

    const val EMERGENCY = "EMERGENCY"
    const val NAVI_PC = "NAVI_PC"
    const val WHEEL_BRAKE = "WHEEL_BRAKE"
    const val MAGNETIC = "MAGNETIC"
    const val LIDAR = "LIDAR"
    const val WHEEL_MOTOR = "WHEEL_MOTOR"
    const val SENSOR3D = "SENSOR3D"
    const val FAN_MOTOR = "FAN_MOTOR"
    const val IMU = "IMU"
    const val BUMPER = "BUMPER"
    const val SONAR = "SONAR"
    const val MICOM = "MICOM"
    const val DOCKING = "DOCKING"
    const val UNDOCKING = "UNDOCKING"
    const val DRIVING_TIMEOUT = "DRIVING_TIMEOUT"

    val instance = RobotErrorConstant

    val naviDevices = arrayOf<String>(
        EMERGENCY,
        NAVI_PC,
        WHEEL_BRAKE,
        MAGNETIC,
        LIDAR,
        WHEEL_MOTOR,
        SENSOR3D,
        FAN_MOTOR,
        IMU,
        BUMPER,
        SONAR,
        MICOM
    )
    val ROBOT_ERROR_CONFIGS: ArrayList<ErrorConfig> = ArrayList(
        Arrays.asList( //RP Errors
            //TODO : add failed POI name as Locale
            ErrorConfig(
                ErrorStatusBean.TYPE.DRIVING, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.NAVI_EVENT_ERROR, -1, -1,
                NavigationErrorBean.eERR_TIMEOUT, "3000", true, true, "TIMEOUT error"
            ),  //TODO : need to check moving fail
            ErrorConfig(
                ErrorStatusBean.TYPE.BATTERY, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.RP_BATTERY_STATUS, 0x400, -1,
                NavigationErrorBean.eERR_EMPTY, "8000", true, false, "BATTERY unknown error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.BATTERY, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.RP_BATTERY_STATUS, 0x80, -1,
                NavigationErrorBean.eERR_EMPTY, "8001", true, false, "BATTERY discharging error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.BATTERY, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.RP_BATTERY_STATUS, 0x100, -1,
                NavigationErrorBean.eERR_EMPTY, "8002", true, false, "BATTERY unknown error"
            ),  //TODO : need to check abnormal error should be reported to server, and is real error?
            ErrorConfig(
                ErrorStatusBean.TYPE.BATTERY, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.RP_BATTERY_STATUS, 0x200, -1,
                NavigationErrorBean.eERR_EMPTY, "8003", false, false, "BATTERY abnormal zero error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.BATTERY, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.RP_BATTERY_STATUS, 0x040, -1,
                NavigationErrorBean.eERR_EMPTY, "8180", true, false, "BATTERY BMS error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.BATTERY, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.RP_BATTERY_STATUS, 0x002, -1,
                NavigationErrorBean.eERR_EMPTY, "8181", true, false, "BATTERY low voltage error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.BATTERY, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.RP_BATTERY_STATUS, 0x001, -1,
                NavigationErrorBean.eERR_EMPTY, "8182", true, false, "BATTERY high voltage error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.BATTERY, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.RP_BATTERY_STATUS, 0x004, -1,
                NavigationErrorBean.eERR_EMPTY, "8183", true, false, "BATTERY high current error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.DRIVING, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.NAVI_EVENT_ERROR, -1, -1,
                NavigationErrorBean.eERR_ACTION, "2020", true, false, "NAVI Path error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.DOCKING, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.NAVI_ACTION_INFO, -1, -1,
                NavigationErrorBean.eERR_ACTION, "2021", true, false, "DOCKING error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.UNDOCKING, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.NAVI_ACTION_INFO, -1, -1,
                NavigationErrorBean.eERR_ACTION, "2022", true, false, "UNDOCKING error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.SLAM, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.NAVI_EVENT_ERROR, -1, -1,
                NavigationErrorBean.eERR_SLAM_NON_OPERATION, "2030", true, false, "SLAM out error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.SLAM, ErrorConfig.TYPE_EVENT_ERR, "", "", "",
                EventIndex.NAVI_EVENT_ERROR, -1, -1,
                NavigationErrorBean.eERR_SLAM, "2031", true, false, "SLAM out error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.SLAM,
                ErrorConfig.TYPE_EVENT_ERR,
                "",
                "",
                "",
                EventIndex.NAVI_EVENT_ERROR,
                -1,
                -1,
                NavigationErrorBean.eERR_MAP_LOADING_FAIL,
                "2032",
                true,
                false,
                "SLAM map loading error"
            ),  //Navi errors
            ErrorConfig(
                ErrorStatusBean.TYPE.NAVI_PC, ErrorConfig.TYPE_NAVI_ERR, "", "+", "",
                -1, -1, -1,
                NavigationErrorBean.eERR_SENSOR, "3080", true, false, "NAVI_PC error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.MICOM, ErrorConfig.TYPE_NAVI_ERR, "", "+", "",
                -1, -1, -1,
                NavigationErrorBean.eERR_SENSOR, "4020", true, false, "MICOM dead error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.IMU, ErrorConfig.TYPE_NAVI_ERR, "", "+", "",
                -1, -1, -1,
                NavigationErrorBean.eERR_SENSOR, "4040", true, false, "IMU error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.BUMPER, ErrorConfig.TYPE_NAVI_ERR, "", "+", "",
                -1, -1, -1,
                NavigationErrorBean.eERR_EMPTY, "4050", false, true, "BUMPER pressed notification"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.WHEEL_MOTOR, ErrorConfig.TYPE_NAVI_ERR, "", "+", "",
                -1, -1, -1,
                NavigationErrorBean.eERR_SENSOR, "4060", true, false, "MOTOR error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.LIDAR, ErrorConfig.TYPE_NAVI_ERR, "", "+", "",
                -1, -1, -1,
                NavigationErrorBean.eERR_SENSOR, "4080", true, false, "LIDAR error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.SENSOR3D, ErrorConfig.TYPE_NAVI_ERR, "", "+", "",
                -1, -1, -1, NavigationErrorBean.eERR_SENSOR, "4090", true, false, "SENSOR3D error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.SONAR, ErrorConfig.TYPE_NAVI_ERR, "", "+", "",
                -1, -1, -1,
                NavigationErrorBean.eERR_SENSOR, "4100", true, false, "SONAR error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.EMERGENCY,
                ErrorConfig.TYPE_NAVI_ERR,
                "",
                "6",
                "",
                -1,
                -1,
                -1,
                NavigationErrorBean.eERR_EMPTY,
                "4130",
                false,
                true,
                "EMERGENCY Pressed notification"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.MAGNETIC, ErrorConfig.TYPE_NAVI_ERR, "", "+", "",
                -1, -1, -1,
                NavigationErrorBean.eERR_SENSOR, "4200", true, false, "MAGNETIC error"
            ),
            ErrorConfig(
                ErrorStatusBean.TYPE.FAN_MOTOR, ErrorConfig.TYPE_NAVI_ERR, "", "+", "",
                -1, -1, -1,
                NavigationErrorBean.eERR_SENSOR, "4300", true, false, "FAN error"
            )
        )
    )

    fun getTypeErrorConfigs(errorType: String): ArrayList<ErrorConfig>? {
        val typedErrorConfig = ArrayList<ErrorConfig>()
        for (config in ROBOT_ERROR_CONFIGS) {
            if (config.getType() == errorType) {
                typedErrorConfig.add(config)
            }
        }
        return typedErrorConfig
    }

    fun getEventErrorConfigs(event: Int): ArrayList<ErrorConfig>? {
        val errorConfigIncludeEvent = ArrayList<ErrorConfig>()
        for (config in ROBOT_ERROR_CONFIGS) {
            if (config.getEventIndex() == event) {
                errorConfigIncludeEvent.add(config)
            }
        }
        return errorConfigIncludeEvent
    }
}