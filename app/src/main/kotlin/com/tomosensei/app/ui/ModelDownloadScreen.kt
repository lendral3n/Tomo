package com.tomosensei.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.tomosensei.core.ai.DownloadProgress
import com.tomosensei.core.ai.ModelDownloader
import com.tomosensei.core.designsystem.components.Overline
import com.tomosensei.core.designsystem.components.WashiBackground
import com.tomosensei.core.designsystem.components.WashiCard
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.JetBrainsMono
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SuccessMoss
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.core.designsystem.theme.WashiCreamLight
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ModelDownloadViewModel @Inject constructor(
    private val downloader: ModelDownloader,
) : ViewModel() {

    private val _state = MutableStateFlow<DownloadProgress?>(initialState())
    val state: StateFlow<DownloadProgress?> = _state.asStateFlow()

    val unmetered: Boolean get() = downloader.isOnUnmeteredNetwork()

    fun start(allowMetered: Boolean = false) {
        viewModelScope.launch {
            downloader.download(allowMetered).collect { _state.value = it }
        }
    }

    private fun initialState(): DownloadProgress? =
        if (downloader.isAvailable()) DownloadProgress.Done(downloader.targetFile()) else null
}

@Composable
fun ModelDownloadScreen(
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ModelDownloadViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var allowMetered by remember { mutableStateOf(false) }

    WashiBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 52.dp, start = 24.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Sensei AI Model",
                style = TextStyle(
                    fontFamily = ShipporiMincho,
                    fontWeight = FontWeight.W600,
                    fontSize = 22.sp,
                    color = SumiBlack,
                ),
            )
            Text(
                text = "Gemma 4 E4B-it · 3.6 GB · arm64-v8a",
                style = TextStyle(fontFamily = Manrope, fontSize = 12.sp, color = SumiMid),
            )
            WashiCard(modifier = Modifier.fillMaxWidth()) {
                StatusBlock(state = state, unmetered = viewModel.unmetered)
            }
            ProgressBlock(state = state)
            ActionRow(
                state = state,
                allowMetered = allowMetered,
                onToggleMetered = { allowMetered = !allowMetered },
                onStart = { scope.launch { viewModel.start(allowMetered) } },
                onBack = onBack,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Tip: model di-cache di filesDir. Restart app setelah selesai untuk swap dari stub ke real backend.",
                style = TextStyle(fontFamily = Manrope, fontSize = 11.sp, color = SumiLight, lineHeight = 16.sp),
            )
        }
    }
}

@Composable
private fun StatusBlock(state: DownloadProgress?, unmetered: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Overline(text = "Status")
        val (label, color) = when (state) {
            is DownloadProgress.Done -> "Tersedia" to SuccessMoss
            is DownloadProgress.InProgress -> "Mengunduh…" to HankoRed
            is DownloadProgress.Starting -> "Memulai…" to HankoRed
            is DownloadProgress.Failed -> "Gagal" to HankoRed
            null -> "Belum diunduh" to SumiMid
        }
        Text(
            text = label,
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 18.sp,
                color = color,
            ),
        )
        Text(
            text = if (unmetered) "Wi-Fi terdeteksi ✓" else "Saat ini di jaringan metered (mobile data)",
            style = TextStyle(fontFamily = Manrope, fontSize = 12.sp, color = SumiMid),
        )
    }
}

@Composable
private fun ProgressBlock(state: DownloadProgress?) {
    when (state) {
        is DownloadProgress.InProgress -> {
            WashiCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    LinearProgressIndicator(
                        progress = { state.fraction },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = HankoRed,
                        trackColor = SumiLight.copy(alpha = 0.2f),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "${(state.fraction * 100).toInt()}%",
                            style = TextStyle(
                                fontFamily = JetBrainsMono,
                                fontWeight = FontWeight.W600,
                                fontSize = 13.sp,
                                color = SumiDark,
                            ),
                        )
                        Text(
                            text = "${state.bytesRead / 1024 / 1024} / " +
                                "${state.totalBytes.coerceAtLeast(0L) / 1024 / 1024} MB",
                            style = TextStyle(
                                fontFamily = JetBrainsMono,
                                fontSize = 12.sp,
                                color = SumiMid,
                            ),
                        )
                    }
                }
            }
        }
        is DownloadProgress.Failed -> {
            WashiCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = state.message,
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontSize = 13.sp,
                        color = HankoRed,
                    ),
                )
            }
        }
        else -> Unit
    }
}

@Composable
private fun ActionRow(
    state: DownloadProgress?,
    allowMetered: Boolean,
    onToggleMetered: () -> Unit,
    onStart: () -> Unit,
    onBack: () -> Unit,
) {
    val canStart = state !is DownloadProgress.InProgress &&
        state !is DownloadProgress.Starting &&
        state !is DownloadProgress.Done
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.weight(1f),
            shape = CircleShape,
        ) {
            Text("Tutup", style = TextStyle(fontFamily = Manrope, fontSize = 13.sp, color = SumiDark))
        }
        Button(
            onClick = onStart,
            enabled = canStart,
            modifier = Modifier.weight(2f),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = HankoRed,
                contentColor = WashiCreamLight,
                disabledContainerColor = SumiLight.copy(alpha = 0.3f),
            ),
        ) {
            Text(
                text = when (state) {
                    is DownloadProgress.Done -> "Sudah ada"
                    is DownloadProgress.InProgress, is DownloadProgress.Starting -> "Mengunduh…"
                    else -> if (allowMetered) "Mulai (data)" else "Mulai (Wi-Fi)"
                },
                style = TextStyle(fontFamily = Manrope, fontWeight = FontWeight.W700, fontSize = 13.sp),
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (allowMetered) "Izinkan mobile data ✓" else "Tap untuk izinkan mobile data",
            style = TextStyle(
                fontFamily = Manrope,
                fontSize = 11.sp,
                color = if (allowMetered) HankoRed else SumiLight,
            ),
            modifier = Modifier.clickable(onClick = onToggleMetered),
        )
    }
}

