package com.example.guessup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guessup.model.Category
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.draw.scale
import kotlinx.coroutines.delay

private const val DEFAULT_DURATION_SECONDS = 60
private const val MIN_DURATION_SECONDS = 30
private const val MAX_DURATION_SECONDS = 120
private const val DURATION_STEP_SECONDS = 15

@Composable
fun DurationScreen(
    category: Category,
    onStartGame: (Int) -> Unit
) {

    var durationSeconds by remember {
        mutableIntStateOf(DEFAULT_DURATION_SECONDS)
    }

    var durationPulse by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(durationSeconds) {

        durationPulse = true

        delay(120)

        durationPulse = false
    }

    val durationScale by animateFloatAsState(
        targetValue =
            if (durationPulse) {
                1.10f
            } else {
                1f
            },
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = 600f
        ),
        label = "DurationPulse"
    )

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .safeDrawingPadding()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(42.dp)
            )

            /*
             * Categoria
             */
            Surface(
                modifier = Modifier.size(82.dp),
                shape = RoundedCornerShape(26.dp),
                color = MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.14f
                )
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = category.emoji,
                        fontSize = 44.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = category.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Escolhe a duração da ronda",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 5.dp)
            )

            Spacer(
                modifier = Modifier.height(70.dp)
            )

            /*
             * Selector de duração
             */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(26.dp)
            ) {

                DurationArrowButton(
                    text = "‹",
                    enabled =
                        durationSeconds > MIN_DURATION_SECONDS,
                    onClick = {
                        durationSeconds -=
                            DURATION_STEP_SECONDS
                    }
                )

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = formatDuration(
                            durationSeconds
                        ),
                        fontSize = 60.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp,
                        modifier = Modifier.scale(
                            durationScale
                        )
                    )

                    Text(
                        text = "minutos",
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }

                DurationArrowButton(
                    text = "›",
                    enabled =
                        durationSeconds < MAX_DURATION_SECONDS,
                    onClick = {
                        durationSeconds +=
                            DURATION_STEP_SECONDS
                    }
                )
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Surface(
                shape = RoundedCornerShape(50.dp),
                color =
                    MaterialTheme.colorScheme.surface
            ) {

                Text(
                    text = "30 seg  •  passos de 15 seg  •  máximo 2 min",
                    style = MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                )
            }

            Spacer(
                modifier = Modifier.weight(1f)
            )

            /*
             * Começar
             */
            Button(
                onClick = {
                    onStartGame(durationSeconds)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        MaterialTheme.colorScheme.primary
                )
            ) {

                Text(
                    text = "▶   COMEÇAR",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )
        }
    }
}

@Composable
private fun DurationArrowButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(62.dp),
        shape = CircleShape,
        contentPadding =
            androidx.compose.foundation.layout.PaddingValues(
                0.dp
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {

        Text(
            text = text,
            fontSize = 40.sp,
            fontWeight = FontWeight.Light,
            color = if (enabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

private fun formatDuration(
    totalSeconds: Int
): String {

    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    return "%02d:%02d".format(
        minutes,
        seconds
    )
}