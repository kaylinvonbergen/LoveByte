package com.example.lovebyte.ui.components.minigames
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// for 'by remember' delegated properties
// https://developer.android.com/develop/ui/compose/state
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.SolidColor

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

    // for toggle between sensor and "public" mode
    var isPublicMode by remember { mutableStateOf(false) }

    // hold current lux value
    var luxValue by remember { mutableFloatStateOf(100f) }

    // control visibility of bypass button
    var showSkipButton by remember { mutableStateOf(false) }

    // give it a warmup so it doesn't just immediately assume it's dark enough
    var canFinish by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        delay(500L) /// wait 0.5s to let the sensor calibrate
        canFinish = true
    }

    // show skip button after 10 seconds
    LaunchedEffect(Unit) {
        delay(5000L)
        showSkipButton = true
    }



    // register sensor only if NOT in public mode
    DisposableEffect(isPublicMode) {
        if (isPublicMode) {
            // dispose it if we're in public mode
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
            onDispose { sensorManager.unregisterListener(listener) }
        }
    }

    // main UI container
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(24.dp)
    ) {
        // accessibility toggle ("public" mode)
        Row(
            modifier = Modifier.align(Alignment.TopStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isPublicMode,
                onCheckedChange = { isPublicMode = it }
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isPublicMode) "Public Mode" else "Sensor Mode",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium
            )
        }

        // central content column area
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "THE SHADOWS PROTECT THE DATA",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.height(8.dp))

            // switches the button types if needed
            if (isPublicMode) {
                Text("Tap the sun to extinguish the light...", color = Color.Yellow.copy(alpha = 0.8f))
                Spacer(Modifier.height(32.dp))
                // the "sun" button
                IconButton(
                    onClick = { onFinished(true) },
                    modifier = Modifier.size(80.dp).background(Color.Yellow, MaterialTheme.shapes.extraLarge)
                ) {
                    Text("☀️", style = MaterialTheme.typography.headlineLarge)
                }
            // normal sensor version
            } else {
                Text("Cover your light sensor to encrypt...", color = Color.Gray)
                Spacer(Modifier.height(32.dp))
                // visual feedback showing how close the user is to the dark threshold
                CircularProgressIndicator(
                    progress = { (luxValue / 100f).coerceIn(0f, 1f) },
                    color = Color.Yellow,
                    trackColor = Color.DarkGray
                )
            }

            // bypass to allow for failure after 10 second timer
            if (showSkipButton) {
                Spacer(Modifier.height(48.dp))
                OutlinedButton(
                    onClick = onContinueAnyway,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        "BYPASS SENSOR",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}