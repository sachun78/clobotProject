package com.lge.support.second.application

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.lge.support.second.application.databinding.ActivityAdminPageBinding
import java.io.File
import java.lang.Math.ceil
import kotlin.math.roundToInt

class AdminPage : AppCompatActivity() {
    private lateinit var mAdminPageBinding: ActivityAdminPageBinding

    //탭 페이지 담을 리스트
    var viewList = ArrayList<View>()

    ////////////////////shared preference/////////////////////
    lateinit var pref: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    //////일반설정 - 고정홍보 멘트의 switch
//    var fixPromote: Boolean =pref.getBoolean(
//        "fixPromoteMent",
//        viewList[0].findViewById<Switch>(R.id.fixPromoteSwitch).isChecked)

    //기본 설정 시스템 볼륨
    var volume: Int = 0
    var maxVolume: Int = 0

    //디스크 사용 정보
    var totalSize = 0.0
    var useSize = 0.0
    var percentage = 0.0f
    val drives: Array<File> = File.listRoots()

    //spinner 값들
    val chargeT = arrayOf("10%", "15%", "20%") //강제 충전
    val moveOn = arrayOf("40%", "45%") //이동 콘텐츠 활성화
    var startT = ArrayList<String>() //이동 제한 시작 시간
    var endT = ArrayList<String>() //이동 제한 끝 시간
    var endHour = ArrayList<String>() //세 번째 메뉴 - 예약 종료 시간
    var endMin = ArrayList<String>()

    //atc...
    var hour = 9
    var min = 10

    ////////////for sharedPreference////////////
    //일반 설정 페이지
    var fixPromote: Boolean = true //고정 홍보 멘트, 초기값 false
    var docentM: Boolean = true //이동 해설 모드
    var mentRepeat: Int = 0 //멘트 반복 시간
    //추가 설정 페이지
    var forceChargeM: Boolean = true //강제 충전 모드
    var moveLimitS: Int = 0 //이동 제한 시작 시간
    var moveLimitEnd: Int = 0 // 이동 제한 종료 시간
    var forceCharge: Int = 0 //강제 충전 퍼센트
    var scheduleM: Boolean = true //스케줄 모드
    var selectedM: Int = 0
    var moveContOn: Int = 0 //이동 콘텐츠 활성화 퍼센테이지
    var moveContOff: Int = 0 // 비활성화
    var controlOpt: Boolean = true //컨트롤러(옵션) 설정
    var findCharger: Boolean = true //부팅 시 충전기 찾기
    //예약 종료 페이지
    var reservationE : Boolean = true
    var sunH: Int = 0 //일
    var sunM: Int = 0
    var monH: Int = 0 //월
    var monM: Int = 0
    var tueH: Int = 0 //화
    var tueM: Int = 0
    var wedH : Int = 0 //수
    var wedM : Int = 0
    var thuH : Int = 0 //목
    var thuM : Int = 0
    var friH : Int = 0 //금
    var friM : Int = 0
    var satH : Int = 0 //토
    var satM : Int = 0

    ///////////////////////////////////////onCreate////////////////////////////////////////////////
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdminPageBinding = ActivityAdminPageBinding.inflate(layoutInflater)
        setContentView(mAdminPageBinding.root)

