package com.example.realestatemanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = DarkPrimary,
    primaryVariant = DarkPrimary,
    secondary = DarkSecondary,
    background= DarkBg,
    surface = DarkBg,
    onPrimary = DarkWriting,
    onSecondary = DarkWriting,
    onBackground = DarkWriting,
    onSurface = DarkWriting
)

private val LightColorPalette = lightColors(
    primary = Primary,
    primaryVariant = Primary,
    secondary = Secondary,
    background= bg,
    surface = bg,
    onPrimary = bg,
    onSecondary = Writing,
    onBackground = Writing,
    onSurface = Writing

)

@Composable
fun RealEstateManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}