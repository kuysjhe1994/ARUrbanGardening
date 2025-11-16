package com.arurbangarden.real.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Reminder(
    val id: String,
    val type: ReminderType,
    val title: String,
    val titleTagalog: String,
    val message: String,
    val messageTagalog: String,
    val time: ReminderTime,
    val isEnabled: Boolean = true,
    val soundRes: Int? = null,  // Sound resource ID
    val plantId: String? = null  // Optional: specific plant reminder
) : Parcelable

enum class ReminderType {
    WATERING,      // Water plant reminder
    SUNLIGHT_CHECK, // Check sunlight reminder
    WEEKLY_PHOTO   // Weekly photo reminder
}

data class ReminderTime(
    val hour: Int,      // 0-23
    val minute: Int,   // 0-59
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5, 6, 7) // 1=Monday, 7=Sunday
) {
    fun getNextTriggerTime(): Long {
        // Calculate next trigger time based on current time and schedule
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
        calendar.set(java.util.Calendar.MINUTE, minute)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        
        // If time has passed today, move to next day
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        
        return calendar.timeInMillis
    }
}

data class SafetyModeSettings(
    val isEnabled: Boolean = true,
    val allowSocialSharing: Boolean = false,
    val allowLocationSharing: Boolean = false,
    val allowDataUpload: Boolean = false,
    val requireParentalConsent: Boolean = true,
    val hasParentalConsent: Boolean = false,
    val adultPIN: String? = null
)

