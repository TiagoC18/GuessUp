package com.example.guessup.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.guessup.model.RoundResult
import kotlinx.coroutines.delay

@Composable
fun ResultsScreen(
    result: RoundResult,
    onPlayAgain: () -> Unit,
    onBackToCategories: () -> Unit
) {

    val totalAnswers =
        result.cards.size

    val successPercentage =
        if (totalAnswers > 0) {

            (
                    result.correctCount.toFloat() /
                            totalAnswers.toFloat() *
                            100
                    ).toInt()

        } else {
            0
        }

    /*
     * Só começamos a animação algum tempo
     * depois de entrar no ResultsScreen.
     *
     * Isto dá tempo para:
     *
     * landscape
     *    ↓
     * portrait
     *    ↓
     * ResultsScreen estabilizar
     *    ↓
     * começar animação
     */
    var animateStats by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        delay(550L)

        animateStats = true
    }

    /*
     * Números animados.
     *
     * Duração ligeiramente maior para
     * conseguirmos realmente ver a contagem.
     */
    val animatedCorrectCount by
    animateIntAsState(
        targetValue =
            if (animateStats) {
                result.correctCount
            } else {
                0
            },
        animationSpec = tween(
            durationMillis = 900
        ),
        label = "CorrectCount"
    )

    val animatedSkippedCount by
    animateIntAsState(
        targetValue =
            if (animateStats) {
                result.skippedCount
            } else {
                0
            },
        animationSpec = tween(
            durationMillis = 900
        ),
        label = "SkippedCount"
    )

    val animatedTotalCount by
    animateIntAsState(
        targetValue =
            if (animateStats) {
                totalAnswers
            } else {
                0
            },
        animationSpec = tween(
            durationMillis = 900
        ),
        label = "TotalCount"
    )

    val animatedPercentage by
    animateIntAsState(
        targetValue =
            if (animateStats) {
                successPercentage
            } else {
                0
            },
        animationSpec = tween(
            durationMillis = 900
        ),
        label = "SuccessPercentage"
    )

    val backgroundGradient =
        Brush.verticalGradient(
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
            modifier = Modifier.fillMaxSize()
        ) {

            /*
             * IMPORTANTE:
             *
             * Deixámos de usar LazyColumn.
             *
             * Assim TODAS as respostas são criadas
             * imediatamente e todas as animações
             * começam logo ao abrir este ecrã.
             */
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 24.dp,
                        bottom = 20.dp
                    ),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                /*
                 * ==========================
                 * CABEÇALHO
                 * ==========================
                 */
                Column(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "🏆",
                        fontSize = 54.sp
                    )

                    Text(
                        text = "Fim da ronda!",
                        style =
                            MaterialTheme
                                .typography
                                .headlineLarge,
                        fontWeight =
                            FontWeight.ExtraBold,
                        modifier =
                            Modifier.padding(
                                top = 8.dp
                            )
                    )

                    Text(
                        text =
                            result.categoryName,
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,
                        modifier =
                            Modifier.padding(
                                top = 3.dp
                            )
                    )

                    Text(
                        text =
                            "$animatedPercentage% de acertos",
                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,
                        fontWeight =
                            FontWeight.Bold,
                        modifier =
                            Modifier.padding(
                                top = 7.dp
                            )
                    )
                }

                /*
                 * ==========================
                 * ESTATÍSTICAS
                 * ==========================
                 */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 16.dp,
                            bottom = 12.dp
                        ),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    ResultStatCard(
                        value =
                            animatedCorrectCount
                                .toString(),
                        label = "Acertos",
                        emoji = "✓",
                        isPositive = true,
                        modifier =
                            Modifier.weight(1f)
                    )

                    ResultStatCard(
                        value =
                            animatedSkippedCount
                                .toString(),
                        label = "Passadas",
                        emoji = "×",
                        isPositive = false,
                        modifier =
                            Modifier.weight(1f)
                    )

                    ResultStatCard(
                        value =
                            animatedTotalCount
                                .toString(),
                        label = "Total",
                        emoji = "•",
                        isPositive = null,
                        modifier =
                            Modifier.weight(1f)
                    )
                }

                /*
                 * ==========================
                 * RESPOSTAS
                 * ==========================
                 */
                Text(
                    text = "Respostas",
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    fontWeight =
                        FontWeight.Bold,
                    modifier = Modifier.padding(
                        top = 4.dp,
                        bottom = 2.dp
                    )
                )

                /*
                 * Como NÃO estamos numa LazyColumn,
                 * todas estas funções são compostas
                 * imediatamente.
                 *
                 * Logo:
                 *
                 * resposta 1
                 *    ↓
                 * resposta 2
                 *    ↓
                 * resposta 3
                 *    ↓
                 * ...
                 *
                 * mesmo que algumas estejam fora
                 * do ecrã neste momento.
                 */
                result.cards.forEachIndexed { index, card ->

                    AnimatedResultCard(
                        index = index,
                        isCorrect =
                            card.isCorrect,
                        word =
                            card.word
                    )
                }
            }

            /*
             * ==========================
             * AÇÕES FIXAS
             * ==========================
             */
            Surface(
                color =
                    MaterialTheme
                        .colorScheme
                        .surface,
                tonalElevation = 6.dp
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            16.dp
                        )
                ) {

                    Button(
                        onClick =
                            onPlayAgain,
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                18.dp
                            )
                    ) {

                        Text(
                            text =
                                "↻   JOGAR NOVAMENTE",
                            fontWeight =
                                FontWeight.Bold,
                            modifier =
                                Modifier.padding(
                                    vertical = 6.dp
                                )
                        )
                    }

                    OutlinedButton(
                        onClick =
                            onBackToCategories,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 8.dp
                                ),
                        shape =
                            RoundedCornerShape(
                                18.dp
                            )
                    ) {

                        Text(
                            text = "Categorias"
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ResultStatCard(
    value: String,
    label: String,
    emoji: String,
    isPositive: Boolean?,
    modifier: Modifier = Modifier
) {

    val accentColor =
        when (isPositive) {

            true ->
                MaterialTheme
                    .colorScheme
                    .secondary

            false ->
                MaterialTheme
                    .colorScheme
                    .error

            null ->
                MaterialTheme
                    .colorScheme
                    .primary
        }

    Card(
        modifier = modifier,
        shape =
            RoundedCornerShape(
                20.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 16.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = emoji,
                color =
                    accentColor,
                fontSize =
                    22.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = value,
                fontSize =
                    30.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                color =
                    accentColor,
                modifier =
                    Modifier.padding(
                        top = 3.dp
                    )
            )

            Text(
                text = label,
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                textAlign =
                    TextAlign.Center
            )
        }
    }
}


@Composable
private fun AnimatedResultCard(
    index: Int,
    isCorrect: Boolean,
    word: String
) {

    var visible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {

        /*
         * Começamos as respostas ligeiramente
         * depois das estatísticas começarem.
         *
         * Stats:
         * ~550 ms
         *
         * Primeira resposta:
         * ~700 ms
         */
        delay(
            700L +
                    index.coerceAtMost(12) * 55L
        )

        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter =
            fadeIn(
                animationSpec =
                    tween(
                        durationMillis =
                            280
                    )
            ) +
                    scaleIn(
                        initialScale =
                            0.94f,
                        animationSpec =
                            tween(
                                durationMillis =
                                    280
                            )
                    )
    ) {

        Card(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                RoundedCornerShape(
                    16.dp
                ),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        if (isCorrect) {

                            MaterialTheme
                                .colorScheme
                                .secondary
                                .copy(
                                    alpha = 0.12f
                                )

                        } else {

                            MaterialTheme
                                .colorScheme
                                .error
                                .copy(
                                    alpha = 0.10f
                                )
                        }
                )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 13.dp
                    ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text =
                        if (isCorrect) {
                            "✓"
                        } else {
                            "×"
                        },
                    fontSize =
                        22.sp,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        if (isCorrect) {

                            MaterialTheme
                                .colorScheme
                                .secondary

                        } else {

                            MaterialTheme
                                .colorScheme
                                .error
                        }
                )

                Text(
                    text = word,
                    style =
                        MaterialTheme
                            .typography
                            .bodyLarge,
                    fontWeight =
                        FontWeight.Medium,
                    modifier =
                        Modifier.padding(
                            start = 14.dp
                        )
                )
            }
        }
    }
}