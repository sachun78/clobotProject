package com.lge.support.second.application.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.lge.support.second.application.R

class questionModel {
    var question: String? = null

    constructor(question: String) {
        this.question = question
    }
}

class answerlist2Model {
    var text: String? = null
    var url: String? = null

    constructor(text:String, url:String) {
        this.text = text
        this.url = url
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