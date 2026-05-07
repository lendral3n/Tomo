package com.tomosensei.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomosensei.core.designsystem.components.HankoSize
import com.tomosensei.core.designsystem.components.HankoStamp
import com.tomosensei.core.designsystem.theme.HankoRedFaded
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.WashiCream
import kotlinx.coroutines.delay

/**
 * One-shot splash gating the app shell. The hanko stamp scales up from
 * 0.6× and the wordmark fades in beneath it. After [HOLD_MS] we trigger
 * [onTimeout] — caller swaps to the real shell composable.
 */
@Composable
fun TomoSplash(onTimeout: () -> Unit) {
    val scale = remember { Animatable(0.6f) }
    val fade = remember { Animatable(0f) }
    val wordmarkFade = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1f, tween(620, easing = EaseOutCubic))
    }
    LaunchedEffect(Unit) {
        fade.animateTo(1f, tween(380))
        wordmarkFade.animateTo(1f, tween(420))
        delay(HOLD_MS)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        HankoRedFaded.copy(alpha = 0.10f),
                        WashiCream,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .graphicsLayer { alpha = fade.value }
                    .scale(scale.value),
                contentAlignment = Alignment.Center,
            ) {
                HankoStamp(text = "智", size = HankoSize.Lg, rotationDegrees = -8f)
            }
            Spacer(Modifier.height(8.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer { alpha = wordmarkFade.value },
            ) {
                Text(
                    text = "Tomo Sensei",
                    style = TextStyle(
                        fontFamily = ShipporiMincho,
                        fontWeight = FontWeight.W700,
                        fontSize = 24.sp,
                        color = SumiBlack,
                        letterSpacing = (-0.4).sp,
                    ),
                )
                Text(
                    text = "智センセイ",
                    style = TextStyle(
                        fontFamily = ShipporiMincho,
                        fontWeight = FontWeight.W500,
                        fontSize = 12.sp,
                        color = SumiLight,
                        letterSpacing = 1.6.sp,
                    ),
                )
            }
            Spacer(Modifier.height(40.dp))
            Text(
                text = "Friction sebagai fitur · ・",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 11.sp,
                    color = SumiLight.copy(alpha = 0.55f),
                    letterSpacing = 1.sp,
                ),
                modifier = Modifier.graphicsLayer { alpha = wordmarkFade.value },
            )
        }
    }
}

private const val HOLD_MS = 900L
