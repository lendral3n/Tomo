package com.tomosensei.feature.drill.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomosensei.core.designsystem.components.WashiBackground
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.JetBrainsMono
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.WashiCream
import com.tomosensei.feature.drill.DrillViewModel
import com.tomosensei.feature.drill.model.DrillUiState
import com.tomosensei.feature.drill.rememberJapaneseTts

private const val SWIPE_DISMISS_THRESHOLD_PX = 280f

@Composable
fun DrillScreen(
    modifier: Modifier = Modifier,
    viewModel: DrillViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tts = rememberJapaneseTts()

    WashiBackground(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                streak = (state as? DrillUiState.Ready)?.streakDays ?: 0,
                gatesPassed = (state as? DrillUiState.Ready)?.reviewedToday ?: 0,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                when (val s = state) {
                    DrillUiState.Loading -> CircularProgressIndicator(color = HankoRed)
                    DrillUiState.Empty -> EmptyContent()
                    is DrillUiState.Ready -> ReadyContent(
                        state = s,
                        onTap = { viewModel.onTapFlip() },
                        onAnswer = viewModel::onAnswer,
                        onSpeak = { tts.speak(s.current.front) },
                    )
                }
            }
            // Bottom space reserved for nav bar; actual nav lives in app shell.
            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun TopBar(streak: Int, gatesPassed: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 52.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "智センセイ",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W700,
                fontSize = 16.sp,
                color = SumiBlack,
                letterSpacing = 0.6.sp,
            ),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CounterChip(emoji = "🔥", value = streak)
            CounterChip(emoji = "🔓", value = gatesPassed)
        }
    }
}

@Composable
private fun CounterChip(emoji: String, value: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(text = emoji, fontSize = 15.sp)
        Text(
            text = value.toString(),
            style = TextStyle(
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.W600,
                fontSize = 13.sp,
                color = SumiDark,
            ),
        )
    }
}

@Composable
private fun ReadyContent(
    state: DrillUiState.Ready,
    onTap: () -> Unit,
    onAnswer: (Boolean) -> Unit,
    onSpeak: () -> Unit,
) {
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { translationY = dragOffsetY }
                .pointerInput(state.current.id) {
                    detectTapGestures(onTap = { onTap() })
                }
                .pointerInput(state.current.id) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            when {
                                dragOffsetY < -SWIPE_DISMISS_THRESHOLD_PX -> onAnswer(true)
                                dragOffsetY > SWIPE_DISMISS_THRESHOLD_PX -> onAnswer(false)
                            }
                            dragOffsetY = 0f
                        },
                        onDragCancel = { dragOffsetY = 0f },
                        onVerticalDrag = { _, dragAmount -> dragOffsetY += dragAmount },
                    )
                },
        ) {
            DrillCard(
                card = state.current,
                flipped = state.flipped,
                position = state.position,
                total = state.total,
                onSpeak = onSpeak,
            )
        }
        if (!state.flipped) {
            Spacer(Modifier.height(14.dp))
            SwipeHints()
        }
    }
}

@Composable
private fun SwipeHints() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        Text(
            text = "↑ tau",
            style = MaterialTheme.typography.labelLarge.copy(
                fontFamily = Manrope,
                fontWeight = FontWeight.W600,
                color = com.tomosensei.core.designsystem.theme.SuccessMoss,
                fontSize = 12.sp,
            ),
        )
        Text(
            text = "↓ lupa",
            style = MaterialTheme.typography.labelLarge.copy(
                fontFamily = Manrope,
                fontWeight = FontWeight.W600,
                color = HankoRed,
                fontSize = 12.sp,
            ),
        )
    }
}

@Composable
private fun EmptyContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(WashiCream),
        )
        Text(
            text = "Sudah selesai untuk sekarang",
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                color = SumiBlack,
            ),
        )
        Text(
            text = "Tidak ada kartu yang due. Balik lagi nanti.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Manrope,
                color = SumiLight,
            ),
        )
    }
}
