package com.example.guessup.data

import android.content.Context
import com.example.guessup.model.AppSettings
import com.example.guessup.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SettingsRepository {

    private const val PREFS_NAME =
        "guessup_settings"

    private const val KEY_VIBRATION =
        "vibration_enabled"

    private const val KEY_SOUND =
        "sound_enabled"

    private const val KEY_COUNTDOWN =
        "countdown_seconds"

    private const val KEY_THEME =
        "theme_mode"

    private lateinit var context: Context

    private var initialized = false

    private val _settings =
        MutableStateFlow(
            AppSettings()
        )

    val settings: StateFlow<AppSettings> =
        _settings.asStateFlow()

    fun init(context: Context) {

        if (initialized) {
            return
        }

        this.context =
            context.applicationContext

        initialized = true

        val preferences =
            this.context.getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )

        val themeName =
            preferences.getString(
                KEY_THEME,
                ThemeMode.SYSTEM.name
            )

        val theme =
            runCatching {
                ThemeMode.valueOf(
                    themeName
                        ?: ThemeMode.SYSTEM.name
                )
            }.getOrDefault(
                ThemeMode.SYSTEM
            )

        _settings.value =
            AppSettings(
                vibrationEnabled =
                    preferences.getBoolean(
                        KEY_VIBRATION,
                        true
                    ),

                soundEnabled =
                    preferences.getBoolean(
                        KEY_SOUND,
                        true
                    ),

                countdownSeconds =
                    preferences.getInt(
                        KEY_COUNTDOWN,
                        3
                    ),

                themeMode = theme
            )
    }

    fun setVibrationEnabled(
        enabled: Boolean
    ) {

        update(
            _settings.value.copy(
                vibrationEnabled = enabled
            )
        )
    }

    fun setSoundEnabled(
        enabled: Boolean
    ) {

        update(
            _settings.value.copy(
                soundEnabled = enabled
            )
        )
    }

    fun setCountdownSeconds(
        seconds: Int
    ) {

        update(
            _settings.value.copy(
                countdownSeconds =
                    seconds.coerceIn(
                        1,
                        5
                    )
            )
        )
    }

    fun setThemeMode(
        themeMode: ThemeMode
    ) {

        update(
            _settings.value.copy(
                themeMode = themeMode
            )
        )
    }

    private fun update(
        newSettings: AppSettings
    ) {

        _settings.value =
            newSettings

        save()
    }

    private fun save() {

        if (!initialized) {
            return
        }

        context
            .getSharedPreferences(
                PREFS_NAME,
                Context.MODE_PRIVATE
            )
            .edit()
            .putBoolean(
                KEY_VIBRATION,
                _settings.value.vibrationEnabled
            )
            .putBoolean(
                KEY_SOUND,
                _settings.value.soundEnabled
            )
            .putInt(
                KEY_COUNTDOWN,
                _settings.value.countdownSeconds
            )
            .putString(
                KEY_THEME,
                _settings.value.themeMode.name
            )
            .apply()
    }
}