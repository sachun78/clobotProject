package com.lge.support.second.application.view.docent

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.filament.Skybox
import com.google.android.filament.utils.ModelViewer
import com.google.android.filament.utils.Utils
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import java.nio.ByteBuffer

class test_docent : Fragment(), SurfaceHolder.Callback {
    private lateinit var surfaceView: SurfaceView
    private lateinit var modelViewer: ModelViewer
    private lateinit var choreographer: Choreographer

    companion object {
        private const val TAG = "MOVE_DOCENT"

        init {
            Utils.init()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_test_docent, container, false)

        val mActivity = activity as MainActivity
        mActivity.findViewById<LinearLayout>(R.id.top).visibility = View.GONE
        mActivity.findViewById<ImageView>(R.id.qrImg).visibility = View.GONE

        rootView.findViewById<Button>(R.id.test_docent_b1).setOnClickListener {
            var dialog = Dialog(mActivity)
            dialog.setContentView(R.layout.docent_end_dialog_layout)
            dialog.show()

            dialog.findViewById<Button>(R.id.yes_btn).setOnClickListener {
                dialog.hide()
                parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            dialog.findViewById<Button>(R.id.no_btn).setOnClickListener {
                dialog.hide()
            }
        }

        surfaceView = rootView.findViewById(R.id.test_surface_view)
        choreographer = Choreographer.getInstance()

        modelViewer = ModelViewer(surfaceView)
        surfaceView.setOnTouchListener(modelViewer)

        modelViewer.scene.skybox = Skybox.Builder().build(modelViewer.engine)

        val buffer = readAsset(context, "k002.glb")
        modelViewer.loadModelGlb(buffer)
        modelViewer.transformToUnitCube()

        return rootView
    }

    private fun readAsset(context: Context?, assetName: String): ByteBuffer {
        val input = context?.assets?.open(assetName)
        val bytes = ByteArray(input!!.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }

    override fun surfaceCreated(p0: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated");

    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        Log.d(TAG, "surfaceChanged");
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        Log.e(TAG, "surfaceDestroyed");
    }

    override fun onResume() {
        super.onResume()
        choreographer.postFrameCallback(frameCallback)
    }

    override fun onPause() {
        super.onPause()
        choreographer.removeFrameCallback(frameCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        choreographer.removeFrameCallback(frameCallback)
    }

    private val frameCallback = object : Choreographer.FrameCallback {
        private val startTime = System.nanoTime()
        override fun doFrame(currentTime: Long) {
            val seconds = (currentTime - startTime).toDouble() / 1_000_000_000
            choreographer.postFrameCallback(this)
            modelViewer.animator?.apply {
                if (animationCount > 0) {
                    applyAnimation(0, seconds.toFloat())
                }
                updateBoneMatrices()
            }
            modelViewer.render(currentTime)
        }
    }
}