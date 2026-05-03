package com.tomosensei.feature.stats

import androidx.lifecycle.ViewModel
import com.tomosensei.core.data.repository.UserStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope

data class StatsUiState(
    val streak: Int = 0,
    val totalReviews: Int = 0,
    val cardsLearned: Int = 0,
    val gatesPassed: Int = 0,
    val dailyGoal: Int = 10,
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    statsRepository: UserStatsRepository,
) : ViewModel() {
    val state: StateFlow<StatsUiState> = statsRepository.observe()
        .let { flow ->
            kotlinx.coroutines.flow.flow {
                flow.collect { entity ->
                    if (entity == null) {
                        emit(StatsUiState())
                    } else {
                        emit(
                            StatsUiState(
                                streak = entity.streak,
                                totalReviews = entity.totalReviews,
                                cardsLearned = entity.cardsLearned,
                                gatesPassed = entity.gatesPassed,
                                dailyGoal = entity.dailyGoal,
                            ),
                        )
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())
}
