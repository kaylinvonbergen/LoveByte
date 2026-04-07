package com.example.lovebyte.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.DialogueNode
import com.example.lovebyte.data.model.DialogueChoice
import com.example.lovebyte.ui.components.minigames.SyntaxSliderMinigame
import com.example.lovebyte.data.model.SliderBlock

// game screen composable
@Composable
fun GameScreen(
    state: LoveByteState,
    currentNode: DialogueNode?,
    onNodeAdvanced: (Int) -> Unit,
    onChoiceSelected: (DialogueChoice) -> Unit,
    onMinigameResult: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
    onNextChapter: () -> Unit
) {
    // node initiation
    // TODO: make this not a dummy node soon
    var showChapterComplete by remember { mutableStateOf(false) }

    // if chapter completed, show a little pop-up that allows you to move to the next or back to chapter selection
    if (showChapterComplete) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Chapter Complete!") },
            text = { Text("You've successfully mastered the basics of Python's syntax. Ready for the next challenge?") },
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
            blocks = pythonChapter1Blocks,
            onFinished = onMinigameResult
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
                        if (currentNode.choices.isNullOrEmpty()) {
                            if (currentNode.nextNodeId != null) {
                                onNodeAdvanced(currentNode.nextNodeId)
                            } else {
                                showChapterComplete = true
                            }
                        }
                    }
                ) {
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

// dummy data

val pythonChapter1Blocks = listOf(
    SliderBlock(id = 1, code = "def water_plants():", targetLevel = 0),
    SliderBlock(id = 2, code = "    if soil_dry == True:", targetLevel = 1),
    SliderBlock(id = 3, code = "        pump.turn_on()", targetLevel = 2),
    SliderBlock(id = 4, code = "    return \"Done!\"", targetLevel = 1)
)

// dummy nodes!
val dummyNodes = mapOf(
    101 to DialogueNode(
        id = 101,
        speaker = "Python",
        text = "Oh, hey! Sorry, I was just finishing an automation script for my local botanical garden. I'm Python. You're here for the workshop, right? Just... leave the semicolons at the door. We like to keep the air—and the syntax—clean here.",
        emotion = "Friendly",
        nextNodeId = 102
    ),
    102 to DialogueNode(
        id = 102,
        speaker = "System",
        text = "He offers you a cup of jasmine tea. How do you respond?",
        choices = listOf(
            DialogueChoice("Thanks! Honestly, I'm used to Java's boilerplate.", 103),
            DialogueChoice("I agree. Braces are for teeth, not logic flow.", 104)
        )
    ),
    103 to DialogueNode(
        id = 103,
        speaker = "Python",
        text = "Java? Oof. That's a lot of typing just to say hello. I'm more of a 'get straight to the point' kind of guy, but I respect the hustle! Maybe I can show you how to simplify things.",
        emotion = "Encouraging",
        nextNodeId = 105
    ),
    104 to DialogueNode(
        id = 104,
        speaker = "Python",
        text = "Haha! Exactly! Why waste energy on curly braces when you can let the whitespace breathe? I think we're going to get along just fine.",
        emotion = "Happy",
        nextNodeId = 105
    ),
    105 to DialogueNode(
        id = 105,
        speaker = "Python",
        text = "Actually, since you're here... my moisture sensors in the greenhouse are sending back some really messy strings. It's a bit of a 'spaghetti code' situation.",
        emotion = "Thinking",
        nextNodeId = 106
    ),
    106 to DialogueNode(
        id = 106,
        speaker = "System",
        text = "He gestures to a holographic display buzzing with scattered brackets and stray symbols.",
        choices = listOf(
            DialogueChoice("I'm a pro at cleanup. Let's do it!", 107),
            DialogueChoice("Wait, is this a test?", 108)
        )
    ),
    107 to DialogueNode(
        id = 107,
        speaker = "Python",
        text = "That's the spirit! Just filter out the noise. If we keep the logic clean, the jasmine plants will stay hydrated. Ready?",
        emotion = "Excited",
        triggerEvent = "SYNTAX_DASH"
    ),
    108 to DialogueNode(
        id = 108,
        speaker = "Python",
        text = "A test? No, no! Think of it as... a collaborative debugging session. Low pressure, high reward! What do you say?",
        emotion = "Laughing",
        nextNodeId = 106
    ),
    109 to DialogueNode(
        id = 109,
        speaker = "Python",
        text = "Wow! Everything is aligned perfectly. You've got a real eye for structure. The greenhouse sensors are reading clear as day now. Chapter 1: Complete!",
        emotion = "Blushing",
        nextNodeId = null
    ),
    110 to DialogueNode(
        id = 110,
        speaker = "Python",
        text = "Ah, almost! A few of those blocks are a bit... out of place. Want to try one more time?",
        emotion = "Pensive",
        choices = listOf(
            DialogueChoice("Let me try again!", 107),
            DialogueChoice("I'm more of a 'chaotic' coder.", 109)
        )
    ),

    201 to DialogueNode(
        id = 201,
        speaker = "Python",
        text = "Welcome back! Now that the greenhouse is organized, let's talk about how I remember things. In my world, we use variables—think of them as labeled jars for your data.",
        emotion = "Happy",
        nextNodeId = 202
    ),
    202 to DialogueNode(
        id = 202,
        speaker = "Python",
        text = "Try creating a variable for our jasmine tea temperature. Use: 'temp = 180'",
        emotion = "Thinking",
        nextNodeId = null // This would eventually trigger the next minigame or popup
    )


)