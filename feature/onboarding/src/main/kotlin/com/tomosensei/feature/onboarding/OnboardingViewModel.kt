package com.tomosensei.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomosensei.core.data.db.entity.SettingsEntity
import com.tomosensei.core.data.db.entity.UserStatsEntity
import com.tomosensei.core.data.repository.SettingsRepository
import com.tomosensei.core.data.repository.UserStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferences: OnboardingPreferences,
    private val settingsRepository: SettingsRepository,
    private val statsRepository: UserStatsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingProgress())
    val state: StateFlow<OnboardingProgress> = _state.asStateFlow()

    fun selectPreset(preset: Preset) {
        _state.update { it.copy(preset = preset) }
    }

    fun setPin(value: String) {
        if (value.length > PIN_LENGTH || value.any { !it.isDigit() }) return
        _state.update { it.copy(pinDigits = value, pinError = null) }
    }

    fun setPinConfirm(value: String) {
        if (value.length > PIN_LENGTH || value.any { !it.isDigit() }) return
        _state.update { it.copy(pinConfirm = value, pinError = null) }
    }

    fun setDailyGoal(goal: Int) {
        _state.update { it.copy(dailyGoal = goal) }
    }

    fun next() {
        _state.update { it.copy(step = it.step + 1) }
    }

    fun back() {
        _state.update { it.copy(step = (it.step - 1).coerceAtLeast(0)) }
    }

    /** Validates PIN, persists everything to Room, flips the DataStore flag. */
    fun finish(onDone: () -> Unit) {
        val s = _state.value
        if (s.pinDigits.length != PIN_LENGTH) {
            _state.update { it.copy(pinError = "PIN harus $PIN_LENGTH digit") }
            return
        }
        if (s.pinDigits != s.pinConfirm) {
            _state.update { it.copy(pinError = "Konfirmasi PIN tidak cocok") }
            return
        }
        viewModelScope.launch {
            settingsRepository.upsert(
                SettingsEntity(
                    id = 0,
                    emergencyPinHash = PinHasher.hash(s.pinDigits),
                    pauseSchedule = """{"start":"00:00","end":"00:00"}""",
                    modelLoaded = false,
                    soundEnabled = true,
                    hapticEnabled = true,
                ),
            )
            statsRepository.upsert(
                UserStatsEntity(
                    id = 0,
                    streak = 0,
                    lastActiveDate = "",
                    totalReviews = 0,
                    cardsLearned = 0,
                    gatesPassed = 0,
                    targetLevel = "N5",
                    dailyGoal = s.dailyGoal,
                ),
            )
            preferences.markCompleted()
            onDone()
        }
    }

    companion object {
        const val PIN_LENGTH = 4
    }
}
