package com.tomosensei.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Cream paper card with deep aged-paper shadow. Mirrors `.tomo-card-base`
 * in tomo-components.jsx. Tone is light by default; pass [tone] = surface
 * variant for the slightly aged look.
 */
@Composable
fun WashiCard(
    modifier: Modifier = Modifier,
    tone: Color = MaterialTheme.colorScheme.surface,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = tone,
        tonalElevation = 0.dp,
        shadowElevation = 18.dp,
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(tone)
                .padding(24.dp),
        ) {
            content()
            // Grain overlay placeholder. Real grain texture (SVG noise from
            // tomo-components.jsx) goes in via a custom DrawModifier later.
            Box(Modifier.fillMaxSize().background(Color.Transparent))
        }
    }
}
