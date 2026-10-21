package com.example.jetpackcompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.jetpackcompose.ui.viewmodel.ThemeMode

private val PaperLightColorScheme = lightColorScheme(
    primary = PaperLightPrimary,
    onPrimary = Color.White,
    primaryContainer = PaperLightSurfaceVariant,
    onPrimaryContainer = PaperLightOnSurface,
    secondary = PaperLightSecondary,
    onSecondary = Color.White,
    background = PaperLightBackground,
    onBackground = PaperLightOnSurface,
    surface = PaperLightSurface,
    onSurface = PaperLightOnSurface,
    surfaceContainer = PaperLightSurfaceVariant,
    surfaceContainerLow = PaperLightSurface,
    surfaceContainerHigh = PaperLightSurfaceVariant,
    outline = Color(0xFFD0C8B8),
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFC62828)
)

private val PaperDarkColorScheme = darkColorScheme(
    primary = PaperDarkPrimary,
    onPrimary = Color(0xFF121212),
    primaryContainer = PaperDarkSurfaceVariant,
    onPrimaryContainer = PaperDarkOnSurface,
    secondary = PaperDarkSecondary,
    onSecondary = Color.Black,
    background = PaperDarkBackground,
    onBackground = PaperDarkOnSurface,
    surface = PaperDarkSurface,
    onSurface = PaperDarkOnSurface,
    surfaceContainer = PaperDarkSurfaceVariant,
    surfaceContainerLow = PaperDarkSurface,
    surfaceContainerHigh = PaperDarkSurfaceVariant,
    outline = Color(0xFF444444),
    errorContainer = Color(0xFF3E1212),
    onErrorContainer = Color(0xFFEF9A9A)
)

@Composable
fun JetpackComposeTheme(
    themeMode: ThemeMode = ThemeMode.EPAPER_LIGHT,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.EPAPER_LIGHT -> false
        ThemeMode.DARK -> true
    }

    val colorScheme = if (darkTheme) PaperDarkColorScheme else PaperLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
