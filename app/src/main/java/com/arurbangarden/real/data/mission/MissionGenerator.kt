package com.arurbangarden.real.data.mission

import com.arurbangarden.real.data.model.*
import java.util.UUID

/**
 * Generates daily missions for kids
 * Simple, 1-3 step missions only
 */
object MissionGenerator {
    
    /**
     * Generate daily missions
     */
    fun generateDailyMissions(): List<Mission> {
        return listOf(
            generateSoilCheckMission(),
            generateSunlightFindMission(),
            generatePhotoMission(),
            generateWaterPlantMission(),
            generateARIdentifyMission()
        )
    }
    
    /**
     * Mission: Check if soil is dry
     */
    private fun generateSoilCheckMission(): Mission {
        return Mission(
            id = UUID.randomUUID().toString(),
            title = "Check Soil",
            titleTagalog = "Tingnan ang Lupa",
            description = "Check if the soil is dry",
            descriptionTagalog = "Tingnan kung tuyo ang lupa",
            type = MissionType.SOIL_CHECK,
            steps = listOf(
                MissionStep(
                    id = "step1",
                    instruction = "Touch the soil with your finger",
                    instructionTagalog = "Hawakan ang lupa gamit ang daliri",
                    order = 1
                ),
                MissionStep(
                    id = "step2",
                    instruction = "Check if it feels dry",
                    instructionTagalog = "Tingnan kung tuyo",
                    order = 2
                )
            ),
            iconRes = android.R.drawable.ic_menu_compass, // Replace with custom icon
            points = 10,
            isARIntegrated = false
        )
    }
    
    /**
     * Mission: Find a place with sunlight
     */
    private fun generateSunlightFindMission(): Mission {
        return Mission(
            id = UUID.randomUUID().toString(),
            title = "Find Sunlight",
            titleTagalog = "Hanapin ang Araw",
            description = "Find a place where your plant can get sunlight",
            descriptionTagalog = "Hanapin ang lugar na may sikat ng araw",
            type = MissionType.SUNLIGHT_FIND,
            steps = listOf(
                MissionStep(
                    id = "step1",
                    instruction = "Look for a sunny spot",
                    instructionTagalog = "Hanapin ang lugar na may araw",
                    order = 1
                ),
                MissionStep(
                    id = "step2",
                    instruction = "Place your plant there",
                    instructionTagalog = "Ilagay ang halaman doon",
                    order = 2
                )
            ),
            iconRes = android.R.drawable.ic_menu_compass,
            points = 15,
            isARIntegrated = false
        )
    }
    
    /**
     * Mission: Take a photo of your plant
     */
    private fun generatePhotoMission(): Mission {
        return Mission(
            id = UUID.randomUUID().toString(),
            title = "Take Plant Photo",
            titleTagalog = "Kumuha ng Larawan",
            description = "Take a photo of your plant today!",
            descriptionTagalog = "Kumuha ng larawan ng iyong halaman ngayon!",
            type = MissionType.PHOTO_TAKE,
            steps = listOf(
                MissionStep(
                    id = "step1",
                    instruction = "Open camera",
                    instructionTagalog = "Buksan ang camera",
                    order = 1
                ),
                MissionStep(
                    id = "step2",
                    instruction = "Take a photo of your plant",
                    instructionTagalog = "Kumuha ng larawan ng halaman",
                    order = 2
                )
            ),
            iconRes = android.R.drawable.ic_menu_camera,
            points = 20,
            isARIntegrated = false,
            badgeReward = BadgeType.PHOTOGRAPHER
        )
    }
    
    /**
     * Mission: Water the plant
     */
    private fun generateWaterPlantMission(): Mission {
        return Mission(
            id = UUID.randomUUID().toString(),
            title = "Water Plant",
            titleTagalog = "Diligan ang Halaman",
            description = "Give water to your plant",
            descriptionTagalog = "Bigyan ng tubig ang halaman",
            type = MissionType.WATER_PLANT,
            steps = listOf(
                MissionStep(
                    id = "step1",
                    instruction = "Get water",
                    instructionTagalog = "Kumuha ng tubig",
                    order = 1
                ),
                MissionStep(
                    id = "step2",
                    instruction = "Pour water on the soil",
                    instructionTagalog = "Ibuhos ang tubig sa lupa",
                    order = 2
                ),
                MissionStep(
                    id = "step3",
                    instruction = "Don't overwater!",
                    instructionTagalog = "Huwag sobra!",
                    order = 3
                )
            ),
            iconRes = android.R.drawable.ic_menu_compass,
            points = 15,
            isARIntegrated = false
        )
    }
    
    /**
     * Mission: Identify plant using AR (AR-integrated)
     */
    private fun generateARIdentifyMission(): Mission {
        return Mission(
            id = UUID.randomUUID().toString(),
            title = "Identify with AR",
            titleTagalog = "Tukuyin gamit ang AR",
            description = "Point your camera to your plant",
            descriptionTagalog = "Itutok ang camera sa halaman",
            type = MissionType.IDENTIFY_PLANT,
            steps = listOf(
                MissionStep(
                    id = "step1",
                    instruction = "Open AR camera",
                    instructionTagalog = "Buksan ang AR camera",
                    order = 1
                ),
                MissionStep(
                    id = "step2",
                    instruction = "Point camera to plant",
                    instructionTagalog = "Itutok ang camera sa halaman",
                    order = 2
                ),
                MissionStep(
                    id = "step3",
                    instruction = "Wait for identification",
                    instructionTagalog = "Maghintay ng identification",
                    order = 3
                )
            ),
            iconRes = android.R.drawable.ic_menu_camera,
            points = 25,
            isARIntegrated = true,
            badgeReward = BadgeType.AR_EXPLORER
        )
    }
    
