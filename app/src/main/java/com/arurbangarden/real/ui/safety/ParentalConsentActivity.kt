package com.arurbangarden.real.ui.safety

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.arurbangarden.real.R
import com.arurbangarden.real.databinding.ActivityParentalConsentBinding
import com.arurbangarden.real.ui.MainActivity

class ParentalConsentActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityParentalConsentBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentalConsentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.btnAgree.setOnClickListener {
            saveConsent(true)
        }
        
        binding.btnDisagree.setOnClickListener {
            saveConsent(false)
        }
        
        binding.btnReadTips.setOnClickListener {
            startActivity(Intent(this, SafeUseTipsActivity::class.java))
        }
    }
    
    private fun saveConsent(agreed: Boolean) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putBoolean("parental_consent", agreed).apply()
        prefs.edit().putBoolean("parental_consent_shown", true).apply()
        
        if (agreed) {
            Toast.makeText(this, "Consent saved. Thank you!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Some features may be limited.", Toast.LENGTH_SHORT).show()
        }
        
        // Go to main activity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

