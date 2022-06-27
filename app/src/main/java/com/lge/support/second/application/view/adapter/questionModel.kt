package com.lge.support.second.application.view.adapter

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