        //Shared Preference 초기화
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE)
        editor = pref.edit()


        //볼륨 설정을 위한 권한 필요(볼륨 외에도 디바이스 세팅 관련 기능 사용)
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //if (Build.VERSION.SDK_INT >= 23) {
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            this.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
        }
        //}

        ///////////////////////tab, view setting//////////////////////////////
        viewList.add(layoutInflater.inflate(R.layout.fragment_admin_normal, null))
        viewList.add(layoutInflater.inflate(R.layout.fragment_admin_additional, null))
        viewList.add(layoutInflater.inflate(R.layout.fragment_admin_reservation_end, null))

        mAdminPageBinding.adminViewPager.adapter = pagerAdapter()

        mAdminPageBinding.adminTab.setupWithViewPager(mAdminPageBinding.adminViewPager)

        mAdminPageBinding.adminTab.getTabAt(0)?.setText("일반 설정")
        mAdminPageBinding.adminTab.getTabAt(1)?.setText("추가 설정")
        mAdminPageBinding.adminTab.getTabAt(2)?.setText("예약 종료")

        //////////button listener/////////
        mAdminPageBinding.adminCloseBtn.setOnClickListener {
            this.finish()
        }

        //이전에 저장해 둔 값 불러오기(만약 저장된 값이 없다면 초기화) => default : On(true)상태
        fixPromote = pref.getBoolean("fixPromoteMent", true)
        docentM = pref.getBoolean("docentMode", true)
        mentRepeat = pref.getInt("mentRepeatT", 0)
        forceChargeM = pref.getBoolean("forceChageMode", true)
        moveLimitS = pref.getInt("moveLimitStart", 0)
        moveLimitEnd = pref.getInt("moveLimitEnd", 0)
        forceCharge = pref.getInt("forceCharge", 0)
        scheduleM = pref.getBoolean("scheduleMode", true)
        selectedM = pref.getInt("selectedScheduleMode", 0)
        moveContOn = pref.getInt("moveContentOn", 0)
        moveContOff = pref.getInt("moveContentOff", 0)
        controlOpt = pref.getBoolean("controllerOption", true)
        findCharger = pref.getBoolean("findCharger", true)
        reservationE = pref.getBoolean("reservationEndMode", true)
        sunH = pref.getInt("sundayH", 0)
        sunM = pref.getInt("sundayM", 0)

        monH = pref.getInt("mondayH",0)
        monM = pref.getInt("mondayM",0)

        tueH = pref.getInt("tuesdayH",0)
        tueM = pref.getInt("tuesdayM",0)

        wedH = pref.getInt("wednesdayH",0)
        wedM = pref.getInt("wednesdayM",0)

        thuH = pref.getInt("thursdayH",0)
        thuM = pref.getInt("thursdayM",0)

        friH = pref.getInt("fridayH",0)
        friM = pref.getInt("fridayM",0)

        satH = pref.getInt("saturdayH",0)
        satM = pref.getInt("saturdayM",0)

        ///////////////////////////////first tab page setting page///////////////////////////////
        ///////fix promote ment///////
        //저장해 둔 값 확인해서 기본 화면 세팅
        viewList[0].findViewById<Switch>(R.id.fixPromoteSwitch).isChecked = fixPromote
        //저장 버튼 -> 저장
        viewList[0].findViewById<Button>(R.id.fixPromoteSave).setOnClickListener {
            fixPromote = viewList[0].findViewById<Switch>(R.id.fixPromoteSwitch).isChecked
            editor.putBoolean("fixPromoteMent", fixPromote)
            editor.apply()
        }

        ///////ment repeat time///////
        val mentTspinner = viewList[0].findViewById<Spinner>(R.id.mentRepeatT)

        val repeatTime = arrayOf("30초", "1분", "5분", "10분") //멘트 반복 시간 (고정 값)
        val ArrayAdp =
            ArrayAdapter(this@AdminPage, android.R.layout.simple_spinner_dropdown_item, repeatTime)
        mentTspinner.adapter = ArrayAdp

        mentTspinner.setSelection(mentRepeat)
        mentTspinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mentRepeat = position
                editor.putInt("mentRepeatT", position)
                editor.apply()
                Log.d("AdminPage", "selected Time is " + mentTspinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        })
