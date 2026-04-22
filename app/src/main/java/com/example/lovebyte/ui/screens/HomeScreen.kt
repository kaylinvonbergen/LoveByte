package com.example.lovebyte.ui.screens
//HomeScreen.kt
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lovebyte.ui.theme.LoveByteTheme

import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.data.model.Chapter

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


@Composable
fun HomeScreen(
    state: LoveByteState,
    onContinueClicked: () -> Unit,
    onSwapClicked: () -> Unit,
    onLocationPermissionGranted: (android.content.Context) -> Unit,
    onLocationPermissionDenied: () -> Unit,
    onOnboardingNext: () -> Unit,
    onOnboardingFinish: () -> Unit,
) {
    // Identify the "onLocationPermissionGranted: () -> Unit,hero" for the header (most recent or random)
    val heroLanguage = if (state.currentLanguage != ProgrammingLanguage.NONE) {
        state.currentLanguage
    } else {
        ProgrammingLanguage.PYTHON
    }

    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onLocationPermissionGranted(context)
        } else {
            onLocationPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                onLocationPermissionGranted(context)
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    if (state.shouldShowOnboarding && state.onboardingStep == 1) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Welcome to LoveByte!") },
            text = {
                Text(
                    "LoveByte is an interactive way to learn the basics of certain programming languages by interacting with them and playing mini games. By talking to the languages, you’ll learn their syntax and maybe even get to know them!"
                )
            },
            confirmButton = {
                Button(onClick = onOnboardingNext) {
                    Text("Next")
                }
            }
        )
    }

    if (state.shouldShowOnboarding && state.onboardingStep == 2) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Mini-Game Sensors") },
            text = {
                Text(
                    "For the mini-games, you’ll be using the built-in sensors on your device. This may include tilting your phone, walking around a little, or covering your camera. If at any point you wish not to use your sensors, switch to “Private Mode,” and an alternate form of the game will be shown."
                )
            },
            confirmButton = {
                Button(onClick = onOnboardingFinish) {
                    Text("Okay")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        // 1. Header Section (Profile photo + Dynamic Greeting from weather)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // show the first letter of the hero language (e.g., 'P' for Python)
                    // TODO: replace with sprite once we make them
                    Text(
                        text = heroLanguage.displayName.take(1),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // dynamic message based on Weather :3
            val greetingText =
                if (state.cityName.isNotBlank() && state.weatherDescription.isNotBlank()) {
                    "Hey, it's ${state.weatherDescription.lowercase()} in ${state.cityName}. Perfect time for some ${heroLanguage.displayName}!"
                } else {
                    "Hey, you're back! Time to get to ${heroLanguage.displayName}!"
                }

            Text(
                text = greetingText,
                style = MaterialTheme.typography.titleMedium
            )
        }


        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 2. Character Sprite (helps fill the blank space)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = MaterialTheme.shapes.large
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${heroLanguage.displayName} Sprite",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Progress Section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Current Progress", style = MaterialTheme.typography.labelLarge)
                        Text("${state.progressPercentage}%", style = MaterialTheme.typography.labelLarge)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { state.progressFraction },
                        modifier = Modifier.fillMaxWidth().height(12.dp),
                        strokeCap = Round
                    )
                    Spacer(Modifier.height(8.dp))
                    val displayChapter = if (state.currentLanguage == ProgrammingLanguage.NONE) {
                        1
                    } else {
                        state.currentChapter
                    }

                    Text(
                        text = "${heroLanguage.displayName}: Chapter $displayChapter",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }



        // 4. Action Buttons
        Column(
            modifier = Modifier.padding(top = 24.dp), // add a little breathing room
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onContinueClicked,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                //enabled = state.currentLanguage != ProgrammingLanguage.NONE
            ) {
                Text(if (state.currentLanguage != ProgrammingLanguage.NONE) "Continue" else "Start")
            }

            OutlinedButton(
                onClick = onSwapClicked,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("Swap Routes")
            }
        }
    }
}