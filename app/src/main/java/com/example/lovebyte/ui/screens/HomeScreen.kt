package com.example.lovebyte.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.ui.components.general.PixelButton
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings

import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import com.example.lovebyte.ui.components.general.getSpriteForCharacter

import androidx.compose.ui.res.painterResource

import com.example.lovebyte.R

import com.example.lovebyte.ui.components.general.LoveByteHeader


@Composable
fun HomeScreen(
    state: LoveByteState,
    onContinueClicked: () -> Unit,
    onSwapClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    onOnboardingNext: () -> Unit,
    onOnboardingPlacementComplete: (pythonLevel: Int, kotlinLevel: Int) -> Unit,
    onOnboardingFinish: () -> Unit,
) {
    val heroLanguage = if (state.currentLanguage != ProgrammingLanguage.NONE) {
        state.currentLanguage
    } else {
        ProgrammingLanguage.PYTHON
    }

    // val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val sakuraPink = Color(0xFFFFB7C5)
    val deepPink = Color(0xFFFF85A1)
    val inkBrown = Color(0xFF5D4037)
    val pixelWhite = Color(0xFFFFFFFF)
    val pixelRoundedShape = CutCornerShape(8.dp)

    var pythonLevel by remember { mutableIntStateOf(1) }
    var kotlinLevel by remember { mutableIntStateOf(1) }

    // Onboarding Dialogs
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

    // proficiency ranking
    if (state.shouldShowOnboarding && state.onboardingStep == 2) {
        AlertDialog(
            onDismissRequest = { },
            shape = pixelRoundedShape,
            containerColor = pixelWhite,
            modifier = Modifier.border(4.dp, deepPink, pixelRoundedShape),
            title = {
                Text(
                    "Choose Your Starting Point",
                    style = MaterialTheme.typography.titleMedium,
                    color = deepPink
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Rank your current proficiency for each language from 1 to 3.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = inkBrown
                    )

                    ProficiencyRow(
                        languageName = "Python",
                        selectedLevel = pythonLevel,
                        onLevelSelected = { pythonLevel = it },
                        inkBrown = inkBrown,
                        deepPink = deepPink
                    )

                    ProficiencyRow(
                        languageName = "Kotlin",
                        selectedLevel = kotlinLevel,
                        onLevelSelected = { kotlinLevel = it },
                        inkBrown = inkBrown,
                        deepPink = deepPink
                    )
                }
            },
            confirmButton = {
                PixelButton(
                    onClick = {
                        onOnboardingPlacementComplete(pythonLevel, kotlinLevel)
                    },
                    text = "Next",
                    color = deepPink
                )
            }
        )
    }

    // tell user where they start
    if (state.shouldShowOnboarding && state.onboardingStep == 3) {
        val pythonStartChapter = state.progressMap[ProgrammingLanguage.PYTHON] ?: 1
        val kotlinStartChapter = state.progressMap[ProgrammingLanguage.KOTLIN] ?: 1

        AlertDialog(
            onDismissRequest = { },
            shape = pixelRoundedShape,
            containerColor = pixelWhite,
            modifier = Modifier.border(4.dp, deepPink, pixelRoundedShape),
            title = {
                Text(
                    "Starting Placement",
                    style = MaterialTheme.typography.titleMedium,
                    color = deepPink
                )
            },
            text = {
                Text(
                    "You have been placed at Chapter $pythonStartChapter for Python and Chapter $kotlinStartChapter for Kotlin.\n\nFeel free to go back and complete earlier chapters anyway!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = inkBrown
                )
            },
            confirmButton = {
                PixelButton(
                    onClick = onOnboardingNext,
                    text = "Next",
                    color = deepPink
                )
            }
        )
    }

    // explain mini-games
    if (state.shouldShowOnboarding && state.onboardingStep == 4) {
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
                .padding(24.dp)
        ) {
            // header Section stays at the top for BOTH modes
            LoveByteHeader(
                heroLanguage = heroLanguage,
                state = state,
                deepPink = deepPink,
                inkBrown = inkBrown,
                pixelWhite = pixelWhite,
                pixelRoundedShape = pixelRoundedShape
            )

            // handles centering for portrait while allowing the row to split for landscape
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isLandscape) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            SpriteSection(heroLanguage, sakuraPink, deepPink, pixelRoundedShape, Modifier.fillMaxHeight())
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        Column(
                            modifier = Modifier
                                .weight(1.2f)
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ProgressSection(state, heroLanguage, deepPink, sakuraPink, inkBrown, pixelWhite, pixelRoundedShape)
                            ActionButtons(state, deepPink, onContinueClicked, onSwapClicked, onSettingsClicked, isLandscape)
                        }
                    }
                } else {
                    // PORTRAIT MODE
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        SpriteSection(
                            heroLanguage,
                            sakuraPink,
                            deepPink,
                            pixelRoundedShape,
                            Modifier.height(380.dp)
                        )

                        Box(modifier = Modifier.offset(y = (-20).dp)) {
                            ProgressSection(
                                state,
                                heroLanguage,
                                deepPink,
                                sakuraPink,
                                inkBrown,
                                pixelWhite,
                                pixelRoundedShape
                            )
                        }
                    }
                }
            }

            // buttons pinned to bottom in portrait mode
            if (!isLandscape) {
                ActionButtons(state, deepPink, onContinueClicked, onSwapClicked, onSettingsClicked, isLandscape)
            }
        }
    }
}

