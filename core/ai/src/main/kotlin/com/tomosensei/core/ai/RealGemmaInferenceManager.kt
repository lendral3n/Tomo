package com.tomosensei.core.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInferenceSession
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * Real Gemma 4 E4B-it backend, talking to MediaPipe Tasks GenAI.
 *
 * Lifecycle:
 *  - Stays in [InferenceState.NotLoaded] until [initialize] succeeds.
 *  - [generate] yields tokens streamed by `generateResponseAsync`.
 *  - Sessions are short-lived: a fresh [LlmInferenceSession] is created
 *    per [generate] call and closed when the flow terminates. The
 *    underlying [LlmInference] is reused across calls.
 *
 * NOTE: API surface tracks google-ai-edge/gallery as of mid-2026. If a
 * library update changes the signatures, swap them here without
 * touching call sites — [GemmaInferenceManager] is the stable boundary.
 */
@Singleton
class RealGemmaInferenceManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val modelDownloader: ModelDownloader,
) : GemmaInferenceManager {

    private val _state = MutableStateFlow<InferenceState>(InferenceState.NotLoaded)
    override val state: Flow<InferenceState> = _state.asStateFlow()

    private var llmInference: LlmInference? = null
    private val initLock = Mutex()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override suspend fun initialize() = initLock.withLock {
        if (llmInference != null) return@withLock
        if (!modelDownloader.isAvailable()) {
            _state.value = InferenceState.Failed("Model belum di-download.")
            return@withLock
        }
        _state.value = InferenceState.Loading
        runCatching {
            withContext(Dispatchers.IO) {
                val options = LlmInference.LlmInferenceOptions.builder()
                    .setModelPath(modelDownloader.targetFile().absolutePath)
                    .setMaxTokens(MAX_TOKENS)
                    .setMaxNumImages(1)
                    .build()
                llmInference = LlmInference.createFromOptions(context, options)
            }
        }.onSuccess {
            _state.value = InferenceState.Ready
        }.onFailure { t ->
            Log.e(TAG, "LlmInference init failed", t)
            _state.value = InferenceState.Failed(t.message ?: "Init gagal")
        }
    }

    override fun generate(
        prompt: String,
        image: Bitmap?,
        config: GenerationConfig,
    ): Flow<InferenceChunk> = callbackFlow {
        val engine = llmInference
        if (engine == null) {
            trySend(InferenceChunk.Error("Model belum siap. Init dulu."))
            close()
            return@callbackFlow
        }

        val session = runCatching {
            LlmInferenceSession.createFromOptions(
                engine,
                LlmInferenceSession.LlmInferenceSessionOptions.builder()
                    .setTopK(config.topK)
                    .setTemperature(config.temperature)
                    .build(),
            )
        }.getOrElse { t ->
            Log.e(TAG, "Session create failed", t)
            trySend(InferenceChunk.Error(t.message ?: "Session gagal"))
            close()
            return@callbackFlow
        }

        runCatching {
            session.addQueryChunk(prompt)
            image?.let { bitmap ->
                session.addImage(BitmapImageBuilder(bitmap).build())
            }
            val accumulated = StringBuilder()
            session.generateResponseAsync { partial, done ->
                if (partial != null) {
                    accumulated.append(partial)
                    trySend(InferenceChunk.Token(partial))
                }
                if (done) {
                    trySend(InferenceChunk.Done(accumulated.toString()))
                    close()
                }
            }
        }.onFailure { t ->
            Log.e(TAG, "Generate failed", t)
            trySend(InferenceChunk.Error(t.message ?: "Inference gagal"))
            close(t)
        }

        awaitClose {
            scope.launch { runCatching { session.close() } }
        }
    }

    override fun release() {
        runCatching { llmInference?.close() }
        llmInference = null
        _state.value = InferenceState.NotLoaded
    }

    companion object {
        private const val TAG = "RealGemma"
        private const val MAX_TOKENS = 2048
    }
}
