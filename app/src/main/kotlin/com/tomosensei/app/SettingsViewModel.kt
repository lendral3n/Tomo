package com.tomosensei.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomosensei.core.data.db.dao.AnalyticsDao
import com.tomosensei.core.data.db.dao.ChatMessageDao
import com.tomosensei.core.data.repository.UserStatsRepository
import com.tomosensei.core.data.db.entity.UserStatsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val analyticsDao: AnalyticsDao,
    private val chatMessageDao: ChatMessageDao,
    private val statsRepository: UserStatsRepository,
) : ViewModel() {

    private val _resetState = MutableStateFlow(ResetState.Idle)
    val resetState: StateFlow<ResetState> = _resetState.asStateFlow()

    /**
     * Hard reset: drops FSRS progress + gate logs + chat history, then
     * zeroes out streak / cards-learned / gates-passed on UserStats while
     * preserving the user's preferences (dailyGoal, target level, PIN).
     * Cards stay seeded — they're content, not progress.
     */
    fun resetProgress() {
        if (_resetState.value == ResetState.Working) return
        _resetState.value = ResetState.Working
        viewModelScope.launch {
            analyticsDao.resetAllProgress()
            analyticsDao.resetAllGateLogs()
            chatMessageDao.clearSession("default")
            val current = statsRepository.get()
            statsRepository.upsert(
                (current ?: UserStatsEntity(
                    id = 0,
                    streak = 0,
                    lastActiveDate = "",
                    totalReviews = 0,
                    cardsLearned = 0,
                    gatesPassed = 0,
                    targetLevel = "N5",
                    dailyGoal = 15,
                )).copy(
                    streak = 0,
                    lastActiveDate = "",
                    totalReviews = 0,
                    cardsLearned = 0,
                    gatesPassed = 0,
                ),
            )
            _resetState.value = ResetState.Done
        }
    }

    fun acknowledgeDone() {
        _resetState.value = ResetState.Idle
    }
}

enum class ResetState { Idle, Working, Done }
