package com.example.googlecloudmanager


enum class Language(var s: String) {
    Korean("ko-KR") {
        override fun toString(): String {
            return s
        }
    },
    English("en-US"),
    Japanese("ja-JP"),
    Chinese("zh-CN")
}