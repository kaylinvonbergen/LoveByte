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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lovebyte.data.model.DialogueChoice
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.ui.screens.CharSelectScreen
import com.example.lovebyte.ui.screens.GameScreen
import com.example.lovebyte.ui.screens.HomeScreen
import com.example.lovebyte.ui.screens.TimelineScreen
import com.example.lovebyte.ui.screens.dummyNodes
import com.example.lovebyte.ui.theme.LoveByteTheme
import com.example.lovebyte.viewmodel.LoveByteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            LoveByteTheme {

                // NavController -- the "remote control" for the UI stuffs
                // https://developer.android.com/develop/ui/compose/navigation
                val navController = rememberNavController()
                val viewModel: LoveByteViewModel = viewModel()
                val state = viewModel.state.collectAsState().value

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

                // keep non-node app-level state roughly synced from the ViewModel
                LaunchedEffect(
                    state.currentLanguage,
                    state.progressMap,
                    state.weatherDescription,
                    state.cityName,
                    state.isPaused,
                    state.isMiniGameActive,
                    state.isChapterComplete,
                    state.errorMessage
                ) {
                    gameState = gameState.copy(
                        weatherDescription = state.weatherDescription,
                        cityName = state.cityName,
                        currentLanguage = if (state.currentLanguage != ProgrammingLanguage.NONE) {
                            state.currentLanguage
                        } else {
                            gameState.currentLanguage
                        },
                        progressMap = state.progressMap,
                        isPaused = state.isPaused,
                        isMiniGameActive = state.isMiniGameActive,
                        isChapterComplete = state.isChapterComplete,
                        errorMessage = state.errorMessage
                    )
                }

                // wrap everything in a Scaffold so we can potentially add a TopBar or BottomBar later
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // "stage" where screens are swapped
                    NavHost(
                        navController = navController,
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
                            // for now, we create a dummy state so it compiles
                            HomeScreen(
                                state = state,
                                onContinueClicked = {
                                    if (state.currentLanguage != ProgrammingLanguage.NONE) {
                                        val currentLang = state.currentLanguage.name
                                        val chapterId = state.currentChapter
                                        navController.navigate("chapter/$currentLang/$chapterId")
                                    }
                                },
                                onSwapClicked = {
                                    navController.navigate("charselect")
                                }
                            )
                        }

                        // 3. CharacterSelection
                        composable("charselect") {
                            // We pass the dummyState so the screen knows the weather and current progress
                            CharSelectScreen(
                                state = state,
                                onCharacterSelected = { selectedLanguage ->
                                    viewModel.onLanguageSelected(selectedLanguage)
                                    gameState = gameState.copy(currentLanguage = selectedLanguage)
                                    // selectedLanguage is now the Enum object, so we use .name for the route
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

                            // create a dummy state (
                            // update the currentLanguage to match the one the user clicked on
                            TimelineScreen(
                                state = state.copy(currentLanguage = selectedLang),
                                onChapterSelected = { chId ->
                                    viewModel.onChapterSelected(chId)

                                    val startNode = (chId * 100) + 1
                                    val updatedMap = gameState.progressMap.toMutableMap().apply {
                                        put(selectedLang, chId)
                                    }
                                    gameState = gameState.copy(
                                        currentLanguage = selectedLang,
                                        progressMap = updatedMap,
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

                            val selectedLang = try {
                                ProgrammingLanguage.valueOf(langName)
                            } catch (e: Exception) {
                                ProgrammingLanguage.PYTHON
                            }

                            // sync state if it doesn't match the URL
                            // runs whenever chapterID or langName changes
                            LaunchedEffect(chapterId, langName) {
                                viewModel.loadChapter(selectedLang, chapterId)

                                // only update if state is out of sync, avoid infinite recomp
                                if (gameState.currentChapter != chapterId || gameState.currentLanguage != selectedLang) {
                                    val startNode = (chapterId * 100) + 1
                                    val updatedMap = gameState.progressMap.toMutableMap().apply {
                                        put(selectedLang, chapterId)
                                    }
                                    gameState = gameState.copy(
                                        currentLanguage = selectedLang,
                                        progressMap = updatedMap,
                                        dialogueIndex = startNode,
                                        isMiniGameActive = false
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

                                    viewModel.onChapterSelected(nextCh)

                                    // push new chapter on to navigation stack
                                    navController.navigate("chapter/${gameState.currentLanguage.name}/$nextCh")
                                }
                            )
                        }

                        // 6. Settings
                        // TODO: finish this later when we have more setting we'll care about
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
fun tempCharSelectScreen(onCharacterSelected: (String) -> Unit) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text("Select a Character", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        androidx.compose.material3.Button(onClick = { onCharacterSelected("PYTHON") }) { Text("Python-kun") }
        androidx.compose.material3.Button(onClick = { onCharacterSelected("KOTLIN") }) { Text("Kotlin-chan") }
    }
}

@Composable
fun tempTimelineScreen(language: String, onChapterSelected: (Int) -> Unit) {
    Column {
        Text("Chapters for $language")
        androidx.compose.material3.Button(onClick = { onChapterSelected(1) }) { Text("Chapter 1") }
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