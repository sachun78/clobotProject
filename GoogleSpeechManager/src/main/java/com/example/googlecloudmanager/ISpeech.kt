package com.example.googlecloudmanager

import com.example.googlecloudmanager.common.Language
import java.io.InputStream

interface ISpeech {
    companion object {
        var language: Language = Language.Korean
    }

    fun initialize(
        _stream: InputStream,
        _sessionId: String?,
    );

    fun setLanguage(_language: Language) {
        language = _language
    }

    fun getLanguage(): Language {
        return language
    }
}