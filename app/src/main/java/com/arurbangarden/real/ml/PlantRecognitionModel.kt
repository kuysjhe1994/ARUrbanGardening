package com.arurbangarden.real.ml

import android.content.Context
import android.graphics.Bitmap
import com.arurbangarden.real.data.model.Plant
import com.arurbangarden.real.data.model.PlantRecognitionResult
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.max
import kotlinx.coroutines.tasks.await

class PlantRecognitionModel(private val context: Context) {
    
    private var interpreter: Interpreter? = null
    private val imageProcessor: ImageProcessor
    private val inputImageSize = 224  // Standard input size for mobile models
    
    // Plant database - in production, load from JSON/database
    private val plantDatabase = createPlantDatabase()
    
    init {
        imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(inputImageSize, inputImageSize, ResizeOp.ResizeMethod.BILINEAR))
            .build()
        
        try {
            loadModel()
        } catch (e: Exception) {
            // Model loading failed - will use fallback
            e.printStackTrace()
        }
    }
    
    private fun loadModel() {
        // In production, load from assets/models/plant_model.tflite
        // For now, we'll create a placeholder structure
        // The actual model file should be placed in app/src/main/assets/models/
        try {
            val modelBuffer = loadModelFile("models/plant_model.tflite")
            val options = Interpreter.Options().apply {
                setNumThreads(4)
                setUseNNAPI(true)  // Use Neural Networks API if available
            }
            interpreter = Interpreter(modelBuffer, options)
        } catch (e: Exception) {
            // Model file not found - use fallback recognition
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
     * Recognize plant from bitmap image
     * Returns PlantRecognitionResult with plant info and confidence
     * Uses coroutine for async ML Kit fallback
     */
    suspend fun recognizePlant(bitmap: Bitmap): PlantRecognitionResult {
        return if (interpreter != null) {
            recognizeWithTFLite(bitmap)
        } else {
            // Fallback: Use ML Kit Image Labeling
            recognizeWithFallback(bitmap)
        }
    }
    
    /**
     * Synchronous version for backward compatibility
     * Note: This will block the thread if using ML Kit fallback
     */
    fun recognizePlantSync(bitmap: Bitmap): PlantRecognitionResult {
        return if (interpreter != null) {
            recognizeWithTFLite(bitmap)
        } else {
            // For sync version, return null if no TFLite model
            // Caller should use async version instead
            PlantRecognitionResult(
                plant = null,
                confidence = 0.0f
            )
        }
    }
    
    private fun recognizeWithTFLite(bitmap: Bitmap): PlantRecognitionResult {
        val tensorImage = TensorImage.fromBitmap(bitmap)
        val processedImage = imageProcessor.process(tensorImage)
        
        // Model output: [num_classes] float array
        val outputBuffer = Array(1) { FloatArray(plantDatabase.size) }
        val inputBuffer = processedImage.buffer
        
        interpreter?.run(inputBuffer, outputBuffer)
        
        val predictions = outputBuffer[0]
        val maxIndex = predictions.indices.maxByOrNull { predictions[it] } ?: 0
        val confidence = predictions[maxIndex]
        
        val plant = if (confidence > 0.5f && maxIndex < plantDatabase.size) {
            plantDatabase[maxIndex]
        } else {
            null
        }
        
        return PlantRecognitionResult(
            plant = plant,
            confidence = confidence
        )
    }
    
    private suspend fun recognizeWithFallback(bitmap: Bitmap): PlantRecognitionResult {
        // Use ML Kit Image Labeling as real fallback
        return try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            
            val labels = labeler.process(image).await()
            
            // Try to match labels with plant database
            var bestMatch: Plant? = null
            var bestConfidence = 0.0f
            
            labels.forEach { label ->
                val labelText = label.text.lowercase()
                val labelConfidence = label.confidence
                
                // Search plant database for matches
                plantDatabase.forEach { plant ->
                    val plantName = plant.name.lowercase()
                    val plantNameTagalog = plant.nameTagalog.lowercase()
                    val scientificName = plant.scientificName.lowercase()
                    
                    // Check if label matches any plant name
                    if (labelText.contains(plantName) || 
                        labelText.contains(plantNameTagalog) ||
                        labelText.contains(scientificName) ||
                        plantName.contains(labelText) ||
                        plantNameTagalog.contains(labelText)) {
                        
                        if (labelConfidence > bestConfidence) {
                            bestMatch = plant
                            bestConfidence = labelConfidence
                        }
                    }
                }
                
                // Also check for generic plant-related keywords
                if (labelText.contains("plant") || 
                    labelText.contains("herb") || 
                    labelText.contains("vegetable") ||
                    labelText.contains("leaf") ||
                    labelText.contains("foliage")) {
                    // If we have a generic match but no specific plant, use first plant with lower confidence
                    if (bestMatch == null && plantDatabase.isNotEmpty()) {
                        bestMatch = plantDatabase.first()
                        bestConfidence = labelConfidence * 0.5f // Lower confidence for generic match
                    }
                }
            }
            
            PlantRecognitionResult(
                plant = bestMatch,
                confidence = bestConfidence
            )
        } catch (e: Exception) {
            e.printStackTrace()
            PlantRecognitionResult(
                plant = null,
                confidence = 0.0f
            )
        }
    }
    
    private fun createPlantDatabase(): List<Plant> {
        // Use expanded plant database
        return com.arurbangarden.real.data.database.PlantDatabase.getAllPlants()
    }
    
    // Legacy method - kept for reference
    private fun createPlantDatabaseLegacy(): List<Plant> {
        // Common urban plants in the Philippines
        return listOf(
            Plant(
                id = "basil",
                name = "Basil",
                nameTagalog = "Bawang",
                scientificName = "Ocimum basilicum",
                waterFrequency = com.arurbangarden.real.data.model.WaterFrequency.DAILY,
                sunlightNeeds = com.arurbangarden.real.data.model.SunlightNeeds.FULL_SUN,
                careTips = listOf(
                    "Water daily, keep soil moist",
                    "Needs 6-8 hours of sunlight",
                    "Harvest leaves regularly"
                ),
                careTipsTagalog = listOf(
                    "Diligan araw-araw, panatilihing basa ang lupa",
                    "Kailangan ng 6-8 oras na sikat ng araw",
                    "Anihin ang dahon nang regular"
                )
            ),
            Plant(
                id = "mint",
                name = "Mint",
                nameTagalog = "Mentha",
                scientificName = "Mentha",
                waterFrequency = com.arurbangarden.real.data.model.WaterFrequency.DAILY,
                sunlightNeeds = com.arurbangarden.real.data.model.SunlightNeeds.PARTIAL_SUN,
                careTips = listOf(
                    "Water frequently, likes moist soil",
                    "Partial shade is fine",
                    "Can spread quickly"
                ),
                careTipsTagalog = listOf(
                    "Diligan nang madalas, gusto ng basang lupa",
                    "Bahagyang lilim ay okay",
                    "Mabilis kumalat"
                )
            ),
            Plant(
                id = "tomato",
                name = "Tomato",
                nameTagalog = "Kamatis",
                scientificName = "Solanum lycopersicum",
                waterFrequency = com.arurbangarden.real.data.model.WaterFrequency.EVERY_2_DAYS,
                sunlightNeeds = com.arurbangarden.real.data.model.SunlightNeeds.FULL_SUN,
                careTips = listOf(
                    "Water deeply every 2 days",
                    "Needs full sun",
                    "Support with stakes"
                ),
                careTipsTagalog = listOf(
                    "Diligan nang malalim bawat 2 araw",
                    "Kailangan ng buong araw",
                    "Suportahan ng kahoy"
                )
            ),
            Plant(
                id = "kangkong",
                name = "Water Spinach",
                nameTagalog = "Kangkong",
                scientificName = "Ipomoea aquatica",
                waterFrequency = com.arurbangarden.real.data.model.WaterFrequency.DAILY,
                sunlightNeeds = com.arurbangarden.real.data.model.SunlightNeeds.PARTIAL_SUN,
                careTips = listOf(
                    "Keep soil very moist",
                    "Can grow in partial shade",
                    "Harvest young leaves"
                ),
                careTipsTagalog = listOf(
                    "Panatilihing napakabasa ang lupa",
                    "Pwedeng tumubo sa bahagyang lilim",
                    "Anihin ang batang dahon"
                )
            ),
            Plant(
                id = "chili",
                name = "Chili Pepper",
                nameTagalog = "Sili",
                scientificName = "Capsicum annuum",
                waterFrequency = com.arurbangarden.real.data.model.WaterFrequency.EVERY_2_DAYS,
                sunlightNeeds = com.arurbangarden.real.data.model.SunlightNeeds.FULL_SUN,
                careTips = listOf(
                    "Water when soil is dry",
                    "Needs full sun",
                    "Fertilize monthly"
                ),
                careTipsTagalog = listOf(
                    "Diligan kapag tuyo ang lupa",
                    "Kailangan ng buong araw",
                    "Patabain buwan-buwan"
                )
            )
        )
    }
    
    fun getPlantById(id: String): Plant? {
        return plantDatabase.find { it.id == id }
    }
    
    fun getAllPlants(): List<Plant> {
        return plantDatabase
    }
}

