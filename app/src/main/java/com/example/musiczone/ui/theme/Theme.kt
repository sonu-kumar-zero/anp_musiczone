package com.example.musiczone.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = MusicZonePurple,
    secondary = MusicZonePurpleSoft,
    tertiary = MusicZonePurpleBright,

    background = MusicZoneBackground,
    surface = MusicZoneSurface,
    surfaceVariant = MusicZoneElevated,

    onPrimary = MusicZoneOnPrimary,
    onSecondary = MusicZoneOnPrimary,
    onTertiary = MusicZoneOnPrimary,

    onBackground = MusicZoneTextPrimary,
    onSurface = MusicZoneTextPrimary,
    onSurfaceVariant = MusicZoneTextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = MusicZonePurpleDark,
    secondary = MusicZonePurple,
    tertiary = MusicZonePurpleDark
)

@Composable
fun MusicZoneTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
//    val colorScheme = if (darkTheme) {
//        DarkColorScheme
//    } else {
//        LightColorScheme
//    }

    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}