//        Log.d("AdminPage", "selected position is " + viewList[0].findViewById<Spinner>(R.id.mentRepeatT).selectedItemPosition)

        ///////docent mode/////
        //저장해 둔 값 확인해서 기본 화면 세팅
        viewList[0].findViewById<Switch>(R.id.docentModeSwitch).isChecked = docentM
        //저장 버튼 클릭 -> 저장
        viewList[0].findViewById<Button>(R.id.docentModeSave).setOnClickListener {
            docentM = viewList[0].findViewById<Switch>(R.id.docentModeSwitch).isChecked
            editor.putBoolean("docentMode", fixPromote)
            editor.apply()
        }

        ////// basic setting - volume/////
        val volumeControl = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = volumeControl.getStreamMaxVolume(AudioManager.STREAM_MUSIC) // maxVolume is 15
        volume =
            ceil((volumeControl.getStreamVolume(AudioManager.STREAM_MUSIC)).toDouble() / maxVolume.toDouble() * 100).toInt()
        viewList[0].findViewById<TextView>(R.id.volume).text = volume.toString()

        viewList[0].findViewById<Button>(R.id.volDownBtn).setOnClickListener {
            if (volume > 0) {
                volume -= 5
                volChange(volume)
            }
            viewList[0].findViewById<TextView>(R.id.volume).text = volume.toString()
        }

        viewList[0].findViewById<Button>(R.id.volUpBtn).setOnClickListener {
            if (volume < 100) {
                volume += 5
                volChange(volume)
            }
            viewList[0].findViewById<TextView>(R.id.volume).text = volume.toString()
        }

        ////// state info - battery/////
        MainActivity.robotViewModel.batterySOC.observe(this) {
            viewList[0].findViewById<TextView>(R.id.battery).text = "{$it}%"
        }

        ////// state info  - disc/////
        for (drive in drives) {
            //driveName = drive.getAbsolutePath()
            totalSize = drive.getTotalSpace().toDouble() // / Math.pow(1024.0, 3.0)
            useSize = drive.getUsableSpace().toDouble() // / Math.pow(1024.0, 3.0)

            //Log.d("check", "$driveName")
            Log.d("AdminPage", "total disc is $totalSize GB")
            Log.i("AdminPage", totalSize::class.simpleName.toString())
            Log.d("AdminPage", "use disc is $useSize GB")
            Log.d("AdminPage", "disc use percentage is " + useSize / totalSize * 100 + "%")

            percentage = ((useSize / totalSize * 100) * 10).roundToInt() / 10f
            Log.d("AdminPage", percentage.toString())

            viewList[0].findViewById<TextView>(R.id.usage).text = "{$percentage}%"
        }

        ///////////////////////////////second tab page setting page///////////////////////////////
        ////// move limit time /////
        val startSpin = viewList[1].findViewById<Spinner>(R.id.limitTStart)
        val endSpin = viewList[1].findViewById<Spinner>(R.id.limitTimeEnd)

        while (hour < 22) {
            startT.add(hour.toString() + ":00")
            startT.add(hour.toString() + ":30")
            endT.add(hour.toString() + ":00")
            endT.add(hour.toString() + ":30")
            hour += 1
        }
        startT.remove("21:30")
        endT.remove("9:00")

        val startAdp = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, startT)
        startSpin.adapter = startAdp
        val endAdp = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, endT)
        endSpin.adapter = endAdp

        //저장해 둔 값 확인해서 기본 화면 세팅
        startSpin.setSelection(moveLimitS)
        endSpin.setSelection(moveLimitEnd)

        //클릭 리스너(선택 -> 저장)
        startSpin.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                moveLimitS = position
                editor.putInt("moveLimitStart", position)
                editor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })
        endSpin.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                moveLimitEnd = position
                editor.putInt("moveLimitEnd", position)
                editor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        ////// enforcement charge /////
        val chargeSpin = viewList[1].findViewById<Spinner>(R.id.enforceCharge)

        val chargeAdp = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, chargeT)
        chargeSpin.adapter = chargeAdp

        chargeSpin.setSelection(forceCharge)
        chargeSpin.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                forceCharge = position
                editor.putInt("forceCharge", position)
                editor.apply()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        //////schedule mode//////
        viewList[1].findViewById<Switch>(R.id.scheduleSwitch).isChecked = scheduleM
        viewList[1].findViewById<RadioGroup>(R.id.schedule_radioGroup).check(selectedM)

        Log.d("AdminPage", "Button 1 id " + R.id.byCycle)
        Log.d("AdminPage", "Button 2 id " + R.id.byTime)
        Log.d("AdminPage", "Button 3 id " + R.id.byNow)

        viewList[1].findViewById<Button>(R.id.scheduleModeSave).setOnClickListener {
            scheduleM = viewList[1].findViewById<Switch>(R.id.scheduleSwitch).isChecked
            selectedM =
                viewList[1].findViewById<RadioGroup>(R.id.schedule_radioGroup).checkedRadioButtonId
            editor.putBoolean("scheduleMode", scheduleM)
            editor.putInt("selectedScheduleMode", selectedM)
            editor.apply()
            Log.d("AdminPage", "selected Mode is " + selectedM)
        }

        ///// move contents /////
        val moveSpin = viewList[1].findViewById<Spinner>(R.id.moveContentsOn)
        val moveAdp = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, moveOn)
        moveSpin.adapter = moveAdp

        moveSpin.setSelection(moveContOn)
        viewList[1].findViewById<Button>(R.id.batterySave).setOnClickListener {
            moveContOn = moveSpin.selectedItemPosition
            editor.putInt("moveContentOn", moveContOn)
            editor.apply()
        }

        /////controller(option) setting/////
        viewList[1].findViewById<Switch>(R.id.optSwitch).isChecked = controlOpt
        viewList[1].findViewById<Button>(R.id.optSwitch).setOnClickListener {
            controlOpt = viewList[1].findViewById<Switch>(R.id.optSwitch).isChecked
            editor.putBoolean("controllerOption", controlOpt)
            editor.apply()
        }

        ///// robot management /////
        val programEndBtn = viewList[1].findViewById<Button>(R.id.programEnd)
        val robotEndBtn = viewList[1].findViewById<Button>(R.id.robotEnd)
        val chargerBtn = viewList[1].findViewById<Button>(R.id.goCharger)

        programEndBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
