package com.tomosensei.feature.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomosensei.core.data.db.dao.AnalyticsDao
import com.tomosensei.core.data.db.dao.CardDao
import com.tomosensei.core.data.db.dao.CardProgressDao
import com.tomosensei.core.data.db.dao.WeakCard
import com.tomosensei.core.data.repository.UserStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StatsUiState(
    val streak: Int = 0,
    val totalReviews: Int = 0,
    val cardsLearned: Int = 0,
    val gatesPassed: Int = 0,
    val dailyGoal: Int = 10,
)

data class MasteryUiState(
    val masteredCount: Int = 0,
    val totalCards: Int = 0,
) {
    val fraction: Float get() =
        if (totalCards == 0) 0f else (masteredCount.toFloat() / totalCards.toFloat()).coerceIn(0f, 1f)
}

@HiltViewModel
class StatsViewModel @Inject constructor(
    statsRepository: UserStatsRepository,
    private val cardDao: CardDao,
    private val progressDao: CardProgressDao,
    private val analyticsDao: AnalyticsDao,
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

    private val _mastery = MutableStateFlow(MasteryUiState())
    val mastery: StateFlow<MasteryUiState> = _mastery.asStateFlow()

    private val _weakCards = MutableStateFlow<List<WeakCard>>(emptyList())
    val weakCards: StateFlow<List<WeakCard>> = _weakCards.asStateFlow()

    init {
        viewModelScope.launch { refresh() }
    }

    fun refresh() {
        viewModelScope.launch {
            val total = cardDao.count()
            val mastered = progressDao.countMastered()
            _mastery.update { MasteryUiState(masteredCount = mastered, totalCards = total) }
            _weakCards.update { analyticsDao.weakCards(limit = 5) }
        }
    }
}
