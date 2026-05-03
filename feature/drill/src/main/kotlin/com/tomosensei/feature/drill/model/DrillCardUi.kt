package com.tomosensei.feature.drill.model

/**
 * View-layer projection of a card + its SRS progress, ready to render
 * without leaking Room types into Compose.
 */
data class DrillCardUi(
    val id: String,
    val front: String,
    val reading: String,
    val meaning: String,
    val type: String,
    val examples: List<DrillExampleUi>,
)

data class DrillExampleUi(
    val jp: String,
    val reading: String,
    val translation: String,
)
