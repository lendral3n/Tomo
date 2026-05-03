package com.tomosensei.feature.drill.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tomosensei.core.designsystem.components.HankoStamp
import com.tomosensei.core.designsystem.components.WashiCard
import com.tomosensei.feature.drill.model.DrillCardUi

@Composable
fun DrillCard(
    card: DrillCardUi,
    flipped: Boolean,
    modifier: Modifier = Modifier,
) {
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        label = "drill-card-flip",
    )

    Box(
        modifier = modifier.graphicsLayer {
            rotationY = rotation
            cameraDistance = 12f * density
        },
    ) {
        if (rotation <= 90f) {
            DrillCardFront(card)
        } else {
            Box(
                modifier = Modifier.graphicsLayer { rotationY = 180f },
            ) {
                DrillCardBack(card)
            }
        }
    }
}

@Composable
private fun DrillCardFront(card: DrillCardUi) {
    WashiCard(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            HankoStamp(
                character = "友",
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = card.front,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.W600,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            )
            Text(
                text = "tap untuk lihat arti  •  swipe ↑ tau  ↓ lupa",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }
}

@Composable
private fun DrillCardBack(card: DrillCardUi) {
    WashiCard(
        modifier = Modifier.fillMaxSize(),
        tone = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = card.reading,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = card.meaning,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W700,
            )
            Spacer(Modifier.height(4.dp))
            card.examples.take(2).forEach { ex ->
                Column {
                    Text(
                        text = ex.jp,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = ex.reading,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = ex.translation,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}
