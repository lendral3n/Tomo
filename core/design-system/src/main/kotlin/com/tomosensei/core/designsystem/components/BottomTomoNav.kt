package com.tomosensei.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.SumiLight
import com.tomosensei.core.designsystem.theme.WashiCream

/**
 * Five-tab nav with kanji glyphs as primary affordance, mirroring
 * BottomNav() in tomo-components.jsx. Items not yet wired up render
 * inactive but tappable — the click handler decides what to do.
 */
data class TomoNavItem(
    val id: String,
    val kanji: String,
    val label: String,
    val enabled: Boolean = true,
)

val DefaultTomoNav: List<TomoNavItem> = listOf(
    TomoNavItem("drill", "練", "Drill", enabled = true),
    TomoNavItem("chat", "会", "Chat", enabled = true),
    TomoNavItem("photo", "撮", "Foto", enabled = true),
    TomoNavItem("stats", "塾", "Stats", enabled = true),
    TomoNavItem("settings", "設", "Atur", enabled = true),
)

@Composable
fun BottomTomoNav(
    items: List<TomoNavItem>,
    activeId: String,
    onSelect: (TomoNavItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    val inactiveColor = SumiLight.copy(alpha = 0.55f)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(WashiCream.copy(alpha = 0f), WashiCream.copy(alpha = 0.96f), WashiCream),
                ),
            )
            .padding(top = 24.dp, bottom = 20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            items.forEach { item ->
                val isActive = item.id == activeId
                val color = when {
                    isActive -> HankoRed
                    !item.enabled -> inactiveColor.copy(alpha = 0.30f)
                    else -> inactiveColor
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                    modifier = Modifier.clickable { onSelect(item) },
                ) {
                    Text(
                        text = item.kanji,
                        style = TextStyle(
                            fontFamily = ShipporiMincho,
                            fontSize = 18.sp,
                            fontWeight = if (isActive) FontWeight.W700 else FontWeight.W400,
                            color = color,
                        ),
                    )
                    Text(
                        text = item.label.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontFamily = Manrope,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.W600,
                            letterSpacing = 1.5.sp,
                            color = color,
                        ),
                    )
                }
            }
        }
    }
}
