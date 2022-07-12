package com.lge.support.second.application.view.docent

import android.app.Dialog
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R

class move_docent : Fragment(), SurfaceHolder.Callback {
    var TAG = "MOVE_DOCENT"
    var surfaceView: SurfaceView? = null
    var surfaceHolder: SurfaceHolder? = null
    lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_move_docent, container, false)

        val mActivity = activity as MainActivity
        //mActivity.findViewById<LinearLayout>(R.id.top).visibility = View.GONE

        MainActivity.subVideo.show()

        rootView.findViewById<Button>(R.id.move_docent_b1).setOnClickListener {
            var dialog = Dialog(mActivity)
            mediaPlayer.pause()
            MainActivity.subVideo.mediaPlayer.pause()
            dialog.setContentView(R.layout.docent_end_dialog_layout)
            dialog.show()

            dialog.findViewById<Button>(R.id.yes_btn).setOnClickListener {
                dialog.hide()
//                fragmentManager?.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                parentFragmentManager.popBackStack()
            }
            dialog.findViewById<Button>(R.id.no_btn).setOnClickListener {
                dialog.hide()
                mediaPlayer.start()
                MainActivity.subVideo.mediaPlayer.start()
            }
        }

        surfaceView = rootView?.findViewById(R.id.surface_view)
        surfaceHolder = surfaceView?.holder
        surfaceHolder?.addCallback(this);

        Log.d("move_docent", "onCreateView")
        return rootView
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated");
        (activity as MainActivity).findViewById<LinearLayout>(R.id.top).visibility = View.GONE
        //if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
//        } else {
//            mediaPlayer!!.reset()
//        }

        try {
//            val path =
//                "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                //var uri = Uri.parse("android.resource://" + "com.lge.support.second.application" + "/raw/docent_10_")
            var uri = Uri.parse(docent_select.uri)
            context?.let { mediaPlayer.setDataSource(it, uri) }

                //mediaPlayer!!.setVolume(0F, 0F) //볼륨 제거

            mediaPlayer.setDisplay(surfaceHolder) // 화면 호출
            mediaPlayer.prepare() // 비디오 load 준비
            mediaPlayer.setOnCompletionListener { p0 ->
                println("onCompletion!")
                p0?.release()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_main, docent_end())
                    .addToBackStack(null).commit()
            }; // 비디오 재생 완료 리스너

            mediaPlayer.start()
        } catch (e: Exception) {
            Log.e(TAG, "surface view error : " + e.message)
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        Log.d(TAG, "surfaceChanged");
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        Log.e(TAG, "surfaceDestroyed");
        MainActivity.subVideo.hide()
        mediaPlayer.release() //자원해제
        (activity as MainActivity).findViewById<LinearLayout>(R.id.top).visibility = View.VISIBLE
    }


}