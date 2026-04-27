package com.example.lovebyte.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.ui.components.general.PixelButton

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
    // identify the "hero" for the header (most recent or random)
    val heroLanguage = if (state.currentLanguage != ProgrammingLanguage.NONE) {
        state.currentLanguage
    } else {
        ProgrammingLanguage.PYTHON
    }

    val context = LocalContext.current

    // color palette
    val sakuraPink = Color(0xFFFFB7C5)
    val deepPink = Color(0xFFFF85A1)
    val inkBrown = Color(0xFF5D4037)
    val pixelWhite = Color(0xFFFFFFFF)

    val pixelRoundedShape = CutCornerShape(8.dp)

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
            shape = pixelRoundedShape,
            containerColor = pixelWhite,
            modifier = Modifier.border(4.dp, deepPink, pixelRoundedShape), // make more pixel-coded
            title = { Text("Welcome to LoveByte!", style = MaterialTheme.typography.titleMedium, color = deepPink) },
            text = {
                Text(
                    "LoveByte is an interactive way to learn the basics of certain programming languages by interacting with them and playing mini games. By talking to the languages, you’ll learn their syntax and maybe even get to know them!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = inkBrown
                )
            },
            confirmButton = {
                PixelButton(onClick = onOnboardingNext, text = "Next", color = deepPink)
            }
        )
    }

    if (state.shouldShowOnboarding && state.onboardingStep == 2) {
        AlertDialog(
            onDismissRequest = { },
            shape = pixelRoundedShape,
            containerColor = pixelWhite,
            modifier = Modifier.border(4.dp, deepPink, pixelRoundedShape),
            title = { Text("Mini-Game Sensors", style = MaterialTheme.typography.titleMedium, color = deepPink) },
            text = {
                Text(
                    "For the mini-games, you’ll be using the built-in sensors on your device. This may include tilting your phone, walking around a little, or covering your camera. If at any point you wish not to use your sensors, switch to “Private Mode,” and an alternate form of the game will be shown.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = inkBrown
                )
            },
            confirmButton = {
                PixelButton(onClick = onOnboardingFinish, text = "Okay", color = deepPink)
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF5F7)
    ) {
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
                    modifier = Modifier
                        .size(72.dp)
                        .border(4.dp, deepPink, pixelRoundedShape),
                    shape = pixelRoundedShape,
                    color = pixelWhite
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // show the first letter of the hero language (e.g., 'P' for Python)
                        // TODO: replace with sprite once we make them
                        Text(
                            text = heroLanguage.displayName.take(1),
                            style = MaterialTheme.typography.headlineMedium,
                            color = deepPink
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // dynamic message based on Weather :3
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = CutCornerShape(topStart = 0.dp, bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp),
                    color = pixelWhite,
                    border = BorderStroke(3.dp, deepPink)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = heroLanguage.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            color = deepPink,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                        val greetingText =
                            if (state.cityName.isNotBlank() && state.weatherDescription.isNotBlank()) {
                                "Hey, it's ${state.weatherDescription.lowercase()} in ${state.cityName}. Perfect time for some ${heroLanguage.displayName}!"
                            } else {
                                "Hey, you're back! Time to get to ${heroLanguage.displayName}!"
                            }

                        Text(
                            text = greetingText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = inkBrown
                        )
                    }
                }
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
                    color = sakuraPink.copy(alpha = 0.1f),
                    shape = pixelRoundedShape,
                    border = BorderStroke(2.dp, deepPink.copy(alpha = 0.2f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${heroLanguage.displayName} Sprite",
                            style = MaterialTheme.typography.titleMedium,
                            color = deepPink
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // 3. Progress Section
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = pixelRoundedShape,
                    border = BorderStroke(4.dp, deepPink),
                    color = pixelWhite
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("SYNERGY", style = MaterialTheme.typography.labelLarge, color = deepPink)
                            Text("${state.progressPercentage}%", style = MaterialTheme.typography.labelLarge, color = deepPink)
                        }
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { state.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .border(3.dp, deepPink),
                            color = sakuraPink,
                            trackColor = Color(0xFFFDEEF4),
                            strokeCap = StrokeCap.Butt
                        )
                        Spacer(Modifier.height(8.dp))
                        val displayChapter = if (state.currentLanguage == ProgrammingLanguage.NONE) {
                            1
                        } else {
                            state.currentChapter
                        }

                        Text(
                            text = "${heroLanguage.displayName.uppercase()}: CHAPTER $displayChapter",
                            style = MaterialTheme.typography.labelLarge,
                            color = inkBrown,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // 4. Action Buttons
            Column(
                modifier = Modifier.padding(top = 24.dp), // add a little breathing room
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PixelButton(
                    onClick = onContinueClicked,
                    text = if (state.currentLanguage != ProgrammingLanguage.NONE) "CONTINUE" else "START",
                    color = deepPink
                )

                PixelButton(
                    onClick = onSwapClicked,
                    text = "SWAP ROUTES",
                    color = Color(0xFFB19CD9)
                )
            }
        }
    }
}
