package com.arurbangarden.real.ml

import android.content.Context
import android.graphics.Bitmap
import com.arurbangarden.real.data.model.PestDetectionResult
import com.arurbangarden.real.data.model.Severity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlinx.coroutines.tasks.await

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
     * Synchronous version - use detectPestAsync for ML Kit fallback
     */
    fun detectPest(bitmap: Bitmap): PestDetectionResult {
        return if (interpreter != null) {
            detectWithTFLite(bitmap)
        } else {
            // For sync version without model, return safe recommendation
            PestDetectionResult(
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
    
    private fun detectWithTFLite(bitmap: Bitmap): PestDetectionResult {
        // Process with TensorFlow Lite model
        // Model output format: [num_classes] float array
        // For now, if model exists, process it
        // In production, implement actual TFLite inference
        
        // This is a placeholder - actual implementation depends on model architecture
        // For safety, always recommend consulting with adult if model detects issues
        return PestDetectionResult(
            issue = "Analysis in progress",
            issueTagalog = "Sinusuri pa",
            confidence = 0.5f,
            recommendedActions = listOf(
                "Wait for analysis to complete",
                "Consult with adult if concerned"
            ),
            recommendedActionsTagalog = listOf(
                "Maghintay na matapos ang pagsusuri",
                "Kumonsulta sa matanda kung may alala"
            ),
            severity = Severity.LOW,
            requiresAdultHelp = false
        )
    }
    
    private suspend fun detectWithFallback(bitmap: Bitmap): PestDetectionResult {
        // Use ML Kit Image Labeling as real fallback
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            
            val labels = labeler.process(image).await()
            
            // Check for pest/disease related keywords
            var hasIssue = false
            var issueKeywords = mutableListOf<String>()
            var maxConfidence = 0.0f
            
            labels.forEach { label ->
                val labelText = label.text.lowercase()
                val labelConfidence = label.confidence
                
                // Check for pest/disease indicators
                if (labelText.contains("pest") ||
                    labelText.contains("disease") ||
                    labelText.contains("fungus") ||
                    labelText.contains("mold") ||
                    labelText.contains("rot") ||
                    labelText.contains("wilt") ||
                    labelText.contains("yellow") ||
                    labelText.contains("brown") ||
                    labelText.contains("spot") ||
                    labelText.contains("damage") ||
                    labelText.contains("insect")) {
                    
                    hasIssue = true
                    issueKeywords.add(label.text)
                    if (labelConfidence > maxConfidence) {
                        maxConfidence = labelConfidence
                    }
                }
            }
            
            if (hasIssue) {
                // Issue detected - recommend adult help
                PestDetectionResult(
                    issue = issueKeywords.joinToString(", "),
                    issueTagalog = "Posibleng problema: ${issueKeywords.joinToString(", ")}",
                    confidence = maxConfidence,
                    recommendedActions = listOf(
                        "Show to adult immediately",
                        "Isolate plant if possible",
                        "Take clear photos for expert consultation"
                    ),
                    recommendedActionsTagalog = listOf(
                        "Ipakita agad sa matanda",
                        "Ihiwalay ang halaman kung maaari",
                        "Kumuha ng malinaw na larawan para sa konsultasyon"
                    ),
                    severity = if (maxConfidence > 0.7f) Severity.HIGH else Severity.MEDIUM,
                    requiresAdultHelp = true
                )
            } else {
                // No issue detected
                PestDetectionResult(
                    issue = "No issue detected",
                    issueTagalog = "Walang natukoy na problema",
                    confidence = 0.8f,
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
        } catch (e: Exception) {
            e.printStackTrace()
            // Safe fallback - always recommend adult consultation on error
            PestDetectionResult(
                issue = "Unable to analyze",
                issueTagalog = "Hindi ma-analyze",
                confidence = 0.0f,
                recommendedActions = listOf("Consult with adult"),
                recommendedActionsTagalog = listOf("Kumonsulta sa matanda"),
                severity = Severity.LOW,
                requiresAdultHelp = true
            )
        }
    }
    
    /**
     * Async version for ML Kit fallback
     */
    suspend fun detectPestAsync(bitmap: Bitmap): PestDetectionResult {
        return if (interpreter != null) {
            detectWithTFLite(bitmap)
        } else {
            detectWithFallback(bitmap)
        }
    }
}

