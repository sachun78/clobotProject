package kr.co.clobot.robot.common.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import kr.co.clobot.robot.common.MainActivity
import kr.co.clobot.robot.common.R
import kr.co.clobot.robot.common.databinding.FragmentTheaterMapBinding


class theater_map : Fragment() {

    private var _binding: FragmentTheaterMapBinding? = null
    private val binding get() = _binding!!

    //선택된 버튼
    lateinit var selectedBtn : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTheaterMapBinding.inflate(inflater, container, false)

        //기본은 첫 번째 버튼 클릭되어 있게
        selectedBtn = binding.theaterB1
        selectedBtn.isSelected = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.theaterB1.setOnClickListener {
            selectedBtn.isSelected = false

            selectedBtn = binding.theaterB1
            selectedBtn.isSelected = true
        }

        binding.theaterB2.setOnClickListener{
            selectedBtn.isSelected = false

            selectedBtn = binding.theaterB2
            selectedBtn.isSelected = true
        }

        binding.theaterB3.setOnClickListener{
            selectedBtn.isSelected = false

            selectedBtn = binding.theaterB3
            selectedBtn.isSelected = true
        }

        binding.theaterB4.setOnClickListener{
            selectedBtn.isSelected = false

            binding.theaterMapI2.setImageResource(R.color.white)
            binding.theaterMapI1.setImageResource(R.color.gray)

            selectedBtn = binding.theaterB4
            selectedBtn.isSelected = true
        }

        binding.theaterB5.setOnClickListener{
            selectedBtn.isSelected = false

            selectedBtn = binding.theaterB5
            selectedBtn.isSelected = true
        }

        binding.theaterB6.setOnClickListener{
            selectedBtn.isSelected = false

            binding.theaterMapI1.setImageResource(R.color.select)

            selectedBtn = binding.theaterB6
            selectedBtn.isSelected = true
        }

        binding.theaterB7.setOnClickListener{
            selectedBtn.isSelected = false

            binding.theaterMapI2.setImageResource(R.color.gray)
            binding.theaterMapI1.setImageResource(R.color.white)

            selectedBtn = binding.theaterB7
            selectedBtn.isSelected = true
        }

        binding.theaterB8.setOnClickListener{
            selectedBtn.isSelected = false

            selectedBtn = binding.theaterB8
            selectedBtn.isSelected = true
        }

    }
}