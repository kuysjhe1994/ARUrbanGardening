package com.arurbangarden.real.ui.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.arurbangarden.real.R
import com.arurbangarden.real.databinding.ActivityOnboardingBinding
import com.arurbangarden.real.ui.MainActivity
import com.arurbangarden.real.ui.safety.ParentalConsentActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityOnboardingBinding
    private var currentStep = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        showStep(0)
        
        binding.btnNext.setOnClickListener {
            if (currentStep < 2) {
                currentStep++
                showStep(currentStep)
            } else {
                requestPermissions()
            }
        }
        
        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }
    
    private fun showStep(step: Int) {
        when (step) {
            0 -> {
                binding.textTitle.text = getString(R.string.onboarding_welcome)
                binding.textSubtitle.text = getString(R.string.onboarding_welcome_subtitle)
            }
            1 -> {
                binding.textTitle.text = getString(R.string.onboarding_camera_permission)
                binding.textSubtitle.text = getString(R.string.onboarding_camera_permission)
            }
            2 -> {
                binding.textTitle.text = getString(R.string.onboarding_privacy)
                binding.textSubtitle.text = getString(R.string.onboarding_privacy_text)
            }
        }
        
        if (step == 2) {
            binding.btnNext.text = getString(R.string.onboarding_get_started)
        } else {
            binding.btnNext.text = getString(R.string.onboarding_next)
        }
    }
    
    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.CAMERA
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            permissions.add(Manifest.permission.BLUETOOTH)
            permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
        
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        
        Dexter.withContext(this)
            .withPermissions(permissions)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        showParentalConsentDialog()
                    } else {
                        // Show explanation
                        AlertDialog.Builder(this@OnboardingActivity)
                            .setTitle(R.string.permission_required)
                            .setMessage(R.string.permission_camera_required)
                            .setPositiveButton(R.string.ok, null)
                            .show()
                    }
                }
                
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }
    
    private fun showParentalConsentDialog() {
        // Show full parental consent activity
        startActivity(Intent(this, ParentalConsentActivity::class.java))
        finish()
    }
    
    private fun finishOnboarding() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.edit().putBoolean("onboarding_completed", true).apply()
        
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

