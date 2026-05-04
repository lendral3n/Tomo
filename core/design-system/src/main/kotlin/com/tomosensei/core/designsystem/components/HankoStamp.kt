package com.tomosensei.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tomosensei.core.designsystem.theme.HankoRed
import com.tomosensei.core.designsystem.theme.ShipporiMincho
import com.tomosensei.core.designsystem.theme.WashiCreamLight

enum class HankoSize(
    val fontSize: Int,
    val padX: Int,
    val padY: Int,
    val border: Float,
) {
    Sm(fontSize = 10, padX = 8, padY = 4, border = 1.5f),
    Md(fontSize = 13, padX = 11, padY = 7, border = 2f),
    Lg(fontSize = 17, padX = 14, padY = 10, border = 2f),
}

/**
 * Filled red seal — text in washi cream on hanko red background, slight
 * rotation, soft shadow. Mirrors HankoStamp in tomo-components.jsx.
 */
@Composable
fun HankoStamp(
    text: String,
    modifier: Modifier = Modifier,
    size: HankoSize = HankoSize.Md,
    color: Color = HankoRed,
    rotationDegrees: Float = -8f,
) {
    Box(
        modifier = modifier
            .rotate(rotationDegrees)
            .shadow(2.dp, RoundedCornerShape(3.dp))
            .background(color, RoundedCornerShape(3.dp))
            .border(size.border.dp, WashiCreamLight.copy(alpha = 0.35f), RoundedCornerShape(3.dp))
            .padding(horizontal = size.padX.dp, vertical = size.padY.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = ShipporiMincho,
                fontWeight = FontWeight.W800,
                fontSize = size.fontSize.sp,
                letterSpacing = (size.fontSize * 0.12f).sp,
                color = WashiCreamLight,
            ),
        )
    }
}
