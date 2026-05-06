package com.tomosensei.service.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomosensei.core.designsystem.components.HankoSize
import com.tomosensei.core.designsystem.components.HankoStamp
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.core.designsystem.theme.WashiCreamLight
import com.tomosensei.core.designsystem.theme.ZenKakuGothic
import com.tomosensei.service.gateengine.GateRequest

/**
 * Lv 1 — Whisper. Small bubble in the bottom-right corner over whatever
 * the user is doing. Tap-to-dismiss only; no answer commitment. Logs as
 * skipped if the user just taps it away.
 */
@Composable
fun WhisperGate(request: GateRequest, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.Transparent),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .width(180.dp)
                .shadow(8.dp, RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(WashiCreamLight)
                .clickable(onClick = onDismiss)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            HankoStamp(text = "N5", size = HankoSize.Sm)
            Text(
                text = request.cardFront,
                style = TextStyle(
                    fontFamily = ShipporiMincho,
                    fontWeight = FontWeight.W700,
                    fontSize = 28.sp,
                    color = SumiBlack,
                ),
            )
            if (request.cardReading.isNotBlank()) {
                Text(
                    text = request.cardReading,
                    style = TextStyle(
                        fontFamily = ZenKakuGothic,
                        fontSize = 12.sp,
                        color = SumiMid,
                    ),
                )
            }
            Text(
                text = request.cardMeaning,
                style = TextStyle(
                    fontFamily = Manrope,
                    fontWeight = FontWeight.W500,
                    fontSize = 13.sp,
                    color = SumiDark,
                ),
            )
            Text(
                text = "tap untuk dismiss →",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 10.sp,
                    color = SumiLight,
                    letterSpacing = 0.6.sp,
                ),
            )
        }
    }
}
