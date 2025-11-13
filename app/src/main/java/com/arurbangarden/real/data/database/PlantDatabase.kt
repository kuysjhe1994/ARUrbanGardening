package com.arurbangarden.real.data.database

import com.arurbangarden.real.data.model.Plant
import com.arurbangarden.real.data.model.SunlightNeeds
import com.arurbangarden.real.data.model.WaterFrequency

/**
 * Expanded plant database for AR Urban Garden
 * Common urban plants in the Philippines
 */
object PlantDatabase {
    
    fun getAllPlants(): List<Plant> {
        return listOf(
            // Herbs
            createBasil(),
            createMint(),
            createOregano(),
            createLemongrass(),
            createGinger(),
            
            // Vegetables
            createTomato(),
            createKangkong(),
            createChili(),
            createEggplant(),
            createOkra(),
            createPechay(),
            createLettuce(),
            
            // Fruits
            createPapaya(),
            createBanana(),
            createCalamansi(),
            
            // Ornamental
            createAloeVera(),
            createSansevieria()
        )
    }
    
    fun getPlantById(id: String): Plant? {
        return getAllPlants().find { it.id == id }
    }
    
    // Herbs
    private fun createBasil() = Plant(
        id = "basil",
        name = "Basil",
        nameTagalog = "Bawang",
        scientificName = "Ocimum basilicum",
        waterFrequency = WaterFrequency.DAILY,
        sunlightNeeds = SunlightNeeds.FULL_SUN,
        careTips = listOf(
            "Water daily, keep soil moist",
            "Needs 6-8 hours of sunlight",
            "Harvest leaves regularly to encourage growth",
            "Pinch flowers to extend leaf production"
        ),
        careTipsTagalog = listOf(
            "Diligan araw-araw, panatilihing basa ang lupa",
            "Kailangan ng 6-8 oras na sikat ng araw",
            "Anihin ang dahon nang regular para tumubo",
            "Gupitin ang bulaklak para mas maraming dahon"
        )
    )
    
    private fun createMint() = Plant(
        id = "mint",
        name = "Mint",
        nameTagalog = "Mentha",
        scientificName = "Mentha",
        waterFrequency = WaterFrequency.DAILY,
        sunlightNeeds = SunlightNeeds.PARTIAL_SUN,
        careTips = listOf(
            "Water frequently, likes moist soil",
            "Partial shade is fine",
            "Can spread quickly - use container",
            "Harvest regularly to control growth"
        ),
        careTipsTagalog = listOf(
            "Diligan nang madalas, gusto ng basang lupa",
            "Bahagyang lilim ay okay",
            "Mabilis kumalat - gamitin ang container",
            "Anihin nang regular para kontrolin ang paglago"
        )
    )
    
    private fun createOregano() = Plant(
        id = "oregano",
        name = "Oregano",
        nameTagalog = "Oregano",
        scientificName = "Origanum vulgare",
        waterFrequency = WaterFrequency.EVERY_2_DAYS,
        sunlightNeeds = SunlightNeeds.FULL_SUN,
        careTips = listOf(
            "Water when soil feels dry",
            "Needs full sun",
            "Drought tolerant once established",
            "Harvest before flowering"
        ),
        careTipsTagalog = listOf(
            "Diligan kapag tuyo ang lupa",
            "Kailangan ng buong araw",
            "Matagal na hindi kailangan ng tubig kapag naitanim na",
            "Anihin bago mag-bulaklak"
        )
    )
    
    private fun createLemongrass() = Plant(
        id = "lemongrass",
        name = "Lemongrass",
        nameTagalog = "Tanglad",
        scientificName = "Cymbopogon citratus",
        waterFrequency = WaterFrequency.DAILY,
        sunlightNeeds = SunlightNeeds.FULL_SUN,
        careTips = listOf(
            "Keep soil consistently moist",
            "Needs full sun",
            "Can grow tall - needs space",
            "Harvest from base"
        ),
        careTipsTagalog = listOf(
            "Panatilihing basa ang lupa",
            "Kailangan ng buong araw",
            "Pwedeng tumaas - kailangan ng espasyo",
            "Anihin mula sa base"
        )
    )
    
    private fun createGinger() = Plant(
        id = "ginger",
        name = "Ginger",
        nameTagalog = "Luya",
        scientificName = "Zingiber officinale",
        waterFrequency = WaterFrequency.EVERY_2_DAYS,
        sunlightNeeds = SunlightNeeds.PARTIAL_SUN,
        careTips = listOf(
            "Water regularly but don't overwater",
            "Prefers partial shade",
            "Harvest after 8-10 months",
            "Keep soil well-drained"
        ),
        careTipsTagalog = listOf(
            "Diligan nang regular pero huwag sobra",
            "Mas gusto ng bahagyang lilim",
            "Anihin pagkatapos ng 8-10 buwan",
            "Panatilihing maayos ang drainage ng lupa"
        )
    )
    
