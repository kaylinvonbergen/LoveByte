package com.example.lovebyte.ui.components.minigames

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.data.model.EfficiencyLoopChallenge
import com.example.lovebyte.ui.components.general.PixelButton
import kotlinx.coroutines.delay

@Composable
fun EfficiencyStepper(
    challenge: EfficiencyLoopChallenge,
    timeLimitSeconds: Int = 30,
    onFinished: (Boolean) -> Unit,
    onContinueAnyway: () -> Unit
) {
    val context = LocalContext.current
    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    // color palette
    val softMatcha = Color(0xFFB2F2BB)
    val deepPink = Color(0xFFFF85A1)
    val inkBrown = Color(0xFF5D4037)
    val pixelWhite = Color(0xFFFFFFFF)
    val pixelRoundedShape = CutCornerShape(8.dp)

    val stepDetectorSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) }
    val stepCounterSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) }

    var baselineStepCount by remember { mutableStateOf<Float?>(null) }
    var stepsTaken by remember { mutableIntStateOf(0) }
    var sensorAvailable by remember { mutableStateOf(true) }
    var isPublicMode by remember { mutableStateOf(false) }

    // public mode state (Slider logic)
    var passesCompleted by remember { mutableIntStateOf(0) }
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var passLocked by remember { mutableStateOf(false) }

    // timer state
    var timeLeft by remember { mutableIntStateOf(timeLimitSeconds) }
    var isGameOver by remember { mutableStateOf(false) }

    // countdown timer logic
    LaunchedEffect(timeLeft, isGameOver, challenge.id) {
        if (timeLeft > 0 && !isGameOver) {
            delay(1000L)
            timeLeft -= 1
        } else if (timeLeft == 0) {
            isGameOver = true
        }
    }

    // FAILURE DIALOG
    if (isGameOver) {
        AlertDialog(
            onDismissRequest = { },
            shape = pixelRoundedShape,
            containerColor = pixelWhite,
            modifier = Modifier.border(4.dp, Color.Red, pixelRoundedShape),
            title = { Text("COMPILATION HALTED", color = Color.Red) },
            text = {
                Text("The efficiency loop timed out. Optimization failed. Retry or skip?", color = inkBrown)
            },
            confirmButton = {
                PixelButton(
                    text = "RETRY",
                    onClick = {
                        timeLeft = timeLimitSeconds
                        isGameOver = false
                        baselineStepCount = null
                        stepsTaken = 0
                        passesCompleted = 0
                        sliderPosition = 0f
                        passLocked = false
                    },
                    color = deepPink
                )
            },
            dismissButton = {
                TextButton(onClick = onContinueAnyway) {
                    Text("CONTINUE ANYWAY", color = inkBrown)
                }
            }
        )
    }

    // hardware lifecycle: Register sensor only in Private Mode to save battery
    DisposableEffect(isPublicMode, isGameOver) {
        if (isPublicMode || isGameOver) {
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event ?: return
                    when (event.sensor.type) {
                        Sensor.TYPE_STEP_DETECTOR -> stepsTaken += 1
                        Sensor.TYPE_STEP_COUNTER -> {
                            val absoluteSteps = event.values[0]
                            if (baselineStepCount == null) baselineStepCount = absoluteSteps
                            stepsTaken = (absoluteSteps - (baselineStepCount ?: absoluteSteps)).toInt().coerceAtLeast(0)
                        }
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            val registered = when {
                stepDetectorSensor != null -> sensorManager.registerListener(listener, stepDetectorSensor, SensorManager.SENSOR_DELAY_GAME)
                stepCounterSensor != null -> sensorManager.registerListener(listener, stepCounterSensor, SensorManager.SENSOR_DELAY_GAME)
                else -> false
            }
            sensorAvailable = registered
            onDispose { sensorManager.unregisterListener(listener) }
        }
    }

    val currentCount = if (isPublicMode) passesCompleted else stepsTaken
    val isCorrect = currentCount >= challenge.correctSteps
    val progress = if (challenge.correctSteps == 0) 1f else (currentCount.toFloat() / challenge.correctSteps.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5F7))
            .padding(16.dp)
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isPublicMode) "PUBLIC MODE (SLIDE)" else "PRIVATE MODE (STEPS)",
                    style = MaterialTheme.typography.labelLarge,
                    color = deepPink
                )
                Switch(
                    checked = isPublicMode,
                    onCheckedChange = {
                        if (!isGameOver) {
                            isPublicMode = it
                            // flush state on switch to prevent "ghost wins"
                            baselineStepCount = null
                            stepsTaken = 0
                            passesCompleted = 0
                            sliderPosition = 0f
                            passLocked = false
                        }
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = deepPink)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("OPTIMIZATION TIME", style = MaterialTheme.typography.labelSmall, color = inkBrown)
                Text(
                    text = "${timeLeft}s",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (timeLeft < 10) Color.Red else softMatcha
                )
            }
        }

        // stability/timer bar
        LinearProgressIndicator(
            progress = { timeLeft.toFloat() / timeLimitSeconds.toFloat() },
            modifier = Modifier.fillMaxWidth().height(12.dp).border(2.dp, inkBrown),
            color = if (timeLeft < 10) Color.Red else softMatcha,
            trackColor = pixelWhite,
            strokeCap = StrokeCap.Butt
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "EFFICIENCY STEPPER",
            style = MaterialTheme.typography.headlineSmall,
            color = deepPink
        )

        Text(
            text = challenge.prompt,
            style = MaterialTheme.typography.bodyMedium,
            color = inkBrown,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // --- CODE PREVIEW CARD ---
        Surface(
            modifier = Modifier.fillMaxWidth().border(2.dp, deepPink, pixelRoundedShape),
            color = pixelWhite,
            shape = pixelRoundedShape
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(challenge.codeLines) { line ->
                    Text(
                        text = line,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodySmall,
                        color = inkBrown
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- INTERACTION AREA ---
        if (isPublicMode) {
            PublicModePassSlider(
                currentCount = passesCompleted,
                targetCount = challenge.correctSteps,
                sliderPosition = sliderPosition,
                enabled = !isGameOver,
                softMatcha = softMatcha,
                inkBrown = inkBrown,
                pixelWhite = pixelWhite,
                onSliderPositionChange = { newValue ->
                    if (!passLocked && !isGameOver) {
                        sliderPosition = newValue
                        if (newValue >= 0.98f) {
                            passesCompleted += 1
                            passLocked = true
                        }
                    }
                },
                onSliderReleased = {
                    if (!isGameOver && (passLocked || sliderPosition < 0.98f)) {
                        sliderPosition = 0f
                        passLocked = false
                    }
                }
            )
        } else if (!sensorAvailable) {
            Text("Hardware Step Sensor unavailable.", color = Color.Red)
        } else {
            // STEP COUNTER UI
            Surface(
                modifier = Modifier.fillMaxWidth().border(3.dp, deepPink, pixelRoundedShape),
                color = pixelWhite,
                shape = pixelRoundedShape
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("LOOP ITERATIONS REQUIRED: ${challenge.correctSteps}", color = inkBrown)
                    Text(
                        text = "$stepsTaken",
                        style = MaterialTheme.typography.headlineLarge,
                        color = if (isCorrect) softMatcha else deepPink
                    )
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).height(8.dp).border(1.dp, inkBrown),
                        color = softMatcha,
                        trackColor = Color.Transparent
                    )
                    Text(
                        text = if (isCorrect) "OPTIMIZATION COMPLETE" else "KEEP MOVING TO SCALE THE LOOP",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCorrect) softMatcha else inkBrown
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- FOOTER BUTTONS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PixelButton(
                text = "RESET",
                onClick = {
                    baselineStepCount = null
                    stepsTaken = 0
                    passesCompleted = 0
                    sliderPosition = 0f
                    passLocked = false
                    timeLeft = timeLimitSeconds
                    isGameOver = false
                },
                color = Color.Gray,
                modifier = Modifier.weight(1f),
                enabled = !isGameOver
            )

            PixelButton(
                text = "COMPILE",
                onClick = { onFinished(isCorrect) },
                enabled = isCorrect && !isGameOver,
                color = if (isCorrect) softMatcha else Color.LightGray,
                textColor = if (isCorrect) inkBrown else Color.White,
                modifier = Modifier.weight(1.2f)
            )
        }
    }
}

@Composable
private fun PublicModePassSlider(
    currentCount: Int,
    targetCount: Int,
    sliderPosition: Float,
    enabled: Boolean,
    softMatcha: Color,
    inkBrown: Color,
    pixelWhite: Color,
    onSliderPositionChange: (Float) -> Unit,
    onSliderReleased: () -> Unit
) {
    val progress = if (targetCount == 0) 1f else (currentCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)
    val pixelRoundedShape = CutCornerShape(8.dp)

    Surface(
        modifier = Modifier.fillMaxWidth().border(3.dp, inkBrown, pixelRoundedShape),
        color = pixelWhite,
        shape = pixelRoundedShape
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text("ITERATION PASSES: $currentCount / $targetCount", style = MaterialTheme.typography.titleSmall, color = inkBrown)

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).height(12.dp).border(2.dp, inkBrown),
                color = softMatcha,
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Butt
            )

            Slider(
                value = sliderPosition,
                onValueChange = onSliderPositionChange,
                valueRange = 0f..1f,
                onValueChangeFinished = onSliderReleased,
                enabled = enabled,
                colors = SliderDefaults.colors(thumbColor = inkBrown, activeTrackColor = softMatcha)
            )

            Text(
                "Drag the slider $targetCount times to optimize.",
                style = MaterialTheme.typography.labelSmall,
                color = inkBrown.copy(alpha = 0.6f)
            )
        }
    }
}