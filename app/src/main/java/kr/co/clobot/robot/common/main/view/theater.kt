package kr.co.clobot.robot.common.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import kr.co.clobot.robot.common.R
import kr.co.clobot.robot.common.databinding.FragmentTheaterBinding

class theater : Fragment() {
    private var _binding: FragmentTheaterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTheaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.theaterI1.setOnClickListener {
            findNavController().navigate(R.id.action_theater_to_theater_map)
        }
    }
}