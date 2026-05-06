package com.tomosensei.feature.emergency.ui

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomosensei.core.designsystem.components.Overline
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiDark
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.core.designsystem.theme.WashiCream
import com.tomosensei.core.designsystem.theme.WashiCreamLight
import com.tomosensei.feature.emergency.EmergencyViewModel

@Composable
fun EmergencyPinScreen(
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EmergencyViewModel = hiltViewModel(),
) {
    LaunchedEffect(Unit) { viewModel.setSuccessHandler(onUnlock) }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WashiCream)
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Overline(text = "PIN Darurat")
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Masukkan PIN untuk bypass.",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 22.sp,
                color = SumiBlack,
            ),
        )
        Spacer(Modifier.height(24.dp))
        PinDots(filled = state.digits.length, total = EmergencyViewModel.PIN_LENGTH)
        if (state.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = state.error ?: "",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 12.sp,
                    color = HankoRed,
                ),
            )
        }
        Spacer(Modifier.height(40.dp))
        NumPad(
            onKey = viewModel::onKey,
            onBackspace = viewModel::onBackspace,
        )
        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clickable {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                    runCatching { context.startActivity(intent) }
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "📞", fontSize = 14.sp)
            Spacer(Modifier.size(6.dp))
            Text(
                text = "Panggilan darurat",
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 12.sp,
                    color = SumiLight,
                ),
            )
        }
    }
}

@Composable
private fun PinDots(filled: Int, total: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (i < filled) HankoRed else SumiLight.copy(alpha = 0.25f)),
            )
        }
    }
}

@Composable
private fun NumPad(onKey: (Int) -> Unit, onBackspace: () -> Unit) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "⌫"),
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { label ->
                    NumKey(
                        label = label,
                        onClick = {
                            when (label) {
                                "" -> Unit
                                "⌫" -> onBackspace()
                                else -> onKey(label.toInt())
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun NumKey(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(if (label.isBlank()) WashiCream else WashiCreamLight)
            .let { mod -> if (label.isBlank()) mod else mod.clickable(onClick = onClick) },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = if (label == "⌫") Manrope else ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 24.sp,
                color = if (label.isBlank()) SumiLight.copy(alpha = 0.2f) else SumiDark,
            ),
        )
    }
}
