package com.arurbangarden.real

import android.app.Application
import android.content.Context
import androidx.preference.PreferenceManager

class ARUrbanGardenApplication : Application() {
    
    companion object {
        lateinit var instance: ARUrbanGardenApplication
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize reminders if enabled
        initializeReminders()
    }
    
    private fun initializeReminders() {
        val prefs = getSharedPreferences()
        val remindersEnabled = prefs.getBoolean("reminders_enabled", true)
        
        if (remindersEnabled) {
            val reminderManager = com.arurbangarden.real.data.reminder.ReminderManager(this)
            reminderManager.createDefaultReminders().forEach { reminder ->
                reminderManager.scheduleReminder(reminder)
            }
        }
    }
    
    fun getSharedPreferences() = PreferenceManager.getDefaultSharedPreferences(this)
    
    fun getLanguage(): String {
        return getSharedPreferences().getString("language", "tagalog") ?: "tagalog"
    }
    
    fun isVoiceGuidanceEnabled(): Boolean {
        return getSharedPreferences().getBoolean("voice_guidance", true)
    }
    
    fun isHighContrastEnabled(): Boolean {
        return getSharedPreferences().getBoolean("high_contrast", false)
    }
    
    fun hasParentalConsent(): Boolean {
        return getSharedPreferences().getBoolean("parental_consent", false)
    }
    
    fun isCloudSyncEnabled(): Boolean {
        return hasParentalConsent() && 
               getSharedPreferences().getBoolean("cloud_sync", false)
    }
}

