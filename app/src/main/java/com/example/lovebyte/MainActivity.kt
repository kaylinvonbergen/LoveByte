package com.example.lovebyte
//MainActivity.kt
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.lovebyte.data.model.*
import com.example.lovebyte.ui.screens.*
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
                            SplashScreen(state = state, onTimeout = {
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
                                        navController.navigate("chapter/$currentLang/$chapterId/true")
                                    }
                                },
                                onSwapClicked = {
                                    navController.navigate("charselect")
                                },
                                onLocationPermissionGranted = { context ->
                                    viewModel.updateWeatherFromLocation(context)
                                },
                                onLocationPermissionDenied = {
                                    viewModel.setLocationDenied()
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
                                    navController.navigate("chapter/${selectedLang.name}/$chId/false")
                                },
                                onBackPressed = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 5. Chapter (The Game + Minigame)
                        composable("chapter/{language}/{chapterId}/{resume}") { backStackEntry ->
                            val resume =
                                backStackEntry.arguments?.getString("resume")?.toBooleanStrictOrNull() ?: false
                            val chapterId =
                                backStackEntry.arguments?.getString("chapterId")?.toIntOrNull() ?: 1
                            val langName =
                                backStackEntry.arguments?.getString("language") ?: "PYTHON"

                            val selectedLang = try {
                                ProgrammingLanguage.valueOf(langName)
                            } catch (e: Exception) {
                                ProgrammingLanguage.PYTHON
                            }

                            // sync state when entering screen
                            LaunchedEffect(chapterId, langName, resume) {
                                if (resume) {
                                    viewModel.resumeChapter(selectedLang, chapterId)
                                } else {
                                    viewModel.loadChapter(selectedLang, chapterId)
                                }
                            }

                            // main game screen
                            GameScreen(
                                state = state,
                                currentNode = viewModel.getCurrentNode(),
                                onNodeAdvanced = { nextId ->
                                    viewModel.advanceToNode(nextId)
                                },
                                onChoiceSelected = { choice ->
                                    viewModel.handleChoiceSelected(choice)
                                },
                                onMinigameResult = { success ->
                                    viewModel.handleMinigameResult(success)
                                },
                                onBackPressed = {
                                    navController.navigate("timeline/$langName") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                },
                                onNextChapter = {
                                    val nextCh = state.currentChapter
                                    val languageName = state.currentLanguage.name

                                    navController.navigate("chapter/$languageName/$nextCh/false")
                                },
                                onChapterCompleted = {
                                    viewModel.markCurrentChapterComplete()
                                }
                            )
                        }

                        // 6. Settings
                        // TODO: finish this later when we have more setting we'll care about
                        composable("settings") {
                            SettingsScreen(state = state)
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
        Text("Select a Character", style = MaterialTheme.typography.headlineLarge)
        Button(onClick = { onCharacterSelected("PYTHON") }) { Text("Python-kun") }
        Button(onClick = { onCharacterSelected("KOTLIN") }) { Text("Kotlin-chan") }
    }
}

@Composable
fun tempTimelineScreen(language: String, onChapterSelected: (Int) -> Unit) {
    Column {
        Text("Chapters for $language")
        Button(onClick = { onChapterSelected(1) }) { Text("Chapter 1") }
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