package com.example.guessup.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import kotlin.math.abs
import kotlin.math.atan2

class TiltDetector(
    context: Context,
    private val onCorrect: () -> Unit,
    private val onPass: () -> Unit,
    private val onReturnedToNeutral: () -> Unit
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager

    private val windowManager =
        context.getSystemService(
            Context.WINDOW_SERVICE
        ) as WindowManager

    private val rotationSensor =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_ROTATION_VECTOR
        )

    /*
     * Posição inicial do telemóvel.
     */
    private var neutralRotationMatrix:
            FloatArray? = null

    /*
     * Depois de uma resposta,
     * bloqueia novas respostas.
     */
    private var locked = false

    /*
     * Momento em que começou a estar
     * ESTÁVEL junto à posição neutra.
     */
    private var neutralStableStartedAt:
            Long? = null

    /*
     * Valores usados para calcular
     * a velocidade de movimento.
     */
    private var lastTiltAngle:
            Float? = null

    private var lastSensorTimestamp:
            Long? = null

    /*
     * Inclinação necessária para responder.
     */
    private val triggerAngle =
        35f

    /*
     * Zona considerada próxima
     * da posição original.
     */
    private val neutralAngle =
        15f

    /*
     * Tem de permanecer estável nesta zona
     * durante 300 ms.
     */
    private val neutralHoldTimeMs =
        300L

    /*
     * Velocidade máxima permitida para
     * considerarmos que o telefone está parado.
     *
     * graus / segundo
     */
    private val neutralVelocityThreshold =
        18f

    fun start() {

        neutralRotationMatrix = null

        locked = false

        neutralStableStartedAt = null

        lastTiltAngle = null

        lastSensorTimestamp = null

        rotationSensor?.let { sensor ->

            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }
    }

    fun stop() {

        sensorManager.unregisterListener(
            this
        )
    }

    fun recalibrate() {

        neutralRotationMatrix = null

        locked = false

        neutralStableStartedAt = null

        lastTiltAngle = null

        lastSensorTimestamp = null
    }

    override fun onSensorChanged(
        event: SensorEvent
    ) {

        if (
            event.sensor.type !=
            Sensor.TYPE_ROTATION_VECTOR
        ) {
            return
        }

        /*
         * Matriz original do sensor.
         */
        val rotationMatrix =
            FloatArray(9)

        SensorManager
            .getRotationMatrixFromVector(
                rotationMatrix,
                event.values
            )

        /*
         * Corrige os eixos para landscape.
         */
        val remappedMatrix =
            FloatArray(9)

        val displayRotation =
            windowManager
                .defaultDisplay
                .rotation

        when (displayRotation) {

            Surface.ROTATION_0 -> {

                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Y,
                    remappedMatrix
                )
            }

            Surface.ROTATION_90 -> {

                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_Y,
                    SensorManager.AXIS_MINUS_X,
                    remappedMatrix
                )
            }

            Surface.ROTATION_180 -> {

                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_MINUS_X,
                    SensorManager.AXIS_MINUS_Y,
                    remappedMatrix
                )
            }

            Surface.ROTATION_270 -> {

                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_MINUS_Y,
                    SensorManager.AXIS_X,
                    remappedMatrix
                )
            }
        }

        /*
         * Primeira leitura:
         * posição inicial = neutro.
         */
        if (neutralRotationMatrix == null) {

            neutralRotationMatrix =
                remappedMatrix.copyOf()

            lastSensorTimestamp =
                event.timestamp

            Log.d(
                "GuessUpTilt",
                "CALIBRATED"
            )

            return
        }

        /*
         * Inclinação RELATIVA à posição inicial.
         */
        val tiltAngle =
            getRelativeTiltAngle(
                neutral =
                    neutralRotationMatrix!!,
                current =
                    remappedMatrix
            )

        /*
         * Calcula a velocidade angular.
         *
         * Isto permite perceber se o jogador
         * está parado no centro ou apenas
         * a atravessar rapidamente o centro.
         */
        val previousAngle =
            lastTiltAngle

        val previousTimestamp =
            lastSensorTimestamp

        var angularVelocity =
            Float.MAX_VALUE

        if (
            previousAngle != null &&
            previousTimestamp != null
        ) {

            val deltaTimeSeconds =
                (
                        event.timestamp -
                                previousTimestamp
                        ) / 1_000_000_000f

            if (deltaTimeSeconds > 0f) {

                val angleChange =
                    normalizeAngle(
                        tiltAngle -
                                previousAngle
                    )

                angularVelocity =
                    abs(angleChange) /
                            deltaTimeSeconds
            }
        }

        lastTiltAngle =
            tiltAngle

        lastSensorTimestamp =
            event.timestamp

        /*
         * ============================
         * ESTAMOS BLOQUEADOS
         * ============================
         *
         * Depois de responder:
         *
         * 1. tem de voltar perto de 0°
         * 2. tem de praticamente parar
         * 3. tem de permanecer assim 300 ms
         */
        if (locked) {

            val now =
                SystemClock.elapsedRealtime()

            val isNearNeutral =
                abs(tiltAngle) <
                        neutralAngle

            val isStable =
                angularVelocity <
                        neutralVelocityThreshold

            if (
                isNearNeutral &&
                isStable
            ) {

                /*
                 * Começou a estar realmente
                 * estável no centro.
                 */
                if (
                    neutralStableStartedAt ==
                    null
                ) {

                    neutralStableStartedAt =
                        now

                    Log.d(
                        "GuessUpTilt",
                        "NEUTRAL STABLE START"
                    )
                }

                /*
                 * Só fica READY depois
                 * dos 300 ms completos.
                 */
                if (
                    now -
                    neutralStableStartedAt!! >=
                    neutralHoldTimeMs
                ) {

                    locked = false

                    neutralStableStartedAt =
                        null

                    Log.d(
                        "GuessUpTilt",
                        "READY AGAIN | angle=$tiltAngle | velocity=$angularVelocity"
                    )

                    /*
                     * Só AGORA o GameScreen
                     * mostra a próxima palavra.
                     */
                    onReturnedToNeutral()
                }

            } else {

                /*
                 * Ainda está a mexer ou saiu
                 * da zona neutra.
                 *
                 * Reinicia os 300 ms.
                 */
                neutralStableStartedAt =
                    null
            }

            return
        }

        /*
         * ============================
         * RESPOSTA CORRETA
         * ============================
         */
        if (
            tiltAngle >
            triggerAngle
        ) {

            locked = true

            neutralStableStartedAt =
                null

            Log.d(
                "GuessUpTilt",
                "CORRECT | angle=$tiltAngle"
            )

            onCorrect()

            return
        }

        /*
         * ============================
         * PASSAR
         * ============================
         */
        if (
            tiltAngle <
            -triggerAngle
        ) {

            locked = true

            neutralStableStartedAt =
                null

            Log.d(
                "GuessUpTilt",
                "PASS | angle=$tiltAngle"
            )

            onPass()
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
        // Não é necessário.
    }

    /*
     * Calcula a rotação relativamente
     * à posição original.
     */
    private fun getRelativeTiltAngle(
        neutral: FloatArray,
        current: FloatArray
    ): Float {

        val relative =
            FloatArray(9)

        /*
         * transpose(neutral) × current
         */
        for (row in 0..2) {

            for (column in 0..2) {

                var value =
                    0f

                for (k in 0..2) {

                    value +=
                        neutral[
                            k * 3 + row
                        ] *
                                current[
                                    k * 3 +
                                            column
                                ]
                }

                relative[
                    row * 3 +
                            column
                ] = value
            }
        }

        val angleRadians =
            atan2(
                relative[7].toDouble(),
                relative[8].toDouble()
            )

        return Math.toDegrees(
            angleRadians
        ).toFloat()
    }

    /*
     * Normaliza um ângulo para
     * -180° ... +180°.
     */
    private fun normalizeAngle(
        angle: Float
    ): Float {

        var result =
            angle

        while (result > 180f) {
            result -= 360f
        }

        while (result < -180f) {
            result += 360f
        }

        return result
    }
}