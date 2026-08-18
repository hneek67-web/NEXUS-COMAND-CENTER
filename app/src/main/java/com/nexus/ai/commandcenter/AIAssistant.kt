package com.nexus.ai.commandcenter

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class AIAssistant(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
            tts.setPitch(0.7f)
            tts.setSpeechRate(1.1f)
            isReady = true
        }
    }

    fun speak(message: String) {
        if (isReady) tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
