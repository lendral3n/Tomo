package com.tomosensei.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomosensei.core.ai.GemmaInferenceManager
import com.tomosensei.core.ai.InferenceChunk
import com.tomosensei.core.ai.PromptBuilder
import com.tomosensei.core.common.Clock
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val inferenceManager: GemmaInferenceManager,
    private val clock: Clock,
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    fun send(userMessage: String) {
        if (userMessage.isBlank() || _state.value.isStreaming) return
        val now = clock.nowMillis()
        val userMsg = ChatMessageUi(
            id = now,
            role = ChatRole.User,
            text = userMessage.trim(),
        )
        val placeholderId = now + 1
        val senseiPlaceholder = ChatMessageUi(
            id = placeholderId,
            role = ChatRole.Sensei,
            text = "",
            streaming = true,
        )
        _state.update {
            it.copy(
                messages = it.messages + userMsg + senseiPlaceholder,
                draft = "",
                isStreaming = true,
            )
        }
        viewModelScope.launch {
            val prompt = PromptBuilder.chatPrompt(level = "N5", userMessage = userMsg.text)
            val accumulated = StringBuilder()
            inferenceManager.generate(prompt = prompt).collect { chunk ->
                when (chunk) {
                    is InferenceChunk.Token -> {
                        accumulated.append(chunk.text)
                        updateMessage(placeholderId, accumulated.toString(), streaming = true)
                    }
                    is InferenceChunk.Done -> {
                        updateMessage(placeholderId, chunk.fullText.ifBlank { accumulated.toString() }, streaming = false)
                        _state.update { it.copy(isStreaming = false) }
                    }
                    is InferenceChunk.Error -> {
                        updateMessage(placeholderId, "[error] ${chunk.message}", streaming = false)
                        _state.update { it.copy(isStreaming = false) }
                    }
                }
            }
        }
    }

    fun onDraftChange(value: String) {
        _state.update { it.copy(draft = value) }
    }

    private fun updateMessage(id: Long, text: String, streaming: Boolean) {
        _state.update { current ->
            current.copy(
                messages = current.messages.map { msg ->
                    if (msg.id == id) msg.copy(text = text, streaming = streaming) else msg
                },
            )
        }
    }
}

data class ChatUiState(
    val messages: List<ChatMessageUi> = emptyList(),
    val draft: String = "",
    val isStreaming: Boolean = false,
)

data class ChatMessageUi(
    val id: Long,
    val role: ChatRole,
    val text: String,
    val streaming: Boolean = false,
)

enum class ChatRole { User, Sensei }
