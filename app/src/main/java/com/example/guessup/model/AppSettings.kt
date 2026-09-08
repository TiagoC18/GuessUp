package com.example.guessup.model

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class AppSettings(
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val countdownSeconds: Int = 3,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)