package com.arurbangarden.real.ml

import android.content.Context
import android.graphics.Bitmap
import com.arurbangarden.real.data.model.Plant
import com.arurbangarden.real.data.model.PlantRecognitionResult
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.max

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
     */
    fun recognizePlant(bitmap: Bitmap): PlantRecognitionResult {
        return if (interpreter != null) {
            recognizeWithTFLite(bitmap)
        } else {
            // Fallback: Use simple color/shape analysis or ML Kit
            recognizeWithFallback(bitmap)
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
    
    private fun recognizeWithFallback(bitmap: Bitmap): PlantRecognitionResult {
        // Fallback implementation using simple heuristics
        // In production, integrate ML Kit Image Labeling here
        // For now, return a placeholder
        return PlantRecognitionResult(
            plant = null,
            confidence = 0.0f
        )
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

