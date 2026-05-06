package com.tomosensei.service.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomosensei.core.designsystem.components.HankoSize
import com.tomosensei.core.designsystem.components.HankoStamp
import com.tomosensei.core.designsystem.components.Overline
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.JetBrainsMono
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SuccessMoss
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.core.designsystem.theme.WashiCream
import com.tomosensei.core.designsystem.theme.WashiCreamLight
import com.tomosensei.core.designsystem.theme.YamiDeep
import com.tomosensei.service.gateengine.GateRequest

private const val MAX_ATTEMPTS = 3

/**
 * Lv 4 — Correct to Pass. Multi-choice (4 options); the correct answer
 * lives at a stable index but the visual order is shuffled per-card so
 * the user can't pattern-match. Up to MAX_ATTEMPTS wrong picks before
 * the gate gives up (logged as failed but still dismisses — Lv 5/6 are
 * the ones with timeout punishments).
 */
@Composable
fun CorrectToPassGate(
    request: GateRequest,
    distractors: List<String>,
    onResult: (passed: Boolean, attempts: Int) -> Unit,
) {
    val correct = request.cardMeaning
    // Stable shuffled order per card.
    val options = remember(request.cardId) {
        (distractors.take(3) + correct).shuffled()
    }
    var attempts by remember { mutableIntStateOf(0) }
    var locked by remember { mutableStateOf(false) }
    var picked by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(YamiDeep.copy(alpha = 0.92f), YamiDeep.copy(alpha = 0.96f)),
                ),
            )
            .padding(horizontal = 24.dp, vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Overline(text = "Pilih arti", color = WashiCream.copy(alpha = 0.55f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(WashiCreamLight)
                    .padding(24.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = request.cardFront,
                            style = TextStyle(
                                fontFamily = ShipporiMincho,
                                fontWeight = FontWeight.W600,
                                fontSize = 44.sp,
                                color = SumiBlack,
                            ),
                        )
                        HankoStamp(text = "N5", size = HankoSize.Sm)
                    }
                    if (request.cardReading.isNotBlank()) {
                        Text(
                            text = request.cardReading,
                            style = TextStyle(
                                fontFamily = Manrope,
                                fontSize = 14.sp,
                                color = SumiMid,
                            ),
                        )
                    }
                }
            }
            options.forEachIndexed { idx, option ->
                ChoiceRow(
                    letter = ('A' + idx).toString(),
                    text = option,
                    state = when {
                        locked && option == correct -> ChoiceState.Correct
                        picked == option && option != correct -> ChoiceState.Wrong
                        else -> ChoiceState.Idle
                    },
                    enabled = !locked,
                    onClick = {
                        if (locked) return@ChoiceRow
                        attempts += 1
                        picked = option
                        if (option == correct) {
                            locked = true
                            onResult(true, attempts)
                        } else if (attempts >= MAX_ATTEMPTS) {
                            locked = true
                            onResult(false, attempts)
                        }
                    },
                )
            }
            Text(
                text = "${attempts.coerceAtMost(MAX_ATTEMPTS)} / $MAX_ATTEMPTS attempts",
                style = TextStyle(
                    fontFamily = JetBrainsMono,
                    fontSize = 11.sp,
                    color = WashiCream.copy(alpha = 0.45f),
                ),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}

private enum class ChoiceState { Idle, Correct, Wrong }

@Composable
private fun ChoiceRow(
    letter: String,
    text: String,
    state: ChoiceState,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val (bg, border) = when (state) {
        ChoiceState.Idle -> WashiCream.copy(alpha = 0.10f) to WashiCream.copy(alpha = 0.18f)
        ChoiceState.Correct -> SuccessMoss.copy(alpha = 0.30f) to SuccessMoss
        ChoiceState.Wrong -> HankoRed.copy(alpha = 0.30f) to HankoRed
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .let { if (enabled) it.clickable(onClick = onClick) else it }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "$letter.",
            style = TextStyle(
                fontFamily = JetBrainsMono,
                fontWeight = FontWeight.W600,
                fontSize = 12.sp,
                color = HankoRed,
            ),
        )
        Text(
            text = text,
            style = TextStyle(
                fontFamily = Manrope,
                fontWeight = FontWeight.W500,
                fontSize = 15.sp,
                color = if (state == ChoiceState.Idle) WashiCreamLight else SumiDark,
            ),
        )
        Spacer(Modifier.weight(1f))
        @Suppress("UNUSED_VARIABLE")
        val ignored: Color = border // keep border swatch in mind for future stroke ring
    }
}
