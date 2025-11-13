package com.arurbangarden.real.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GrowthRecord(
    val id: String,
    val plantId: String,
    val anchorId: String? = null,  // ARCore anchor ID
    val timestamp: Long,
    val photoPath: String,
    val metadata: GrowthMetadata
) : Parcelable

@Parcelize
data class GrowthMetadata(
    val height: Float? = null,  // cm
    val diameter: Float? = null,  // cm
    val soilMoisture: Int? = null,  // percentage
    val soilPH: Float? = null,
    val temperature: Float? = null,  // Celsius
    val weatherCondition: String? = null,
    val weatherTemperature: Float? = null,
    val weatherHumidity: Int? = null,
    val notes: String? = null
) : Parcelable

data class GrowthTimeline(
    val plantId: String,
    val records: List<GrowthRecord>
) {
    fun getHeightOverTime(): List<Pair<Long, Float>> {
        return records
            .filter { it.metadata.height != null }
            .map { Pair(it.timestamp, it.metadata.height!!) }
            .sortedBy { it.first }
    }
}

