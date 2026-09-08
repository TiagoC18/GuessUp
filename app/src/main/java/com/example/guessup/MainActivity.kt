package com.example.guessup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.guessup.data.CategoryRepository
import com.example.guessup.data.SettingsRepository
import com.example.guessup.model.ThemeMode
import com.example.guessup.navigation.GuessUpNavigation
import com.example.guessup.ui.theme.GuessUpTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        CategoryRepository.init(
            applicationContext
        )

        SettingsRepository.init(
            applicationContext
        )

        enableEdgeToEdge()

        setContent {

            val settings by
            SettingsRepository
                .settings
                .collectAsState()

            val systemDark =
                isSystemInDarkTheme()

            val useDarkTheme =
                when (
                    settings.themeMode
                ) {

                    ThemeMode.SYSTEM ->
                        systemDark

                    ThemeMode.LIGHT ->
                        false

                    ThemeMode.DARK ->
                        true
                }

            GuessUpTheme(
                darkTheme = useDarkTheme
            ) {

                GuessUpNavigation()
            }
        }
    }
}