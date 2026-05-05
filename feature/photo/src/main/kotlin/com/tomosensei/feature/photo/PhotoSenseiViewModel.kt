package com.tomosensei.feature.photo

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomosensei.core.ai.GemmaInferenceManager
import com.tomosensei.core.ai.InferenceChunk
import com.tomosensei.core.ai.PhotoAnalysis
import com.tomosensei.core.ai.PhotoAnalysisParser
import com.tomosensei.core.ai.PromptBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface PhotoSenseiState {
    data object Idle : PhotoSenseiState
    data class Analyzing(val raw: String = "") : PhotoSenseiState
    data class Result(val analysis: PhotoAnalysis) : PhotoSenseiState
    data class Failed(val message: String) : PhotoSenseiState
}

@HiltViewModel
class PhotoSenseiViewModel @Inject constructor(
    private val inferenceManager: GemmaInferenceManager,
) : ViewModel() {

    private val _state = MutableStateFlow<PhotoSenseiState>(PhotoSenseiState.Idle)
    val state: StateFlow<PhotoSenseiState> = _state.asStateFlow()

    fun analyze(bitmap: Bitmap, level: String = "N5") {
        _state.value = PhotoSenseiState.Analyzing()
        viewModelScope.launch {
            val accumulated = StringBuilder()
            inferenceManager.generate(
                prompt = PromptBuilder.photoPrompt(level),
                image = bitmap,
            ).collect { chunk ->
                when (chunk) {
                    is InferenceChunk.Token -> {
                        accumulated.append(chunk.text)
                        _state.update { PhotoSenseiState.Analyzing(accumulated.toString()) }
                    }
                    is InferenceChunk.Done -> {
                        val parsed = PhotoAnalysisParser.parse(
                            chunk.fullText.ifBlank { accumulated.toString() },
                        )
                        _state.value = PhotoSenseiState.Result(parsed)
                    }
                    is InferenceChunk.Error -> {
                        _state.value = PhotoSenseiState.Failed(chunk.message)
                    }
                }
            }
        }
    }

    fun reset() {
        _state.value = PhotoSenseiState.Idle
    }
}
