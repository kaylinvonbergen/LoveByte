package com.example.lovebyte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lovebyte.ui.theme.LoveByteTheme

import androidx.compose.runtime.*

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument


import com.example.lovebyte.ui.screens.HomeScreen
import com.example.lovebyte.ui.screens.CharSelectScreen
import com.example.lovebyte.ui.screens.TimelineScreen
import com.example.lovebyte.ui.screens.GameScreen

import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.data.model.Chapter

import com.example.lovebyte.data.model.*

import com.example.lovebyte.ui.screens.dummyNodes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoveByteTheme {


                // NavController -- the "remote control" for the UI stuffs
                // https://developer.android.com/develop/ui/compose/navigation
                val navController = rememberNavController()

                // TODO: make actual data
                var gameState by remember {
                    mutableStateOf(
                        LoveByteState(
                            weatherDescription = "Sunny", // Default until API call is added
                            cityName = "Boston",
                            currentLanguage = ProgrammingLanguage.PYTHON,
                            dialogueIndex = 101,
                            progressMap = mapOf(
                                ProgrammingLanguage.PYTHON to 1,
                                ProgrammingLanguage.KOTLIN to 1
                            )
                        )
                    )
                }

                // wrap everything in a Scaffold so we can potentially add a TopBar or BottomBar later
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // "stage" where screens are swapped
                    NavHost(navController = navController,
                        startDestination = "splash", // where the app begins
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. Splash Screen!
                        composable("splash") {
                            SplashScreen(state = gameState, onTimeout = { // Added state = gameState
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }

                        // 2. Home, kind of like the title screen
                        composable("home") {
                            HomeScreen(
                                state = gameState,
                                onContinueClicked = {
                                    val currentLang = gameState.currentLanguage.name
                                    val chapterId = gameState.currentChapter
                                    navController.navigate("chapter/$currentLang/$chapterId")
                                },
                                onSwapClicked = {
                                    navController.navigate("charselect")
                                }
                            )
                        }

                        // 3. CharacterSelection
                        composable("charselect") {
                            CharSelectScreen(
                                state = gameState,
                                onCharacterSelected = { selectedLanguage ->
                                    gameState = gameState.copy(currentLanguage = selectedLanguage)
                                    navController.navigate("timeline/${selectedLanguage.name}")
                                },
                                onBackPressed = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 4. Timeline (Chapter Selection)
                        composable("timeline/{language}") { backStackEntry ->
                            val langName = backStackEntry.arguments?.getString("language") ?: "PYTHON"
                            val selectedLang = try {
                                ProgrammingLanguage.valueOf(langName)
                            } catch (e: Exception) {
                                ProgrammingLanguage.PYTHON
                            }

                            TimelineScreen(
                                state = gameState.copy(currentLanguage = selectedLang),
                                onChapterSelected = { chId ->
                                    val startNode = (chId * 100) + 1
                                    gameState = gameState.copy(
                                        currentLanguage = selectedLang,
                                        dialogueIndex = startNode
                                    )
                                    navController.navigate("chapter/${selectedLang.name}/$chId")
                                },
                                onBackPressed = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 5. Chapter (The Game + Minigame)
                        composable("chapter/{language}/{chapterId}") { backStackEntry ->
                            // pull values from the URL
                            val chapterId = backStackEntry.arguments?.getString("chapterId")?.toIntOrNull() ?: 1
                            val langName = backStackEntry.arguments?.getString("language") ?: "PYTHON"

                            // sync state if it doesn't match the URL
                            // runs whenever chapterID or langName changes
                            LaunchedEffect(chapterId, langName) {
                                val selectedLang = ProgrammingLanguage.valueOf(langName)

                                // only update if state is out of sync, avoid infinite recomp
                                if (gameState.currentChapter != chapterId || gameState.currentLanguage != selectedLang) {
                                    val startNode = (chapterId * 100) + 1
                                    val updatedMap = gameState.progressMap.toMutableMap().apply {
                                        put(selectedLang, chapterId)
                                    }
                                    gameState = gameState.copy(
                                        currentLanguage = selectedLang,
                                        progressMap = updatedMap,
                                        dialogueIndex = startNode
                                    )
                                }
                            }

                            // main game screen
                            GameScreen(
                                state = gameState,
                                // advance to next node, check for minigame
                                onNodeAdvanced = { nextId: Int ->
                                    gameState = gameState.copy(dialogueIndex = nextId)
                                    val nextNode = dummyNodes[nextId]
                                    if (nextNode?.triggerEvent != null) {
                                        gameState = gameState.copy(isMiniGameActive = true)
                                    }
                                },
                                // handle branching choices (update index, check for events)
                                onChoiceSelected = { choice: DialogueChoice ->
                                    gameState = gameState.copy(dialogueIndex = choice.targetNodeId)
                                    if (dummyNodes[choice.targetNodeId]?.triggerEvent != null) {
                                        gameState = gameState.copy(isMiniGameActive = true)
                                    }
                                },
                                // process minigame outcome, jump to win or lose node
                                onMinigameResult = { success: Boolean ->
                                    // TODO: remove dummy data
                                    gameState = gameState.copy(
                                        isMiniGameActive = false,
                                        dialogueIndex = if (success) 109 else 110
                                    )
                                },
                                // route back to timeline if back pressed
                                onBackPressed = {
                                    navController.navigate("timeline/$langName") {
                                        // prevents the stack from getting huge if they go back and forth
                                        popUpTo("home") { inclusive = false }
                                    }
                                },
                                // chapter complete popup logic
                                onNextChapter = {
                                    val nextCh = gameState.currentChapter + 1
                                    val startNode = (nextCh * 100) + 1

                                    // Create a new map with the updated progress
                                    val updatedMap = gameState.progressMap.toMutableMap().apply {
                                        put(gameState.currentLanguage, nextCh)
                                    }

                                    gameState = gameState.copy(
                                        progressMap = updatedMap,
                                        dialogueIndex = startNode,
                                        isMiniGameActive = false
                                    )

                                    // push new chapter on to navigation stack
                                    navController.navigate("chapter/${gameState.currentLanguage.name}/$nextCh")
                                }
                            )
                        }

                        // 6. Settings
                        composable("settings") {
                            SettingsScreen(state = gameState)
                        }



                    }



                }






                }
            }
        }
}


// temp dummy versions

@Composable
fun SplashScreen(state: LoveByteState, onTimeout: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        // You could even use state.cityName here if you wanted a "Loading Boston..." vibe
        Button(onClick = onTimeout) {
            Text("LoveByte (Click to Start)")
        }
    }
}


@Composable
fun SettingsScreen(state: LoveByteState) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Current City: ${state.cityName}")
        Text("Weather: ${state.weatherDescription}")
    }
}