package com.arurbangarden.real.data.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import com.arurbangarden.real.data.model.Reminder
import com.arurbangarden.real.data.model.ReminderType
import com.arurbangarden.real.receiver.ReminderReceiver
import java.util.Calendar
import java.util.UUID

class ReminderManager(private val context: Context) {
    
    private val alarmManager: AlarmManager = 
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    /**
     * Schedule a reminder
     */
    fun scheduleReminder(reminder: Reminder) {
        if (!reminder.isEnabled) {
            cancelReminder(reminder)
            return
        }
        
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("reminder_id", reminder.id)
            putExtra("reminder_type", reminder.type.name)
            putExtra("title", reminder.title)
            putExtra("title_tagalog", reminder.titleTagalog)
            putExtra("message", reminder.message)
            putExtra("message_tagalog", reminder.messageTagalog)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val triggerTime = reminder.time.getNextTriggerTime()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
    
    /**
     * Cancel a reminder
     */
    fun cancelReminder(reminder: Reminder) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
    
    /**
     * Create default reminders
     */
    fun createDefaultReminders(): List<Reminder> {
        return listOf(
            Reminder(
                id = UUID.randomUUID().toString(),
                type = ReminderType.WATERING,
                title = "Time to Water! 💧",
                titleTagalog = "Oras na para Diligan! 💧",
                message = "Don't forget to water your plant today!",
                messageTagalog = "Huwag kalimutang diligan ang halaman ngayon!",
                time = com.arurbangarden.real.data.model.ReminderTime(
                    hour = 9,  // 9 AM
                    minute = 0,
                    daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7) // Daily
                )
            ),
            Reminder(
                id = UUID.randomUUID().toString(),
                type = ReminderType.SUNLIGHT_CHECK,
                title = "Check Sunlight! ☀️",
                titleTagalog = "Tingnan ang Araw! ☀️",
                message = "Make sure your plant gets enough sunlight!",
                messageTagalog = "Siguraduhing nakakakuha ng sapat na araw ang halaman!",
                time = com.arurbangarden.real.data.model.ReminderTime(
                    hour = 12,  // 12 PM
                    minute = 0,
                    daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7) // Daily
                )
            ),
            Reminder(
                id = UUID.randomUUID().toString(),
                type = ReminderType.WEEKLY_PHOTO,
                title = "Weekly Photo! 📸",
                titleTagalog = "Lingguhang Larawan! 📸",
                message = "Take a photo to track your plant's growth!",
                messageTagalog = "Kumuha ng larawan para subaybayan ang paglago!",
                time = com.arurbangarden.real.data.model.ReminderTime(
                    hour = 10,  // 10 AM
                    minute = 0,
                    daysOfWeek = listOf(1) // Monday only
                )
            )
        )
    }
    
    /**
     * Check if reminders are enabled
     */
    fun areRemindersEnabled(): Boolean {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getBoolean("reminders_enabled", true)
    }
}

