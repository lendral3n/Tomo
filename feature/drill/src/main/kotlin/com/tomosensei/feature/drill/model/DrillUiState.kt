package com.tomosensei.feature.drill.model

sealed interface DrillUiState {
    data object Loading : DrillUiState
    data object Empty : DrillUiState
    data class Ready(
        val current: DrillCardUi,
        val flipped: Boolean = false,
        val reviewedToday: Int = 0,
        val streakDays: Int = 0,
    ) : DrillUiState
}
