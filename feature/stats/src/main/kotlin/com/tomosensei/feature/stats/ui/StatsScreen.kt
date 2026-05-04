package com.tomosensei.feature.stats.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tomosensei.core.designsystem.components.Overline
import com.tomosensei.core.designsystem.components.SectionHeader
import com.tomosensei.core.designsystem.components.WashiBackground
import com.tomosensei.core.designsystem.components.WashiCard
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.JetBrainsMono
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SumiBlack
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.SumiMid
import com.tomosensei.feature.stats.StatsUiState
import com.tomosensei.feature.stats.StatsViewModel

@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    WashiBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 52.dp, bottom = 100.dp),
        ) {
            HeaderRow()
            Spacer(Modifier.height(12.dp))
            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                StreakHero(state = state)
                StatsGrid(state = state)
                Column {
                    SectionHeader(label = "Vocab Sering Lupa")
                    Spacer(Modifier.height(12.dp))
                    Placeholder(text = "Belum ada data — mulai drill dulu.")
                }
            }
        }
    }
}

@Composable
private fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Progress",
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W600,
                fontSize = 16.sp,
                color = SumiBlack,
            ),
        )
        Text(
            text = "⋯",
            style = TextStyle(fontSize = 16.sp, color = SumiLight),
        )
    }
}

@Composable
private fun StreakHero(state: StatsUiState) {
    WashiCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Overline(text = "Streak")
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = state.streak.toString(),
                    style = TextStyle(
                        fontFamily = ShipporiMincho,
                        fontWeight = FontWeight.W700,
                        fontSize = 64.sp,
                        color = SumiBlack,
                        letterSpacing = (-2.5).sp,
                    ),
                )
                Text(
                    text = "hari",
                    modifier = Modifier.padding(bottom = 12.dp),
                    style = TextStyle(
                        fontFamily = Manrope,
                        fontSize = 13.sp,
                        color = SumiMid,
                    ),
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "🔥",
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontSize = 32.sp,
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (state.streak > 0) "Tetap konsisten — sehari saja terlewat reset jadi 0."
                       else "Mulai streak pertamamu hari ini.",
                style = TextStyle(
                    fontFamily = ShipporiMincho,
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.sp,
                    color = SumiMid,
                ),
            )
        }
    }
}

@Composable
private fun StatsGrid(state: StatsUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                label = "Total kartu",
                value = state.cardsLearned.toString(),
                sub = "dipelajari",
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "Review",
                value = state.totalReviews.toString(),
                sub = "total seumur",
                modifier = Modifier.weight(1f),
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatTile(
                label = "Gate dilewati",
                value = state.gatesPassed.toString(),
                sub = "× di-trigger",
                modifier = Modifier.weight(1f),
            )
            StatTile(
                label = "Target harian",
                value = state.dailyGoal.toString(),
                sub = "kartu / hari",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatTile(
    label: String,
    value: String,
    sub: String,
    modifier: Modifier = Modifier,
) {
    WashiCard(modifier = modifier.fillMaxWidth()) {
        Column {
            Overline(text = label)
            Spacer(Modifier.height(6.dp))
            Text(
                text = value,
                style = TextStyle(
                    fontFamily = ShipporiMincho,
                    fontWeight = FontWeight.W700,
                    fontSize = 26.sp,
                    color = SumiBlack,
                ),
            )
            Text(
                text = sub,
                style = TextStyle(
                    fontFamily = Manrope,
                    fontSize = 11.sp,
                    color = SumiLight,
                ),
            )
        }
    }
}

@Composable
private fun Placeholder(text: String) {
    WashiCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = Manrope,
                color = SumiMid,
            ),
        )
    }
}

@Composable
@Suppress("unused")
private fun MonoText(text: String) {
    // Reserved for future heatmap/weak-word numerics.
    androidx.compose.material3.Text(
        text = text,
        style = TextStyle(
            fontFamily = JetBrainsMono,
            fontSize = 11.sp,
            color = HankoRed,
        ),
    )
}
