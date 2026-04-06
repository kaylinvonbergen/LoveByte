package com.example.lovebyte.ui.components.minigames

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

import com.example.lovebyte.data.model.SliderBlock

// the base game composable
@Composable
fun SyntaxSliderMinigame(
    blocks: List<SliderBlock>,
    onFinished: (Boolean) -> Unit
) {
    // is the block "active" (being slid into place by the user)
    var currentActiveIndex by remember { mutableIntStateOf(0) }
    // trying to wait for a return to a neutral position before moving
    var isWaitingForNeutral by remember { mutableStateOf(false) }
    val tabWidthPx = 120f
    val context = LocalContext.current

    // sensor stuff!
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    var tiltVelocity by remember { mutableFloatStateOf(0f) }

    // https://developer.android.com/develop/ui/compose/side-effects
    // helps avoid the "leakage" of the sensor listeners
    // Unit tells it to run exactly once
    DisposableEffect(Unit) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
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
                        isActive = index == currentActiveIndex && !isWaitingForNeutral,
                        isSolved = index < currentActiveIndex,
                        tiltVelocity = tiltVelocity,
                        tabWidthPx = tabWidthPx,
                        onSlotted = {
                            if (index == currentActiveIndex) {
                                isWaitingForNeutral = true
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
                    text = if (isWaitingForNeutral) "Hold steady..." else "Assemble the Logic",
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (isWaitingForNeutral)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Gently tilt your phone to slide the block into its slot.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )
            }
        }

        // "finished button"
        // TODO: maybe try to make it possible to fail this game later
        Button(
            onClick = { onFinished(currentActiveIndex >= blocks.size) },
            enabled = currentActiveIndex >= blocks.size,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        ) {
            Text("Run Script", style = MaterialTheme.typography.titleLarge)
        }
    }
}

// puzzle line composable!
@Composable
private fun SyntaxPuzzleLine(
    block: SliderBlock,
    isActive: Boolean,
    isSolved: Boolean,
    tiltVelocity: Float,
    tabWidthPx: Float,
    onSlotted: () -> Unit
) {
    val density = LocalDensity.current
    val offsetX = remember(block.id) { Animatable(0f) }
    val cardHorizontalPadding = 12.dp

    val codeTextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 16.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )

    // https://developer.android.com/develop/ui/compose/side-effects
    // state manager for the blocks, how should it be behaving?
    LaunchedEffect(isActive, isSolved, block.id) {
        when {
            // lock it down if it's solved
            isSolved -> offsetX.animateTo(block.targetLevel * tabWidthPx)
            // stops moving
            isActive -> {
                offsetX.stop()
                offsetX.snapTo(0f)
            }
            // shimmies around while idle
            else -> offsetX.animateTo(
                20f,
                infiniteRepeatable(tween(2000), RepeatMode.Reverse)
            )
        }
    }

    // more or less the physics engine
    LaunchedEffect(tiltVelocity, isActive) {
        if (isActive) {
            val targetX = block.targetLevel * tabWidthPx
            // take current position, add tilt speed, coerce it so it doesn't fly off the screen
            val newOffset = (offsetX.value + (tiltVelocity * 50f)).coerceIn(0f, 1000f)

            // update the position
            offsetX.snapTo(newOffset)

            // snap it to correct if it's within 25 pixels. nice little "click" animation.
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

            // ghost slots to show where to put the code
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

            // active block
            Card(
                modifier = Modifier.offset(
                    x = with(density) { offsetX.value.toDp() }
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