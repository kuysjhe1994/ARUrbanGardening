package com.arurbangarden.real.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.arurbangarden.real.R
import com.arurbangarden.real.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}

