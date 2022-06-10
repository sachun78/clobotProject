package com.example.googlecloudmanager

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.texttospeech.v1.*
import com.google.protobuf.ByteString
import java.io.*

object GoogleTTS {
    private const val TAG = "Google TTS API"

    private var ttsClient: TextToSpeechClient? = null
    private var projectId: String? = null
    private var sessionId: String? = null
    private var mp = MediaPlayer()

    @Throws(IOException::class)
    fun initialize(
        _stream: InputStream,
        _sessionId: String?,
    ) {
        sessionId = _sessionId
        val credentials: GoogleCredentials = GoogleCredentials.fromStream(_stream)
            .createScoped("https://www.googleapis.com/auth/cloud-platform")
        projectId = (credentials as ServiceAccountCredentials).getProjectId()
        println(projectId)
        val speechSettings = TextToSpeechSettings.newBuilder()
            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
            .build()

        ttsClient = TextToSpeechClient.create(speechSettings)
        Log.i(TAG, "connection success!")
    }

    fun speak(_context: Context?, text: String) {
        if (ttsClient == null) return
        val input: SynthesisInput = SynthesisInput.newBuilder().setText(text).build()
        val voice: VoiceSelectionParams = VoiceSelectionParams.newBuilder()
            .setLanguageCode("ko-KR")
            .setSsmlGender(SsmlVoiceGender.MALE)
            .build()
        val audioConfig: AudioConfig =
            AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.MP3)
                .setSampleRateHertz(44100).build()
        val response: SynthesizeSpeechResponse? =
            ttsClient?.synthesizeSpeech(input, voice, audioConfig)
        val audioContents: ByteString = response!!.audioContent

        val path = _context?.getExternalFilesDir(null)
        val file = File(path, "output.mp3")
        println(file.path)
        FileOutputStream(file).use { out ->
            out.write(audioContents.toByteArray())
            Log.i(TAG, "Audio content written to file \"output.mp3\"")
            out.close()
        }
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

}