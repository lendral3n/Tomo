package com.tomosensei.core.ai

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Streams the Gemma 4 E4B-it bundle (~3.6 GB) into [Context.filesDir].
 *
 * Pipeline:
 *  1. Wi-Fi gate — refuse on metered networks unless caller opts in.
 *  2. HEAD-style range to learn total size, then GET to a temp file.
 *  3. Buffered read in 8 KB chunks, emitting [DownloadProgress.InProgress]
 *     every CHUNK_REPORT_BYTES so the UI's progress bar moves but the
 *     flow doesn't drown the consumer.
 *  4. SHA-256 verification against [MODEL_CHECKSUM] (when populated) —
 *     mismatch deletes the file and emits [DownloadProgress.Failed].
 *  5. Atomic rename temp → target on success.
 */
@Singleton
class ModelDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
    }

    fun targetFile(): File = File(context.filesDir, MODEL_FILENAME)

    fun isAvailable(): Boolean = targetFile().exists() && targetFile().length() > 0L

    fun isOnUnmeteredNetwork(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
    }

    fun download(allowMetered: Boolean = false): Flow<DownloadProgress> = flow {
        if (!allowMetered && !isOnUnmeteredNetwork()) {
            emit(DownloadProgress.Failed("Wi-Fi diperlukan untuk download model 3.6 GB"))
            return@flow
        }
        if (isAvailable()) {
            emit(DownloadProgress.Done(targetFile()))
            return@flow
        }
        emit(DownloadProgress.Starting)

        val tempFile = File(context.filesDir, "$MODEL_FILENAME.part")
        runCatching { tempFile.delete() }

        val request = Request.Builder().url(MODEL_URL).build()
        val response: Response = try {
            httpClient.newCall(request).execute()
        } catch (e: IOException) {
            emit(DownloadProgress.Failed("Koneksi gagal: ${e.message}"))
            return@flow
        }

        response.use { resp ->
            if (!resp.isSuccessful) {
                emit(DownloadProgress.Failed("HTTP ${resp.code}: ${resp.message}"))
                return@flow
            }
            val body = resp.body ?: run {
                emit(DownloadProgress.Failed("Empty response body"))
                return@flow
            }
            val total = body.contentLength().takeIf { it > 0 } ?: -1L
            val digest = MessageDigest.getInstance("SHA-256")
            var bytesRead = 0L
            var sinceLastReport = 0L

            try {
                body.byteStream().use { input ->
                    tempFile.outputStream().use { output ->
                        val buf = ByteArray(BUFFER_SIZE)
                        while (true) {
                            val n = input.read(buf)
                            if (n == -1) break
                            output.write(buf, 0, n)
                            digest.update(buf, 0, n)
                            bytesRead += n
                            sinceLastReport += n
                            if (sinceLastReport >= CHUNK_REPORT_BYTES) {
                                emit(DownloadProgress.InProgress(bytesRead, total))
                                sinceLastReport = 0L
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                runCatching { tempFile.delete() }
                emit(DownloadProgress.Failed("Download terputus: ${e.message}"))
                return@flow
            }

            if (MODEL_CHECKSUM.isNotBlank()) {
                val actual = digest.digest().joinToString("") { "%02x".format(it) }
                if (actual != MODEL_CHECKSUM) {
                    runCatching { tempFile.delete() }
                    emit(DownloadProgress.Failed("Checksum mismatch — file rusak"))
                    return@flow
                }
            }

            val ok = tempFile.renameTo(targetFile())
            if (!ok) {
                emit(DownloadProgress.Failed("Gagal commit file ke filesDir"))
                return@flow
            }
            emit(DownloadProgress.Done(targetFile()))
        }
    }.flowOn(Dispatchers.IO)

    companion object {
        const val MODEL_FILENAME = "gemma-4-e4b-it.litertlm"
        const val MODEL_URL =
            "https://huggingface.co/google/gemma-4-e4b-it-litertlm/resolve/main/model.litertlm"

        // Populate when the canonical hosted file is finalized; until then
        // we skip checksum verification so devs can sideload arbitrary
        // builds for testing.
        const val MODEL_CHECKSUM = ""

        private const val BUFFER_SIZE = 8 * 1024
        private const val CHUNK_REPORT_BYTES = 1L * 1024 * 1024 // 1 MB
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
