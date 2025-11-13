package com.arurbangarden.real.ml

import android.content.Context
import android.graphics.Bitmap
import com.arurbangarden.real.data.model.PestDetectionResult
import com.arurbangarden.real.data.model.Severity
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PestDetectionModel(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private val inputImageSize = 224
    
    init {
        try {
            loadModel()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadModel() {
        try {
            val modelBuffer = loadModelFile("models/pest_model.tflite")
            val options = Interpreter.Options().apply {
                setNumThreads(4)
                setUseNNAPI(true)
            }
            interpreter = Interpreter(modelBuffer, options)
        } catch (e: Exception) {
            interpreter = null
        }
    }
    
    private fun loadModelFile(filePath: String): MappedByteBuffer {
        val assetManager = context.assets
        val fileDescriptor = assetManager.openFd(filePath)
        val inputStream = FileInputStream(fileDescriptor.createInputStream())
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    
    /**
     * Detect pest or disease from close-up photo
     */
    fun detectPest(bitmap: Bitmap): PestDetectionResult {
        return if (interpreter != null) {
            detectWithTFLite(bitmap)
        } else {
            detectWithFallback(bitmap)
        }
    }
    
    private fun detectWithTFLite(bitmap: Bitmap): PestDetectionResult {
        // Similar to plant recognition but for pest/disease
        // Placeholder implementation
        return PestDetectionResult(
            issue = "Unknown",
            issueTagalog = "Hindi kilala",
            confidence = 0.0f,
            recommendedActions = listOf("Consult with adult"),
            recommendedActionsTagalog = listOf("Kumonsulta sa matanda"),
            severity = Severity.LOW,
            requiresAdultHelp = true
        )
    }
    
    private fun detectWithFallback(bitmap: Bitmap): PestDetectionResult {
        // Fallback: Return safe recommendation
        return PestDetectionResult(
            issue = "No issue detected",
            issueTagalog = "Walang natukoy na problema",
            confidence = 0.0f,
            recommendedActions = listOf(
                "Keep plant healthy",
                "Monitor regularly",
                "Ask adult if concerned"
            ),
            recommendedActionsTagalog = listOf(
                "Panatilihing malusog ang halaman",
                "Subaybayan nang regular",
                "Tanungin ang matanda kung may alala"
            ),
            severity = Severity.LOW,
            requiresAdultHelp = false
        )
    }
}

