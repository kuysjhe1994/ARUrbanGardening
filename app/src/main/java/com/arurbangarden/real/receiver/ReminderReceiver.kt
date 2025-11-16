package com.arurbangarden.real.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.arurbangarden.real.ARUrbanGardenApplication
import com.arurbangarden.real.R
import com.arurbangarden.real.ui.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        // Check if reminders are enabled
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val remindersEnabled = prefs.getBoolean("reminders_enabled", true)
        
        if (!remindersEnabled) {
            return
        }
        
        val reminderId = intent.getStringExtra("reminder_id") ?: return
        val reminderType = intent.getStringExtra("reminder_type") ?: return
        val title = intent.getStringExtra("title") ?: ""
        val titleTagalog = intent.getStringExtra("title_tagalog") ?: ""
        val message = intent.getStringExtra("message") ?: ""
        val messageTagalog = intent.getStringExtra("message_tagalog") ?: ""
        
        // Get language preference
        val app = context.applicationContext as ARUrbanGardenApplication
        val isTagalog = app.getLanguage() == "tagalog"
        
        val displayTitle = if (isTagalog) titleTagalog else title
        val displayMessage = if (isTagalog) messageTagalog else message
        
        // Create notification
        showNotification(context, reminderId, displayTitle, displayMessage)
    }
    
    private fun showNotification(
        context: Context,
        reminderId: String,
        title: String,
        message: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Create notification channel (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "reminder_channel",
                "Plant Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for plant care"
                enableVibration(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null
                )
            }
            notificationManager.createNotificationChannel(channel)
        }
        
        // Create intent to open app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Build notification
        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.ic_notification) // Create this icon
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .build()
        
        notificationManager.notify(reminderId.hashCode(), notification)
    }
}

