package com.tomosensei.feature.drill.ui

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomosensei.core.designsystem.components.HankoSize
import com.tomosensei.core.designsystem.components.HankoStamp
import com.tomosensei.core.designsystem.components.Overline
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
import com.tomosensei.core.designsystem.theme.WashiCreamDark
import com.tomosensei.core.designsystem.theme.WashiCreamLight
import com.tomosensei.core.designsystem.theme.ZenKakuGothic
import com.tomosensei.feature.drill.model.DrillCardUi

@Composable
fun DrillCard(
    card: DrillCardUi,
    flipped: Boolean,
    position: Int,
    total: Int,
    onSpeak: () -> Unit,
    modifier: Modifier = Modifier,
    swipeProgress: Float = 0f,
) {
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 520, easing = EaseInOutCubic),
        label = "drill-card-flip",
    )
    // Subtle scale dip mid-flip — prevents the card from feeling like a flat
    // sticker rotating, gives it a touch of depth.
    val scale by animateFloatAsState(
        targetValue = if (rotation in 30f..150f) 0.95f else 1f,
        animationSpec = tween(durationMillis = 260, easing = EaseInOutCubic),
        label = "drill-card-scale",
    )
    Box(
        modifier = modifier.graphicsLayer {
            rotationY = rotation
            scaleX = scale
            scaleY = scale
            cameraDistance = 14f * density
        },
    ) {
        if (rotation <= 90f) {
            DrillCardFront(
                card = card,
                position = position,
                total = total,
                onSpeak = onSpeak,
                swipeProgress = swipeProgress,
            )
        } else {
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                DrillCardBack(card = card, position = position, total = total)
            }
        }
    }
}

@Composable
private fun DrillCardFront(
    card: DrillCardUi,
    position: Int,
    total: Int,
    onSpeak: () -> Unit,
    swipeProgress: Float,
) {
    val tint = when {
        swipeProgress < -0.2f -> SuccessMoss.copy(
            alpha = (-swipeProgress * 0.10f).coerceIn(0f, 0.12f),
        )
        swipeProgress > 0.2f -> HankoRed.copy(
            alpha = (swipeProgress * 0.10f).coerceIn(0f, 0.12f),
        )
        else -> WashiCreamLight
    }
    WashiCard(modifier = Modifier.fillMaxWidth(), tone = tint) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.Top,
            ) {
                Overline(text = card.type.ifBlank { "vocab" })
                HankoStamp(text = "N5", size = HankoSize.Sm)
            }
            // Hero kanji
            Text(
                text = card.front,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = ShipporiMincho,
                    fontWeight = FontWeight.W600,
                    fontSize = 80.sp,
                    color = SumiBlack,
                    letterSpacing = (-1.6).sp,
                ),
            )
            Text(
                text = "🔊  dengar",
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.W500,
                    color = SumiMid,
                ),
            )
            CardFooter(
                left = { Overline(text = "tap untuk arti", color = SumiLight.copy(alpha = 0.65f)) },
                right = { CounterText(position, total) },
            )
        }
    }
}

@Composable
private fun DrillCardBack(card: DrillCardUi, position: Int, total: Int) {
    WashiCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.Top,
            ) {
                Overline(text = card.type.ifBlank { "vocab" })
                HankoStamp(text = "N5", size = HankoSize.Sm)
            }
            Text(
                text = card.front,
                style = TextStyle(
                    fontFamily = ShipporiMincho,
                    fontWeight = FontWeight.W600,
                    fontSize = 44.sp,
                    color = SumiBlack,
                    letterSpacing = (-0.4).sp,
                ),
            )
            if (card.reading.isNotBlank()) {
                Text(
                    text = card.reading,
                    style = TextStyle(
                        fontFamily = ZenKakuGothic,
                        fontWeight = FontWeight.W400,
                        fontSize = 20.sp,
                        color = SumiMid,
                        letterSpacing = 1.2.sp,
                    ),
                )
            }
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(HankoRed),
            )
            Text(
                text = card.meaning,
                style = TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.W500,
                    fontSize = 22.sp,
                    color = SumiDark,
                ),
            )
            // Optional grammar/note row reusing the first example's translation as flavor.
            card.examples.firstOrNull()?.let { ex ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(WashiCreamDark.copy(alpha = 0.55f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(16.dp)
                            .background(HankoRed),
                    )
                    Column {
                        Text(
                            text = ex.jp,
                            style = TextStyle(
                                fontFamily = ShipporiMincho,
                                fontWeight = FontWeight.W500,
                                fontSize = 14.sp,
                                color = SumiDark,
                            ),
                        )
                        if (ex.translation.isNotBlank()) {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = ex.translation,
                                style = TextStyle(
                                    fontFamily = Manrope,
                                    fontWeight = FontWeight.W400,
                                    fontSize = 12.sp,
                                    color = SumiMid,
                                    lineHeight = 16.sp,
                                ),
                            )
                        }
                    }
                }
            }
            CardFooter(
                left = {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
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
                            text = "·",
                            style = TextStyle(color = SumiLight.copy(alpha = 0.27f), fontSize = 12.sp),
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
                },
                right = { CounterText(position, total) },
            )
        }
    }
}

@Composable
private fun CardFooter(
    left: @Composable () -> Unit,
    right: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(SumiLight.copy(alpha = 0.13f)),
    )
    Spacer(Modifier.height(2.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        left()
        right()
    }
}

@Composable
private fun CounterText(position: Int, total: Int) {
    Text(
        text = "%02d / %02d".format(position, total),
        style = TextStyle(
            fontFamily = JetBrainsMono,
            fontWeight = FontWeight.W400,
            fontSize = 11.sp,
            color = SumiLight.copy(alpha = 0.55f),
        ),
    )
}
