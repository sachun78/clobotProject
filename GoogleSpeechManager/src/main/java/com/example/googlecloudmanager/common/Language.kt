package com.example.googlecloudmanager.common


enum class Language(var s: String) {
    Korean("ko-KR") {
        override fun toString(): String {
            return s
        }
    },
    English("en-US") {
        override fun toString(): String {
            return s
        }
    },
    Japanese("ja-JP") {
        override fun toString(): String {
            return s
        }
    },
    Chinese("zh-CN") {
        override fun toString(): String {
            return s
        }
    }
}