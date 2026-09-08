package com.example.guessup.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.guessup.R

@Composable
fun HomeScreen(
    onPlayClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    var showLogo by remember {
        mutableStateOf(false)
    }

    var showTitle by remember {
        mutableStateOf(false)
    }

    var showButton by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        showLogo = true

        delay(100)

        showTitle = true

        delay(120)

        showButton = true
    }

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

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp)
                .size(52.dp)
                .clickable {
                    onSettingsClick()
                },
            shape = CircleShape,
            color =
                MaterialTheme.colorScheme.surface
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "⚙",
                    fontSize = 25.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            /*
             * Logo
             */
            AnimatedVisibility(
                visible = showLogo,
                enter =
                    fadeIn(
                        animationSpec = tween(350)
                    ) +
                            scaleIn(
                                initialScale = 0.75f,
                                animationSpec = tween(350)
                            )
            ) {

                Surface(
                    shape = RoundedCornerShape(32.dp),
                    color = MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.14f
                    )
                ) {

                    Image(
                        painter = painterResource(
                            id = R.drawable.guessup_logo
                        ),
                        contentDescription = "GuessUp",
                        modifier = Modifier
                            .size(150.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            /*
             * Nome da app
             */
            AnimatedVisibility(
                visible = showTitle,
                enter =
                    fadeIn(
                        animationSpec = tween(350)
                    ) +
                            slideInVertically(
                                initialOffsetY = {
                                    it / 4
                                },
                                animationSpec = tween(350)
                            )
            ) {

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "GUESS UP",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        letterSpacing = 2.sp
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Text(
                        text = "Adivinha. Inclina. Ri.",
                        style = MaterialTheme.typography.titleMedium,
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(50.dp)
            )

            /*
             * Botão principal
             */
            AnimatedVisibility(
                visible = showButton,
                enter =
                    fadeIn(
                        animationSpec = tween(350)
                    ) +
                            slideInVertically(
                                initialOffsetY = {
                                    it / 2
                                },
                                animationSpec = tween(350)
                            )
            ) {

                Button(
                    onClick = onPlayClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Text(
                        text = "▶   JOGAR",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            /*
             * Pequena informação
             */
            Text(
                text = "Sem anúncios  •  As tuas categorias",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}