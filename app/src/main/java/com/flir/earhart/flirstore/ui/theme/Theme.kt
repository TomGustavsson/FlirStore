package com.flir.earhart.flirstore.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.flir.earhart.flirstore.R

private val DarkColorPalette = darkColors(
    primary = Color(0xff0a0a14),
    primaryVariant = Color(0xff22222b),
    secondary = Color(0xff6db0f8),
    secondaryVariant = Color(0xff2589f4),
    background = Color(0xff22222b),
    surface = Color(0xff18181b),
    error = Color(0xffe93b3b),
    onPrimary = Color(0xfffafafa),
    onSecondary = Color(0xffa1a1a3),
    onSurface = Color(0xfffafafa),
    onBackground = Color(0xfffafafa),
    onError = Color(0xffffffff)
)

private val LightColorPalette = lightColors(
    primary = Color(0xfffafafa),
    primaryVariant = Color(0xffffffff),
    secondary = Color(0xff6db0f8),
    secondaryVariant = Color(0xff2589f4),
    background = Color(0xffe7e7e6),
    surface = Color(0xfffafafa),
    error = Color(0xffec4242),
    onPrimary = Color(0xff0a0a14),
    onSecondary = Color(0xff858587),
    onSurface = Color(0xff0a0a14),
    onBackground = Color(0xff0a0a14),
    onError = Color(0xfffafafa)
)

val flirTypography = Typography(
    defaultFontFamily = FontFamily(Font(R.font.fui_industry_medium)),
    h1 = TextStyle(
        fontFamily = FontFamily(Font(R.font.fui_industry_medium)),
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        letterSpacing = 0.sp
    ),
    h2 = TextStyle(
        fontFamily = FontFamily(Font(R.font.fui_industry_medium)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.sp,
        lineHeight = 36.sp
    ),
    button = TextStyle(
        fontFamily = FontFamily(Font(R.font.fui_industry_bold)),
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.sp,
        lineHeight = 24.sp
    ),
)

@Composable
fun FlirStoreTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = flirTypography,
        content = content
    )
}