package com.lge.support.second.application.view.template

import android.app.PendingIntent.getActivity
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
import com.lge.support.second.application.view.adapter.answerlist2Model
import io.grpc.InternalChannelz.id


class answer_list_2 : Fragment() {

    private var _binding: FragmentAnswerList2Binding? = null
    private val binding get() = _binding!!
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
        _binding = FragmentAnswerList2Binding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity
        mActivity.findViewById<ImageView>(R.id.qrImg).visibility = View.GONE

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
        }

        adapter = answerList2Adapter(nameList, getActivity()?.getApplicationContext())
        binding.list2Gridview.adapter = adapter

        binding.list2Gridview.setOnItemClickListener { adapterView, view, position, id ->
//            Log.i
            Toast.makeText(context, "click $id", Toast.LENGTH_SHORT).show()
            Log.i("answer_list_2", "text is " + nameList[position].text.toString())
            Log.i("answer_list_2", "in_type is " + Arrtype[position])

//            nameList[position].text?.let { MainActivity.viewModel.getResponse(it, Arrtype[position]) }
            MainActivity.viewModel.getResponse(nameList[position].text.toString(), Arrtype[position])

            //MainActivity.viewModel.getResponse("����", "param")
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        MainActivity.viewModel.ttsStop()
    }
}

class answerList2Adapter : BaseAdapter {

    var nameList = ArrayList<answerlist2Model>()
//    var context: Context? = null
    lateinit var context:Context

    constructor(nameList: ArrayList<answerlist2Model>, context: Context?) : super() {
        this.nameList = nameList
        if (context != null) {
            this.context = context
        }
    }

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {
        val nameGridList = this.nameList[position]
        var inflator = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var nameView = inflator.inflate(R.layout.answer_list2_row_item, null)

        var text = nameView.findViewById<TextView>(R.id.answer_list2_t1)
        var img = nameView.findViewById<ImageView>(R.id.answer_list2_i1)

        if (text != null) {
            text.text = nameGridList.text
        }
        //nameView.findViewById<ImageView>(R.id.questionImg).setImageResource()
        if(img != null) {
            Glide.with(this.context).load(nameGridList.url).into(img)
        }
        //nameView.findViewById<TextView>(R.id.question).text = nameGridList.name

        return nameView
    }

    override fun getCount(): Int {
        return nameList.size
    }

    override fun getItem(position: Int): Any {
        return nameList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

}