    /**
     * Generate mission by type
     */
    fun generateMissionByType(type: MissionType): Mission {
        return when (type) {
            MissionType.SOIL_CHECK -> generateSoilCheckMission()
            MissionType.SUNLIGHT_FIND -> generateSunlightFindMission()
            MissionType.PHOTO_TAKE -> generatePhotoMission()
            MissionType.WATER_PLANT -> generateWaterPlantMission()
            MissionType.IDENTIFY_PLANT -> generateARIdentifyMission()
            MissionType.MEASURE_PLANT -> generateMeasureMission()
            MissionType.PLACE_GARDEN -> generatePlaceGardenMission()
            MissionType.GROWTH_RECORD -> generateGrowthRecordMission()
            MissionType.PEST_CHECK -> generatePestCheckMission()
            MissionType.FERTILIZE -> generateFertilizeMission()
        }
    }
    
    private fun generateMeasureMission(): Mission {
        return Mission(
            id = UUID.randomUUID().toString(),
            title = "Measure Plant",
            titleTagalog = "Sukatin ang Halaman",
            description = "Measure how tall your plant is",
            descriptionTagalog = "Sukatin ang taas ng halaman",
            type = MissionType.MEASURE_PLANT,
            steps = listOf(
                MissionStep(
                    id = "step1",
                    instruction = "Open AR measure tool",
                    instructionTagalog = "Buksan ang AR measure",
                    order = 1
                ),
                MissionStep(
                    id = "step2",
                    instruction = "Measure plant height",
                    instructionTagalog = "Sukatin ang taas",
                    order = 2
                )
            ),
            iconRes = android.R.drawable.ic_menu_manage,
            points = 20,
            isARIntegrated = true
        )
    }
    
    private fun generatePlaceGardenMission(): Mission {
        return Mission(
            id = UUID.randomUUID().toString(),
            title = "Place Garden",
            titleTagalog = "Ilagay ang Hardin",
            description = "Use AR to place a virtual garden",
            descriptionTagalog = "Gamitin ang AR para maglagay ng hardin",
            type = MissionType.PLACE_GARDEN,
            steps = listOf(
                MissionStep(
                    id = "step1",
                    instruction = "Find flat surface",
                    instructionTagalog = "Hanapin ang patag na lugar",
                    order = 1
                ),
                MissionStep(
                    id = "step2",
                    instruction = "Place virtual pots",
                    instructionTagalog = "Ilagay ang virtual pots",
                    order = 2
                )
            ),
            iconRes = android.R.drawable.ic_menu_compass,
            points = 25,
            isARIntegrated = true
        )
    }
    
    private fun generateGrowthRecordMission(): Mission {
        return Mission(
            id = UUID.randomUUID().toString(),
            title = "Record Growth",
            titleTagalog = "Record ang Paglago",
            description = "Take a photo to track growth",
            descriptionTagalog = "Kumuha ng larawan para subaybayan ang paglago",
            type = MissionType.GROWTH_RECORD,
            steps = listOf(
                MissionStep(
                    id = "step1",
                    instruction = "Open growth tracking",
                    instructionTagalog = "Buksan ang growth tracking",
                    order = 1
                ),
                MissionStep(
                    id = "step2",
                    instruction = "Take photo",
                    instructionTagalog = "Kumuha ng larawan",
                    order = 2
                )
            ),
            iconRes = android.R.drawable.ic_menu_gallery,
            points = 15,
            isARIntegrated = false,
            badgeReward = BadgeType.GROWTH_TRACKER
        )
    }
    
    private fun generatePestCheckMission(): Mission {
        return Mission(
            id = UUID.randomUUID().toString(),
            title = "Check for Pests",
            titleTagalog = "Tingnan ang Peste",
            description = "Check if your plant has any problems",
            descriptionTagalog = "Tingnan kung may problema ang halaman",
            type = MissionType.PEST_CHECK,
            steps = listOf(
                MissionStep(
                    id = "step1",
                    instruction = "Look at the leaves",
                    instructionTagalog = "Tingnan ang dahon",
                    order = 1
                ),
                MissionStep(
                    id = "step2",
                    instruction = "Check for bugs or spots",
                    instructionTagalog = "Tingnan kung may insekto o spots",
                    order = 2
                )
            ),
            iconRes = android.R.drawable.ic_menu_compass,
            points = 15,
            isARIntegrated = false
        )
    }
    
    private fun generateFertilizeMission(): Mission {
        return Mission(
            id = UUID.randomUUID().toString(),
            title = "Fertilize Plant",
            titleTagalog = "Patabain ang Halaman",
            description = "Add fertilizer to help plant grow",
            descriptionTagalog = "Maglagay ng pataba para tumubo",
            type = MissionType.FERTILIZE,
            steps = listOf(
                MissionStep(
                    id = "step1",
                    instruction = "Ask adult for help",
                    instructionTagalog = "Humiling ng tulong sa matanda",
                    order = 1
                ),
                MissionStep(
                    id = "step2",
                    instruction = "Add fertilizer to soil",
                    instructionTagalog = "Maglagay ng pataba sa lupa",
                    order = 2
                )
            ),
            iconRes = android.R.drawable.ic_menu_compass,
            points = 20,
            isARIntegrated = false
        )
    }
}

