package com.example.lovebyte.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.data.model.*
import com.example.lovebyte.ui.components.minigames.SyntaxSliderMinigame
import com.example.lovebyte.data.content.*

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
    // node initiation
    // TODO: make this not a dummy node soon
    var showChapterComplete by remember { mutableStateOf(false) }

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
            title = { Text("Chapter Complete!") },
            text = { Text("You've successfully mastered the basics of ${state.currentLanguage.name}'s syntax. Ready for the next challenge?") },
            confirmButton = {
                // next chapter
                Button(onClick = {
                    showChapterComplete = false
                    onNextChapter()
                }) {
                    Text("Next Chapter")
                }
            },
            // chapter select time!
            dismissButton = {
                TextButton(onClick = onBackPressed) {
                    Text("Chapter Select")
                }
            }
        )
    }

    // check for if a minigame is active, check for the minigame
    if (state.isMiniGameActive && currentNode?.triggerEvent == "SYNTAX_DASH") {
        SyntaxSliderMinigame(
            blocks = activeBlocks,
            onFinished = { success ->
                // close the game state
                onMinigameResult(success)

                // logic-based branching
                // explicitly route to prevent the engine from guessing the next ID
                if (success) {
                    onNodeAdvanced(109) // success node (Blushing)
                } else {
                    onNodeAdvanced(110) // failure/retry node (Pensive)
                }
            },
            onContinueAnyway = {
                // user failed but clicked "Continue Anyway"
                // tell the engine where to go BEFORE closing the overlay
                onNodeAdvanced(110)
                // pass false so the ViewModel knows it wasn't a perfect run
                onMinigameResult(false)
            }
        )
        // if there's a current node, display the associated text and sprite
        // means no minigame is active
    } else if (currentNode != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.1f)
            ) {}

            Box(
                modifier = Modifier.fillMaxSize().padding(bottom = 240.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "[ ${state.currentLanguage.name} Sprite ]",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            currentNode.emotion,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 160.dp),
                    shape = MaterialTheme.shapes.extraLarge,
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
                    // physics engine notes: the card handles the main dialogue flow
                    Column(Modifier.padding(24.dp)) {
                        Text(
                            text = currentNode.speaker,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = currentNode.text,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp
                        )

                        if (currentNode.choices.isNullOrEmpty()) {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomEnd) {
                                Text("▼", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // branching choice, generates vertical list of buttons
                if (!currentNode.choices.isNullOrEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        currentNode.choices.forEach { choice ->
                            Button(
                                onClick = { onChoiceSelected(choice) },
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Text(choice.choiceText)
                            }
                        }
                    }
                }
            }
        }
        // safety net if an error occurs, returns us back to timeline instead of crashing app
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Button(onClick = onBackPressed) {
                Text("Return to Timeline")
            }
        }
    }
}