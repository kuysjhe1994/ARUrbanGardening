package com.arurbangarden.real.ui.plant

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.arurbangarden.real.R
import com.arurbangarden.real.databinding.ActivityPlantRecognitionBinding
import com.arurbangarden.real.ml.PlantRecognitionModel
import com.arurbangarden.real.ui.ar.ARActivity
import com.arurbangarden.real.util.ImageUtils
import kotlinx.coroutines.launch

class PlantRecognitionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityPlantRecognitionBinding
    private lateinit var plantRecognitionModel: PlantRecognitionModel
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantRecognitionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        plantRecognitionModel = PlantRecognitionModel(this)
        
        if (!checkCameraPermission()) {
            requestCameraPermission()
            return
        }
        
        setupCamera()
        setupUI()
    }
    
    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }
    
    private fun bindCameraUseCases() {
        val cameraProvider = cameraProvider ?: return
        
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.previewView.surfaceProvider)
        }
        
        imageCapture = ImageCapture.Builder().build()
        
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(ContextCompat.getMainExecutor(this)) { imageProxy ->
                    analyzeImage(imageProxy)
                }
            }
        
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun analyzeImage(imageProxy: ImageProxy) {
        // Convert ImageProxy to Bitmap for ML model
        val bitmap = try {
            ImageUtils.toBitmap(imageProxy)
        } catch (e: Exception) {
            imageProxy.close()
            return
        }
        
        lifecycleScope.launch {
            val result = plantRecognitionModel.recognizePlant(bitmap)
            
            if (result.plant != null && result.confidence > 0.8f) {
                runOnUiThread {
                    showRecognitionResult(result.plant!!.name, result.confidence)
                }
            }
        }
        
        imageProxy.close()
    }
    
    private fun showRecognitionResult(plantName: String, confidence: Float) {
        binding.textResult.text = getString(R.string.plant_recognition_found, plantName)
        binding.textConfidence.text = getString(R.string.plant_recognition_confidence, (confidence * 100).toInt())
        binding.layoutResult.visibility = View.VISIBLE
        
        // Show AR overlay button
        binding.btnShowAR.visibility = View.VISIBLE
    }
    
    private fun setupUI() {
        binding.btnCapture.setOnClickListener {
            captureImage()
        }
        
        binding.btnShowAR.setOnClickListener {
            // Launch AR view with recognized plant
            startActivity(android.content.Intent(this, ARActivity::class.java).apply {
                putExtra("mode", "care")
                putExtra("plant_name", binding.textResult.text.toString())
            })
        }
        
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    
    private fun captureImage() {
        val imageCapture = imageCapture ?: return
        
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = ImageUtils.toBitmap(image)
                    analyzeImage(image)
                    super.onCaptureSuccess(image)
                }
                
                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@PlantRecognitionActivity,
                        "Error capturing image",
                        Toast.LENGTH_SHORT
                    ).show()
                    super.onError(exception)
                }
            }
        )
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
                setupCamera()
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

