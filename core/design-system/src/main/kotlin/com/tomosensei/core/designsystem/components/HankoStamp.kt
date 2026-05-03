package com.tomosensei.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.material3.Text
import com.tomosensei.core.designsystem.theme.HankoRed

/**
 * Square red seal with a single Japanese character. Inspired by the
 * HankoStampAnim block in tomo-animations.jsx — slightly rotated,
 * imperfect edges via stroke variance.
 */
@Composable
fun HankoStamp(
    character: String,
    modifier: Modifier = Modifier,
    size: Dp = 64.dp,
    color: Color = HankoRed,
    rotationDegrees: Float = -6f,
) {
    Box(
        modifier = modifier
            .size(size)
            .rotate(rotationDegrees),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(size)) {
            val inset = this.size.width * 0.06f
            drawRect(
                color = color,
                topLeft = Offset(inset, inset),
                size = androidx.compose.ui.geometry.Size(
                    width = this.size.width - inset * 2f,
                    height = this.size.height - inset * 2f,
                ),
                style = Stroke(width = this.size.width * 0.08f),
            )
        }
        Text(
            text = character,
            color = color,
            style = TextStyle(
                fontWeight = FontWeight.W700,
                fontSize = (size.value * 0.55f).sp,
            ),
        )
    }
}