    // Vegetables
    private fun createTomato() = Plant(
        id = "tomato",
        name = "Tomato",
        nameTagalog = "Kamatis",
        scientificName = "Solanum lycopersicum",
        waterFrequency = WaterFrequency.EVERY_2_DAYS,
        sunlightNeeds = SunlightNeeds.FULL_SUN,
        careTips = listOf(
            "Water deeply every 2 days",
            "Needs full sun (6-8 hours)",
            "Support with stakes or cage",
            "Fertilize every 2 weeks"
        ),
        careTipsTagalog = listOf(
            "Diligan nang malalim bawat 2 araw",
            "Kailangan ng buong araw (6-8 oras)",
            "Suportahan ng kahoy o cage",
            "Patabain bawat 2 linggo"
        )
    )
    
    private fun createKangkong() = Plant(
        id = "kangkong",
        name = "Water Spinach",
        nameTagalog = "Kangkong",
        scientificName = "Ipomoea aquatica",
        waterFrequency = WaterFrequency.DAILY,
        sunlightNeeds = SunlightNeeds.PARTIAL_SUN,
        careTips = listOf(
            "Keep soil very moist or grow in water",
            "Can grow in partial shade",
            "Harvest young leaves and stems",
            "Grows quickly - harvest regularly"
        ),
        careTipsTagalog = listOf(
            "Panatilihing napakabasa ang lupa o itanim sa tubig",
            "Pwedeng tumubo sa bahagyang lilim",
            "Anihin ang batang dahon at tangkay",
            "Mabilis tumubo - anihin nang regular"
        )
    )
    
    private fun createChili() = Plant(
        id = "chili",
        name = "Chili Pepper",
        nameTagalog = "Sili",
        scientificName = "Capsicum annuum",
        waterFrequency = WaterFrequency.EVERY_2_DAYS,
        sunlightNeeds = SunlightNeeds.FULL_SUN,
        careTips = listOf(
            "Water when soil is dry",
            "Needs full sun",
            "Fertilize monthly",
            "Harvest when peppers are firm"
        ),
        careTipsTagalog = listOf(
            "Diligan kapag tuyo ang lupa",
            "Kailangan ng buong araw",
            "Patabain buwan-buwan",
            "Anihin kapag matigas na ang sili"
        )
    )
    
    private fun createEggplant() = Plant(
        id = "eggplant",
        name = "Eggplant",
        nameTagalog = "Talong",
        scientificName = "Solanum melongena",
        waterFrequency = WaterFrequency.EVERY_2_DAYS,
        sunlightNeeds = SunlightNeeds.FULL_SUN,
        careTips = listOf(
            "Water deeply, keep soil moist",
            "Needs full sun",
            "Support with stakes",
            "Harvest when skin is shiny"
        ),
        careTipsTagalog = listOf(
            "Diligan nang malalim, panatilihing basa ang lupa",
            "Kailangan ng buong araw",
            "Suportahan ng kahoy",
            "Anihin kapag makintab ang balat"
        )
    )
    
    private fun createOkra() = Plant(
        id = "okra",
        name = "Okra",
        nameTagalog = "Okra",
        scientificName = "Abelmoschus esculentus",
        waterFrequency = WaterFrequency.EVERY_2_DAYS,
        sunlightNeeds = SunlightNeeds.FULL_SUN,
        careTips = listOf(
            "Water regularly",
            "Needs full sun",
            "Harvest when pods are 3-4 inches",
            "Harvest every 2-3 days"
        ),
        careTipsTagalog = listOf(
            "Diligan nang regular",
            "Kailangan ng buong araw",
            "Anihin kapag 3-4 pulgada ang pod",
            "Anihin bawat 2-3 araw"
        )
    )
    
    private fun createPechay() = Plant(
        id = "pechay",
        name = "Chinese Cabbage",
        nameTagalog = "Pechay",
        scientificName = "Brassica rapa",
        waterFrequency = WaterFrequency.DAILY,
        sunlightNeeds = SunlightNeeds.PARTIAL_SUN,
        careTips = listOf(
            "Keep soil moist",
            "Partial sun is fine",
            "Harvest whole plant or outer leaves",
            "Can be harvested in 30-40 days"
        ),
        careTipsTagalog = listOf(
            "Panatilihing basa ang lupa",
            "Bahagyang araw ay okay",
            "Anihin ang buong halaman o panlabas na dahon",
            "Pwedeng anihin sa 30-40 araw"
        )
    )
    
