package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TarkovColorScheme = darkColorScheme(
    primary = TacticalAmber,
    onPrimary = Color.Black,
    primaryContainer = TacticalAmberDark,
    onPrimaryContainer = TacticalAmberLight,
    secondary = TacticalEmerald,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    tertiary = TacticalCyan,
    onTertiary = Color.Black,
    background = TarkovBackground,
    onBackground = TextPrimary,
    surface = TarkovSurface,
    onSurface = TextPrimary,
    surfaceVariant = TarkovSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = TarkovSurfaceBorder,
    error = TacticalHazardRed,
    onError = Color.White
)

@Composable
fun TarkovRadarTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = TarkovColorScheme,
        typography = Typography,
        content = content
    )
}
