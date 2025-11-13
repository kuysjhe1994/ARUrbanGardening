package com.arurbangarden.real.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.arurbangarden.real.databinding.ActivityMainBinding
import com.arurbangarden.real.ui.ar.ARActivity
import com.arurbangarden.real.ui.onboarding.OnboardingActivity
import com.arurbangarden.real.ui.plant.PlantRecognitionActivity
import com.arurbangarden.real.ui.settings.SettingsActivity
import com.arurbangarden.real.ui.tracking.GrowthTrackingActivity

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        checkFirstLaunch()
        setupUI()
    }
    
    private fun checkFirstLaunch() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val hasCompletedOnboarding = prefs.getBoolean("onboarding_completed", false)
        
        if (!hasCompletedOnboarding) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
    }
    
    private fun setupUI() {
        binding.btnIdentifyPlant.setOnClickListener {
            startActivity(Intent(this, PlantRecognitionActivity::class.java))
        }
        
        binding.btnPlaceGarden.setOnClickListener {
            startActivity(Intent(this, ARActivity::class.java).apply {
                putExtra("mode", "placement")
            })
        }
        
        binding.btnMeasure.setOnClickListener {
            startActivity(Intent(this, ARActivity::class.java).apply {
                putExtra("mode", "measurement")
            })
        }
        
        binding.btnTrackGrowth.setOnClickListener {
            startActivity(Intent(this, GrowthTrackingActivity::class.java))
        }
        
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}