    private fun createLettuce() = Plant(
        id = "lettuce",
        name = "Lettuce",
        nameTagalog = "Letsugas",
        scientificName = "Lactuca sativa",
        waterFrequency = WaterFrequency.DAILY,
        sunlightNeeds = SunlightNeeds.PARTIAL_SUN,
        careTips = listOf(
            "Keep soil consistently moist",
            "Prefers partial shade in hot weather",
            "Harvest outer leaves or whole head",
            "Can be grown year-round"
        ),
        careTipsTagalog = listOf(
            "Panatilihing basa ang lupa",
            "Mas gusto ng bahagyang lilim sa mainit na panahon",
            "Anihin ang panlabas na dahon o buong ulo",
            "Pwedeng itanim buong taon"
        )
    )
    
    // Fruits
    private fun createPapaya() = Plant(
        id = "papaya",
        name = "Papaya",
        nameTagalog = "Papaya",
        scientificName = "Carica papaya",
        waterFrequency = WaterFrequency.EVERY_2_DAYS,
        sunlightNeeds = SunlightNeeds.FULL_SUN,
        careTips = listOf(
            "Water deeply but allow soil to dry between",
            "Needs full sun",
            "Needs space - can grow tall",
            "Fruits in 6-9 months"
        ),
        careTipsTagalog = listOf(
            "Diligan nang malalim pero hayaang matuyo ang lupa",
            "Kailangan ng buong araw",
            "Kailangan ng espasyo - pwedeng tumaas",
            "Namumunga sa 6-9 buwan"
        )
    )
    
    private fun createBanana() = Plant(
        id = "banana",
        name = "Banana",
        nameTagalog = "Saging",
        scientificName = "Musa",
        waterFrequency = WaterFrequency.DAILY,
        sunlightNeeds = SunlightNeeds.FULL_SUN,
        careTips = listOf(
            "Keep soil moist",
            "Needs full sun",
            "Needs large space",
            "Fruits in 9-12 months"
        ),
        careTipsTagalog = listOf(
            "Panatilihing basa ang lupa",
            "Kailangan ng buong araw",
            "Kailangan ng malaking espasyo",
            "Namumunga sa 9-12 buwan"
        )
    )
    
    private fun createCalamansi() = Plant(
        id = "calamansi",
        name = "Calamansi",
        nameTagalog = "Calamansi",
        scientificName = "Citrus × microcarpa",
        waterFrequency = WaterFrequency.EVERY_2_DAYS,
        sunlightNeeds = SunlightNeeds.FULL_SUN,
        careTips = listOf(
            "Water when soil is dry",
            "Needs full sun",
            "Can be grown in containers",
            "Fruits in 2-3 years"
        ),
        careTipsTagalog = listOf(
            "Diligan kapag tuyo ang lupa",
            "Kailangan ng buong araw",
            "Pwedeng itanim sa container",
            "Namumunga sa 2-3 taon"
        )
    )
    
    // Ornamental/Medicinal
    private fun createAloeVera() = Plant(
        id = "aloe_vera",
        name = "Aloe Vera",
        nameTagalog = "Aloe Vera",
        scientificName = "Aloe vera",
        waterFrequency = WaterFrequency.WEEKLY,
        sunlightNeeds = SunlightNeeds.PARTIAL_SUN,
        careTips = listOf(
            "Water sparingly - let soil dry completely",
            "Prefers partial sun",
            "Drought tolerant",
            "Harvest gel from mature leaves"
        ),
        careTipsTagalog = listOf(
            "Diligan nang kaunti - hayaang matuyo ang lupa",
            "Mas gusto ng bahagyang araw",
            "Matagal na hindi kailangan ng tubig",
            "Kunin ang gel mula sa mature na dahon"
        )
    )
    
    private fun createSansevieria() = Plant(
        id = "sansevieria",
        name = "Snake Plant",
        nameTagalog = "Sansevieria",
        scientificName = "Sansevieria trifasciata",
        waterFrequency = WaterFrequency.BIWEEKLY,
        sunlightNeeds = SunlightNeeds.PARTIAL_SUN,
        careTips = listOf(
            "Water very sparingly",
            "Can tolerate low light",
            "Very drought tolerant",
            "Good for beginners"
        ),
        careTipsTagalog = listOf(
            "Diligan nang napaka-kaunti",
            "Pwedeng tumubo sa mababang liwanag",
            "Napakatagal na hindi kailangan ng tubig",
            "Maganda para sa mga nagsisimula"
        )
    )
}

