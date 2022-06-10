package com.example.googlecloudmanager

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.util.Log
import com.example.googlecloudmanager.common.Resource
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.api.gax.rpc.ClientStream
import com.google.api.gax.rpc.ResponseObserver
import com.google.api.gax.rpc.StreamController
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.speech.v1.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.atomic.AtomicBoolean

object GoogleSTT {
    private const val TAG = "GoogleSpeechToTextAPI"
    private var speechClient: SpeechClient? = null
    private var projectId: String? = null
    private var sessionId: String? = null

    private var audioEmitter: AudioEmitter? = null
    private var requestStream: ClientStream<StreamingRecognizeRequest>? = null

    // * setting values
    private var language: Language = Language.Korean
    private var isRecoding: Boolean = false
    private var speech_timeout: Long = 8000
    private var sampleRate = 16000

    // Text
    var text: String = ""

    @Throws(IOException::class)
    fun initialize(
        _stream: InputStream,
        _sessionId: String?,
        _language: Language?
    ) {
        sessionId = _sessionId
        val credentials: GoogleCredentials = GoogleCredentials.fromStream(_stream)
            .createScoped("https://www.googleapis.com/auth/cloud-platform")
        projectId = (credentials as ServiceAccountCredentials).getProjectId()
        val speechSettings = SpeechSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build()
        speechClient = SpeechClient.create(speechSettings)
        Log.d(TAG, "connected, $projectId")

        if (_language != null) {
            language = _language
        }

        audioEmitter = AudioEmitter()
    }

    fun setLanguage(_language: Language) {
        language = _language
    }

    private fun speech_stop() {
        Log.i(TAG, "speech_stop called")
        if (!isRecoding) return
        // ensure mic data stops
        requestStream?.closeSend()
        audioEmitter?.stop()
        isRecoding = false
    }

    @Deprecated("not flowed function", ReplaceWith("speechToTextFlow"))
    fun speechToText() {
        val isFirstRequest = AtomicBoolean(true)
        isRecoding = true
        text = ""

        requestStream = speechClient?.streamingRecognizeCallable()
            ?.splitCall(object : ResponseObserver<StreamingRecognizeResponse> {
                override fun onStart(controller: StreamController?) {
                    Log.i(TAG, "start recognize")
                }

                override fun onResponse(response: StreamingRecognizeResponse?) {
                    when {
                        response?.resultsCount!! > 0 -> {
                            text = response.getResults(0).getAlternatives(0).transcript
                            println(text)
                        }
                        else -> println(response.error)
                    }
                }

                override fun onError(t: Throwable?) {
                    Log.e(TAG, "an error occurred", t)
                }

                override fun onComplete() {
                    Log.d(TAG, "stream closed")
                }
            })
        audioEmitter!!.start { bytes ->
            val builder = StreamingRecognizeRequest.newBuilder()
                .setAudioContent(bytes)
            if (isFirstRequest.getAndSet(false)) {
                builder.streamingConfig = StreamingRecognitionConfig.newBuilder()
                    .setConfig(
                        RecognitionConfig.newBuilder()
                            .setLanguageCode(language.toString())
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setSampleRateHertz(sampleRate).build()
                    )
                    .setInterimResults(true)
                    .setSingleUtterance(true)
                    .build()
            }
            requestStream!!.send(builder.build())
        }
        // 8초 뒤 입력 종료 (3초 뒤 시작 , 5초 동안 음성 인식)
        Handler().postAtTime({ speech_stop() }, speech_timeout)
    }

    fun speechToTextFlow(): Flow<Resource<Boolean>> = flow {
        if (isRecoding) {
            emit(Resource.Error("Already Using Recoding Resource. please try later!"))
        }

        try {
            isRecoding = true
            val isFirstRequest = AtomicBoolean(true)
            emit(Resource.Loading(false))
            requestStream = speechClient?.streamingRecognizeCallable()
                ?.splitCall(object : ResponseObserver<StreamingRecognizeResponse> {
                    override fun onStart(controller: StreamController?) {
                        Log.i(TAG, "start recognize")
                    }

                    override fun onResponse(response: StreamingRecognizeResponse?) {
                        when {
                            response?.resultsCount!! > 0 -> {
                                text = response.getResults(0).getAlternatives(0).transcript
                                println(text)
                            }
                            else -> println(response.error)
                        }
                    }

                    override fun onError(t: Throwable?) {
                        Log.e(TAG, "an error occurred", t)
                    }

                    override fun onComplete() {
                        Log.d(TAG, "stream closed")
                    }
                })

            delay(3000)
            val tone = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
            tone.startTone(ToneGenerator.TONE_PROP_BEEP, 250)

            audioEmitter!!.start { bytes ->
                val builder = StreamingRecognizeRequest.newBuilder()
                    .setAudioContent(bytes)
                if (isFirstRequest.getAndSet(false)) {
                    builder.streamingConfig = StreamingRecognitionConfig.newBuilder()
                        .setConfig(
                            RecognitionConfig.newBuilder()
                                .setLanguageCode(language.toString())
                                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                                .setSampleRateHertz(sampleRate).build()
                        )
                        .setInterimResults(true)
                        .setSingleUtterance(true)
                        .build()
                }
                requestStream!!.send(builder.build())
            }
            // 8초 뒤 입력 종료 (3초 뒤 시작 , 5초 동안 음성 인식)
            delay(speech_timeout)
            speech_stop()
            emit(Resource.Success(true))
        } catch (e: IOException) {
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}