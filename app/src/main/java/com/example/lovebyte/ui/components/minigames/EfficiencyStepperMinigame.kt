package com.example.lovebyte.ui.components.minigames

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.data.model.EfficiencyLoopChallenge
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

    val stepDetectorSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    }
    val stepCounterSensor = remember {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    var baselineStepCount by remember { mutableStateOf<Float?>(null) }
    var stepsTaken by remember { mutableIntStateOf(0) }
    var sensorAvailable by remember { mutableStateOf(true) }

    var isPublicMode by remember { mutableStateOf(false) }

    // Public mode state
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

    // failure dialog
    if (isGameOver) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Out of Time") },
            text = {
                Text(
                    "You ran out of time before reaching the correct loop count. Retry the challenge or continue anyway?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        timeLeft = timeLimitSeconds
                        isGameOver = false
                        baselineStepCount = null
                        stepsTaken = 0
                        passesCompleted = 0
                        sliderPosition = 0f
                        passLocked = false
                    }
                ) {
                    Text("Retry")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onContinueAnyway()
                    }
                ) {
                    Text("Continue Anyway")
                }
            }
        )
    }

    DisposableEffect(isPublicMode, isGameOver) {
        if (isPublicMode || isGameOver) {
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event ?: return

                    when (event.sensor.type) {
                        Sensor.TYPE_STEP_DETECTOR -> {
                            stepsTaken += 1
                        }

                        Sensor.TYPE_STEP_COUNTER -> {
                            val absoluteSteps = event.values[0]
                            if (baselineStepCount == null) {
                                baselineStepCount = absoluteSteps
                            }
                            stepsTaken = (absoluteSteps - (baselineStepCount ?: absoluteSteps))
                                .toInt()
                                .coerceAtLeast(0)
                        }
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            val registered = when {
                stepDetectorSensor != null -> {
                    sensorManager.registerListener(
                        listener,
                        stepDetectorSensor,
                        SensorManager.SENSOR_DELAY_GAME
                    )
                }

                stepCounterSensor != null -> {
                    sensorManager.registerListener(
                        listener,
                        stepCounterSensor,
                        SensorManager.SENSOR_DELAY_GAME
                    )
                }

                else -> false
            }

            sensorAvailable = registered

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    val currentCount = if (isPublicMode) passesCompleted else stepsTaken
    val isCorrect = currentCount >= challenge.correctSteps
    val progress = if (challenge.correctSteps == 0) 1f
    else (currentCount.toFloat() / challenge.correctSteps.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isPublicMode) "Public Mode (Slider)" else "Private Mode (Steps)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    checked = isPublicMode,
                    onCheckedChange = {
                        if (!isGameOver) {
                            isPublicMode = it
                            baselineStepCount = null
                            stepsTaken = 0
                            passesCompleted = 0
                            sliderPosition = 0f
                            passLocked = false
                        }
                    }
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("Time Left", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = "${timeLeft}s",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (timeLeft < 10) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }
        }

        LinearProgressIndicator(
            progress = { timeLeft.toFloat() / timeLimitSeconds.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = if (timeLeft < 10) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Efficiency Stepper",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = challenge.prompt,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(challenge.codeLines) { line ->
                    Text(
                        text = line,
                        fontFamily = FontFamily.Monospace,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isPublicMode) {
            PublicModePassSlider(
                currentCount = passesCompleted,
                targetCount = challenge.correctSteps,
                sliderPosition = sliderPosition,
                enabled = !isGameOver,
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
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No step sensor was found on this device.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Target steps: ${challenge.correctSteps}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Your steps: $stepsTaken",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isCorrect) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = if (isCorrect) {
                            "Nice! You reached the target."
                        } else {
                            "Keep walking until you reach the loop count."
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = {
                    baselineStepCount = null
                    stepsTaken = 0
                    passesCompleted = 0
                    sliderPosition = 0f
                    passLocked = false
                    timeLeft = timeLimitSeconds
                    isGameOver = false
                },
                enabled = !isGameOver,
                modifier = Modifier.weight(1f)
            ) {
                Text("Reset")
            }

            Button(
                onClick = { onFinished(isCorrect) },
                enabled = isCorrect && !isGameOver && (isPublicMode || sensorAvailable),
                modifier = Modifier.weight(1f)
            ) {
                Text("Submit")
            }
        }
    }
}

@Composable
private fun PublicModePassSlider(
    currentCount: Int,
    targetCount: Int,
    sliderPosition: Float,
    enabled: Boolean,
    onSliderPositionChange: (Float) -> Unit,
    onSliderReleased: () -> Unit
) {
    val progress = if (targetCount == 0) 1f
    else (currentCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Drag the slider to the end $targetCount time(s).",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Completed passes: $currentCount / $targetCount",
                style = MaterialTheme.typography.titleLarge,
                color = if (currentCount >= targetCount) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = if (currentCount >= targetCount) {
                    "Nice! You reached the target."
                } else {
                    "Slide all the way across, then repeat."
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Slider(
                value = sliderPosition,
                onValueChange = onSliderPositionChange,
                valueRange = 0f..1f,
                onValueChangeFinished = onSliderReleased,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}