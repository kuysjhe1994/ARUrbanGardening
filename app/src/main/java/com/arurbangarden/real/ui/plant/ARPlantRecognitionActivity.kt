package com.arurbangarden.real.ui.plant

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import java.util.Locale
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.arurbangarden.real.R
import com.arurbangarden.real.ar.ARCoreManager
import com.arurbangarden.real.databinding.ActivityArPlantRecognitionBinding
import com.arurbangarden.real.ml.PlantRecognitionModel
import com.arurbangarden.real.ui.plant.care.ARCareGuideActivity
import com.arurbangarden.real.util.ARCoreImageUtils
import com.google.ar.core.*
import com.google.ar.core.exceptions.CameraNotAvailableException
import android.view.Surface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

/**
 * AR Plant Recognition Activity - Real-time plant detection using ARCore camera
 * Features:
 * - Real-time detection from ARCore camera frames
 * - On-device ML model (TensorFlow Lite)
 * - Kid-friendly UI with large buttons
 * - Voice guidance option
 * - Fallback "Try again" animation
 * - AR anchor to detected plant
 */
class ARPlantRecognitionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArPlantRecognitionBinding
    private lateinit var arCoreManager: ARCoreManager
    private lateinit var plantRecognitionModel: PlantRecognitionModel
    private lateinit var arSurfaceView: GLSurfaceView
    
    private var session: Session? = null
    private var isProcessingFrame = AtomicBoolean(false)
    private var detectedPlantAnchor: Anchor? = null
    private var lastDetectionTime = 0L
    private val detectionInterval = 1000L // Process every 1 second
    private var voiceEnabled = false
    private var mediaPlayer: MediaPlayer? = null
    private var textToSpeech: TextToSpeech? = null
    
    private val handler = Handler(Looper.getMainLooper())
    private var processingRunnable: Runnable? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArPlantRecognitionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        plantRecognitionModel = PlantRecognitionModel(this)
        
        if (!checkCameraPermission()) {
            requestCameraPermission()
            return
        }
        
        initializeAR()
        initializeVoice()
        setupUI()
    }
    
    private fun initializeVoice() {
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.ENGLISH) ?: TextToSpeech.LANG_MISSING_DATA
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    voiceEnabled = false
                    binding.btnVoiceToggle.visibility = View.GONE
                }
            } else {
                voiceEnabled = false
                binding.btnVoiceToggle.visibility = View.GONE
            }
        }
    }
    
    private fun initializeAR() {
        arCoreManager = ARCoreManager(this)
        
        if (!arCoreManager.initialize()) {
            showError(getString(R.string.ar_not_available))
            return
        }
        
        session = arCoreManager.getSession()
        
        if (session == null) {
            showError(getString(R.string.ar_session_failed))
            return
        }
        
        // Configure AR session for plane detection and camera
        session?.apply {
            setCameraConfig(CameraConfig(session))
            configure(
                Config(session).apply {
                    planeFindingMode = Config.PlaneFindingMode.HORIZONTAL_AND_VERTICAL
                    lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                }
            )
        }
        
        setupARSurfaceView()
        startCameraFrameProcessing()
    }
    
    private fun setupARSurfaceView() {
        arSurfaceView = binding.arSurfaceView
        arSurfaceView.setPreserveEGLContextOnPause(true)
        arSurfaceView.setEGLContextClientVersion(2)
        arSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        
        // Render AR camera preview
        arSurfaceView.holder.addCallback(object : android.view.SurfaceHolder.Callback {
            override fun surfaceCreated(holder: android.view.SurfaceHolder) {
                // Surface created
            }
            
            override fun surfaceChanged(
                holder: android.view.SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                session?.setDisplayGeometry(Surface.ROTATION_0, width, height)
            }
            
            override fun surfaceDestroyed(holder: android.view.SurfaceHolder) {
                // Surface destroyed
            }
        })
    }
    
    private fun startCameraFrameProcessing() {
        processingRunnable = object : Runnable {
            override fun run() {
                if (!isProcessingFrame.get() && System.currentTimeMillis() - lastDetectionTime > detectionInterval) {
                    processCameraFrame()
                }
                handler.postDelayed(this, 500) // Check every 500ms
            }
        }
        handler.post(processingRunnable!!)
    }
    
    private fun processCameraFrame() {
        val session = this.session ?: return
        
        if (isProcessingFrame.get()) return
        
        try {
            val frame = session.update() ?: return
            
            if (!frame.hasDisplayGeometryChanged()) {
                // Use camera texture from ARCore
                val camera = frame.camera
                
                if (camera.trackingState == TrackingState.TRACKING) {
                    // Get camera image for ML processing
                    val image = frame.acquireCameraImage()
                    
                    lifecycleScope.launch {
                        isProcessingFrame.set(true)
                        
                        try {
                            val bitmap = withContext(Dispatchers.Default) {
                                ARCoreImageUtils.convertImageToBitmap(image)
                            }
                            
                            if (bitmap != null) {
                                val result = withContext(Dispatchers.Default) {
                                    plantRecognitionModel.recognizePlant(bitmap)
                                }
                                
                                withContext(Dispatchers.Main) {
                                    handleRecognitionResult(result, frame)
                                }
                                
                                // Recycle bitmap to free memory
                                bitmap.recycle()
                            }
                            
                            image.close()
                        } catch (e: Exception) {
                            image.close()
                            e.printStackTrace()
                        } finally {
                            isProcessingFrame.set(false)
                            lastDetectionTime = System.currentTimeMillis()
                        }
                    }
                }
            }
        } catch (e: CameraNotAvailableException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    
    private fun handleRecognitionResult(result: com.arurbangarden.real.data.model.PlantRecognitionResult, frame: Frame) {
        val plant = result.plant
        val confidence = result.confidence
        
        if (plant != null && confidence >= 0.8f) {
            // High confidence detection
            showPlantDetected(plant, confidence)
            
            // Create anchor at detected location
            if (detectedPlantAnchor == null) {
                createPlantAnchor(frame)
            }
            
            // Speak plant name if voice enabled
            if (voiceEnabled) {
                speakText(getString(R.string.plant_detected, plant.name))
            }
        } else if (confidence < 0.5f) {
            // Low confidence - show try again
            showTryAgainAnimation()
        } else {
            // Medium confidence - show detecting
            showDetecting()
        }
    }
    
    private fun showPlantDetected(plant: com.arurbangarden.real.data.model.Plant, confidence: Float) {
        binding.apply {
            // Hide detecting UI
            layoutDetecting.visibility = View.GONE
            layoutTryAgain.visibility = View.GONE
            
            // Show result
            layoutResult.visibility = View.VISIBLE
            textPlantName.text = plant.name
            textPlantNameTagalog.text = plant.nameTagalog
            textConfidence.text = getString(R.string.confidence_percent, (confidence * 100).toInt())
            
            // Show kid-friendly facts
            showPlantFacts(plant)
            
            // Show AR Care Guide button
            btnShowCareGuide.visibility = View.VISIBLE
            btnShowCareGuide.startAnimation(
                AnimationUtils.loadAnimation(this@ARPlantRecognitionActivity, R.anim.bounce)
            )
            
            // Play success sound (if available)
            // playSound(R.raw.success_sound)
        }
    }
    
    private fun showPlantFacts(plant: com.arurbangarden.real.data.model.Plant) {
        val facts = mutableListOf<String>()
        
        // Add sunlight fact
        val sunlightText = when (plant.sunlightNeeds) {
            com.arurbangarden.real.data.model.SunlightNeeds.FULL_SUN -> 
                getString(R.string.fact_sunlight_full)
            com.arurbangarden.real.data.model.SunlightNeeds.PARTIAL_SUN -> 
                getString(R.string.fact_sunlight_partial)
            com.arurbangarden.real.data.model.SunlightNeeds.SHADE -> 
                getString(R.string.fact_sunlight_shade)
        }
        facts.add(sunlightText)
        
        // Add water fact
        val waterText = when (plant.waterFrequency) {
            com.arurbangarden.real.data.model.WaterFrequency.DAILY -> 
                getString(R.string.fact_water_daily)
            com.arurbangarden.real.data.model.WaterFrequency.EVERY_2_DAYS -> 
                getString(R.string.fact_water_2days)
            com.arurbangarden.real.data.model.WaterFrequency.WEEKLY -> 
                getString(R.string.fact_water_weekly)
            com.arurbangarden.real.data.model.WaterFrequency.BIWEEKLY -> 
                getString(R.string.fact_water_biweekly)
        }
        facts.add(waterText)
        
        // Add soil fact (simplified)
        facts.add(getString(R.string.fact_soil_general))
        
        // Display facts
        binding.apply {
            if (facts.isNotEmpty()) {
                textFact1.text = facts[0]
                textFact1.visibility = View.VISIBLE
            }
            if (facts.size > 1) {
                textFact2.text = facts[1]
                textFact2.visibility = View.VISIBLE
            }
            if (facts.size > 2) {
                textFact3.text = facts[2]
                textFact3.visibility = View.VISIBLE
            }
        }
    }
    
    private fun showTryAgainAnimation() {
        binding.apply {
            layoutDetecting.visibility = View.GONE
            layoutResult.visibility = View.GONE
            
            layoutTryAgain.visibility = View.VISIBLE
            imageTryAgain.startAnimation(
                AnimationUtils.loadAnimation(this@ARPlantRecognitionActivity, R.anim.shake)
            )
        }
        
        if (voiceEnabled) {
            speakText(getString(R.string.try_again_message))
        }
    }
    
    private fun showDetecting() {
        binding.apply {
            layoutTryAgain.visibility = View.GONE
            layoutResult.visibility = View.GONE
            
            layoutDetecting.visibility = View.VISIBLE
            progressDetecting.startAnimation(
                AnimationUtils.loadAnimation(this@ARPlantRecognitionActivity, R.anim.rotate)
            )
        }
    }
    
    private fun createPlantAnchor(frame: Frame) {
        // Create anchor at center of screen where plant is detected
        val hitResults = frame.hitTest(frame.width / 2f, frame.height / 2f)
        
        hitResults.firstOrNull()?.let { hitResult ->
            detectedPlantAnchor = arCoreManager.createAnchor(hitResult)
        }
    }
    
    private fun setupUI() {
        binding.apply {
            // Voice toggle
            btnVoiceToggle.setOnClickListener {
                voiceEnabled = !voiceEnabled
                btnVoiceToggle.setImageResource(
                    if (voiceEnabled) R.drawable.ic_volume_on else R.drawable.ic_volume_off
                )
                if (voiceEnabled) {
                    speakText(getString(R.string.voice_enabled))
                }
            }
            
            // Show Care Guide
            btnShowCareGuide.setOnClickListener {
                val plantName = textPlantName.text.toString()
                if (plantName.isNotEmpty()) {
                    startActivity(
                        android.content.Intent(
                            this@ARPlantRecognitionActivity,
                            ARCareGuideActivity::class.java
                        ).apply {
                            putExtra("plant_name", plantName)
                            putExtra("anchor_id", detectedPlantAnchor?.trackable?.trackingId)
                        }
                    )
                }
            }
            
            // Back button
            btnBack.setOnClickListener {
                finish()
            }
            
            // Retry button (try again)
            btnRetry.setOnClickListener {
                detectedPlantAnchor?.detach()
                detectedPlantAnchor = null
                showDetecting()
            }
        }
    }
    
    private fun speakText(text: String) {
        if (!voiceEnabled || textToSpeech == null) return
        
        try {
            textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun playSound(resourceId: Int) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, resourceId)
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun onResume() {
        super.onResume()
        arCoreManager.resume()
        arSurfaceView.onResume()
    }
    
    override fun onPause() {
        super.onPause()
        arCoreManager.pause()
        arSurfaceView.onPause()
        processingRunnable?.let { handler.removeCallbacks(it) }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        detectedPlantAnchor?.detach()
        arCoreManager.release()
        mediaPlayer?.release()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        processingRunnable?.let { handler.removeCallbacks(it) }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Handle touch for AR interaction if needed
        return super.onTouchEvent(event)
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
                showError(getString(R.string.camera_permission_required))
                finish()
            }
        }
    }
    
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
    }
}