//            builder.setTitle("프로그램 ")
            builder.setMessage("program finish?").setPositiveButton("yes",
                DialogInterface.OnClickListener { dialog, id ->
                    Log.d(
                        "AdminPage",
                        "programFinish click"
                    )
                })
                .setNegativeButton("no",
                    DialogInterface.OnClickListener { dialog, id ->
                        Log.d(
                            "AdminPage",
                            "programFinish cancel"
                        )
                    })
            builder.show()
        }
        robotEndBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("robot finish?").setPositiveButton("yes",
                DialogInterface.OnClickListener { dialog, id ->
                    Log.d(
                        "AdminPage",
                        "robot Finish click"
                    )
                })
                .setNegativeButton("no",
                    DialogInterface.OnClickListener { dialog, id ->
                        Log.d(
                            "AdminPage",
                            "robot Finish cancel"
                        )
                    })
            builder.show()
        }
        chargerBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("move to charger?").setPositiveButton("yes",
                DialogInterface.OnClickListener { dialog, id ->
                    Log.d(
                        "AdminPage",
                        "move to charger"
                    )
                })
                .setNegativeButton("no",
                    DialogInterface.OnClickListener { dialog, id ->
                        Log.d(
                            "AdminPage",
                            "don't move to charger"
                        )
                    })
            builder.show()
        }

        /////find charger/////
        viewList[1].findViewById<Switch>(R.id.findCharger).isChecked = findCharger
        viewList[1].findViewById<Button>(R.id.findChargerSave).setOnClickListener {
            findCharger = viewList[1].findViewById<Switch>(R.id.findCharger).isChecked
            editor.putBoolean("findCharger", findCharger)
            editor.apply()
        }

        ///////////////////////////////third tab page setting page///////////////////////////////
        viewList[2].findViewById<Switch>(R.id.endMode).isChecked = reservationE
        //sunday
        val hourSpin1 = viewList[2].findViewById<Spinner>(R.id.finishHour1)
        hour = 18
        while (hour < 25) {
            endHour.add(hour.toString())
            hour += 1
        }
        val hourAdp = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, endHour)
        hourSpin1.adapter = hourAdp

        val minSpin1 = viewList[2].findViewById<Spinner>(R.id.finishMin1)
        endMin.add("00")
        while (min < 60) {
            endMin.add(min.toString())
            min += 10
        }
        val minAdp = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, endMin)
        minSpin1.adapter = minAdp

        hourSpin1.setSelection(sunH)
        minSpin1.setSelection(sunM)

        //monday
        val hourSpin2 = viewList[2].findViewById<Spinner>(R.id.finishHour2)
        hourSpin2.adapter = hourAdp
        val minSpin2 = viewList[2].findViewById<Spinner>(R.id.finishMin2)
        minSpin2.adapter = minAdp

        hourSpin2.setSelection(monH)
        minSpin2.setSelection(monM)

        //tuesday
        val hourSpin3 = viewList[2].findViewById<Spinner>(R.id.finishHour3)
        hourSpin3.adapter = hourAdp
        val minSpin3 = viewList[2].findViewById<Spinner>(R.id.finishMin3)
        minSpin3.adapter = minAdp

        hourSpin3.setSelection(tueH)
        minSpin3.setSelection(tueM)

        //wednesday
        val hourSpin4 = viewList[2].findViewById<Spinner>(R.id.finishHour4)
        hourSpin4.adapter = hourAdp
        val minSpin4 = viewList[2].findViewById<Spinner>(R.id.finishMin4)
        minSpin4.adapter = minAdp

        hourSpin4.setSelection(wedH)
        minSpin4.setSelection(wedM)

        //thursday
        val hourSpin5 = viewList[2].findViewById<Spinner>(R.id.finishHour5)
        hourSpin5.adapter = hourAdp
        val minSpin5 = viewList[2].findViewById<Spinner>(R.id.finishMin5)
        minSpin5.adapter = minAdp

        hourSpin5.setSelection(thuH)
        minSpin5.setSelection(thuM)

        //friday
        val hourSpin6 = viewList[2].findViewById<Spinner>(R.id.finishHour6)
        hourSpin6.adapter = hourAdp
        val minSpin6 = viewList[2].findViewById<Spinner>(R.id.finishMin6)
        minSpin6.adapter = minAdp

        hourSpin6.setSelection(friH)
        minSpin6.setSelection(friM)

        //saturday
        val hourSpin7 = viewList[2].findViewById<Spinner>(R.id.finishHour7)
        hourSpin7.adapter = hourAdp
        val minSpin7 = viewList[2].findViewById<Spinner>(R.id.finishMin7)
        minSpin7.adapter = minAdp

        hourSpin7.setSelection(satH)
        minSpin7.setSelection(satM)

        viewList[2].findViewById<Button>(R.id.endModeSave).setOnClickListener {
            reservationE = viewList[2].findViewById<Switch>(R.id.endMode).isChecked //예약 종료 모드 on인지 off인지
            editor.putBoolean("reservationEndMode", reservationE)
            //일요일
            sunH = hourSpin1.selectedItemPosition
            sunM = minSpin1.selectedItemPosition
            editor.putInt("sundayH", sunH)
            editor.putInt("sundayM", sunM)
            //월요일
            monH = hourSpin2.selectedItemPosition
            monM = minSpin2.selectedItemPosition
            editor.putInt("mondayH", monH)
            editor.putInt("mondayM", monM)
            //화요일
            tueH = hourSpin3.selectedItemPosition
            tueM = minSpin3.selectedItemPosition
            editor.putInt("tuesdayH", tueH)
            editor.putInt("tuesdayM", tueM)
            //수요일
            wedH = hourSpin4.selectedItemPosition
            wedM = minSpin4.selectedItemPosition
            editor.putInt("wednesdayH", wedH)
            editor.putInt("wednesdayM", wedM)
            Log.d("AdminPage", "수요일 " + wedH + " : " + wedM)
            //목요일
            thuH = hourSpin5.selectedItemPosition
            thuM = minSpin5.selectedItemPosition
            editor.putInt("thursdayH", thuH)
            editor.putInt("thursdayM", thuM)
            //금요일
            friH = hourSpin6.selectedItemPosition
            friM = minSpin6.selectedItemPosition
            editor.putInt("fridayH", friH)
            editor.putInt("fridayM", friM)
            Log.d("AdminPage", "금요일 " + friH + " : " + friM)

            //토요일
            satH = hourSpin7.selectedItemPosition
            satM = minSpin7.selectedItemPosition

            editor.putInt("saturdayH", satH)
            editor.putInt("saturdayM", satM)
            Log.d("AdminPage", "토요일 " + satH + " : " + satM)
            editor.apply()
        }
    } //////end onCreate

    override fun onDestroy() {
        super.onDestroy()
        Log.i("AdminPage", "Destroy")
    }


    ////volume change function////
    private fun volChange(volume: Int) {
        val audioManager =
            this.applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC, //////system volume => STREAM_MUSIC
            (audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * volume / 100.0).toInt(),
            AudioManager.FLAG_PLAY_SOUND
        )
    }

    inner class pagerAdapter() : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any) = view == `object`

        override fun getCount() = viewList.size

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var curView = viewList[position]
            mAdminPageBinding.adminViewPager.addView(curView)
            return curView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            mAdminPageBinding.adminViewPager.removeView(`object` as View)
        }
    }
}