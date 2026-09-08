package com.example.guessup.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.guessup.model.CardResult
import com.example.guessup.model.Category
import com.example.guessup.model.RoundResult
import com.example.guessup.sensor.TiltDetector
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition

@Composable
fun GameScreen(
    category: Category,
    durationSeconds: Int,
    vibrationEnabled: Boolean,
    soundEnabled: Boolean,
    countdownSeconds: Int,
    onGameFinished: (RoundResult) -> Unit
) {

    val context = LocalContext.current
    val activity = context.findActivity()
    val toneGenerator =
        remember {

            ToneGenerator(
                AudioManager.STREAM_MUSIC,
                70
            )
        }

    DisposableEffect(
        toneGenerator
    ) {

        onDispose {
            toneGenerator.release()
        }
    }

    /*
     * LANDSCAPE + FULLSCREEN
     *
     * Quando entramos no GameScreen:
     * - força landscape
     * - esconde status bar
     * - esconde navigation bar
     *
     * Quando saímos:
     * - volta a mostrar as barras
     * - volta a portrait
     */
    DisposableEffect(activity) {

        activity?.let {

            it.requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            val window = it.window

            WindowCompat.setDecorFitsSystemWindows(
                window,
                false
            )

            WindowInsetsControllerCompat(
                window,
                window.decorView
            ).apply {

                hide(
                    WindowInsetsCompat.Type.systemBars()
                )

                systemBarsBehavior =
                    WindowInsetsControllerCompat
                        .BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        onDispose {

            activity?.let {

                val window = it.window

                WindowInsetsControllerCompat(
                    window,
                    window.decorView
                ).show(
                    WindowInsetsCompat.Type.systemBars()
                )

                WindowCompat.setDecorFitsSystemWindows(
                    window,
                    true
                )

                it.requestedOrientation =
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    /*
     * COUNTDOWN
     */
    var preparationCount by remember(
        category.id,
        durationSeconds
    ) {
        mutableIntStateOf(
            countdownSeconds
        )
    }

    var gameStarted by remember(
        category.id,
        durationSeconds
    ) {
        mutableStateOf(false)
    }

    /*
     * TIMER
     */
    var remainingSeconds by remember(
        category.id,
        durationSeconds
    ) {
        mutableIntStateOf(durationSeconds)
    }

    /*
     * PALAVRAS
     */
    var words by remember(category.id) {
        mutableStateOf(
            category.words.shuffled()
        )
    }

    var currentIndex by remember(category.id) {
        mutableIntStateOf(0)
    }

    /*
     * RESULTADOS
     */
    val results = remember(category.id) {
        mutableStateListOf<CardResult>()
    }

    /*
     * Feedback depois da resposta:
     *
     * CORRECT
     * PASSED
     * null
     */
    var answerFeedback by remember {
        mutableStateOf<AnswerFeedback?>(null)
    }

    /*
     * Categoria sem palavras.
     */
    if (words.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Esta categoria não tem palavras.",
                style =
                    MaterialTheme.typography.headlineMedium
            )
        }

        return
    }

    /*
     * COUNTDOWN
     *
     * 3
     * 2
     * 1
     * VAI!
     */
    LaunchedEffect(
        category.id,
        durationSeconds,
        countdownSeconds
    ) {

        preparationCount =
            countdownSeconds

        /*
         * Countdown:
         *
         * 3 → vibração + beep
         * 2 → vibração + beep
         * 1 → vibração + beep
         */
        while (preparationCount > 0) {

            if (vibrationEnabled) {
                context.performCountdownHaptic()
            }

            if (soundEnabled) {
                toneGenerator.startTone(
                    ToneGenerator.TONE_PROP_BEEP,
                    100
                )
            }

            delay(1000L)

            preparationCount--
        }

        /*
         * preparationCount == 0
         *
         * Agora aparece VAI!
         *
         * Feedback ligeiramente mais pronunciado
         * para indicar o início real da ronda.
         */
        if (vibrationEnabled) {
            context.performGoHaptic()
        }

        if (soundEnabled) {
            toneGenerator.startTone(
                ToneGenerator.TONE_PROP_ACK,
                180
            )
        }

        delay(500L)

        gameStarted = true
    }

    /*
     * TIMER DA RONDA
     */
    LaunchedEffect(
        gameStarted,
        vibrationEnabled
    ) {

        if (gameStarted) {

            while (remainingSeconds > 0) {

                delay(1000L)

                remainingSeconds--

                /*
                 * Últimos 10 segundos:
                 *
                 * 00:10 → tick
                 * 00:09 → tick
                 * ...
                 * 00:01 → tick
                 * 00:00 → tick
                 */
                if (
                    vibrationEnabled &&
                    remainingSeconds in 0..10
                ) {

                    context.performTimerTickHaptic()
                }
            }

            onGameFinished(
                RoundResult(
                    categoryId = category.id,
                    categoryName = category.name,
                    durationSeconds = durationSeconds,
                    cards = results.toList()
                )
            )
        }
    }

    /*
     * COUNTDOWN SCREEN
     */
    if (!gameStarted) {

        PreparationCountdownScreen(
            category = category,
            preparationCount = preparationCount
        )

        return
    }

    val currentWord =
        words[currentIndex]

    /*
     * Avança para a palavra seguinte.
     */
    fun nextWord() {

        if (currentIndex == words.lastIndex) {

            /*
             * Acabaram as palavras.
             *
             * Volta a baralhar e continua.
             */
            words =
                category.words.shuffled()

            currentIndex = 0

        } else {

            currentIndex++
        }
    }

    /*
     * Regista uma resposta.
     */
    fun submitAnswer(
        isCorrect: Boolean
    ) {

        /*
         * Não aceitar respostas:
         *
         * - depois do tempo acabar
         * - enquanto estamos a mostrar feedback
         */
        if (
            remainingSeconds <= 0 ||
            answerFeedback != null
        ) {
            return
        }

        Log.d(
            "GuessUpGame",
            "submitAnswer: isCorrect=$isCorrect | word=$currentWord"
        )

        /*
         * Guarda a palavra antes de qualquer
         * alteração de índice.
         */
        val answeredWord =
            currentWord

        results.add(
            CardResult(
                word = answeredWord,
                isCorrect = isCorrect
            )
        )

        if (vibrationEnabled) {

            context.performAnswerHaptic(
                isCorrect = isCorrect
            )
        }

        if (soundEnabled) {

            if (isCorrect) {

                toneGenerator.startTone(
                    ToneGenerator.TONE_PROP_ACK,
                    120
                )

            } else {

                toneGenerator.startTone(
                    ToneGenerator.TONE_PROP_NACK,
                    160
                )
            }
        }

        /*
         * Mostra feedback.
         */
        answerFeedback =
            if (isCorrect) {
                AnswerFeedback.CORRECT
            } else {
                AnswerFeedback.PASSED
            }


    }

    fun returnedToNeutral() {

        /*
         * Só avançamos se existir uma
         * resposta pendente.
         */
        if (answerFeedback == null) {
            return
        }

        /*
         * Agora sim:
         *
         * mostra a próxima palavra.
         */
        nextWord()

        /*
         * E remove o ecrã verde/vermelho.
         */
        answerFeedback = null
    }

    /*
     * O TiltDetector deve receber sempre
     * a versão atual de submitAnswer().
     */
    val currentSubmitAnswer by
    rememberUpdatedState(
        newValue = { isCorrect: Boolean ->

            submitAnswer(
                isCorrect
            )
        }
    )

    val currentReturnedToNeutral by
    rememberUpdatedState(
        newValue = {

            returnedToNeutral()
        }
    )

    /*
     * SENSOR
     */
    val tiltDetector =
        remember(context) {

            TiltDetector(
                context = context,

                onCorrect = {

                    currentSubmitAnswer(
                        true
                    )
                },

                onPass = {

                    currentSubmitAnswer(
                        false
                    )
                },

                onReturnedToNeutral = {

                    currentReturnedToNeutral()
                }
            )
        }

    /*
     * Liga o sensor apenas depois
     * do countdown.
     */
    DisposableEffect(
        gameStarted,
        tiltDetector
    ) {

        if (gameStarted) {
            tiltDetector.start()
        }

        onDispose {
            tiltDetector.stop()
        }
    }

    /*
     * Se acabámos de responder,
     * mostramos o feedback fullscreen.
     */
    val feedback =
        answerFeedback

    if (feedback != null) {

        AnswerFeedbackScreen(
            feedback = feedback
        )

        return
    }

    /*
     * ECRÃ PRINCIPAL DO JOGO
     */
    GameContent(
        category = category,
        word = currentWord,
        remainingSeconds = remainingSeconds,
        answersCount = results.size,
        vibrationEnabled = vibrationEnabled
    )
}

/*
 * ==========================================
 *
 * ECRÃ PRINCIPAL
 *
 * ==========================================
 */

@Composable
private fun GameContent(
    category: Category,
    word: String,
    remainingSeconds: Int,
    answersCount: Int,
    vibrationEnabled: Boolean
) {
    val context = LocalContext.current

    val timerWarning =
        remainingSeconds in 1..10

    /*
     * Vibra apenas uma vez:
     *
     * 00:11
     *   ↓
     * 00:10 → vibração leve
     */
    LaunchedEffect(
        remainingSeconds,
        vibrationEnabled
    ) {

        if (
            remainingSeconds == 10 &&
            vibrationEnabled
        ) {

            context.performTimerWarningHaptic()
        }
    }

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "TimerWarningPulse"
        )

    val pulseScale by
    infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.13f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 420
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "TimerScale"
    )

    val timerScale =
        if (timerWarning) {
            pulseScale
        } else {
            1f
        }

    val backgroundGradient =
        Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.surfaceVariant
            )
        )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(
                horizontal = 34.dp,
                vertical = 22.dp
            )
    ) {

        /*
         * CATEGORIA
         */
        Surface(
            modifier =
                Modifier.align(Alignment.TopStart),
            shape = RoundedCornerShape(50.dp),
            color =
                MaterialTheme.colorScheme.surface.copy(
                    alpha = 0.85f
                )
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 9.dp
                ),
                verticalAlignment =
                    Alignment.CenterVertically,
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = category.emoji,
                    fontSize = 22.sp
                )

                Text(
                    text = category.name,
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        /*
         * TIMER
         */
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .scale(timerScale),
            shape = RoundedCornerShape(50.dp),
            color =
                if (timerWarning){

                    MaterialTheme.colorScheme.error
                        .copy(alpha = 0.15f)

                } else {

                    MaterialTheme.colorScheme.surface
                        .copy(alpha = 0.85f)
                }
        ) {

            Text(
                text =
                    formatDuration(
                        remainingSeconds
                    ),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color =
                    if (remainingSeconds <= 10) {

                        MaterialTheme.colorScheme.error

                    } else {

                        MaterialTheme.colorScheme
                            .onSurface
                    },
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 8.dp
                )
            )
        }

        /*
         * PALAVRA
         */
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.82f),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = word,
                fontSize =
                    getWordFontSize(word),
                fontWeight =
                    FontWeight.ExtraBold,
                textAlign =
                    TextAlign.Center,
                color =
                    MaterialTheme.colorScheme
                        .onBackground,
                maxLines = 3
            )
        }

        /*
         * CONTADOR DE RESPOSTAS
         *
         * Pequeno e discreto.
         */
        Surface(
            modifier =
                Modifier.align(
                    Alignment.BottomEnd
                ),
            shape = RoundedCornerShape(50.dp),
            color =
                MaterialTheme.colorScheme.surface
                    .copy(alpha = 0.75f)
        ) {

            Text(
                text =
                    "$answersCount respostas",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 7.dp
                )
            )
        }

        /*
         * Pequena indicação.
         */
        Text(
            text = "Inclina para responder",
            style =
                MaterialTheme.typography.bodySmall,
            color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant
                    .copy(alpha = 0.7f),
            modifier =
                Modifier.align(
                    Alignment.BottomCenter
                )
        )
    }
}

