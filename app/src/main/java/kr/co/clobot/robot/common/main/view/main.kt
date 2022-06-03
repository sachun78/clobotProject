package kr.co.clobot.robot.common.main.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kr.co.clobot.robot.common.R
import kr.co.clobot.robot.common.databinding.FragmentMainBinding

class main : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View ? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainI2.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_theater)
        }

        binding.mainI3.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_enjoy)
        }
    }
}