package kr.co.clobot.robot.common

import android.app.Fragment
import android.app.ProgressDialog
import android.content.DialogInterface
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.lge.robot.platform.data.*
import com.lge.robot.platform.navigation.NavigationMessageType
import kr.co.clobot.robot.common.main.data.BatteryEvent
import kr.co.clobot.robot.common.main.data.NaviError
import kr.co.clobot.robot.common.main.data.NaviErrorStatus
import kr.co.clobot.robot.common.main.data.RobotErrorConstant
import kr.co.clobot.robot.common.main.database.BatteryDatabase
import kr.co.clobot.robot.common.main.database.BatteryEntity
import kr.co.clobot.robot.common.main.managers.PowerManagerInstance
import kr.co.clobot.robot.common.main.model.NavigationMessage
import kr.co.clobot.robot.common.main.model.NavigationModel

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


open class RobotActivity : AppCompatActivity() {

    private val TAG = RobotActivity::class.java.simpleName

    private lateinit var mPowerModeDisposable: Disposable
    private lateinit var mBatteryDisposable: Disposable
    private lateinit var mErrorDisposable: Disposable
    private lateinit var mNaviDisposable: Disposable

    private lateinit var mProgressDialog: ProgressDialog
    private var mDialog: AlertDialog? = null

    private val viewModel: NavigationModel by viewModels()

    private var mBatteryDatabase: BatteryDatabase? = null


    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        mBatteryDatabase = BatteryDatabase.getInstance(this)
        makeDisposable()
    }

    override fun onPause() {
        super.onPause()
        mNaviDisposable.dispose()
        mPowerModeDisposable.dispose()
        mBatteryDisposable.dispose()
        mErrorDisposable.dispose()
    }

    private fun makeDisposable() {
        mNaviDisposable = EventBus.instance.receiveEvent(EventBus.NAVI_TOPIC)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { naviEventOccured(it) }
            )
        mPowerModeDisposable = EventBus.instance.receiveEvent(EventBus.POWER_TOPIC_MODE)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { updatePowerMode(it) }
            )
        mBatteryDisposable = EventBus.instance.receiveEvent(EventBus.BATTERY_TOPIC)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { saveBatteryData(it) }
            )

        mErrorDisposable = EventBus.instance.receiveEvent(EventBus.ERROR_TOPIC)
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { showError(it) }
            )
    }

    private fun saveBatteryData(data: Any) {
        var battery: BatteryEvent = data as BatteryEvent
        var batteryData = battery.batteryData
        if (battery.type == null) {
            //print to mainactivity
        } else {
            //save to battery event data into database
            var batteryEn = batteryData?.let {
                BatteryEntity(
                    0, battery.type!!, it.soc,
                    batteryData.soh, batteryData.temperature, batteryData.plugged,
                    batteryData.time
                )
            }
            if (batteryEn != null) {
                GlobalScope.launch(Dispatchers.IO) {
                    mBatteryDatabase?.batteryDao()?.insertBatteryInfo(batteryEn)
                }
            }
        }
    }

    private fun checkNaviMessage(msg: NavigationMessage) {
        //DEBUGGING : print msg name
        //Log.d(TAG, "navi message by action" + EventIndex.convertToString(msg.currentMsg))
        when (msg.currentMsg) {
            NavigationMessageType.EXTERN_NAVI_EVENT_GKR_START -> {
                showProgressDialog(
                    R.string.string_gkr_start,
                    resources.getString(R.string.string_gkr_running)
                )
            }
            NavigationMessageType.EXTERN_NAVI_EVENT_GKR_END -> {
                stopProgressDialog()
            }
        }
    }

    private fun showError(data: Any?) {
        var robotError: RobotError = data as RobotError
        checkRobotError(robotError)
    }

    private fun updatePowerMode(data: Any?) {
        viewModel.updatePowerMode(data as Int)
    }

    private fun naviEventOccured(data: Any?) {
        when (data) {
            is NaviStatus2 -> {
                viewModel.updateNaviStaus(data)
            }
            is SLAM3DPos -> {
                Log.d(TAG, "API SLAM3DPos pause")
                viewModel.updateSlam(data)
            }
            is NaviActionInfo -> {
                viewModel.updateNaviActionInfo(data)
            }
            is NavigationMessage -> {
                var msg = data as NavigationMessage
                checkNaviMessage(msg)
            }
            is NaviError -> {
                checkNaviError(data)
            }
        }
    }

    private fun checkNaviError(data: NaviError) {
        when (data.errorId) {
            ErrorReport._e_error_event.eERR_SLAM.ordinal -> {
                showDialog(
                    resources.getString(R.string.string_location_error),
                    resources.getString(R.string.string_reboot_robot),
                    func = { dialog, which ->
                        Log.d(TAG, "close dialog")
                    }
                )
            }
            ErrorReport._e_error_event.eERR_SLAM_NON_OPERATION_AREA.ordinal -> {
                //로봇을 맵 밖으로 강제 이동시
                showDialog(
                    resources.getString(R.string.string_out_of_area),
                    resources.getString(R.string.string_reboot_robot),
                    func = { dialog, which ->
                        Log.d(TAG, "close dialog")
                    }
                )
                //or you can re start gkr
            }
            ErrorReport._e_error_event.eERR_MAP_LOADING_FAIL.ordinal -> {
                // 네비 엔진에서 맵을 못찾을 경우
                showDialog(
                    resources.getString(R.string.string_map_loading_fail),
                    resources.getString(R.string.string_reboot_robot),
                    func = { dialog, which ->
                        Log.d(TAG, "close dialog")
                    }
                )
            }
        }
    }

    private fun checkRobotError(data: RobotError) {
        //check navigation device error
        var naviErrors = HashSet(NaviErrorStatus(data).getErrors())
        if (naviErrors.size != 0) {
            for (i in naviErrors) {
                // check emergency
                Log.d(TAG, "code : " + i.code)
                Log.d(TAG, "code : " + i.safetyCode)
                //check bumper
                if (i.errorDevice.toString() == RobotErrorConstant.EMERGENCY
                    && i.safetyCode.equals("6")
                ) {
                    showDialog(
                        resources.getString(R.string.string_emergency),
                        resources.getString(R.string.string_emergency),
                        func = { dialog, which ->
                            PowerManagerInstance.instance.getPowerManager().robotActivation()
                        }
                    )
                } else if (i.errorDevice.toString() == RobotErrorConstant.BUMPER) {
                    showDialog(
                        resources.getString(R.string.string_bumper_detect),
                        resources.getString(R.string.string_bumper_detect),
                        func = { dialog, which ->
                            Log.d(TAG, "close dialog")
                        }
                    )
                } else {
                    Log.d(TAG, "NAVI DEVICE ERROR , WE NEED TO RESET NAVIGATION");
                }
            }
        } else {
            stopDialog()
        }
    }

    private fun stopProgressDialog() {
        mProgressDialog?.dismiss()
    }

    private fun showProgressDialog(titleId: Int, message: String) {
        mProgressDialog = ProgressDialog(this@RobotActivity)
        mProgressDialog.setTitle(titleId)
        mProgressDialog.setMessage(message)
        mProgressDialog.show()
    }

    private fun showDialog(title: String, message: String, func: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(getText(R.string.string_ok), func)
        mDialog = builder.create()
        mDialog!!.show()
    }

    private fun stopDialog() {
        mDialog?.dismiss()
    }
}