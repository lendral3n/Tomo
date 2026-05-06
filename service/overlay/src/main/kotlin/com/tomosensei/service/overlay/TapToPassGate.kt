package com.tomosensei.service.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.core.designsystem.theme.WashiCreamLight
import com.tomosensei.core.designsystem.theme.YamiDeep
import com.tomosensei.core.designsystem.theme.ZenKakuGothic
import com.tomosensei.service.gateengine.GateRequest
import kotlinx.coroutines.delay

/**
 * Lv 2 — Tap to Pass. Card sits dim for [HOLD_SECONDS], then a 'Lewati'
 * button enables. The countdown nudges the user to actually look at the
 * card before swiping it away.
 */
@Composable
fun TapToPassGate(request: GateRequest, onPass: () -> Unit) {
    var remaining by remember { mutableIntStateOf(HOLD_SECONDS) }
    LaunchedEffect(Unit) {
        while (remaining > 0) {
            delay(1_000)
            remaining -= 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(YamiDeep.copy(alpha = 0.85f), YamiDeep.copy(alpha = 0.92f)),
                ),
            )
            .padding(horizontal = 24.dp, vertical = 48.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(WashiCreamLight)
                    .padding(28.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    HankoStamp(text = "N5", size = HankoSize.Sm)
                    Text(
                        text = request.cardFront,
                        style = TextStyle(
                            fontFamily = ShipporiMincho,
                            fontWeight = FontWeight.W600,
                            fontSize = 56.sp,
                            color = SumiBlack,
                        ),
                    )
                    if (request.cardReading.isNotBlank()) {
                        Text(
                            text = request.cardReading,
                            style = TextStyle(
                                fontFamily = ZenKakuGothic,
                                fontSize = 16.sp,
                                color = SumiMid,
                            ),
                        )
                    }
                    Text(
                        text = request.cardMeaning,
                        style = TextStyle(
                            fontFamily = Manrope,
                            fontWeight = FontWeight.W500,
                            fontSize = 18.sp,
                            color = SumiBlack,
                        ),
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onPass,
                enabled = remaining == 0,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HankoRed,
                    contentColor = WashiCreamLight,
                    disabledContainerColor = WashiCreamLight.copy(alpha = 0.18f),
                    disabledContentColor = WashiCreamLight.copy(alpha = 0.5f),
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 32.dp, vertical = 14.dp,
                ),
            ) {
                Text(
                    text = if (remaining == 0) "Lewati" else "tunggu ${remaining}s",
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontWeight = FontWeight.W700,
                        fontSize = 14.sp,
                    ),
                )
            }
        }
    }
}

private const val HOLD_SECONDS = 3
