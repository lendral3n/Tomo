package com.tomosensei.feature.gateconfig.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomosensei.core.designsystem.components.Overline
import com.tomosensei.core.designsystem.components.WashiBackground
import com.tomosensei.core.designsystem.components.WashiCard
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.feature.gateconfig.GateConfigViewModel
import com.tomosensei.feature.gateconfig.TriggerRowUi

private val intensityLabels = listOf(
    "Off",
    "Whisper",
    "Tap to pass",
    "Answer to pass",
    "Correct to pass",
    "Timeout (side-load)",
    "Hardcore lock",
)

@Composable
fun GateConfigScreen(
    modifier: Modifier = Modifier,
    viewModel: GateConfigViewModel = hiltViewModel(),
) {
    val rows by viewModel.rows.collectAsStateWithLifecycle()
    WashiBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 52.dp, start = 24.dp, end = 24.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = "Konfigurasi gate",
                style = TextStyle(
                    fontFamily = ShipporiMincho,
                    fontWeight = FontWeight.W600,
                    fontSize = 22.sp,
                    color = SumiBlack,
                ),
            )
            Text(
                text = "Slider 0–6 per trigger. 0 = matikan, 1–4 aman buat Play Store, 5–6 cuma side-load.",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 13.sp,
                    color = SumiMid,
                    lineHeight = 18.sp,
                ),
            )
            Spacer(Modifier.height(4.dp))
            rows.forEach { row ->
                TriggerRow(row, onIntensity = { viewModel.setIntensity(row.type, it) })
            }
        }
    }
}

@Composable
private fun TriggerRow(row: TriggerRowUi, onIntensity: (Int) -> Unit) {
    WashiCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Overline(text = row.label)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = intensityLabels[row.intensity],
                        style = TextStyle(
                            fontFamily = ShipporiMincho,
                            fontWeight = FontWeight.W600,
                            fontSize = 17.sp,
                            color = if (row.intensity == 0) SumiLight else SumiBlack,
                        ),
                    )
                }
                Text(
                    text = "Lv ${row.intensity}",
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontWeight = FontWeight.W700,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp,
                        color = if (row.intensity >= 5) HankoRed else SumiMid,
                    ),
                )
            }
            Slider(
                value = row.intensity.toFloat(),
                onValueChange = { onIntensity(it.toInt()) },
                valueRange = 0f..6f,
                steps = 5,
                colors = SliderDefaults.colors(
                    thumbColor = HankoRed,
                    activeTrackColor = HankoRed,
                    inactiveTrackColor = SumiLight.copy(alpha = 0.3f),
                ),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("0", style = TextStyle(fontFamily = Manrope, fontSize = 10.sp, color = SumiLight))
                Text("6", style = TextStyle(fontFamily = Manrope, fontSize = 10.sp, color = SumiLight))
            }
            Text(
                text = "Cooldown ${row.cooldownSeconds}s · Daily cap ${row.dailyCap}",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 11.sp,
                    color = SumiLight,
                ),
            )
        }
    }
}

@Suppress("unused")
private fun unusedRef(): SumiDark = SumiDark
