package com.lge.support.second.application

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.lge.robot.platform.data.SLAM3DPos
import com.lge.robot.platform.util.poi.data.POI
import com.lge.support.second.application.data.robot.MoveState
import com.lge.support.second.application.managers.robot.PoiDbManager
import com.lge.support.second.application.databinding.ActivityTestBinding
import com.lge.support.second.application.managers.mqtt.MessageConnector
import com.lge.support.second.application.view.CustomProgressDialogue
import org.apache.log4j.chainsaw.Main

class TestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTestBinding
    private var mPOIs: ArrayList<POI>? = null
    lateinit var image1: Bitmap
    lateinit var canvas: Canvas
    private var selectedPOI: POI? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mPOIs = PoiDbManager(this).getAllPoi()

        var listView = binding.listview
        var listItems = arrayListOf<POI>()
        var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter

        //fd.create()
        binding.btnUndocking.setOnClickListener {
            MainActivity.robotViewModel.undockingRequest()
        }

        binding.btnDocking.setOnClickListener {
            MainActivity.robotViewModel.dockingRequest()
        }

        binding.btnPois.setOnClickListener {
            listItems.addAll(mPOIs ?: arrayListOf())
            Log.i("HJBAE", "data: $listItems")
            adapter.notifyDataSetChanged()
        }

        binding.btnCruise.setOnClickListener {
//            robotViewModel.cruiseRequest()
            MainActivity.robotViewModel.onGkr()
        }

        binding.btnStop.setOnClickListener {  }

        binding.btnPause.setOnClickListener {  }

        binding.btnResume.setOnClickListener {  }

        binding.btnTmp.setOnClickListener {
            val customDialog: Dialog = CustomProgressDialogue(this)
            customDialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            var isChecked = false
            if (!isChecked) customDialog.show()
            else customDialog.dismiss()
        }

        listView.setOnItemClickListener { parent, view, pos, id ->
            mPOIs?.let {
                selectedPOI = it.get(id.toInt())
                MainActivity.robotViewModel.move(selectedPOI!!)
            }
            Toast.makeText(this, "pos:${pos}, id:${id}", Toast.LENGTH_SHORT).show()
        }

        MainActivity.robotViewModel.moveState.observe(this) {
            when (it) {
                MoveState.MOVE_DONE -> {
                    MainActivity.movement_normal.hide()
                }
                MoveState.MOVING -> TODO()
                MoveState.MOVE_START -> {
                    MainActivity.movement_normal.show()
                }
                MoveState.MOVE_FAIL -> {
                    MainActivity.movement_normal.hide()
                }
                MoveState.SCHEDULE_MOVE -> TODO()
                MoveState.SCHEDULE_WAIT -> TODO()
            }
        }

//        viewModel.queryResult.observe(this) {
//            if (it != null) {
//                MainActivity.viewModel.ttsSpeak(this, it.speech[0])
//            }
//        }

        var finishButton: Button = findViewById<Button>(R.id.testActivityBtn)

        finishButton.setOnClickListener {
            this.finish()
        }

        image1 = BitmapFactory.decodeResource(resources, R.drawable.clobot_ipark_f7);
        val temp = Bitmap.createBitmap(image1, 450, 480, 140, 40)
        canvas = Canvas(temp)
        canvas.drawBitmap(temp, 0f, 0f, null)

        binding.imageView123.setImageBitmap(temp)

        MainActivity.robotViewModel.mSLAM3DPos.observe(this) { pos ->
            draw(pos, 450, 480, 140, 40)
        }
    }

    private fun draw(pos: SLAM3DPos, x: Int, y: Int, width: Int, height: Int) {
        // map에서 bitmap Crop
        val temp = Bitmap.createBitmap(image1, x, y, width, height)
        canvas.drawBitmap(temp, 0f, 0f, null)

        val robot_drawable = getDrawable(R.drawable.ic_robot_body1) as VectorDrawable
        robot_drawable.setBounds(
            0, 0, 12, 12,
        )

        if (selectedPOI != null) {
            val pinDrawable = getDrawable(R.drawable.ic_map_pin) as VectorDrawable
            pinDrawable.setBounds(
                0, 0, 16, 16,
            )

            canvas.save();
            canvas.translate(
                selectedPOI!!.positionX * 10 - x,
                (y + height - selectedPOI!!.positionY * 10)
            )
            pinDrawable.draw(canvas)

            canvas.restore()
        }

        canvas.save();
        // 510 -> y좌표 시작점 480 + height 40
        canvas.translate(
            (pos.robotPos.x * 10 - x).toFloat(),
            ((y + height) - pos.robotPos.y * 10).toFloat()
        )
        // vector img size 12/2 = 6f
        canvas.rotate(
            (pos.robotPos.deg * -1).toFloat(),
            6f,
            6f
        )
        robot_drawable.draw(canvas)
        canvas.restore()
        binding.imageView123.invalidate()
    }
}