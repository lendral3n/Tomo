package com.tomosensei.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomosensei.feature.onboarding.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AppShellState {
    data object Loading : AppShellState
    data object Onboarding : AppShellState
    data object MainApp : AppShellState
}

@HiltViewModel
class AppShellViewModel @Inject constructor(
    private val onboardingPreferences: OnboardingPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow<AppShellState>(AppShellState.Loading)
    val state: StateFlow<AppShellState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            onboardingPreferences.isCompleted.collect { done ->
                _state.value = if (done) AppShellState.MainApp else AppShellState.Onboarding
            }
        }
    }
}
