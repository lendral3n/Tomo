package com.tomosensei.core.ai

import android.graphics.Bitmap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

/**
 * Placeholder implementation: yields canned tokens so :feature:chat can
 * exercise the full streaming path before the real LiteRT-LM bridge lands.
 *
 * Swap-in plan once Gemma 4 E4B is wired:
 *   1. Add a sibling class that holds a real `LlmInference` instance.
 *   2. Replace this binding in [com.tomosensei.core.ai.di.AiModule].
 *   3. No call-site changes required.
 */
@Singleton
class StubGemmaInferenceManager @Inject constructor() : GemmaInferenceManager {

    private val _state = MutableStateFlow<InferenceState>(InferenceState.Ready)
    override val state: Flow<InferenceState> = _state.asStateFlow()

    override suspend fun initialize() {
        // No-op: stub is always "ready". Real impl streams Loading -> Ready.
    }

    override fun generate(
        prompt: String,
        image: Bitmap?,
        config: GenerationConfig,
    ): Flow<InferenceChunk> = flow {
        val response = stubReply(prompt)
        val builder = StringBuilder()
        // 80ms per token roughly mirrors Gemma 4 E4B on flagship Snapdragon
        // hardware so the chat UI behaves the same as it will in v1.
        for (token in response.split(" ")) {
            val piece = if (builder.isEmpty()) token else " $token"
            builder.append(piece)
            emit(InferenceChunk.Token(piece))
            delay(80)
        }
        emit(InferenceChunk.Done(builder.toString()))
    }

    override fun release() = Unit

    private fun stubReply(prompt: String): String {
        val trimmed = prompt.trim().lowercase()
        return when {
            "tabe" in trimmed || "食べる" in prompt -> "食べる (たべる) = makan. Ichidan verb. Contoh: 私はりんごを食べます。"
            "halo" in trimmed || "hi" in trimmed -> "Halo! Aku Tomo Sensei. Mau drill kosakata atau tanya grammar?"
            "lupa" in trimmed -> "Tidak apa-apa lupa — itu sinyal kartu butuh diulang. FSRS akan jadwalkan lagi besok."
            else -> "Ini stub response. Gemma 4 E4B akan jawab di sini begitu LiteRT-LM ter-wire (spec §8)."
        }
    }
}
