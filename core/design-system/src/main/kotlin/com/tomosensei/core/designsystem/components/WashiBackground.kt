package com.tomosensei.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.tomosensei.core.designsystem.theme.HankoRedFaded
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.WashiCream

/**
 * Two soft radial bloom layers over the washi cream base, ported from
 * `WashiBg` in tomo-components.jsx — top-left warm cream glow, bottom-right
 * tan glow. Sits behind every screen.
 */
@Composable
fun WashiBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WashiCream),
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val warmCream = HankoRedFaded.copy(alpha = 0.10f)
            val tan = SumiLight.copy(alpha = 0.10f)
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(warmCream, warmCream.copy(alpha = 0f)),
                    center = Offset(size.width * 0.20f, size.height * 0.30f),
                    radius = size.minDimension * 0.85f,
                ),
            )
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(tan, tan.copy(alpha = 0f)),
                    center = Offset(size.width * 0.80f, size.height * 0.70f),
                    radius = size.minDimension * 0.85f,
                ),
            )
        }
        content()
    }
}
