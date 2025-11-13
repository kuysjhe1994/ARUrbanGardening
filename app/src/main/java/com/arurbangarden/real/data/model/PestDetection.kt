package com.arurbangarden.real.data.model

data class PestDetectionResult(
    val issue: String,
    val issueTagalog: String,
    val confidence: Float,
    val recommendedActions: List<String>,
    val recommendedActionsTagalog: List<String>,
    val severity: Severity,
    val requiresAdultHelp: Boolean = true
)

enum class Severity {
    LOW,        // Mababa
    MEDIUM,     // Katamtaman
    HIGH        // Mataas
}

