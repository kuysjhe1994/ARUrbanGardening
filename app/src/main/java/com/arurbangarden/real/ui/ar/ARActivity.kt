package com.arurbangarden.real.ui.ar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arurbangarden.real.ARUrbanGardenApplication
import com.arurbangarden.real.R
import com.arurbangarden.real.ar.ARCoreManager
import com.arurbangarden.real.ar.ARMeasurement
import com.arurbangarden.real.databinding.ActivityArBinding
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Frame
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Trackable
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException

class ARActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityArBinding
    private lateinit var arCoreManager: ARCoreManager
    private lateinit var arMeasurement: ARMeasurement
    private var mode: String = "measurement" // "placement" or "measurement"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        mode = intent.getStringExtra("mode") ?: "measurement"
        
        if (!checkCameraPermission()) {
            requestCameraPermission()
            return
        }
        
        initializeAR()
    }
    
    private fun initializeAR() {
        arCoreManager = ARCoreManager(this)
        
        if (!arCoreManager.initialize()) {
            Toast.makeText(this, "ARCore not available", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        
        arMeasurement = ARMeasurement(arCoreManager)
        setupUI()
    }
    
    private fun setupUI() {
        binding.btnReset.setOnClickListener {
            arMeasurement.reset()
            updateMeasurementDisplay()
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        when (mode) {
            "placement" -> {
                binding.textInstruction.text = getString(R.string.ar_placement_instruction)
            }
            "measurement" -> {
                binding.textInstruction.text = getString(R.string.measurement_instruction)
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        arCoreManager.resume()
    }
    
    override fun onPause() {
        super.onPause()
        arCoreManager.pause()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        arCoreManager.release()
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val session = arCoreManager.getSession() ?: return false
            val frame = session.update() ?: return false
            
            val hitResults = arCoreManager.hitTest(frame, event.x, event.y)
            
            if (hitResults.isNotEmpty()) {
                val hitResult = hitResults.first()
                when (mode) {
                    "measurement" -> {
                        if (arMeasurement.addMeasurementPoint(frame, hitResult)) {
                            updateMeasurementDisplay()
                        }
                    }
                    "placement" -> {
                        // Handle placement mode
                        handlePlacement(hitResult)
                    }
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }
    
    private fun handlePlacement(hitResult: HitResult) {
        // Create virtual pot/bed at hit location
        val anchor = arCoreManager.createAnchor(hitResult)
        // In production, render 3D model at anchor position
        Toast.makeText(this, "Placed at location", Toast.LENGTH_SHORT).show()
    }
    
    private fun updateMeasurementDisplay() {
        when {
            arMeasurement.hasTwoPoints() -> {
                val distance = arMeasurement.getDistance()
                val height = arMeasurement.getHeight()
                
                if (distance != null) {
                    binding.textMeasurement.text = getString(R.string.measurement_result, distance)
                }
                if (height != null) {
                    binding.textMeasurement.append("\n${getString(R.string.measurement_height, height)}")
                }
                binding.textMeasurement.visibility = View.VISIBLE
            }
            arMeasurement.hasOnePoint() -> {
                binding.textMeasurement.text = getString(R.string.measurement_instruction)
                binding.textMeasurement.visibility = View.VISIBLE
            }
            else -> {
                binding.textMeasurement.visibility = View.GONE
            }
        }
    }
    
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_PERMISSION
        )
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeAR()
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
    
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
    }
}

