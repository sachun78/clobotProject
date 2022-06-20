package com.example.googlecloudmanager.common


interface ILanguage<T> {
    fun toLocalString(): String
}


enum class Language(var s: String) : ILanguage<Language> {
    Korean("ko-KR") {
        override fun toString(): String {
            return s
        }

        override fun toLocalString(): String {
            return "ko"
        }

    },
    English("en-US") {
        override fun toString(): String {
            return s
        }

        override fun toLocalString(): String {
            return "en"
        }
    },
    Japanese("ja-JP") {
        override fun toString(): String {
            return s
        }

        override fun toLocalString(): String {
            return "jp"
        }
    },
    Chinese("zh-CN") {
        override fun toString(): String {
            return s
        }

        override fun toLocalString(): String {
            return "cn"
        }
    }
}