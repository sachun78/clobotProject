package com.lge.support.second.application.main.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentChatBinding
import com.lge.support.second.application.main.view.adapter.questionModel
import kotlin.collections.ArrayList


class chat : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    var modalList = ArrayList<questionModel>()
    var questions = arrayOf(
        "국립극장은 무엇을 하는 곳인가요?", "공연예매는 어떻게 해요?", "고객지원센터는 어디에요?",
        "해오름극장이 어디에요?", "무대감독, 음향감독, 조명감독", "백스테이지 출입은 어떻게 하나요?"
    )
    var click: Boolean = false

//    private val viewModel: ChatbotViewModel by lazy {
//        ViewModelProvider(
//            requireActivity(),
//            ChatbotViewModel.Factory(ChatbotRepository(chatbotService), GoogleCloudRepository(googleService))
//        ).get(ChatbotViewModel::class.java)
//    }
//
//    private val chatbotService = ChatbotApi.getInstance()
//    private val googleCredential: InputStream = this.resources.openRawResource(R.raw.credential)
//    private val googleService: GoogleCloudApi =GoogleCloudApi.getInstance(googleCredential, UUID.randomUUID().toString())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity
        mActivity.findViewById<ImageView>(R.id.qiMessage).visibility = View.GONE

        if (click == false) {
            for (i in questions.indices) {
                modalList.add(questionModel(questions[i]))
            }
            click = true
        }

        var customAdapter =
            getActivity()?.getApplicationContext()?.let { CustomAdapter(modalList, it) }

        binding.chatGridView.adapter = customAdapter

        ///////chat page 진입 확인용//////////
        MainActivity.chatPage = true

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gridView = binding.chatGridView

        Handler().postDelayed({
            binding.chatT2.setText("듣고 있어요")
        }, 3000) ///////////2.9sec

        binding.chatI1.setOnClickListener {
//            MainActivity.viewModel.getResponse("하이 큐아이")
//            (activity as MainActivity).changeFragment("answer_1")
            MainActivity.viewModel.speechResponse()
        }

        binding.chatGridView.setOnItemClickListener { adapterView, view, i, l ->
            MainActivity.viewModel.getResponse(questions[i])
            //(activity as MainActivity).changeFragment("${MainActivity.page_id}")
        }

        MainActivity.viewModel.speechResponse()
        MainActivity.viewModel.ischatfirst = true
    }

}

class CustomAdapter(var itemModel: ArrayList<questionModel>, var context: Context) : BaseAdapter() {
    var layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return itemModel.size
    }

    override fun getItem(p0: Int): Any {
        return itemModel[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        var view = view
        if (view == null) {
            view = layoutInflater.inflate(R.layout.row_item, viewGroup, false)
        }

        var question = view?.findViewById<TextView>(R.id.question)

        if (question != null) {
            question.text = itemModel[position].question
        }
        return view!!
    }
}