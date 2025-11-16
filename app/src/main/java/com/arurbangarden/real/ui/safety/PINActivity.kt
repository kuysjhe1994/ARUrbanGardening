package com.arurbangarden.real.ui.safety

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.arurbangarden.real.R
import com.arurbangarden.real.databinding.ActivityPinBinding
import com.arurbangarden.real.ui.settings.SettingsActivity
import java.security.MessageDigest

class PINActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPinBinding
    private var isSettingPIN = false
    private var targetActivity: Class<*>? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        isSettingPIN = intent.getBooleanExtra("setting_pin", false)
        targetActivity = intent.getSerializableExtra("target_activity") as? Class<*>
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.editPin.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        
        if (isSettingPIN) {
            binding.textTitle.text = "Set PIN"
            binding.textSubtitle.text = "Create a 4-digit PIN to protect settings"
            binding.btnSubmit.text = "Set PIN"
            binding.btnSubmit.setOnClickListener {
                setPIN()
            }
        } else {
            binding.textTitle.text = "Enter PIN"
            binding.textSubtitle.text = "Enter your PIN to access settings"
            binding.btnSubmit.text = "Enter"
            binding.btnSubmit.setOnClickListener {
                verifyPIN()
            }
        }
        
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
    
    private fun setPIN() {
        val pin = binding.editPin.text.toString()
        
        if (pin.length != 4) {
            Toast.makeText(this, "PIN must be 4 digits", Toast.LENGTH_SHORT).show()
            return
        }
        
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val hashedPIN = hashPIN(pin)
        prefs.edit().putString("adult_pin", hashedPIN).apply()
        
        Toast.makeText(this, "PIN set successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }
    
    private fun verifyPIN() {
        val pin = binding.editPin.text.toString()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val storedPIN = prefs.getString("adult_pin", null)
        
        if (storedPIN == null) {
            // No PIN set, allow access
            proceedToTarget()
            return
        }
        
        val hashedPIN = hashPIN(pin)
        if (hashedPIN == storedPIN) {
            proceedToTarget()
        } else {
            Toast.makeText(this, "Incorrect PIN", Toast.LENGTH_SHORT).show()
            binding.editPin.setText("")
        }
    }
    
    private fun proceedToTarget() {
        if (targetActivity != null) {
            startActivity(Intent(this, targetActivity))
        }
        finish()
    }
    
    private fun hashPIN(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(pin.toByteArray())
        return hash.joinToString("") { "%02x".format(it) }
    }
    
    companion object {
        fun requirePIN(context: Context, targetActivity: Class<*>): Boolean {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            val hasPIN = prefs.getString("adult_pin", null) != null
            
            if (hasPIN) {
                val intent = Intent(context, PINActivity::class.java).apply {
                    putExtra("target_activity", targetActivity)
                }
                context.startActivity(intent)
                return false
            }
            return true
        }
    }
}

