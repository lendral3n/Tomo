package com.tomosensei.core.ai

import android.graphics.Bitmap
import kotlinx.coroutines.flow.Flow

/**
 * Boundary for on-device inference. The MVP ships a [StubGemmaInferenceManager]
 * that returns canned strings so :feature:chat can be built and demoed
 * without the 3.6 GB Gemma 4 download. The real implementation will live
 * alongside this interface and bind via Hilt once LiteRT-LM is wired up
 * (see spec §8.1).
 *
 * All methods must be safe to call before the model has been downloaded —
 * implementations should surface [InferenceState.NotLoaded] in that case.
 */
interface GemmaInferenceManager {

    val state: Flow<InferenceState>

    /** Loads the model into memory. Idempotent. */
    suspend fun initialize()

    /**
     * Streams the model's response token-by-token. Implementations should
     * close the underlying LlmInferenceSession after the flow completes.
     */
    fun generate(
        prompt: String,
        image: Bitmap? = null,
        config: GenerationConfig = GenerationConfig(),
    ): Flow<InferenceChunk>

    /** Releases native resources. Safe to call multiple times. */
    fun release()
}

data class GenerationConfig(
    val maxTokens: Int = 512,
    val temperature: Float = 0.7f,
    val topK: Int = 40,
)

sealed interface InferenceChunk {
    data class Token(val text: String) : InferenceChunk
    data class Done(val fullText: String) : InferenceChunk
    data class Error(val message: String) : InferenceChunk
}

sealed interface InferenceState {
    data object NotLoaded : InferenceState
    data object Loading : InferenceState
    data object Ready : InferenceState
    data class Failed(val message: String) : InferenceState
}
