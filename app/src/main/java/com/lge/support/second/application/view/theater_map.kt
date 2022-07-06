package com.lge.support.second.application.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentTheaterMapBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class theater_map : Fragment() {

    private var _binding: FragmentTheaterMapBinding? = null
    private val binding get() = _binding!!

    //선택된 버튼
    lateinit var selectedBtn: Button

    ////////////////////////////발화는 한 번만 => 한 번 발화 하면 true로 바꿔서 발화 안 되게
    var ttsA: Boolean = false
    var ttsB: Boolean = false
    var ttsC: Boolean = false
    var ttsD: Boolean = false
    var ttsE: Boolean = false
    var ttsF: Boolean = false
    var ttsG: Boolean = false
    var ttsH: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTheaterMapBinding.inflate(inflater, container, false)

        //기본은 첫 번째 버튼 클릭되어 있게
        selectedBtn = binding.theaterB1
        selectedBtn.isSelected = true
//        GlobalScope.launch {
//            //MainActivity.viewModel.getResponse(resources.getString(R.string.theater_map_t1))
//        }
        ttsA = true

//        binding.theaterMapI2.setImageDrawable(BitmapDrawable(MainActivity.BitmapArray[0]))
//        binding.theaterMapI1.setImageDrawable(BitmapDrawable(MainActivity.BitmapArray[1]))
//
        val mActivity = activity as MainActivity
//        mActivity.changeVisibility(0)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.theaterB1.setOnClickListener {
            selectedBtn.isSelected = false

            //MainActivity.viewModel.stop()

            selectedBtn = binding.theaterB1
            selectedBtn.isSelected = true
        }

        binding.theaterB2.setOnClickListener {
            selectedBtn.isSelected = false

            //MainActivity.viewModel.stop()
//            GlobalScope.launch {
//                if (ttsB == false) {
//                    MainActivity.viewModel.getResponse(resources.getString(R.string.theater_map_t2))
//                    ttsB = true
//                }
//            }

            selectedBtn = binding.theaterB2
            selectedBtn.isSelected = true
        }

        binding.theaterB3.setOnClickListener {
            selectedBtn.isSelected = false

            //MainActivity.viewModel.stop()
//            GlobalScope.launch {
//                if (ttsC == false) {
//                    MainActivity.viewModel.getResponse(resources.getString(R.string.theater_map_t3))
//                    ttsC = true
//                }
//            }

            selectedBtn = binding.theaterB3
            selectedBtn.isSelected = true
        }

        binding.theaterB4.setOnClickListener {
            selectedBtn.isSelected = false

            //MainActivity.viewModel.stop()
//            GlobalScope.launch {
//                if (ttsD == false) {
//                    MainActivity.viewModel.getResponse(resources.getString(R.string.theater_map_t4))
//                    ttsD = true
//                }
//            }

            binding.theaterMapI2.setImageResource(R.color.white)
            binding.theaterMapI1.setImageResource(R.color.grey)

            selectedBtn = binding.theaterB4
            selectedBtn.isSelected = true
        }

        binding.theaterB5.setOnClickListener {
            selectedBtn.isSelected = false

            MainActivity.viewModel.ttsStop()

//            if (ttsE == false) {
//                MainActivity.viewModel.getResponse(resources.getString(R.string.theater_map_t5))
//                ttsE = true
//            }

            selectedBtn = binding.theaterB5
            selectedBtn.isSelected = true
        }

        binding.theaterB6.setOnClickListener {
            selectedBtn.isSelected = false

            MainActivity.viewModel.ttsStop()

//            if (ttsF == false) {
//                MainActivity.viewModel.getResponse(resources.getString(R.string.theater_map_t6))
//                ttsF = true
//            }

            binding.theaterMapI1.setImageResource(R.color.select)

            selectedBtn = binding.theaterB6
            selectedBtn.isSelected = true
        }

        binding.theaterB7.setOnClickListener {
            selectedBtn.isSelected = false

            //MainActivity.viewModel.stop()
//            GlobalScope.launch {
//                if (ttsG == false) {
//                    MainActivity.viewModel.getResponse(resources.getString(R.string.theater_map_t7))
//                    ttsG = true
//                }
//            }

            binding.theaterMapI2.setImageResource(R.color.grey)
            binding.theaterMapI1.setImageResource(R.color.white)

            selectedBtn = binding.theaterB7
            selectedBtn.isSelected = true
        }

        binding.theaterB8.setOnClickListener {
            selectedBtn.isSelected = false

            //MainActivity.viewModel.stop()
//            GlobalScope.launch {
//                if (ttsH == false) {
//                    MainActivity.viewModel.getResponse(resources.getString(R.string.theater_map_t8))
//                    ttsH = true
//                }
//            }

            selectedBtn = binding.theaterB8
            selectedBtn.isSelected = true
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }
}