package com.tomosensei.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = HankoRed,
    onPrimary = WashiCream,
    primaryContainer = HankoRedFaded,
    onPrimaryContainer = SumiBlack,
    secondary = SumiLight,
    onSecondary = WashiCream,
    secondaryContainer = WashiAged,
    onSecondaryContainer = SumiBlack,
    tertiary = KinGold,
    onTertiary = SumiBlack,
    background = WashiCream,
    onBackground = SumiBlack,
    surface = WashiCreamLight,
    onSurface = SumiBlack,
    surfaceVariant = WashiCreamDark,
    onSurfaceVariant = SumiMid,
    error = HankoRedDeep,
    onError = WashiCream,
    outline = SumiMid,
    outlineVariant = SumiLight,
)

private val DarkColors = darkColorScheme(
    primary = HankoRed,
    onPrimary = YamiDeep,
    primaryContainer = HankoRedDeep,
    onPrimaryContainer = WashiCream,
    secondary = SumiLight,
    onSecondary = YamiDeep,
    secondaryContainer = YamiMid,
    onSecondaryContainer = WashiCream,
    tertiary = KinGold,
    onTertiary = YamiDeep,
    background = YamiDeep,
    onBackground = WashiCream,
    surface = YamiMid,
    onSurface = WashiCream,
    surfaceVariant = YamiSoft,
    onSurfaceVariant = WashiCreamDark,
    error = HankoRedFaded,
    onError = YamiDeep,
    outline = SumiLight,
    outlineVariant = SumiMid,
)

@Composable
fun TomoSenseiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = TomoTypography,
        content = content,
    )
}
