package com.arurbangarden.real.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mission(
    val id: String,
    val title: String,
    val titleTagalog: String,
    val description: String,
    val descriptionTagalog: String,
    val type: MissionType,
    val steps: List<MissionStep>,
    val iconRes: Int, // Icon resource ID
    val badgeReward: BadgeType? = null,
    val points: Int = 10,
    val isARIntegrated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long? = null, // Optional due date
    var status: MissionStatus = MissionStatus.PENDING,
    var completedAt: Long? = null
) : Parcelable

@Parcelize
data class MissionStep(
    val id: String,
    val instruction: String,
    val instructionTagalog: String,
    val isCompleted: Boolean = false,
    val order: Int = 0
) : Parcelable

enum class MissionType {
    SOIL_CHECK,        // Check if soil is dry
    SUNLIGHT_FIND,     // Find a place with sunlight
    PHOTO_TAKE,        // Take a photo of plant
    WATER_PLANT,       // Water the plant
    MEASURE_PLANT,     // Measure plant height
    IDENTIFY_PLANT,    // Identify a plant using AR
    PLACE_GARDEN,      // Place virtual garden using AR
    GROWTH_RECORD,     // Record growth progress
    PEST_CHECK,        // Check for pests/diseases
    FERTILIZE          // Fertilize the plant
}

enum class MissionStatus {
    PENDING,      // Not started
    IN_PROGRESS,  // Started but not completed
    COMPLETED,    // Completed
    EXPIRED       // Past due date
}

enum class BadgeType {
    FIRST_MISSION,        // Completed first mission
    DAILY_GARDENER,       // Completed 7 daily missions
    SOIL_EXPERT,          // Completed 5 soil check missions
    PHOTOGRAPHER,         // Completed 10 photo missions
    AR_EXPLORER,          // Completed 5 AR missions
    GROWTH_TRACKER,       // Completed 10 growth records
    WEEKLY_CHAMPION,      // Completed all missions in a week
    PLANT_MASTER          // Completed 50 missions total
}

data class MissionProgress(
    val totalMissions: Int,
    val completedMissions: Int,
    val inProgressMissions: Int,
    val pointsEarned: Int,
    val badgesEarned: List<BadgeType>
) {
    val completionPercentage: Float
        get() = if (totalMissions > 0) {
            (completedMissions.toFloat() / totalMissions.toFloat()) * 100f
        } else {
            0f
        }
}

