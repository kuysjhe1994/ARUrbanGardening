package com.arurbangarden.real.util

import android.content.Context
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import com.arurbangarden.real.ARUrbanGardenApplication
import java.util.Locale

/**
 * Voice guidance utility for child-friendly audio instructions
 */
class VoiceGuidance(private val context: Context) {
    
    private var tts: TextToSpeech? = null
    private var isEnabled = true
    private var language = "tagalog"
    
    init {
        initializeTTS()
        loadSettings()
    }
    
    private fun initializeTTS() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                setLanguage()
            }
        }
    }
    
    private fun loadSettings() {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        isEnabled = prefs.getBoolean("voice_guidance", true)
        language = prefs.getString("language", "tagalog") ?: "tagalog"
    }
    
    private fun setLanguage() {
        val locale = when (language) {
            "tagalog" -> Locale("fil", "PH") // Filipino/Tagalog
            "english" -> Locale.ENGLISH
            else -> Locale.getDefault()
        }
        
        tts?.language = locale
    }
    
    /**
     * Speak text with voice guidance
     */
    fun speak(text: String, tagalogText: String? = null) {
        if (!isEnabled) return
        
        val textToSpeak = when (language) {
            "tagalog" -> tagalogText ?: text
            else -> text
        }
        
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
    }
    
    /**
     * Speak plant identification result
     */
    fun speakPlantFound(plantName: String, plantNameTagalog: String) {
        val text = when (language) {
            "tagalog" -> "Natuklasan: $plantNameTagalog"
            else -> "Found: $plantName"
        }
        speak(text)
    }
    
    /**
     * Speak care action
     */
    fun speakCareAction(action: String, actionTagalog: String) {
        val text = when (language) {
            "tagalog" -> actionTagalog
            else -> action
        }
        speak(text)
    }
    
    /**
     * Speak measurement result
     */
    fun speakMeasurement(distance: Float, unit: String = "cm") {
        val text = when (language) {
            "tagalog" -> "Distansya: ${distance.toInt()} sentimetro"
            else -> "Distance: ${distance.toInt()} $unit"
        }
        speak(text)
    }
    
    fun stop() {
        tts?.stop()
    }
    
    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}

