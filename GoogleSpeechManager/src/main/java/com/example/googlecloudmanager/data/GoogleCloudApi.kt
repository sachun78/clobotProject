package com.example.googlecloudmanager.data

import android.util.Log
import com.example.googlecloudmanager.common.Constant
import com.example.googlecloudmanager.common.Language
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.speech.v1.SpeechClient
import com.google.cloud.speech.v1.SpeechSettings
import com.google.cloud.texttospeech.v1.*
import com.google.cloud.translate.v3.TranslationServiceClient
import com.google.cloud.translate.v3.TranslationServiceSettings
import com.google.protobuf.ByteString
import org.apache.commons.codec.language.bm.Lang
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

interface GoogleCloudApi {
    fun getSpeechClient(): SpeechClient {
        return _speechClient
    }

    fun setLanguage(_language: Language) {
        language = _language
    }

    fun getLanguage(): Language {
        return language
    }

    fun textToAudio(text: String, file: File) {
        val input: SynthesisInput = SynthesisInput.newBuilder().setText(text).build()
        val voice: VoiceSelectionParams = VoiceSelectionParams.newBuilder()
            .setLanguageCode(language.toString())
            .setSsmlGender(SsmlVoiceGender.MALE)
            .build()
        val audioConfig: AudioConfig =
            AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3)
                .setSampleRateHertz(44100).build()
        val response: SynthesizeSpeechResponse? =
            ttsClient.synthesizeSpeech(input, voice, audioConfig)
        val audioContents: ByteString = response!!.audioContent

        FileOutputStream(file).use { out ->
            out.write(audioContents.toByteArray())
            Log.i(TAG, "Audio content written to file \"output.mp3\"")
            out.close()
        }
    }

    companion object {
        private const val TAG = "GoogleCloudApi"
        lateinit var projectId: String
        private lateinit var sessionId: String
        private var language = Language.Korean

        // use on repository
        lateinit var ttsClient: TextToSpeechClient
        lateinit var _speechClient: SpeechClient
        lateinit var translateClient: TranslationServiceClient;

        var googleCloudService: GoogleCloudApi? = null

        fun getInstance(
            _stream: InputStream,
            _sessionId: String,
        ): GoogleCloudApi {
            if (googleCloudService == null) {
                sessionId = _sessionId
                val credentials: GoogleCredentials = GoogleCredentials.fromStream(_stream)
                    .createScoped(Constant.GOOGLE_SCOPE)
                projectId = (credentials as ServiceAccountCredentials).projectId

                // Initialize Text-To-Speech V1
                val textToSppechSettings = TextToSpeechSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build()
                ttsClient = TextToSpeechClient.create(textToSppechSettings)
                Log.i(TAG, "GoogleCloud ttsClient connected!")

                // Initialize Speech-To-Text V1
                val speechSettings = SpeechSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build()
                _speechClient = SpeechClient.create(speechSettings)
                Log.i(TAG, "GoogleCloud speechClient connected!")

                // Initialize Translate V3
                val translationSetting = TranslationServiceSettings.newBuilder()
                    .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                    .build()
                translateClient = TranslationServiceClient.create(translationSetting)
                Log.i(TAG, "GoogleCloud translateClient connected!")

                googleCloudService = object : GoogleCloudApi {}
            }

            Log.i(TAG, "GoogleCloud service created!")
            return googleCloudService!!
        }

    }
}