/*
 * ==========================================
 *
 * COUNTDOWN
 *
 * ==========================================
 */

@Composable
private fun PreparationCountdownScreen(
    category: Category,
    preparationCount: Int
) {

    val backgroundGradient =
        Brush.horizontalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.background,
                MaterialTheme.colorScheme.surfaceVariant
            )
        )

    /*
     * Sempre que:
     *
     * 3 → 2 → 1 → VAI!
     *
     * muda, a animação começa novamente.
     */
    var animateNumber by remember(
        preparationCount
    ) {
        mutableStateOf(false)
    }

    LaunchedEffect(preparationCount) {
        animateNumber = true
    }

    val numberScale by animateFloatAsState(
        targetValue =
            if (animateNumber) {
                1f
            } else {
                0.45f
            },
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = 400f
        ),
        label = "CountdownScale"
    )

    val numberAlpha by animateFloatAsState(
        targetValue =
            if (animateNumber) {
                1f
            } else {
                0f
            },
        animationSpec = tween(
            durationMillis = 180
        ),
        label = "CountdownAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {

        /*
         * Categoria
         */
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp),
            shape = RoundedCornerShape(50.dp),
            color =
                MaterialTheme.colorScheme.surface
        ) {

            Text(
                text =
                    "${category.emoji}  ${category.name}",
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    horizontal = 18.dp,
                    vertical = 9.dp
                )
            )
        }

        Column(
            modifier =
                Modifier.align(
                    Alignment.Center
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    if (preparationCount > 0) {

                        preparationCount.toString()

                    } else {

                        "VAI!"
                    },
                fontSize =
                    if (preparationCount > 0) {
                        120.sp
                    } else {
                        76.sp
                    },
                fontWeight =
                    FontWeight.ExtraBold,
                color =
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .scale(numberScale)
                    .alpha(numberAlpha)
            )

            Text(
                text = "Prepara o telemóvel",
                style =
                    MaterialTheme.typography.titleLarge,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant,
                modifier = Modifier.padding(
                    top = 8.dp
                )
            )
        }
    }
}

