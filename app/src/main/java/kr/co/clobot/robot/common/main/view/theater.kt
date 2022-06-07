package kr.co.clobot.robot.common.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kr.co.clobot.robot.common.MainActivity
import kr.co.clobot.robot.common.R
import kr.co.clobot.robot.common.databinding.FragmentTheaterBinding

class theater : Fragment() {
    //    private var _binding: FragmentTheaterBinding? = null
//    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_theater, container, false)

        val mActivity = activity as MainActivity
        val menu1 = rootView.findViewById<ImageView>(R.id.theater_i1)

        menu1.setOnClickListener {
            mActivity.changeFragment(31)
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}