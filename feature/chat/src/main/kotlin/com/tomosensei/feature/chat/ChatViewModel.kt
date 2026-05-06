package com.tomosensei.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomosensei.core.ai.GemmaInferenceManager
import com.tomosensei.core.ai.InferenceChunk
import com.tomosensei.core.ai.PromptBuilder
import com.tomosensei.core.common.Clock
import com.tomosensei.core.data.db.dao.ChatMessageDao
import com.tomosensei.core.data.db.entity.ChatMessageEntity
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
    private val chatDao: ChatMessageDao,
    private val clock: Clock,
) : ViewModel() {

    private val sessionId = "default"
    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            chatDao.observeSession(sessionId).collect { rows ->
                _state.update { current ->
                    // Only repopulate if local state is empty (cold load) —
                    // streaming-in-flight messages live in memory until done.
                    if (current.messages.isEmpty() || rows.size > current.messages.size) {
                        current.copy(
                            messages = rows.map { it.toUi() },
                        )
                    } else {
                        current
                    }
                }
            }
        }
    }

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
            chatDao.insert(
                ChatMessageEntity(
                    sessionId = sessionId,
                    role = "user",
                    content = userMsg.text,
                    timestamp = userMsg.id,
                ),
            )
            val prompt = PromptBuilder.chatPrompt(level = "N5", userMessage = userMsg.text)
            val accumulated = StringBuilder()
            inferenceManager.generate(prompt = prompt).collect { chunk ->
                when (chunk) {
                    is InferenceChunk.Token -> {
                        accumulated.append(chunk.text)
                        updateMessage(placeholderId, accumulated.toString(), streaming = true)
                    }
                    is InferenceChunk.Done -> {
                        val finalText = chunk.fullText.ifBlank { accumulated.toString() }
                        updateMessage(placeholderId, finalText, streaming = false)
                        _state.update { it.copy(isStreaming = false) }
                        chatDao.insert(
                            ChatMessageEntity(
                                sessionId = sessionId,
                                role = "sensei",
                                content = finalText,
                                timestamp = clock.nowMillis(),
                            ),
                        )
                    }
                    is InferenceChunk.Error -> {
                        updateMessage(placeholderId, "[error] ${chunk.message}", streaming = false)
                        _state.update { it.copy(isStreaming = false) }
                    }
                }
            }
        }
    }

    private fun ChatMessageEntity.toUi(): ChatMessageUi = ChatMessageUi(
        id = id,
        role = if (role == "user") ChatRole.User else ChatRole.Sensei,
        text = content,
        streaming = false,
    )

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
