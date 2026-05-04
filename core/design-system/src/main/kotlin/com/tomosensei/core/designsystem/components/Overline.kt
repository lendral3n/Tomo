package com.tomosensei.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomosensei.core.designsystem.theme.Manrope
import com.tomosensei.core.designsystem.theme.SumiMid

/**
 * Manrope 700 / 10sp / uppercase / wide tracking — section caption used
 * above content blocks and as in-card labels in the prototype.
 */
@Composable
fun Overline(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = SumiMid,
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = color,
        style = MaterialTheme.typography.labelMedium.copy(
            fontFamily = Manrope,
            fontWeight = FontWeight.W700,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
        ),
    )
}

@Composable
fun SectionHeader(
    label: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .width(2.dp)
                .height(18.dp)
                .background(MaterialTheme.colorScheme.primary),
        )
        Overline(text = label)
    }
}
