package com.tomosensei.service.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomosensei.core.designsystem.components.HankoSize
import com.tomosensei.core.designsystem.components.HankoStamp
import com.tomosensei.core.designsystem.components.Overline
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SuccessMoss
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.core.designsystem.theme.WashiCream
import com.tomosensei.core.designsystem.theme.WashiCreamLight
import com.tomosensei.core.designsystem.theme.YamiDeep
import com.tomosensei.core.designsystem.theme.ZenKakuGothic
import com.tomosensei.service.gateengine.GateRequest

/**
 * Level 3 gate (Lv 3 — "Answer to Pass"). User sees a card and must declare
 * whether they remembered the meaning. Either choice dismisses the gate;
 * the FSRS scheduler picks up the rating asynchronously.
 *
 * This composable is rendered inside an overlay window; it draws its own
 * full-screen scrim because the overlay does not auto-dim system UI.
 */
@Composable
fun AnswerToPassGate(
    request: GateRequest,
    onPassed: (knew: Boolean) -> Unit,
    onSkip: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        YamiDeep.copy(alpha = 0.92f),
                        YamiDeep.copy(alpha = 0.96f),
                    ),
                ),
            )
            .padding(horizontal = 24.dp, vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Trigger label
            Text(
                text = triggerLabel(request).uppercase(),
                style = TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.W700,
                    fontSize = 10.sp,
                    letterSpacing = 3.sp,
                    color = WashiCream.copy(alpha = 0.55f),
                ),
            )
            // Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(WashiCreamLight)
                    .padding(28.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                    ) {
                        Overline(text = "vocab")
                        HankoStamp(text = "N5", size = HankoSize.Sm)
                    }
                    Text(
                        text = request.cardFront,
                        style = TextStyle(
                            fontFamily = ShipporiMincho,
                            fontWeight = FontWeight.W600,
                            fontSize = 56.sp,
                            color = SumiBlack,
                            letterSpacing = (-0.8).sp,
                        ),
                    )
                    if (request.cardReading.isNotBlank()) {
                        Text(
                            text = request.cardReading,
                            style = TextStyle(
                                fontFamily = ZenKakuGothic,
                                fontSize = 16.sp,
                                color = SumiMid,
                                letterSpacing = 1.sp,
                            ),
                        )
                    }
                    Box(
                        Modifier
                            .width(40.dp)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(HankoRed),
                    )
                    Text(
                        text = request.cardMeaning,
                        style = TextStyle(
                            fontFamily = Manrope,
                            fontWeight = FontWeight.W500,
                            fontSize = 18.sp,
                            color = SumiDark,
                        ),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedButton(
                    onClick = { onPassed(false) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = HankoRed,
                    ),
                ) {
                    Text(
                        text = "✗ Lupa",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = Manrope,
                            fontWeight = FontWeight.W600,
                        ),
                    )
                }
                Button(
                    onClick = { onPassed(true) },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessMoss,
                        contentColor = WashiCreamLight,
                    ),
                ) {
                    Text(
                        text = "✓ Tau",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = Manrope,
                            fontWeight = FontWeight.W700,
                        ),
                    )
                }
            }
            // Skip
            Text(
                text = "Lewati",
                modifier = Modifier
                    .clip(CircleShape)
                    .padding(8.dp),
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 12.sp,
                    color = SumiLight.copy(alpha = 0.55f),
                ),
            )
        }
    }
}

private fun triggerLabel(request: GateRequest): String = when (request.trigger.key) {
    "unlock" -> "Unlock HP Gate"
    "idle" -> "Idle Reminder"
    "app_launch" -> "App Distraction Gate"
    "manual" -> "Manual Drill"
    else -> "Tomo Gate"
}
