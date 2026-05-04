package com.tomosensei.core.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Streams the Gemma 4 E4B-it bundle (~3.6 GB) into [Context.filesDir].
 *
 * Real download wired in Phase 4.1 — for now this scaffolds the contract
 * the onboarding screen will call into. The Wi-Fi guard and target file
 * path are already correct; only the OkHttp + checksum body is stubbed.
 */
@Singleton
class ModelDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun targetFile(): File = File(context.filesDir, MODEL_FILENAME)

    fun isAvailable(): Boolean = targetFile().exists() && targetFile().length() > 0L

    fun isOnUnmeteredNetwork(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
    }

    /**
     * Emits [DownloadProgress] for each network read. The real implementation
     * will use OkHttp with progress interceptor and verify SHA-256 against
     * [MODEL_CHECKSUM]; this stub completes immediately so the chat UI can
     * fall back to [StubGemmaInferenceManager] without blocking onboarding.
     */
    fun download(allowMetered: Boolean = false): Flow<DownloadProgress> = flow {
        if (!allowMetered && !isOnUnmeteredNetwork()) {
            emit(DownloadProgress.Failed("Wi-Fi diperlukan untuk download model 3.6 GB"))
            return@flow
        }
        emit(DownloadProgress.Starting)
        // TODO(phase-4.1): real OkHttp streaming + checksum + retries
        emit(DownloadProgress.Failed("ModelDownloader belum diimplementasi — pakai stub manager dulu"))
    }

    companion object {
        const val MODEL_FILENAME = "gemma-4-e4b-it.litertlm"
        const val MODEL_URL = "https://huggingface.co/google/gemma-4-e4b-it-litertlm/resolve/main/model.litertlm"
        const val MODEL_CHECKSUM = "" // populate when host URL is finalized
    }
}

sealed interface DownloadProgress {
    data object Starting : DownloadProgress
    data class InProgress(val bytesRead: Long, val totalBytes: Long) : DownloadProgress {
        val fraction: Float get() =
            if (totalBytes <= 0L) 0f else (bytesRead.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
    }
    data class Done(val file: File) : DownloadProgress
    data class Failed(val message: String) : DownloadProgress
}
