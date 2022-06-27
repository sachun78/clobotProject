package com.lge.support.second.application.view.template

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentAnswerList1Binding
import com.lge.support.second.application.view.adapter.questionModel


class answer_list_1 : Fragment() {

    private var _binding: FragmentAnswerList1Binding? = null
    private val binding get() = _binding!!

    var answerList = ArrayList<questionModel>()
    var questions = ArrayList<String>()
    var Arrtype = ArrayList<String>()

    var isCreatedThisPage = false

    val questionStr = MainActivity.inStr
    val answerStr = MainActivity.speechStr

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnswerList1Binding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity
        mActivity.findViewById<ImageView>(R.id.qrImg).visibility = View.GONE

        Log.i("answer_list check ", "is chatPage? " + MainActivity.chatPage)

        binding.answerList1T1.setText(questionStr)
        binding.answerList1T2.setText(answerStr)

        MainActivity.viewModel.queryResult.observe(viewLifecycleOwner) {

            if (it == null) {
                return@observe
            }

            if (isCreatedThisPage == false) {

                for(question in it.messages[0].button) {
                    Log.i("question check", "add " + question)

                    questions.add(question.text)
                    answerList.add(questionModel(question.text))
                    Arrtype.add(question.type)
                }

                isCreatedThisPage = true
            }
        }

        var customAdapter =
            getActivity()?.getApplicationContext()?.let { answer_customAdapter(answerList, it) }

        binding.list1Gridview.adapter = customAdapter

        binding.list1Gridview.setOnItemClickListener { adapterView, view, position, id ->
            Log.i("answer_list check", "position is " + position)
            Log.i("answer_list check", "getResponse is " + questions[position])
            MainActivity.viewModel.getResponse(questions[position], Arrtype[position])
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }
}

class answer_customAdapter(var itemModel: ArrayList<questionModel>, var context: Context) :
    BaseAdapter() {
    var layoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return itemModel.size ///itemModel.size보다 작거나 같은 값을 return
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
            view = layoutInflater.inflate(R.layout.anwer_list_row_item, viewGroup, false)
        }

        var question = view?.findViewById<TextView>(R.id.question)

        if (question != null) {
            question.text = itemModel[position].question
        }
        return view!!
    }
}