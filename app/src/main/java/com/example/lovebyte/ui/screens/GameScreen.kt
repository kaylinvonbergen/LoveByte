package com.example.lovebyte.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.data.model.*
import com.example.lovebyte.ui.components.minigames.*
import com.example.lovebyte.data.content.*
import com.example.lovebyte.ui.components.general.PixelButton

// game screen composable
@Composable
fun GameScreen(
    state: LoveByteState,
    currentNode: DialogueNode?,
    onNodeAdvanced: (Int) -> Unit,
    onChoiceSelected: (DialogueChoice) -> Unit,
    onMinigameResult: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
    onNextChapter: () -> Unit,
    onChapterCompleted: () -> Unit
) {
    var showChapterComplete by remember { mutableStateOf(false) }

    // color palette
    val sakuraPink = Color(0xFFFFB7C5)
    val deepPink = Color(0xFFFF85A1)
    val inkBrown = Color(0xFF5D4037)
    val pixelWhite = Color(0xFFFFFFFF)
    val pixelRoundedShape = CutCornerShape(8.dp)

    // dynamically select blocks based on the current character/language
    val activeBlocks = when (state.currentLanguage.name.uppercase()) {
        "PYTHON" -> pythonChapter1Blocks
        // "KOTLIN" -> kotlinChapter1Blocks // TODO: add this when we create Kotlin's minigame
        else -> emptyList()
    }

    // if chapter completed, show a little pop-up that allows you to move to the next or back to chapter selection
    if (showChapterComplete) {
        AlertDialog(
            onDismissRequest = { },
            shape = pixelRoundedShape,
            containerColor = pixelWhite,
            modifier = Modifier.border(4.dp, deepPink, pixelRoundedShape),
            title = {
                Text(
                    "CHAPTER COMPLETE!",
                    style = MaterialTheme.typography.titleMedium, // pixelated
                    color = deepPink
                )
            },
            text = {
                Text(
                    "You've successfully learned more about ${state.currentLanguage.name}'! Ready for the next challenge?",
                    style = MaterialTheme.typography.bodyLarge, // standard
                    color = inkBrown
                )
            },
            confirmButton = {
                // next chapter
                PixelButton(
                    text = "NEXT CHAPTER",
                    onClick = {
                        showChapterComplete = false
                        onNextChapter()
                    },
                    color = deepPink
                )
            },
            // chapter select time!
            dismissButton = {
                TextButton(onClick = onBackPressed) {
                    Text("CHAPTER SELECT", style = MaterialTheme.typography.labelLarge, color = inkBrown)
                }
            }
        )
    }

    // check for if a minigame is active, check for the minigame
    if (state.isMiniGameActive && currentNode?.triggerEvent != null) {
        when (currentNode.triggerEvent) {
            "SYNTAX_DASH" -> {
                SyntaxSliderMinigame(
                    blocks = activeBlocks,
                    onFinished = { success ->
                        // Route FIRST, then close the minigame overlay
                        if (success) onNodeAdvanced(109) else onNodeAdvanced(110)
                        onMinigameResult(success)
                    },
                    onContinueAnyway = {
                        onMinigameResult(false)
                        onNodeAdvanced(110)
                    }
                )
            }

            "LIGHT_SENSITIVE_SECRET" -> {
                LightSensorMinigame(
                    onFinished = { success ->
                        onMinigameResult(success)
                        // route to Chapter 3 nodes specifically
                        if (success) onNodeAdvanced(306) else onNodeAdvanced(307)
                    },
                    onContinueAnyway = {
                        onMinigameResult(false)
                        onNodeAdvanced(307)
                    }
                )
            }

            "EFFICIENCY_STEPPER" -> {
                EfficiencyStepper(
                    challenge = pythonChapterxLoopChallenges,
                    onFinished = { success ->
                        onMinigameResult(success)
                        if (success) onNodeAdvanced(208) else onNodeAdvanced(209)
                    },
                    onContinueAnyway = {
                        onMinigameResult(false)
                        onNodeAdvanced(209)
                    }
                )
            }
        }
        // if there's a current node, display the associated text and sprite
        // means no minigame is active
    } else if (currentNode != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background tint
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = sakuraPink.copy(alpha = 0.05f)
            ) {}

            // Sprite Area
            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = 260.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "[ ${state.currentLanguage.name.uppercase()} SPRITE ]",
                        style = MaterialTheme.typography.headlineMedium, // pixelated
                        color = deepPink
                    )
                    Surface(
                        shape = pixelRoundedShape,
                        color = Color(0xFFB19CD9), // lavender for emotion tags
                        modifier = Modifier.padding(top = 12.dp).border(2.dp, pixelWhite, pixelRoundedShape)
                    ) {
                        Text(
                            currentNode.emotion.uppercase(),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge, // pixelated
                            color = Color.White
                        )
                    }
                }
            }

            // dialogue UI
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                // physics engine notes: the card handles the main dialogue flow
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 160.dp)
                        .border(4.dp, deepPink, CutCornerShape(topStart = 20.dp, bottomEnd = 20.dp)),
                    shape = CutCornerShape(topStart = 20.dp, bottomEnd = 20.dp),
                    color = pixelWhite,
                    onClick = {
                        // safety check: don't advance if there are active choices to click
                        if (currentNode.choices.isNullOrEmpty()) {
                            if (currentNode.nextNodeId != null) {
                                onNodeAdvanced(currentNode.nextNodeId)
                            } else {
                                onChapterCompleted()
                                showChapterComplete = true
                            }
                        }
                    }
                ) {
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            text = currentNode.speaker,
                            style = MaterialTheme.typography.labelLarge, // Pixelated
                            color = deepPink,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = currentNode.text,
                            style = MaterialTheme.typography.bodyLarge, // Standard for readability
                            lineHeight = 24.sp,
                            color = inkBrown
                        )

                        if (currentNode.choices.isNullOrEmpty()) {
                            Box(Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.BottomEnd) {
                                Text("▼", style = MaterialTheme.typography.labelLarge, color = deepPink)
                            }
                        }
                    }
                }

                // branching choice, generates vertical list of buttons
                if (!currentNode.choices.isNullOrEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        currentNode.choices.forEach { choice ->
                            PixelButton(
                                text = choice.choiceText,
                                onClick = { onChoiceSelected(choice) },
                                color = Color(0xFFB19CD9) // Soft Lavender for choices
                            )
                        }
                    }
                }
            }
        }
        // safety net if an error occurs, returns us back to timeline instead of crashing app
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            PixelButton(
                text = "RETURN TO TIMELINE",
                onClick = onBackPressed,
                color = deepPink
            )
        }
    }
}