/*
 * ==========================================
 *
 * FEEDBACK ACERTO / PASSE
 *
 * ==========================================
 */

@Composable
private fun AnswerFeedbackScreen(
    feedback: AnswerFeedback
) {

    val isCorrect =
        feedback ==
                AnswerFeedback.CORRECT

    val backgroundColor =
        if (isCorrect) {

            MaterialTheme.colorScheme.secondary

        } else {

            MaterialTheme.colorScheme.error
        }

    /*
     * Entrada da animação.
     */
    var visible by remember(
        feedback
    ) {
        mutableStateOf(false)
    }

    LaunchedEffect(feedback) {
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue =
            if (visible) {
                1f
            } else {
                0.6f
            },
        animationSpec = spring(
            dampingRatio = 0.55f,
            stiffness = 500f
        ),
        label = "FeedbackScale"
    )

    val alpha by animateFloatAsState(
        targetValue =
            if (visible) {
                1f
            } else {
                0f
            },
        animationSpec = tween(
            durationMillis = 120
        ),
        label = "FeedbackAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier
                .scale(scale)
                .alpha(alpha),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    if (isCorrect) {
                        "✓"
                    } else {
                        "↷"
                    },
                fontSize = 96.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text =
                    if (isCorrect) {
                        "ACERTOU!"
                    } else {
                        "PASSOU"
                    },
                fontSize = 48.sp,
                fontWeight =
                    FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 2.sp
            )
        }
    }
}

