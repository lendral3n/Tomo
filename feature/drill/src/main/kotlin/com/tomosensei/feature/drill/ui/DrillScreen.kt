package com.tomosensei.feature.drill.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

    Box(modifier = modifier.fillMaxSize()) {
        when (val s = state) {
            DrillUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
            DrillUiState.Empty -> EmptyState(modifier = Modifier.align(Alignment.Center))
            is DrillUiState.Ready -> ReadyState(
                state = s,
                onTap = { viewModel.onTapFlip() },
                onSpeak = { tts.speak(s.current.front) },
                onAnswer = viewModel::onAnswer,
            )
        }
    }
}

@Composable
private fun ReadyState(
    state: DrillUiState.Ready,
    onTap: () -> Unit,
    onSpeak: () -> Unit,
    onAnswer: (Boolean) -> Unit,
) {
    var dragOffsetY by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Header(
            reviewedToday = state.reviewedToday,
            streakDays = state.streakDays,
            onSpeak = onSpeak,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
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
                        onVerticalDrag = { _, dragAmount ->
                            dragOffsetY += dragAmount
                        },
                    )
                },
        ) {
            DrillCard(
                card = state.current,
                flipped = state.flipped,
                modifier = Modifier.fillMaxSize(),
            )
        }
        Footer()
    }
}

@Composable
private fun Header(reviewedToday: Int, streakDays: Int, onSpeak: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "今日 · today",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "$reviewedToday review · 🔥 $streakDays",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        IconButton(onClick = onSpeak) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = "Putar pengucapan",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun Footer() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "↓ lupa",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.error,
        )
        Text(
            text = "↑ tau",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Sudah selesai untuk sekarang",
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Tidak ada kartu yang due. Balik lagi nanti.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
