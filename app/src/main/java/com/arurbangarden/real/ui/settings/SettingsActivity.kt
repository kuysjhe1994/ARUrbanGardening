package com.arurbangarden.real.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.arurbangarden.real.R
import com.arurbangarden.real.databinding.ActivitySettingsBinding
import com.arurbangarden.real.ui.safety.PINActivity
import com.arurbangarden.real.ui.safety.SafeUseTipsActivity
import com.arurbangarden.real.data.reminder.ReminderManager

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check PIN if required
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val hasPIN = prefs.getString("adult_pin", null) != null
        
        if (hasPIN) {
            // Require PIN to access settings
            val intent = Intent(this, PINActivity::class.java).apply {
                putExtra("target_activity", SettingsActivity::class.java)
            }
            startActivity(intent)
            finish()
            return
        }
        
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        
        // Language toggle
        val currentLanguage = prefs.getString("language", "tagalog") ?: "tagalog"
        binding.switchLanguage.isChecked = currentLanguage == "english"
        binding.switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putString("language", if (isChecked) "english" else "tagalog").apply()
        }
        
        // Voice guidance
        binding.switchVoice.isChecked = prefs.getBoolean("voice_guidance", true)
        binding.switchVoice.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("voice_guidance", isChecked).apply()
        }
        
        // High contrast
        binding.switchContrast.isChecked = prefs.getBoolean("high_contrast", false)
        binding.switchContrast.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("high_contrast", isChecked).apply()
        }
        
        // Cloud sync (requires parental consent)
        val hasConsent = prefs.getBoolean("parental_consent", false)
        binding.switchCloud.isEnabled = hasConsent
        binding.switchCloud.isChecked = hasConsent && prefs.getBoolean("cloud_sync", false)
        binding.switchCloud.setOnCheckedChangeListener { _, isChecked ->
            if (hasConsent) {
                prefs.edit().putBoolean("cloud_sync", isChecked).apply()
            }
        }
        
        // Missions toggle (for teachers/parents)
        binding.switchMissions.isChecked = prefs.getBoolean("missions_enabled", true)
        binding.switchMissions.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("missions_enabled", isChecked).apply()
        }
        
        // Reminders toggle
        val reminderManager = ReminderManager(this)
        binding.switchReminders.isChecked = reminderManager.areRemindersEnabled()
        binding.switchReminders.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("reminders_enabled", isChecked).apply()
            if (isChecked) {
                // Schedule reminders
                reminderManager.createDefaultReminders().forEach { reminder ->
                    reminderManager.scheduleReminder(reminder)
                }
            } else {
                // Cancel all reminders
                reminderManager.createDefaultReminders().forEach { reminder ->
                    reminderManager.cancelReminder(reminder)
                }
            }
        }
        
        // Set PIN button
        binding.btnSetPIN.setOnClickListener {
            startActivity(Intent(this, PINActivity::class.java).apply {
                putExtra("setting_pin", true)
            })
        }
        
        // Safe Use Tips button
        binding.btnSafeTips.setOnClickListener {
            startActivity(Intent(this, SafeUseTipsActivity::class.java))
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}

