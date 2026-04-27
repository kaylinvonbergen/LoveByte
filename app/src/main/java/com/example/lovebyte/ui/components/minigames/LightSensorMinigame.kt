package com.example.lovebyte.ui.components.minigames

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.ui.components.general.PixelButton
import kotlinx.coroutines.delay

@Composable
fun LightSensorMinigame(
    onFinished: (Boolean) -> Unit,
    onContinueAnyway: () -> Unit
) {
    // access Android System Service stuff for sensors w/ the current Context
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    // color palatte
    val softMatcha = Color(0xFFB2F2BB)
    val deepPink = Color(0xFFFF85A1)
    val inkBrown = Color(0xFF5D4037)
    val pixelWhite = Color(0xFFFFFFFF)

    // for toggle between sensor and "public" mode
    var isPublicMode by remember { mutableStateOf(false) }

    // hold current lux value
    var luxValue by remember { mutableFloatStateOf(100f) }

    // control visibility of bypass button
    var showSkipButton by remember { mutableStateOf(false) }

    // give it a warmup so it doesn't just immediately assume it's dark enough
    var canFinish by remember(isPublicMode) { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1000L) /// wait 1s to let the sensor calibrate
        canFinish = true
    }

    // show skip button after 5 seconds (matched your code delay)
    LaunchedEffect(Unit) {
        delay(5000L)
        showSkipButton = true
    }

    // register sensor only if NOT in public mode
    DisposableEffect(isPublicMode) {
        if (isPublicMode) {
            // reset lux value when entering public mode so we don't have stale dark values
            luxValue = 100f
            onDispose { }
        } else {
            // get listener for hardware light changes
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                        luxValue = event.values[0]
                        // only finish if the sensor "warmup" is done and it's dark enough
                        if (canFinish && luxValue < 10f) {
                            onFinished(true)
                        }
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }
            // listen to the sensor hardware
            sensorManager.registerListener(listener, lightSensor, SensorManager.SENSOR_DELAY_UI)

            // dispose the sensor once the minigame is closed in order to save battery
            onDispose {
                sensorManager.unregisterListener(listener)
                // rsetting lux here prevents auto-win on re-entry
                luxValue = 100f
            }
        }
    }

    // main UI container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f)) // slightly darker for "Shadows" theme
            .padding(24.dp)
    ) {
        // accessibility toggle ("public" mode)
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isPublicMode,
                onCheckedChange = { isPublicMode = it },
                colors = SwitchDefaults.colors(checkedThumbColor = deepPink)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = if (isPublicMode) "PUBLIC MODE" else "SENSOR MODE",
                color = pixelWhite.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium // pixel font
            )
        }

        // central content column area
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "THE SHADOWS PROTECT THE DATA",
                color = pixelWhite,
                style = MaterialTheme.typography.titleMedium, // Pixel font
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))

            // switches the button types if needed
            if (isPublicMode) {
                Text(
                    "Tap the sun to extinguish the light...",
                    color = softMatcha,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(48.dp))

                // the "sun" button - converted to pixel style
                IconButton(
                    onClick = { onFinished(true) },
                    modifier = Modifier
                        .size(100.dp)
                        .background(softMatcha, CutCornerShape(12.dp))
                        .border(4.dp, pixelWhite, CutCornerShape(12.dp))
                ) {
                    Text("☀️", fontSize = 40.sp)
                }
            } else {
                Text(
                    "Cover your light sensor to encrypt...",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(48.dp))

                // visual feedback showing how close the user is to the dark threshold
                CircularProgressIndicator(
                    progress = { (luxValue / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier.size(80.dp),
                    color = softMatcha,
                    trackColor = Color.DarkGray,
                    strokeWidth = 8.dp
                )
            }

            // bypass to allow for failure after timer
            if (showSkipButton) {
                Spacer(Modifier.height(64.dp))
                PixelButton(
                    text = "BYPASS SENSOR",
                    onClick = onContinueAnyway,
                    color = Color.Transparent,
                    textColor = pixelWhite,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
    }
}