@Composable
private fun SpriteSection(
    heroLanguage: ProgrammingLanguage,
    sakuraPink: Color,
    deepPink: Color,
    pixelRoundedShape: CutCornerShape,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent, // removes the background tint to let the sprite pop
        shape = pixelRoundedShape,
    ) {
        Box(contentAlignment = Alignment.BottomCenter) { // aligns sprite to the bottom of the box
            Image(
                painter = getSpriteForCharacter(
                    character = heroLanguage.displayName,
                    emotion = "Friendly"
                ),
                contentDescription = "${heroLanguage.displayName} Sprite",
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.FillHeight // ensures the character scales to the box height
            )
        }
    }
}

@Composable
private fun ProgressSection(
    state: LoveByteState,
    heroLanguage: ProgrammingLanguage,
    deepPink: Color,
    sakuraPink: Color,
    inkBrown: Color,
    pixelWhite: Color,
    pixelRoundedShape: CutCornerShape
) {
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
            val displayChapter = if (state.currentLanguage == ProgrammingLanguage.NONE) 1 else state.currentChapter

            Text(
                text = "${heroLanguage.displayName.uppercase()}: CHAPTER $displayChapter",
                style = MaterialTheme.typography.labelLarge,
                color = inkBrown,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun ActionButtons(
    state: LoveByteState,
    deepPink: Color,
    onContinueClicked: () -> Unit,
    onSwapClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    isLandscape: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isLandscape) 0.dp else 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val buttonWidth = if (isLandscape) 0.8f else 1f
        PixelButton(
            onClick = onContinueClicked,
            text = if (state.currentLanguage != ProgrammingLanguage.NONE) "CONTINUE" else "START",
            color = deepPink,
            modifier = Modifier.fillMaxWidth(buttonWidth)
        )

        Row(
            modifier = Modifier.fillMaxWidth(buttonWidth),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PixelButton(
                onClick = onSwapClicked,
                text = "SWAP ROUTES",
                color = Color(0xFFB19CD9),
                modifier = Modifier.weight(1f)
            )

            PixelButton(
                onClick = onSettingsClicked,
                color = Color(0xFFB2F2BB),
                modifier = Modifier
                    .width(64.dp)
                    .height(56.dp),
                content = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color(0xFF5D4037)
                    )
                }
            )
        }
    }
}

@Composable
fun ProficiencyRow(
    languageName: String,
    selectedLevel: Int,
    onLevelSelected: (Int) -> Unit,
    inkBrown: Color,
    deepPink: Color
) {
    Column {
        Text(
            text = languageName,
            style = MaterialTheme.typography.labelLarge,
            color = deepPink
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(1, 2, 3).forEach { level ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedLevel == level,
                        onClick = { onLevelSelected(level) }
                    )
                    Text(
                        text = level.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = inkBrown
                    )
                }
            }
        }
    }
}