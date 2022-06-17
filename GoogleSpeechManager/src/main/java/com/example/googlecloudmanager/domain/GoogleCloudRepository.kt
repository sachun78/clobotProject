package com.example.googlecloudmanager.domain

import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.util.Log
import com.example.googlecloudmanager.common.util.AudioEmitter
import com.example.googlecloudmanager.common.Resource
import com.example.googlecloudmanager.data.GoogleCloudApi
import com.google.api.gax.rpc.ClientStream
import com.google.api.gax.rpc.ResponseObserver
import com.google.api.gax.rpc.StreamController
import com.google.cloud.speech.v1.RecognitionConfig
import com.google.cloud.speech.v1.StreamingRecognitionConfig
import com.google.cloud.speech.v1.StreamingRecognizeRequest
import com.google.cloud.speech.v1.StreamingRecognizeResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

class GoogleCloudRepository constructor(
    private val api: GoogleCloudApi
) {

    var ischatfirst = false
    private val TAG = "GoogleCloudRepository"
    private val mp = MediaPlayer()
    private var audioEmitter: AudioEmitter = AudioEmitter()
    private var isRecoding: Boolean = false
    private lateinit var requestStream: ClientStream<StreamingRecognizeRequest>
    private var speech_text: String = ""

    // TTS speak function
    fun speak(_context: Context?, text: String) {
        val path = _context?.externalCacheDir
        val file = File(path, "output.mp3")

        api.textToAudio(text, file)

        FileInputStream(file).use {
            mp.reset()
            mp.setDataSource(it.fd)
            mp.prepare()
            mp.start()
        }
    }

    fun pause() {
        mp.pause()
    }

    fun start() {
        mp.start()
    }

    fun stop() {
        mp.stop()
    }

    // Google Speech
    fun speechStop() {
        Log.i(TAG, "speech_stop called")
        if (!isRecoding) return
        // ensure mic data stops
        requestStream.closeSend()
        audioEmitter.stop()
        isRecoding = false
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun speachToText(): Flow<Resource<String>> = callbackFlow {
        if (isRecoding) {
            //TODO(return after send)
            sendBlocking(Resource.Error("Already Using Recoding Resource. please try later!"))
        }
        try {
            val isFirstRequest = AtomicBoolean(true)
            isRecoding = true
            speech_text = ""

            val callback = object : ResponseObserver<StreamingRecognizeResponse> {
                override fun onStart(controller: StreamController?) {
                    Log.i(TAG, "start recognize")
                }

                override fun onResponse(response: StreamingRecognizeResponse?) {
                    when {
                        response?.resultsCount!! > 0 -> {
                            val transcript = response.getResults(0).getAlternatives(0).transcript
                            speech_text = transcript
                            sendBlocking(Resource.Listen(transcript))
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
            }

            requestStream = api.getSpeechClient().streamingRecognizeCallable().splitCall(callback)

            if(ischatfirst == false) {
                sendBlocking(Resource.Loading("1"))
                delay(1000)
                sendBlocking(Resource.Loading("2"))
                delay(1000)
                sendBlocking(Resource.Loading("3"))
                delay(1000)
            }

            val tone = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
            tone.startTone(ToneGenerator.TONE_PROP_BEEP, 250)

            audioEmitter.start { bytes ->
                val builder = StreamingRecognizeRequest.newBuilder()
                    .setAudioContent(bytes)
                if (isFirstRequest.getAndSet(false)) {
                    builder.streamingConfig = StreamingRecognitionConfig.newBuilder()
                        .setConfig(
                            RecognitionConfig.newBuilder()
                                .setLanguageCode("ko-KR")
                                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                                .setSampleRateHertz(16000).build()
                        )
                        .setInterimResults(true)
                        .setSingleUtterance(true)
                        .build()
                }
                requestStream.send(builder.build())
            }
            // 8초 뒤 입력 종료 (3초 뒤 시작 , 5초 동안 음성 인식)
            delay(5000)
            speechStop()
            sendBlocking(Resource.Complete(speech_text))
            close()
        } catch (e: IOException) {
            sendBlocking(Resource.Error("Couldn't reach server. Check your internet connection."))
        }

        awaitClose {
            Log.i(TAG, "callback flow finish.")
        }
    }
}