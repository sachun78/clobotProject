package com.lge.support.second.application.view.template

import android.content.Context
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.lge.support.second.application.MainActivity
import com.lge.support.second.application.R
import com.lge.support.second.application.databinding.FragmentAnswerList2Binding
import com.lge.support.second.application.view.adapter.answerList2Adapter
import com.lge.support.second.application.view.adapter.answerlist2Model
import io.grpc.InternalChannelz.id
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class answer_list_2 : Fragment() {

    private lateinit var binding: FragmentAnswerList2Binding

    var Arrtype = ArrayList<String>()
    var isCreatedThisPage = false
    val questionStr = MainActivity.inStr
    val answerStr = MainActivity.speechStr

    var adapter: answerList2Adapter? = null
    var nameList = ArrayList<answerlist2Model>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnswerList2Binding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity

        Log.i("answer_list_2", "is chatPage? " + MainActivity.chatPage)

        binding.answerList1T1.setText(questionStr)
        binding.answerList1T2.setText(answerStr)

        MainActivity.viewModel.queryResult.observe(viewLifecycleOwner) {

            if (it == null) {
                return@observe
            }

            if (isCreatedThisPage == false) {
                for (question in it.messages[0].imageButton) {
                    nameList.add(answerlist2Model(question.text, question.url))
                    Arrtype.add(question.type)
                }
                isCreatedThisPage = true
            }
//            adapter?.notifyDataSetChanged()
        }

        adapter = answerList2Adapter(nameList, getActivity()?.getApplicationContext())
        binding.list2Gridview.adapter = adapter

        binding.list2Gridview.setOnItemClickListener { adapterView, view, position, id ->
//            Log.i
            Toast.makeText(context, "click $id", Toast.LENGTH_SHORT).show()
            Log.i("answer_list_2", "text is " + nameList[position].text.toString())
            Log.i("answer_list_2", "in_type is " + Arrtype[position])

//            nameList[position].text?.let { MainActivity.viewModel.getResponse(it, Arrtype[position]) }
            GlobalScope.launch {
                MainActivity.viewModel.getResponse(
                    nameList[position].text.toString(),
                    Arrtype[position]
                )
            }

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }
}
