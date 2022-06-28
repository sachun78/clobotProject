package com.lge.support.second.application

import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.lge.support.second.application.databinding.ActivityAdminPageBinding
import com.lge.support.second.application.view.adapter.questionModel
import java.io.File
import java.lang.Math.round
import kotlin.math.roundToInt
import kotlin.reflect.typeOf

class AdminPage : AppCompatActivity() {
    private lateinit var mAdminPageBinding: ActivityAdminPageBinding
    //탭 페이지 담을 리스트
    var viewList = ArrayList<View>()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdminPageBinding = ActivityAdminPageBinding.inflate(layoutInflater)
        setContentView(mAdminPageBinding.root)

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

        ///////////////////////////////first tab page setting page///////////////////////////////
        ////// basic setting - volume/////
        val volumeControl = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = volumeControl.getStreamMaxVolume(AudioManager.STREAM_MUSIC) // maxVolume is 15
        volume = ((volumeControl.getStreamVolume(AudioManager.STREAM_MUSIC)) / maxVolume) * 100
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

        ///// ment repeat time setting /////
        val mentTspinner = viewList[0].findViewById<Spinner>(R.id.mentRepeatT)
        val repeatTime = arrayOf("30초", "1분", "5분", "10분") //멘트 반복 시간 (고정 값)
        val ArrayAdp = ArrayAdapter(this@AdminPage, android.R.layout.simple_spinner_dropdown_item, repeatTime)
        mentTspinner.adapter = ArrayAdp

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

        while(hour<22) {
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

        ////// enforcement charge /////
        val chargeSpin = viewList[1].findViewById<Spinner>(R.id.enforceCharge)
        val chargeAdp = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, chargeT)
        chargeSpin.adapter = chargeAdp

        ///// move contents /////
        val moveSpin = viewList[1].findViewById<Spinner>(R.id.moveContentsOn)
        val moveAdp = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, moveOn)
        moveSpin.adapter = moveAdp

        ///// robot management /////
        val programEndBtn = viewList[1].findViewById<Button>(R.id.programEnd)
        val robotEndBtn = viewList[1].findViewById<Button>(R.id.robotEnd)
        val chargerBtn = viewList[1].findViewById<Button>(R.id.goCharger)

        programEndBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
//            builder.setTitle("프로그램 ")
            builder.setMessage("program finish?").setPositiveButton("yes",
                DialogInterface.OnClickListener{ dialog, id -> Log.d("AdminPage", "programFinish click")})
                .setNegativeButton("no",
                    DialogInterface.OnClickListener{ dialog, id -> Log.d("AdminPage", "programFinish cancel")})
            builder.show()
        }
        robotEndBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("robot finish?").setPositiveButton("yes",
                DialogInterface.OnClickListener{ dialog, id -> Log.d("AdminPage", "robot Finish click")})
                .setNegativeButton("no",
                    DialogInterface.OnClickListener{ dialog, id -> Log.d("AdminPage", "robot Finish cancel")})
            builder.show()
        }
        chargerBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("move to charger?").setPositiveButton("yes",
                DialogInterface.OnClickListener{ dialog, id -> Log.d("AdminPage", "move to charger")})
                .setNegativeButton("no",
                    DialogInterface.OnClickListener{ dialog, id -> Log.d("AdminPage", "don't move to charger")})
            builder.show()
        }

        ///////////////////////////////third tab page setting page///////////////////////////////
        //sunday
        val hourSpin1 = viewList[2].findViewById<Spinner>(R.id.finishHour1)
        hour = 18
        while(hour<25) {
            endHour.add(hour.toString())
            hour += 1
        }
        val hourAdp = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, endHour)
        hourSpin1.adapter = hourAdp

        val minSpin1 = viewList[2].findViewById<Spinner>(R.id.finishMin1)
        endMin.add("00")
        while(min<60){
            endMin.add(min.toString())
            min += 10
        }
        val minAdp = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, endMin)
        minSpin1.adapter = minAdp

        //monday
        val hourSpin2 = viewList[2].findViewById<Spinner>(R.id.finishHour2)
        hourSpin2.adapter = hourAdp
        val minSpin2 = viewList[2].findViewById<Spinner>(R.id.finishMin2)
        minSpin2.adapter = minAdp

        //tuesday
        val hourSpin3 = viewList[2].findViewById<Spinner>(R.id.finishHour3)
        hourSpin3.adapter = hourAdp
        val minSpin3 = viewList[2].findViewById<Spinner>(R.id.finishMin3)
        minSpin3.adapter = minAdp

        //wednesday
        val hourSpin4 = viewList[2].findViewById<Spinner>(R.id.finishHour4)
        hourSpin4.adapter = hourAdp
        val minSpin4 = viewList[2].findViewById<Spinner>(R.id.finishMin4)
        minSpin4.adapter = minAdp

        //thursday
        val hourSpin5 = viewList[2].findViewById<Spinner>(R.id.finishHour5)
        hourSpin5.adapter = hourAdp
        val minSpin5 = viewList[2].findViewById<Spinner>(R.id.finishMin5)
        minSpin5.adapter = minAdp

        //friday
        val hourSpin6 = viewList[2].findViewById<Spinner>(R.id.finishHour6)
        hourSpin6.adapter = hourAdp
        val minSpin6 = viewList[2].findViewById<Spinner>(R.id.finishMin6)
        minSpin6.adapter = minAdp

        //saturday
        val hourSpin7 = viewList[2].findViewById<Spinner>(R.id.finishHour7)
        hourSpin7.adapter = hourAdp
        val minSpin7 = viewList[2].findViewById<Spinner>(R.id.finishMin7)
        minSpin7.adapter = minAdp
//        minSpin7.setSelection(5) //나중에 저장된 값으로 setting
//        var saveBtn = findViewById<Button>(R.id.saveBtn)
//        var selection : String
//        saveBtn.setOnClickListener {
//            selection = spin.selectedItem.toString()
//            Toast.makeText(applicationContext, selection, Toast.LENGTH_SHORT).show()
//        }

    } //////end onCreate

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