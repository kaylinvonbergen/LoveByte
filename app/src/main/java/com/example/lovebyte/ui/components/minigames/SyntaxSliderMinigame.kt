package com.example.lovebyte.ui.components.minigames

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.data.model.SliderBlock
import com.example.lovebyte.ui.components.general.PixelButton
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

    // color palette
    val deepPink = Color(0xFFFF85A1)
    val inkBrown = Color(0xFF5D4037)
    val pixelWhite = Color(0xFFFFFFFF)
    val softMatcha = Color(0xFFD8E2DC)
    val darkMatcha = Color(0xFF819289)
    val pixelRoundedShape = CutCornerShape(8.dp)

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
            shape = pixelRoundedShape,
            containerColor = pixelWhite,
            modifier = Modifier.border(4.dp, Color.Red, pixelRoundedShape),
            title = {
                Text(
                    "COMPILATION ERROR",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Red
                )
            },
            text = {
                Text(
                    "The logic slipped through your fingers. The system is unstable. Restart the dash or push through with errors?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = inkBrown
                )
            },
            confirmButton = {
                PixelButton(
                    text = "RETRY",
                    onClick = {
                        // reset local game state for a retry
                        timeLeft = timeLimitSeconds
                        currentActiveIndex = 0
                        isGameOver = false
                    },
                    color = deepPink
                )
            },
            dismissButton = {
                TextButton(onClick = {
                    onContinueAnyway()
                }) {
                    Text("CONTINUE ANYWAY", style = MaterialTheme.typography.labelLarge, color = inkBrown)
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
            .background(Color(0xFFFFF5F7))
            .padding(16.dp)
    ) {
        // header w/ accessibility switch and timer
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = if (isPublicMode) "PUBLIC MODE (DRAG)" else "PRIVATE MODE (TILT)",
                    style = MaterialTheme.typography.labelLarge,
                    color = deepPink
                )
                Switch(
                    checked = isPublicMode,
                    onCheckedChange = { isPublicMode = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = deepPink)
                )
            }

            // visual timer
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "SYSTEM STABILITY",
                    style = MaterialTheme.typography.labelSmall,
                    color = inkBrown
                )
                Text(
                    text = "${timeLeft}s",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (timeLeft < 10) Color.Red else darkMatcha
                )
            }
        }

        // stability progress bar
        LinearProgressIndicator(
            progress = { timeLeft.toFloat() / timeLimitSeconds.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .border(2.dp, inkBrown),
            color = if (timeLeft < 10) Color.Red else softMatcha,
            trackColor = pixelWhite,
            strokeCap = StrokeCap.Butt
        )

        Box(modifier = Modifier.weight(1f).padding(top = 24.dp)) {

            // background grid to show tab lines?
            // TODO: polish these eventually
            Row(Modifier.fillMaxSize()) {
                repeat(6) {
                    Box(
                        Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(deepPink.copy(alpha = 0.1f))
                    )
                    Spacer(Modifier.width(90.dp))
                }
            }

            // actual coding block lines
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(blocks, key = { _, block -> block.id }) { index, block ->
                    SyntaxPuzzleLine(
                        block = block,
                        isActive = index == currentActiveIndex && !isWaitingForNeutral && !isGameOver,
                        isSolved = index < currentActiveIndex,
                        tiltVelocity = tiltVelocity,
                        isPublicMode = isPublicMode,
                        tabWidthPx = tabWidthPx,
                        deepPink = deepPink,
                        inkBrown = inkBrown,
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .border(3.dp, deepPink, pixelRoundedShape),
            color = pixelWhite,
            shape = pixelRoundedShape
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    text = when {
                        isGameOver -> "SYSTEM CRASH!"
                        isWaitingForNeutral && !isPublicMode -> "HOLD STEADY..."
                        else -> "ASSEMBLE THE LOGIC"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isWaitingForNeutral || isGameOver) Color.Red else deepPink
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (isPublicMode) "Drag the block to its matching ghost slot."
                    else "Gently tilt your phone to slide the block into its slot.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = inkBrown
                )
            }
        }

        // "finished button"
        PixelButton(
            text = "RUN SCRIPT",
            onClick = { onFinished(true) },
            enabled = currentActiveIndex >= blocks.size && !isGameOver,
            color = if (currentActiveIndex >= blocks.size) softMatcha else Color.Gray,
            // Pass the darker color here for contrast
            textColor = if (currentActiveIndex >= blocks.size) inkBrown else Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (currentActiveIndex >= blocks.size) 1f else 0.5f)
        )
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
    deepPink: Color,
    inkBrown: Color,
    onSlotted: () -> Unit
) {
    val density = LocalDensity.current
    val offsetX = remember(block.id) { Animatable(0f) }
    val cardHorizontalPadding = 12.dp
    val coroutineScope = rememberCoroutineScope()

    val codeTextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 16.sp,
        fontFamily = FontFamily.Monospace,
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
                10f,
                infiniteRepeatable(tween(2000), RepeatMode.Reverse)
            )
        }
    }

    // physics engine for TILT
    LaunchedEffect(tiltVelocity, isActive) {
        if (isActive && !isPublicMode) {
            val targetX = block.targetLevel * tabWidthPx
            val newOffset = (offsetX.value + (tiltVelocity * 60f)).coerceIn(0f, 1000f)
            offsetX.snapTo(newOffset)

            if (abs(offsetX.value - targetX) < 30f) {
                offsetX.animateTo(targetX, spring(stiffness = Spring.StiffnessMediumLow))
                onSlotted()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Box(
            modifier = Modifier.matchParentSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            if (isActive || !isSolved) {
                // ghost target slot
                Surface(
                    modifier = Modifier
                        .offset(x = with(density) { (block.targetLevel * tabWidthPx).toDp() })
                        .border(1.dp, deepPink.copy(alpha = 0.3f), CutCornerShape(4.dp)),
                    color = deepPink.copy(alpha = 0.05f),
                    shape = CutCornerShape(4.dp)
                ) {
                    Text(
                        text = block.code,
                        modifier = Modifier.padding(
                            horizontal = cardHorizontalPadding,
                            vertical = 8.dp
                        ),
                        style = codeTextStyle,
                        color = Color.Transparent,
                        maxLines = 1
                    )
                }
            }

            // the sliding block
            Surface(
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
                                            if (abs(newX - targetX) < 30f) {
                                                offsetX.animateTo(targetX, spring(stiffness = Spring.StiffnessMediumLow))
                                                onSlotted()
                                            }
                                        }
                                    }
                                )
                            }
                        } else Modifier
                    )
                    .border(
                        width = if (isActive) 3.dp else 1.dp,
                        color = if (isSolved) Color(0xFF4CAF50) else if (isActive) deepPink else Color.Gray,
                        shape = CutCornerShape(4.dp)
                    ),
                color = if (isSolved) Color(0xFFE8F5E9) else Color.White,
                shape = CutCornerShape(4.dp)
            ) {
                Text(
                    text = block.code,
                    modifier = Modifier.padding(
                        horizontal = cardHorizontalPadding,
                        vertical = 8.dp
                    ),
                    style = codeTextStyle,
                    color = if (isSolved) Color(0xFF1B5E20)
                    else inkBrown,
                    maxLines = 1
                )
            }
        }
    }
}