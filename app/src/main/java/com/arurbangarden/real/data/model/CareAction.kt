package com.arurbangarden.real.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CareAction(
    val type: CareActionType,
    val status: CareStatus,
    val message: String,
    val messageTagalog: String,
    val iconRes: Int,
    val priority: Int = 0
) : Parcelable

enum class CareActionType {
    WATER,
    SUNLIGHT,
    TRIM,
    FERTILIZE,
    PEST_CONTROL
}

enum class CareStatus {
    NEEDED_NOW,     // Kailangan ngayon
    NEEDED_SOON,    // Kailangan mamaya
    OK              // Okay
}

data class CareRecommendation(
    val plantId: String,
    val actions: List<CareAction>,
    val lastUpdated: Long = System.currentTimeMillis()
)

