package com.lge.support.second.application.main.view.tabView

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentChatFaqBinding
import com.lge.support.second.application.main.view.CustomAdapter
import com.lge.support.second.application.main.view.adapter.questionModel


class chat_faq : Fragment() {
    private var _binding: FragmentChatFaqBinding? = null
    private val binding get() = _binding!!

    var click: Boolean = false

    var modelList = ArrayList<questionModel>()
    var questions = arrayOf(
        "고객지원센터는어디에요?", "question", "question","question",
        "question", "question", "question","question"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatFaqBinding.inflate(inflater, container, false)

        var customAdapter =
            getActivity()?.getApplicationContext()?.let { faqCustomAdapter(modelList, it) }

        binding.faqGridView.adapter = customAdapter

        if (click == false) {
            for (i in questions.indices) {
                modelList.add(questionModel(questions[i]))
            }
            click = true
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.faqGridView.setOnItemClickListener { adapterView, view, i, l ->
            MainActivity.viewModel.getResponse(questions[i])
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.stop()
        Log.d("tk_test", "faq destroy")
    }
}

class faqCustomAdapter(var itemModel: ArrayList<questionModel>, var context: Context) : BaseAdapter() {
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
            view = layoutInflater.inflate(R.layout.faq_row_item, viewGroup, false)
        }

        var question = view?.findViewById<TextView>(R.id.faq_question)

        if (question != null) {
            question.text = itemModel[position].question
        }
        return view!!
    }
}