/*
 * Ajusta automaticamente o tamanho da palavra
 * de acordo com o comprimento.
 */
private fun getWordFontSize(
    word: String
): TextUnit {

    return when {

        word.length <= 10 ->
            78.sp

        word.length <= 18 ->
            68.sp

        word.length <= 28 ->
            58.sp

        word.length <= 40 ->
            48.sp

        else ->
            40.sp
    }
}

/*
 * Segundos → MM:SS
 */
private fun formatDuration(
    totalSeconds: Int
): String {

    val minutes =
        totalSeconds / 60

    val seconds =
        totalSeconds % 60

    return "%02d:%02d".format(
        minutes,
        seconds
    )
}

/*
 * Estado do feedback.
 */
private enum class AnswerFeedback {

    CORRECT,

    PASSED
}

/*
 * Obtém a Activity a partir
 * do Context do Compose.
 */
private fun Context.findActivity(): Activity? {

    return when (this) {

        is Activity ->
            this

        is ContextWrapper ->
            baseContext.findActivity()

        else ->
            null
    }
}

private fun Context.performAnswerHaptic(
    isCorrect: Boolean
) {

    val vibrator: Vibrator =

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            val vibratorManager =
                getSystemService(
                    Context.VIBRATOR_MANAGER_SERVICE
                ) as VibratorManager

            vibratorManager.defaultVibrator

        } else {

            @Suppress("DEPRECATION")
            getSystemService(
                Context.VIBRATOR_SERVICE
            ) as Vibrator
        }

    if (!vibrator.hasVibrator()) {
        return
    }

    if (isCorrect) {

        /*
         * ACERTO
         *
         * Uma vibração curta e relativamente leve.
         */
        vibrator.vibrate(
            VibrationEffect.createOneShot(
                55L,
                140
            )
        )

    } else {

        /*
         * PASSAR / ERRAR
         *
         * Duas vibrações fortes:
         *
         * BZZZ   BZZZZ
         *
         * Muito mais fácil de distinguir do acerto.
         */
        vibrator.vibrate(
            VibrationEffect.createWaveform(
                longArrayOf(
                    0L,    // começa imediatamente
                    90L,   // primeira vibração
                    70L,   // pausa
                    140L   // segunda vibração mais longa
                ),
                intArrayOf(
                    0,
                    255,
                    0,
                    255
                ),
                -1
            )
        )
    }
}

