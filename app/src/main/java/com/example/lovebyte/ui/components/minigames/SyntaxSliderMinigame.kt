package com.example.lovebyte.ui.components.minigames

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.data.model.SliderBlock
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

// the base game composable
@Composable
fun SyntaxSliderMinigame(
    blocks: List<SliderBlock>,
    timeLimitSeconds: Int = 30, // Default time limit for the dash
    onFinished: (Boolean) -> Unit,
    onContinueAnyway: () -> Unit // Callback to skip the game after a failure
) {
    // is the block "active" (being slid into place by the user)
    var currentActiveIndex by remember { mutableIntStateOf(0) }
    // trying to wait for a return to a neutral position before moving
    var isWaitingForNeutral by remember { mutableStateOf(false) }

    // accessibility toggle for "Public Mode" (drag vs tilt)
    var isPublicMode by remember { mutableStateOf(false) }

    // timer state
    var timeLeft by remember { mutableIntStateOf(timeLimitSeconds) }
    var isGameOver by remember { mutableStateOf(false) }

    val tabWidthPx = 120f
    val context = LocalContext.current

    // sensor stuff!
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    var tiltVelocity by remember { mutableFloatStateOf(0f) }

    // countdown timer logic
    LaunchedEffect(key1 = timeLeft, key2 = isGameOver) {
        if (timeLeft > 0 && !isGameOver && currentActiveIndex < blocks.size) {
            delay(1000L)
            timeLeft -= 1
        } else if (timeLeft == 0 && currentActiveIndex < blocks.size) {
            isGameOver = true
            // dialog triggered by isGameOver state below
        }
    }

    // --- FAILURE DIALOG ---
    if (isGameOver) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Compilation Error") },
            text = { Text("The logic slipped through your fingers. The system is unstable. Restart the dash or push through with errors?") },
            confirmButton = {
                Button(onClick = {
                    // reset local game state for a retry
                    timeLeft = timeLimitSeconds
                    currentActiveIndex = 0
                    isGameOver = false
                }) {
                    Text("Retry")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    onContinueAnyway()
                }) {
                    Text("Continue Anyway")
                }
            }
        )
    }

    // https://developer.android.com/develop/ui/compose/side-effects
    // helps avoid the "leakage" of the sensor listeners
    // only register if NOT in Public Mode to save battery
    DisposableEffect(isPublicMode) {
        if (isPublicMode) {
            tiltVelocity = 0f
            //ensure we aren't stuck in a neutral lock when switching to Public Mode
            isWaitingForNeutral = false
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
                        tiltVelocity = event.values[1]
                        // cooldown to wait for that neutral zone
                        if (isWaitingForNeutral && abs(tiltVelocity) < 0.05f) {
                            isWaitingForNeutral = false
                        }
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            sensorManager.registerListener(listener, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
            // tells the listener to turn off the gyroscope hardware
            onDispose { sensorManager.unregisterListener(listener) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // header w/ accessibility switch and timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isPublicMode) "Public Mode (Drag)" else "Private Mode (Tilt)",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                Switch(
                    checked = isPublicMode,
                    onCheckedChange = { isPublicMode = it }
                )
            }

            // visual timer
            Column(horizontalAlignment = Alignment.End) {
                Text("System Stability", style = MaterialTheme.typography.labelSmall)
                Text(
                    text = "${timeLeft}s",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (timeLeft < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }
        }

        // stability progress bar
        LinearProgressIndicator(
            progress = { timeLeft.toFloat() / timeLimitSeconds.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = if (timeLeft < 10) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )

        Box(modifier = Modifier.weight(1f).padding(top = 12.dp)) {

            // background grid to show tab lines?
            // TODO: polish these eventually
            Row(Modifier.fillMaxSize()) {
                repeat(6) {
                    Box(
                        Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(Color.Gray.copy(alpha = 0.1f))
                    )
                    Spacer(Modifier.width(90.dp))
                }
            }

            // actual coding block lines
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(blocks, key = { _, block -> block.id }) { index, block ->
                    SyntaxPuzzleLine(
                        block = block,
                        isActive = index == currentActiveIndex && !isWaitingForNeutral && !isGameOver,
                        isSolved = index < currentActiveIndex,
                        tiltVelocity = tiltVelocity,
                        isPublicMode = isPublicMode,
                        tabWidthPx = tabWidthPx,
                        onSlotted = {
                            if (index == currentActiveIndex) {
                                // FIXED: the neutral phone angle thing shouldn't apply if it's in public mode
                                isWaitingForNeutral = !isPublicMode
                                currentActiveIndex++
                            }
                        }
                    )
                }
            }
        }

        // instructions
        Surface(
            tonalElevation = 3.dp,
            shape = MaterialTheme.shapes.large,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = when {
                        isGameOver -> "System Crash!"
                        isWaitingForNeutral && !isPublicMode -> "Hold steady..."
                        else -> "Assemble the Logic"
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isWaitingForNeutral || isGameOver)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (isPublicMode) "Drag the block to its matching ghost slot."
                    else "Gently tilt your phone to slide the block into its slot.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }

        // "finished button"
        Button(
            onClick = { onFinished(true) },
            enabled = currentActiveIndex >= blocks.size && !isGameOver,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Text("Run Script", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
private fun SyntaxPuzzleLine(
    block: SliderBlock,
    isActive: Boolean,
    isSolved: Boolean,
    tiltVelocity: Float,
    isPublicMode: Boolean,
    tabWidthPx: Float,
    onSlotted: () -> Unit
) {
    val density = LocalDensity.current
    val offsetX = remember(block.id) { Animatable(0f) }
    val cardHorizontalPadding = 12.dp
    val coroutineScope = rememberCoroutineScope()

    val codeTextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    // state manager for the blocks
    LaunchedEffect(isActive, isSolved, block.id) {
        when {
            isSolved -> offsetX.animateTo(block.targetLevel * tabWidthPx)
            isActive -> {
                if (offsetX.value == 0f) offsetX.snapTo(0f)
            }
            else -> offsetX.animateTo(
                20f,
                infiniteRepeatable(tween(2000), RepeatMode.Reverse)
            )
        }
    }

    // physics engine for TILT
    LaunchedEffect(tiltVelocity, isActive) {
        if (isActive && !isPublicMode) {
            val targetX = block.targetLevel * tabWidthPx
            val newOffset = (offsetX.value + (tiltVelocity * 50f)).coerceIn(0f, 1000f)
            offsetX.snapTo(newOffset)

            if (abs(offsetX.value - targetX) < 25f) {
                offsetX.animateTo(targetX, spring(stiffness = Spring.StiffnessMediumLow))
                onSlotted()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            if (isActive || !isSolved) {
                Surface(
                    modifier = Modifier.offset(
                        x = with(density) {
                            (block.targetLevel * tabWidthPx).toDp()
                        }
                    ),
                    color = Color.Black.copy(alpha = 0.05f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = block.code,
                        modifier = Modifier.padding(
                            horizontal = cardHorizontalPadding,
                            vertical = 12.dp
                        ),
                        fontFamily = FontFamily.Monospace,
                        style = codeTextStyle,
                        color = Color.Transparent,
                        maxLines = 1
                    )
                }
            }

            Card(
                modifier = Modifier
                    .offset(x = with(density) { offsetX.value.toDp() })
                    .then(
                        if (isActive && isPublicMode) {
                            Modifier.pointerInput(block.id) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        coroutineScope.launch {
                                            val newX = (offsetX.value + dragAmount.x).coerceIn(0f, 1000f)
                                            offsetX.snapTo(newX)

                                            val targetX = block.targetLevel * tabWidthPx
                                            if (abs(newX - targetX) < 25f) {
                                                offsetX.animateTo(targetX, spring(stiffness = Spring.StiffnessMediumLow))
                                                onSlotted()
                                            }
                                        }
                                    }
                                )
                            }
                        } else Modifier
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        isSolved -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                        isActive -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                        else -> Color.Gray.copy(alpha = 0.1f)
                    }
                ),
                shape = MaterialTheme.shapes.small,
                elevation = if (isActive)
                    CardDefaults.cardElevation(defaultElevation = 4.dp)
                else
                    CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    text = block.code,
                    modifier = Modifier.padding(
                        horizontal = cardHorizontalPadding,
                        vertical = 12.dp
                    ),
                    fontFamily = FontFamily.Monospace,
                    style = codeTextStyle,
                    color = if (isSolved) Color(0xFF1B5E20)
                    else MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
            }
        }
    }
}