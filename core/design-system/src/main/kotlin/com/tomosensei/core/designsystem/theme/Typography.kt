package com.tomosensei.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.tomosensei.core.designsystem.R

/**
 * Brand fonts come from Google Fonts via the Play Services font provider.
 * The first cold launch on a fresh device pulls the font in the background;
 * subsequent runs are cached. Until the font resolves we fall back to the
 * system Serif/SansSerif so layout stays stable.
 *
 * If you ever need a fully-offline build (e.g. for f-droid), drop TTFs into
 * core/design-system/src/main/res/font/ and replace these `Font(googleFont)`
 * entries with `Font(R.font.shippori_mincho_regular)` etc.
 */
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val shipporiMinchoFont = GoogleFont("Shippori Mincho")
private val zenKakuGothicFont = GoogleFont("Zen Kaku Gothic Antique")
private val manropeFont = GoogleFont("Manrope")

val ShipporiMincho: FontFamily = FontFamily(
    Font(googleFont = shipporiMinchoFont, fontProvider = provider, weight = FontWeight.W400),
    Font(googleFont = shipporiMinchoFont, fontProvider = provider, weight = FontWeight.W500),
    Font(googleFont = shipporiMinchoFont, fontProvider = provider, weight = FontWeight.W600),
    Font(googleFont = shipporiMinchoFont, fontProvider = provider, weight = FontWeight.W700),
    Font(googleFont = shipporiMinchoFont, fontProvider = provider, weight = FontWeight.W800),
)

val ZenKakuGothic: FontFamily = FontFamily(
    Font(googleFont = zenKakuGothicFont, fontProvider = provider, weight = FontWeight.W400),
    Font(googleFont = zenKakuGothicFont, fontProvider = provider, weight = FontWeight.W500),
    Font(googleFont = zenKakuGothicFont, fontProvider = provider, weight = FontWeight.W700),
    Font(googleFont = zenKakuGothicFont, fontProvider = provider, weight = FontWeight.W900),
)

val Manrope: FontFamily = FontFamily(
    Font(googleFont = manropeFont, fontProvider = provider, weight = FontWeight.W300),
    Font(googleFont = manropeFont, fontProvider = provider, weight = FontWeight.W400),
    Font(googleFont = manropeFont, fontProvider = provider, weight = FontWeight.W500),
    Font(googleFont = manropeFont, fontProvider = provider, weight = FontWeight.W600),
    Font(googleFont = manropeFont, fontProvider = provider, weight = FontWeight.W700),
    Font(googleFont = manropeFont, fontProvider = provider, weight = FontWeight.W800),
)

@Suppress("unused") // Reference for the Compose preview tooling that doesn't resolve GoogleFont
private val FallbackSerif = FontFamily.Serif

val TomoTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = ShipporiMincho,
        fontWeight = FontWeight.W600,
        fontStyle = FontStyle.Normal,
        fontSize = 56.sp,
        lineHeight = 64.sp,
    ),
    displayMedium = TextStyle(
        fontFamily = ShipporiMincho,
        fontWeight = FontWeight.W500,
        fontSize = 44.sp,
        lineHeight = 52.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = ShipporiMincho,
        fontWeight = FontWeight.W600,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = ShipporiMincho,
        fontWeight = FontWeight.W500,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.W600,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.W600,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.W400,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.W600,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = Manrope,
        fontWeight = FontWeight.W500,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)
