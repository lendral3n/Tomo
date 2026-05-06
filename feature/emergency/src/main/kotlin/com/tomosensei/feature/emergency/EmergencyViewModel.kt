package com.tomosensei.feature.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomosensei.core.data.repository.SettingsRepository
import com.tomosensei.feature.onboarding.PinHasher
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmergencyUiState(
    val digits: String = "",
    val attempts: Int = 0,
    val error: String? = null,
)

@HiltViewModel
class EmergencyViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(EmergencyUiState())
    val state: StateFlow<EmergencyUiState> = _state.asStateFlow()

    fun onKey(digit: Int) {
        val current = _state.value
        if (current.digits.length >= PIN_LENGTH) return
        _state.update { it.copy(digits = it.digits + digit, error = null) }
        if (_state.value.digits.length == PIN_LENGTH) verify()
    }

    fun onBackspace() {
        _state.update { it.copy(digits = it.digits.dropLast(1), error = null) }
    }

    private fun verify() {
        viewModelScope.launch {
            val stored = settingsRepository.get()?.emergencyPinHash
            val candidate = PinHasher.hash(_state.value.digits)
            if (stored != null && stored == candidate) {
                _state.update { it.copy(error = null) }
                onSuccess?.invoke()
            } else {
                _state.update { it.copy(
                    digits = "",
                    attempts = it.attempts + 1,
                    error = "PIN salah (${it.attempts + 1}x)",
                ) }
            }
        }
    }

    private var onSuccess: (() -> Unit)? = null
    fun setSuccessHandler(handler: () -> Unit) { onSuccess = handler }

    companion object {
        const val PIN_LENGTH = 4
    }
}
