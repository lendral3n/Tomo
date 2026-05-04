package com.tomosensei.feature.drill.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.tomosensei.core.designsystem.theme.SuccessMoss
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.WashiCream
import com.tomosensei.feature.drill.DrillViewModel
import com.tomosensei.feature.drill.model.DrillUiState
import com.tomosensei.feature.drill.rememberJapaneseTts
import kotlin.math.abs
import kotlinx.coroutines.launch

private const val SWIPE_DISMISS_THRESHOLD_PX = 220f
private const val SWIPE_FLY_OFF_PX = 1600f
private const val SWIPE_TILT_FACTOR = 0.025f

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
                AnimatedContent(
                    targetState = state,
                    transitionSpec = {
                        (fadeIn(tween(300)) + slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow,
                            ),
                            initialOffsetY = { it / 4 },
                        )).togetherWith(fadeOut(tween(180)))
                    },
                    contentKey = { s ->
                        when (s) {
                            DrillUiState.Loading -> "loading"
                            DrillUiState.Empty -> "empty"
                            is DrillUiState.Ready -> s.current.id
                        }
                    },
                    label = "drill-content",
                ) { s ->
                    when (s) {
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
            }
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
    val offsetY = remember(state.current.id) { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.current.id) {
        // Subtle settle when a brand new card lands.
        offsetY.snapTo(0f)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer {
                    translationY = offsetY.value
                    rotationZ = offsetY.value * SWIPE_TILT_FACTOR
                    val progress = (abs(offsetY.value) / SWIPE_FLY_OFF_PX).coerceIn(0f, 1f)
                    alpha = 1f - progress * 0.4f
                }
                .pointerInput(state.current.id) {
                    detectTapGestures(onTap = { onTap() })
                }
                .pointerInput(state.current.id) {
                    detectVerticalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                when {
                                    offsetY.value < -SWIPE_DISMISS_THRESHOLD_PX -> {
                                        offsetY.animateTo(
                                            targetValue = -SWIPE_FLY_OFF_PX,
                                            animationSpec = tween(280, easing = EaseOutCubic),
                                        )
                                        onAnswer(true)
                                    }
                                    offsetY.value > SWIPE_DISMISS_THRESHOLD_PX -> {
                                        offsetY.animateTo(
                                            targetValue = SWIPE_FLY_OFF_PX,
                                            animationSpec = tween(280, easing = EaseOutCubic),
                                        )
                                        onAnswer(false)
                                    }
                                    else -> offsetY.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessMediumLow,
                                        ),
                                    )
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offsetY.animateTo(0f, spring(stiffness = Spring.StiffnessMedium))
                            }
                        },
                        onVerticalDrag = { _, dragAmount ->
                            scope.launch { offsetY.snapTo(offsetY.value + dragAmount) }
                        },
                    )
                },
        ) {
            DrillCard(
                card = state.current,
                flipped = state.flipped,
                position = state.position,
                total = state.total,
                onSpeak = onSpeak,
                swipeProgress = (offsetY.value / SWIPE_DISMISS_THRESHOLD_PX).coerceIn(-1.5f, 1.5f),
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
    Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
        Text(
            text = "↑ tau",
            style = MaterialTheme.typography.labelLarge.copy(
                fontFamily = Manrope,
                fontWeight = FontWeight.W600,
                color = SuccessMoss,
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
