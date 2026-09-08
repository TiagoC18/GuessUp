package com.example.guessup.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GuessPurpleLight,
    secondary = GuessMint,
    tertiary = GuessYellow,

    background = GuessDarkBackground,
    surface = GuessDarkSurface,
    surfaceVariant = GuessDarkSurfaceVariant,

    onPrimary = GuessDarkText,
    onBackground = GuessDarkText,
    onSurface = GuessDarkText,
    onSurfaceVariant = GuessDarkSecondaryText,

    error = GuessWrong
)

private val LightColorScheme = lightColorScheme(
    primary = GuessPurple,
    secondary = GuessMint,
    tertiary = GuessYellow,

    background = GuessLightBackground,
    surface = GuessLightSurface,
    surfaceVariant = GuessLightSurfaceVariant,

    onPrimary = GuessDarkText,
    onBackground = GuessLightText,
    onSurface = GuessLightText,
    onSurfaceVariant = GuessLightSecondaryText,

    error = GuessWrong
)

@Composable
fun GuessUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme =
        if (darkTheme) {
            DarkColorScheme
        } else {
            LightColorScheme
        }

    val view = LocalView.current

    if (!view.isInEditMode) {

        val window =
            (view.context as Activity).window

        WindowCompat.getInsetsController(
            window,
            view
        ).isAppearanceLightStatusBars =
            !darkTheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}