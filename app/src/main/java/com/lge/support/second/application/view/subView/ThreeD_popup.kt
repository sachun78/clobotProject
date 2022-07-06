package com.lge.support.second.application.view.subView

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import com.google.android.filament.EntityManager
import com.google.android.filament.LightManager
import com.google.android.filament.Skybox
import com.google.android.filament.utils.KtxLoader
import com.google.android.filament.utils.ModelViewer
import com.google.android.filament.utils.Utils
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentThreeDPopupBinding
import com.lge.support.second.application.model.TkTestViewModel.Companion.TAG
import java.nio.ByteBuffer


class threeD_popup : Fragment() , SurfaceHolder.Callback {

    private lateinit var binding : FragmentThreeDPopupBinding

    private lateinit var surfaceView: SurfaceView
    private lateinit var modelViewer: ModelViewer
    private lateinit var choreographer: Choreographer

    init {
        Utils.init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentThreeDPopupBinding.inflate(inflater, container, false)

        (activity as MainActivity).findViewById<LinearLayout>(R.id.top).visibility = View.GONE

        surfaceView = binding.popUpSurface
        choreographer = Choreographer.getInstance()

        modelViewer = ModelViewer(surfaceView)
        surfaceView.setOnTouchListener(modelViewer)

        modelViewer.scene.removeEntity(modelViewer.light)

        val mLight = EntityManager.get().create()

        val options = LightManager.ShadowOptions()
        options.shadowCascades

        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(1f, 1f, 1f)
            .direction(0.5f, -0.5f, 1f)
            .shadowOptions(options)
            .intensityCandela(100_000.0f)
            .build(modelViewer.engine, mLight)

        modelViewer.scene.addEntity(mLight);

        modelViewer.scene.skybox = Skybox.Builder()
            .build(modelViewer.engine)


        val buffer = readAsset(context, "k002.glb")
        modelViewer.loadModelGlb(buffer)
        modelViewer.transformToUnitCube()

        loadEnvironment()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.popUpCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as MainActivity).findViewById<LinearLayout>(R.id.top).visibility = View.VISIBLE
        choreographer.removeFrameCallback(frameCallback)
    }

    override fun onResume() {
        super.onResume()
        choreographer.postFrameCallback(frameCallback)
    }

    override fun onPause() {
        super.onPause()
        choreographer.removeFrameCallback(frameCallback)
    }


    override fun surfaceCreated(p0: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated");
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        Log.d(TAG, "surfaceChanged")
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        Log.d(TAG, "surfaceCreated")
    }

    private fun readAsset(context: Context?, assetName: String): ByteBuffer {
        val input = context?.assets?.open(assetName)
        val bytes = ByteArray(input!!.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }

    fun loadEnvironment() {
        var buffer = readAsset(context, "environment/venetian_crossroads_2k_ibl.ktx")
        KtxLoader.createIndirectLight(modelViewer.engine, buffer).apply {
            intensity = 13_000f
            modelViewer.scene.indirectLight = this
        }
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