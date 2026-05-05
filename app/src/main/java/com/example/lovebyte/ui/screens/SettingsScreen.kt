package com.example.lovebyte.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.ui.components.general.PixelButton

@Composable
fun SettingsScreen(
    state: LoveByteState,
    onPrivateModeChanged: (Boolean) -> Unit,
    onReplayOnboarding: () -> Unit,
    onChangeProficiency: () -> Unit,
    onBackClicked: () -> Unit
) {
    val context = LocalContext.current

    val locationGranted =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    val activityGranted =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_GRANTED

    val deepPink = Color(0xFFE85D7A)
    val inkBrown = Color(0xFF5D4037)
    val pixelWhite = Color.White
    val pixelShape = CutCornerShape(8.dp)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF5F7)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PixelButton(
                    text = "←",
                    onClick = onBackClicked,
                    color = Color(0xFFB2F2BB),
                    textColor = Color.Black,
                    modifier = Modifier.width(64.dp)
                )

                Text(
                    text = "SETTINGS",
                    style = MaterialTheme.typography.headlineMedium,
                    color = deepPink
                )

                Spacer(modifier = Modifier.width(64.dp)) // keeps title centered
            }

            // -------------------------
            // PRIVATE MODE
            // -------------------------
            SettingsCard(deepPink, pixelShape) {
                SettingSwitchRow(
                    title = "Private Mode by Default",
                    description = "Use alternate controls instead of sensors in mini-games.",
                    checked = state.privateModeDefault,
                    onCheckedChange = onPrivateModeChanged,
                    inkBrown = inkBrown
                )
            }

            // -------------------------
            // LEARNING SETTINGS
            // -------------------------
            SettingsCard(deepPink, pixelShape) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    PixelButton(
                        onClick = onChangeProficiency,
                        text = "CHANGE STARTING PROFICIENCY",
                        color = deepPink,
                        modifier = Modifier.fillMaxWidth()
                            .testTag("change_proficiency_button"),
                    )

                    PixelButton(
                        onClick = onReplayOnboarding,
                        text = "REPLAY ONBOARDING",
                        color = deepPink,
                        modifier = Modifier.fillMaxWidth()
                            .testTag("replay_onboarding_button"),
                    )
                }
            }

            // -------------------------
            // PERMISSIONS
            // -------------------------
            SettingsCard(deepPink, pixelShape) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    PermissionRow(
                        "Location Permission",
                        locationGranted,
                        inkBrown
                    )

                    PermissionRow(
                        "Activity Sensor Permission",
                        activityGranted,
                        inkBrown
                    )

                    Text(
                        text = "LoveByte uses location for dynamic weather-based dialogue and activity/sensor permissions for interactive mini-games. Private Mode allows you to play without using sensors.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = inkBrown,
                        modifier = Modifier.testTag("permission_explanation")
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCard(
    borderColor: Color,
    shape: CutCornerShape,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(4.dp, borderColor, shape),
        shape = shape,
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    inkBrown: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = inkBrown)
            Text(description, style = MaterialTheme.typography.bodySmall, color = inkBrown)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag("private_mode_switch")
        )
    }
}

@Composable
private fun PermissionRow(
    title: String,
    granted: Boolean,
    inkBrown: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, color = inkBrown)

        Text(
            text = if (granted) "Granted" else "Not Granted",
            color = if (granted) Color(0xFF4CAF50) else Color.Red
        )
    }
}