package com.example.guessup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guessup.model.AppSettings
import com.example.guessup.model.ThemeMode

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onVibrationChanged: (Boolean) -> Unit,
    onSoundChanged: (Boolean) -> Unit,
    onCountdownChanged: (Int) -> Unit,
    onThemeChanged: (ThemeMode) -> Unit
) {

    val background =
        Brush.verticalGradient(
            listOf(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.surfaceVariant
            )
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .safeDrawingPadding()
            .padding(20.dp),
        verticalArrangement =
            Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Definições",
            style =
                MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Personaliza a experiência de jogo",
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        /*
         * Feedback
         */
        SettingsSection(
            title = "Feedback"
        ) {

            SettingSwitchRow(
                title = "Vibração",
                subtitle =
                    "Feedback diferente para acerto e passe",
                checked =
                    settings.vibrationEnabled,
                onCheckedChange =
                    onVibrationChanged
            )

            SettingSwitchRow(
                title = "Sons",
                subtitle =
                    "Sons durante as respostas",
                checked =
                    settings.soundEnabled,
                onCheckedChange =
                    onSoundChanged
            )
        }

        /*
         * Countdown
         */
        SettingsSection(
            title = "Jogo"
        ) {

            Text(
                text = "Contagem inicial",
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "Tempo para colocares o telemóvel na testa",
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement =
                    Arrangement.Center,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                CountdownButton(
                    text = "‹",
                    enabled =
                        settings.countdownSeconds > 1,
                    onClick = {

                        onCountdownChanged(
                            settings.countdownSeconds - 1
                        )
                    }
                )

                Text(
                    text =
                        "${settings.countdownSeconds} s",
                    fontSize = 30.sp,
                    fontWeight =
                        FontWeight.ExtraBold,
                    modifier = Modifier.padding(
                        horizontal = 32.dp
                    )
                )

                CountdownButton(
                    text = "›",
                    enabled =
                        settings.countdownSeconds < 5,
                    onClick = {

                        onCountdownChanged(
                            settings.countdownSeconds + 1
                        )
                    }
                )
            }
        }

        /*
         * Tema
         */
        SettingsSection(
            title = "Aparência"
        ) {

            Text(
                text = "Tema",
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                ThemeOption(
                    text = "Sistema",
                    selected =
                        settings.themeMode ==
                                ThemeMode.SYSTEM,
                    modifier =
                        Modifier.weight(1f),
                    onClick = {

                        onThemeChanged(
                            ThemeMode.SYSTEM
                        )
                    }
                )

                ThemeOption(
                    text = "Claro",
                    selected =
                        settings.themeMode ==
                                ThemeMode.LIGHT,
                    modifier =
                        Modifier.weight(1f),
                    onClick = {

                        onThemeChanged(
                            ThemeMode.LIGHT
                        )
                    }
                )

                ThemeOption(
                    text = "Escuro",
                    selected =
                        settings.themeMode ==
                                ThemeMode.DARK,
                    modifier =
                        Modifier.weight(1f),
                    onClick = {

                        onThemeChanged(
                            ThemeMode.DARK
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(22.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surface
            )
    ) {

        Column(
            modifier =
                Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            Text(
                text = title.uppercase(),
                style =
                    MaterialTheme.typography.labelMedium,
                color =
                    MaterialTheme.colorScheme.primary,
                fontWeight =
                    FontWeight.Bold
            )

            content()
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange =
                onCheckedChange
        )
    }
}

@Composable
private fun CountdownButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {

    Surface(
        modifier =
            Modifier
                .size(52.dp)
                .clickable(
                    enabled = enabled,
                    onClick = onClick
                ),
        shape =
            RoundedCornerShape(16.dp),
        color =
            MaterialTheme.colorScheme
                .surfaceVariant
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = text,
                fontSize = 32.sp,
                color =
                    if (enabled) {
                        MaterialTheme
                            .colorScheme
                            .primary
                    } else {
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                            .copy(alpha = 0.3f)
                    }
            )
        }
    }
}

@Composable
private fun ThemeOption(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Surface(
        modifier =
            modifier.clickable(
                onClick = onClick
            ),
        shape =
            RoundedCornerShape(14.dp),
        color =
            if (selected) {

                MaterialTheme
                    .colorScheme
                    .primary
                    .copy(alpha = 0.16f)

            } else {

                MaterialTheme
                    .colorScheme
                    .surfaceVariant
            }
    ) {

        Text(
            text = text,
            modifier =
                Modifier.padding(
                    vertical = 12.dp
                ),
            fontWeight =
                if (selected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Normal
                },
            color =
                if (selected) {
                    MaterialTheme
                        .colorScheme
                        .primary
                } else {
                    MaterialTheme
                        .colorScheme
                        .onSurface
                },
            textAlign =
                androidx.compose.ui.text.style
                    .TextAlign.Center
        )
    }
}