private fun Context.performTimerWarningHaptic() {

    val vibrator: Vibrator =

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            val vibratorManager =
                getSystemService(
                    Context.VIBRATOR_MANAGER_SERVICE
                ) as VibratorManager

            vibratorManager.defaultVibrator

        } else {

            @Suppress("DEPRECATION")
            getSystemService(
                Context.VIBRATOR_SERVICE
            ) as Vibrator
        }

    if (!vibrator.hasVibrator()) {
        return
    }

    /*
     * Aviso dos últimos 10 segundos:
     *
     * vibração muito curta e leve.
     */
    vibrator.vibrate(
        VibrationEffect.createOneShot(
            35L,
            80
        )
    )
}

private fun Context.performTimerTickHaptic() {

    val vibrator: Vibrator =

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            val vibratorManager =
                getSystemService(
                    Context.VIBRATOR_MANAGER_SERVICE
                ) as VibratorManager

            vibratorManager.defaultVibrator

        } else {

            @Suppress("DEPRECATION")
            getSystemService(
                Context.VIBRATOR_SERVICE
            ) as Vibrator
        }

    if (!vibrator.hasVibrator()) {
        return
    }

    /*
     * Tick muito curto e leve.
     *
     * Deve sentir-se como o pulsar
     * de um relógio e não como uma resposta.
     */
    vibrator.vibrate(
        VibrationEffect.createOneShot(
            28L,
            65
        )
    )
}

private fun Context.performCountdownHaptic() {

    val vibrator: Vibrator =

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            val vibratorManager =
                getSystemService(
                    Context.VIBRATOR_MANAGER_SERVICE
                ) as VibratorManager

            vibratorManager.defaultVibrator

        } else {

            @Suppress("DEPRECATION")
            getSystemService(
                Context.VIBRATOR_SERVICE
            ) as Vibrator
        }

    if (!vibrator.hasVibrator()) {
        return
    }

    /*
     * 3, 2, 1
     *
     * Um pouco mais perceptível que
     * os ticks dos últimos 10 segundos.
     */
    vibrator.vibrate(
        VibrationEffect.createOneShot(
            45L,
            100
        )
    )
}

private fun Context.performGoHaptic() {

    val vibrator: Vibrator =

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.S
        ) {

            val vibratorManager =
                getSystemService(
                    Context.VIBRATOR_MANAGER_SERVICE
                ) as VibratorManager

            vibratorManager.defaultVibrator

        } else {

            @Suppress("DEPRECATION")
            getSystemService(
                Context.VIBRATOR_SERVICE
            ) as Vibrator
        }

    if (!vibrator.hasVibrator()) {
        return
    }

    /*
     * VAI!
     *
     * Um toque curto mas mais forte,
     * para marcar o início da ronda.
     */
    vibrator.vibrate(
        VibrationEffect.createOneShot(
            70L,
            160
        )
    )
}