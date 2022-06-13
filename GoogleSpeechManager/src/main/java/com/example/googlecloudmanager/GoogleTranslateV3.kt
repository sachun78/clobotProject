package com.example.googlecloudmanager

import android.util.Log
import com.example.googlecloudmanager.common.Constant
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.translate.v3.*
import java.io.InputStream
import java.lang.Exception

object GoogleTranslateV3 : ISpeech {
    lateinit var client: TranslationServiceClient;
    lateinit var projectId: String
    private var sessionId: String? = null

    override fun initialize(_stream: InputStream, _sessionId: String?) {
        sessionId = _sessionId
        val credentials: GoogleCredentials = GoogleCredentials.fromStream(_stream)
            .createScoped(Constant.GOOGLE_SCOPE)
        projectId = (credentials as ServiceAccountCredentials).getProjectId()
        val translationSetting = TranslationServiceSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build()

        client = TranslationServiceClient.create(translationSetting)
    }

    fun translate(text: String): String {
        try {
            val request: TranslateTextRequest = TranslateTextRequest.newBuilder()
                .setParent(
                    LocationName.newBuilder().setProject(projectId).setLocation("global").build()
                        .toString()
                )
                .setMimeType("text/plain")
                .setSourceLanguageCode("en")
                .setTargetLanguageCode("ko")
                .addContents(text)
                .build()
            val response: TranslateTextResponse = client.translateText(request)
            println(response.getTranslations(0).translatedText)
            return response.getTranslations(0).translatedText
//            System.out.printf("Translated Text:\n\t%s\n", translation.getTranslatedText())
        } catch (e: Exception) {
            Log.e("Translate", "error $e")
            throw RuntimeException(e)
        }
    }
}