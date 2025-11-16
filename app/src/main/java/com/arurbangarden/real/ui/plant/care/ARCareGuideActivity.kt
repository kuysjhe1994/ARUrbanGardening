package com.arurbangarden.real.ui.plant.care

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
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
import com.arurbangarden.real.data.model.Plant
import com.arurbangarden.real.databinding.ActivityArCareGuideBinding
import com.arurbangarden.real.ml.PlantRecognitionModel
import com.arurbangarden.real.sensor.AmbientLightSensor
import com.arurbangarden.real.sensor.SoilMoistureSensor
import com.google.ar.core.*
import com.google.ar.core.exceptions.CameraNotAvailableException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * AR Care Guide Activity - Animated AR overlays for plant care instructions
 * Features:
 * - AR overlay icons: Water, Sunlight, Soil, Growth
 * - Animated, colorful, kid-friendly icons
 * - Tap to show step-by-step care instructions
 * - AR anchors to detected plant
 * - Real sensor data (ambient light + soil moisture)
 * - Voice narration for care steps
 * - "Good job!" badges after completing actions
 */
class ARCareGuideActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityArCareGuideBinding
    private lateinit var arCoreManager: ARCoreManager
    private lateinit var plantRecognitionModel: PlantRecognitionModel
    
    private var session: Session? = null
    private var plant: Plant? = null
    private var plantAnchor: Anchor? = null
    
    // AR Overlay components
    private var waterOverlay: AROverlayIcon? = null
    private var sunlightOverlay: AROverlayIcon? = null
    private var soilOverlay: AROverlayIcon? = null
    private var growthOverlay: AROverlayIcon? = null
    
    // Sensors
    private var ambientLightSensor: AmbientLightSensor? = null
    private var soilMoistureSensor: SoilMoistureSensor? = null
    
    // Voice
    private var textToSpeech: TextToSpeech? = null
    private var voiceEnabled = true
    private var mediaPlayer: MediaPlayer? = null
    
    // Completed actions tracking
    private val completedActions = mutableSetOf<String>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArCareGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        val plantName = intent.getStringExtra("plant_name") ?: ""
        
        plantRecognitionModel = PlantRecognitionModel(this)
        plant = plantRecognitionModel.getAllPlants().find { it.name == plantName || it.nameTagalog == plantName }
        
        if (plant == null) {
            showError(getString(R.string.plant_not_found))
            finish()
            return
        }
        
        if (!checkCameraPermission()) {
            requestCameraPermission()
            return
        }
        
        initializeAR()
        initializeSensors()
        initializeVoice()
        setupUI()
        createAROverlays()
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
    }
    
    private fun setupARSurfaceView() {
        binding.arSurfaceView.apply {
            setPreserveEGLContextOnPause(true)
            setEGLContextClientVersion(2)
            setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        }
    }
    
    private fun initializeSensors() {
        // Ambient light sensor
        ambientLightSensor = AmbientLightSensor(this) { lightLevel ->
            lifecycleScope.launch(Dispatchers.Main) {
                updateSunlightAdvice(lightLevel)
            }
        }
        ambientLightSensor?.start()
        
        // Soil moisture sensor (BLE, optional)
        soilMoistureSensor = SoilMoistureSensor(this) { moistureLevel ->
            lifecycleScope.launch(Dispatchers.Main) {
                updateWaterAdvice(moistureLevel)
            }
        }
        soilMoistureSensor?.connect()
    }
    
    private fun initializeVoice() {
        textToSpeech = TextToSpeech(this, this)
    }
    
    override fun onInit(status: Int) {
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
    
    private fun createAROverlays() {
        plant?.let { plant ->
            // Create overlay icons around the plant
            val overlayFactory = AROverlayIconFactory(this, plant)
            
            // Water overlay
            waterOverlay = overlayFactory.createWaterOverlay()
            waterOverlay?.setOnClickListener {
                showCareInstructions(CareAction.WATER, plant)
            }
            
            // Sunlight overlay
            sunlightOverlay = overlayFactory.createSunlightOverlay()
            sunlightOverlay?.setOnClickListener {
                showCareInstructions(CareAction.SUNLIGHT, plant)
            }
            
            // Soil overlay
            soilOverlay = overlayFactory.createSoilOverlay()
            soilOverlay?.setOnClickListener {
                showCareInstructions(CareAction.SOIL, plant)
            }
            
            // Growth overlay
            growthOverlay = overlayFactory.createGrowthOverlay()
            growthOverlay?.setOnClickListener {
                showCareInstructions(CareAction.GROWTH, plant)
            }
            
            // Add overlays to AR view
            binding.arOverlayContainer.addView(waterOverlay)
            binding.arOverlayContainer.addView(sunlightOverlay)
            binding.arOverlayContainer.addView(soilOverlay)
            binding.arOverlayContainer.addView(growthOverlay)
            
            // Animate overlays
            animateOverlays()
        }
    }
    
    private fun animateOverlays() {
        waterOverlay?.let {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce))
        }
        sunlightOverlay?.let {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse))
        }
        soilOverlay?.let {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce))
        }
        growthOverlay?.let {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.pulse))
        }
    }
    
    private fun showCareInstructions(action: CareAction, plant: Plant) {
        val steps = when (action) {
            CareAction.WATER -> getWaterSteps(plant)
            CareAction.SUNLIGHT -> getSunlightSteps(plant)
            CareAction.SOIL -> getSoilSteps(plant)
            CareAction.GROWTH -> getGrowthSteps(plant)
        }
        
        // Show instruction dialog
        showInstructionDialog(action, steps) { completed ->
            if (completed) {
                markActionCompleted(action)
                showGoodJobBadge(action)
                speakText(getString(R.string.good_job_message))
            }
        }
    }
    
    private fun getWaterSteps(plant: Plant): List<String> {
        return when (plant.waterFrequency) {
            com.arurbangarden.real.data.model.WaterFrequency.DAILY -> listOf(
                getString(R.string.water_step1_daily),
                getString(R.string.water_step2_check),
                getString(R.string.water_step3_pour)
            )
            com.arurbangarden.real.data.model.WaterFrequency.EVERY_2_DAYS -> listOf(
                getString(R.string.water_step1_check_soil),
                getString(R.string.water_step2_if_dry),
                getString(R.string.water_step3_water)
            )
            else -> listOf(
                getString(R.string.water_step1_check_soil),
                getString(R.string.water_step2_water_if_needed)
            )
        }
    }
    
    private fun getSunlightSteps(plant: Plant): List<String> {
        return when (plant.sunlightNeeds) {
            com.arurbangarden.real.data.model.SunlightNeeds.FULL_SUN -> listOf(
                getString(R.string.sunlight_step1_find_sunny),
                getString(R.string.sunlight_step2_place_plant),
                getString(R.string.sunlight_step3_check_daily)
            )
            com.arurbangarden.real.data.model.SunlightNeeds.PARTIAL_SUN -> listOf(
                getString(R.string.sunlight_step1_find_partial),
                getString(R.string.sunlight_step2_place_plant),
                getString(R.string.sunlight_step3_avoid_direct)
            )
            else -> listOf(
                getString(R.string.sunlight_step1_find_shade),
                getString(R.string.sunlight_step2_place_plant)
            )
        }
    }
    
    private fun getSoilSteps(plant: Plant): List<String> {
        return listOf(
            getString(R.string.soil_step1_check_moisture),
            getString(R.string.soil_step2_feel_soil),
            getString(R.string.soil_step3_adjust)
        )
    }
    
    private fun getGrowthSteps(plant: Plant): List<String> {
        return listOf(
            getString(R.string.growth_step1_measure_height),
            getString(R.string.growth_step2_take_photo),
            getString(R.string.growth_step3_track_progress)
        )
    }
    
    private fun showInstructionDialog(action: CareAction, steps: List<String>, onComplete: (Boolean) -> Unit) {
        // Create and show instruction dialog (Activity version)
        val dialog = CareInstructionDialogActivity(this, action, steps) { completed ->
            onComplete(completed)
        }
        dialog.show()
        
        // Narrate steps
        if (voiceEnabled) {
            steps.forEachIndexed { index, step ->
                handler.postDelayed({
                    speakText(step)
                }, (index * 2000).toLong())
            }
        }
    }
    
    private fun markActionCompleted(action: CareAction) {
        completedActions.add(action.name)
        
        // Update overlay to show completion
        when (action) {
            CareAction.WATER -> {
                waterOverlay?.setCompleted(true)
                playSound(R.raw.completion_sound)
            }
            CareAction.SUNLIGHT -> {
                sunlightOverlay?.setCompleted(true)
                playSound(R.raw.completion_sound)
            }
            CareAction.SOIL -> {
                soilOverlay?.setCompleted(true)
                playSound(R.raw.completion_sound)
            }
            CareAction.GROWTH -> {
                growthOverlay?.setCompleted(true)
                playSound(R.raw.completion_sound)
            }
        }
    }
    
    private fun showGoodJobBadge(action: CareAction) {
        // Show "Good job!" badge animation
        val badge = GoodJobBadge(this, action)
        binding.root.addView(badge)
        
        badge.startAnimation(AnimationUtils.loadAnimation(this, R.anim.badge_appear))
        
        // Remove badge after animation
        handler.postDelayed({
            binding.root.removeView(badge)
        }, 3000)
    }
    
    private fun updateSunlightAdvice(lightLevel: Float) {
        plant?.let { plant ->
            val advice = when {
                plant.sunlightNeeds == com.arurbangarden.real.data.model.SunlightNeeds.FULL_SUN && lightLevel < 500 -> {
                    getString(R.string.advice_sunlight_too_dark)
                }
                plant.sunlightNeeds == com.arurbangarden.real.data.model.SunlightNeeds.SHADE && lightLevel > 1000 -> {
                    getString(R.string.advice_sunlight_too_bright)
                }
                else -> {
                    getString(R.string.advice_sunlight_good)
                }
            }
            
            sunlightOverlay?.updateAdvice(advice)
            
            if (voiceEnabled) {
                speakText(advice)
            }
        }
    }
    
    private fun updateWaterAdvice(moistureLevel: Float?) {
        if (moistureLevel == null) return
        
        plant?.let { plant ->
            val advice = when (plant.waterFrequency) {
                com.arurbangarden.real.data.model.WaterFrequency.DAILY -> {
                    if (moistureLevel < 30) {
                        getString(R.string.advice_water_needed)
                    } else {
                        getString(R.string.advice_water_good)
                    }
                }
                com.arurbangarden.real.data.model.WaterFrequency.EVERY_2_DAYS -> {
                    if (moistureLevel < 20) {
                        getString(R.string.advice_water_needed)
                    } else {
                        getString(R.string.advice_water_good)
                    }
                }
                else -> {
                    if (moistureLevel < 15) {
                        getString(R.string.advice_water_needed)
                    } else {
                        getString(R.string.advice_water_good)
                    }
                }
            }
            
            waterOverlay?.updateAdvice(advice)
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
            
            // Back button
            btnBack.setOnClickListener {
                finish()
            }
            
            // Plant name
            plant?.let {
                textPlantName.text = it.name
                textPlantNameTagalog.text = it.nameTagalog
            }
        }
    }
    
    private fun speakText(text: String) {
        if (!voiceEnabled || textToSpeech == null) return
        
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
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
        binding.arSurfaceView.onResume()
        ambientLightSensor?.start()
        soilMoistureSensor?.connect()
    }
    
    override fun onPause() {
        super.onPause()
        arCoreManager.pause()
        binding.arSurfaceView.onPause()
        ambientLightSensor?.stop()
        soilMoistureSensor?.disconnect()
        textToSpeech?.stop()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        plantAnchor?.detach()
        arCoreManager.release()
        ambientLightSensor?.stop()
        soilMoistureSensor?.disconnect()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        mediaPlayer?.release()
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
    
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    
    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 100
    }
}

enum class CareAction {
    WATER,
    SUNLIGHT,
    SOIL,
    GROWTH
}

