package com.paizi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PrimaryColor = Color(0xFF6200EE)
private val PrimaryVariant = Color(0xFF3700B3)
private val SecondaryColor = Color(0xFF03DAC6)
private val SecondaryVariant = Color(0xFF018786)
private val BackgroundColor = Color(0xFFFFFFFF)
private val SurfaceColor = Color(0xFFFFFFFF)
private val ErrorColor = Color(0xFFB00020)
private val OnPrimary = Color.White
private val OnSecondary = Color.Black
private val OnBackground = Color.Black
private val OnSurface = Color.Black
private val OnError = Color.White

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,
    secondary = SecondaryColor,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = OnSecondary,
    tertiary = Color(0xFF7030A0),
    onTertiary = Color.White,
    error = ErrorColor,
    onError = OnError,
    background = BackgroundColor,
    onBackground = OnBackground,
    surface = SurfaceColor,
    onSurface = OnSurface
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,
    secondary = SecondaryColor,
    onSecondary = Color.Black,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = Color.Black,
    tertiary = Color(0xFFB39DDB),
    onTertiary = Color.Black,
    error = ErrorColor,
    onError = OnError,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White
)

@Composable
fun PAIZITheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
