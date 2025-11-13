package com.arurbangarden.real.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val id: String,
    val name: String,
    val nameTagalog: String,
    val scientificName: String,
    val waterFrequency: WaterFrequency,
    val sunlightNeeds: SunlightNeeds,
    val careTips: List<String>,
    val careTipsTagalog: List<String>,
    val imageUrl: String? = null
) : Parcelable

enum class WaterFrequency {
    DAILY,      // Araw-araw
    EVERY_2_DAYS,  // Bawat 2 araw
    WEEKLY,     // Lingguhan
    BIWEEKLY    // Bawat 2 linggo
}

enum class SunlightNeeds {
    FULL_SUN,       // Buong araw
    PARTIAL_SUN,    // Bahagyang araw
    SHADE           // Lilim
}

data class PlantRecognitionResult(
    val plant: Plant?,
    val confidence: Float,
    val boundingBox: BoundingBox? = null
)

data class BoundingBox(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float
)

