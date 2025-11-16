package com.arurbangarden.real.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.arurbangarden.real.data.model.SafetyModeSettings

/**
 * Safety Mode Manager
 * Enforces kid-safe restrictions
 */
object SafetyMode {
    
    fun getSettings(context: Context): SafetyModeSettings {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return SafetyModeSettings(
            isEnabled = prefs.getBoolean("safety_mode_enabled", true),
            allowSocialSharing = prefs.getBoolean("allow_social_sharing", false),
            allowLocationSharing = prefs.getBoolean("allow_location_sharing", false),
            allowDataUpload = prefs.getBoolean("allow_data_upload", false),
            requireParentalConsent = prefs.getBoolean("require_parental_consent", true),
            hasParentalConsent = prefs.getBoolean("parental_consent", false),
            adultPIN = prefs.getString("adult_pin", null)
        )
    }
    
    fun isSocialSharingAllowed(context: Context): Boolean {
        val settings = getSettings(context)
        return !settings.isEnabled || settings.allowSocialSharing
    }
    
    fun isLocationSharingAllowed(context: Context): Boolean {
        val settings = getSettings(context)
        return !settings.isEnabled || settings.allowLocationSharing
    }
    
    fun isDataUploadAllowed(context: Context): Boolean {
        val settings = getSettings(context)
        return !settings.isEnabled || settings.allowDataUpload
    }
    
    fun hasParentalConsent(context: Context): Boolean {
        val settings = getSettings(context)
        return !settings.requireParentalConsent || settings.hasParentalConsent
    }
    
    fun isAdBlocked(context: Context): Boolean {
        // Always block ads in kid-safe mode
        return true
    }
    
    fun isTrackingBlocked(context: Context): Boolean {
        // Always block third-party tracking
        return true
    }
}

