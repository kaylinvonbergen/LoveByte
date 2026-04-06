package com.example.lovebyte


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lovebyte.ui.theme.LoveByteTheme
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect

import com.example.lovebyte.ui.screens.HomeScreen
import com.example.lovebyte.ui.screens.CharSelectScreen
import com.example.lovebyte.ui.screens.TimelineScreen

import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.data.model.Chapter
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
                    NavHost(navController = navController,
                        startDestination = "splash", // where the app begins
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 1. Splash Screen!
                        composable("splash") {
                            SplashScreen(onTimeout = {
                                navController.navigate("home"){
                                    popUpTo("splash") {
                                        inclusive = true
                                    }
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
                            // extract the language string from the URL
                            val langName = backStackEntry.arguments?.getString("language") ?: "PYTHON"

                            // convert the string back to our Enum
                            val selectedLang = try {
                                ProgrammingLanguage.valueOf(langName)
                            } catch (e: Exception) {
                                ProgrammingLanguage.PYTHON
                            }

                            // create a dummy state (
                            // update the currentLanguage to match the one the user clicked on
                            TimelineScreen(
                                state = state,

                                onChapterSelected = { chId ->
                                    viewModel.onChapterSelected(chId)
                                    navController.navigate("chapter/${selectedLang.name}/$chId")
                                },
                                onBackPressed = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // 5. Chapter (The Game + Minigame)
                        composable("chapter/{language}/{chapterId}") { backStackEntry ->
                            val lang = backStackEntry.arguments?.getString("language") ?: "python"
                            val chId = backStackEntry.arguments?.getString("chapterId")?.toInt() ?: 1

                            val langEnum = try {
                                ProgrammingLanguage.valueOf(lang.uppercase())
                            } catch (e: Exception) {
                                ProgrammingLanguage.PYTHON
                            }

                            LaunchedEffect(langEnum, chId) {
                                viewModel.loadChapter(langEnum, chId)
                            }

                            GameScreen(
                                language = lang,
                                chapterId = chId,
                                state = state,
                                onNextLineClicked = { viewModel.onNextLineClicked() },
                                onTogglePauseMenu = { viewModel.togglePauseMenu() },
                                onMiniGameSuccess = { viewModel.onMiniGameSuccess() }
                            )
                        }

                        // 6. Settings
                        composable("settings") {
                            SettingsScreen()
                        }



                    }



                }






                }
            }
        }
}


// temp dummy versions

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        androidx.compose.material3.Button(onClick = onTimeout) {
            Text("Splash Screen (Click to Start)")
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
fun GameScreen(
    language: String,
    chapterId: Int,
    state: LoveByteState,
    onNextLineClicked: () -> Unit,
    onTogglePauseMenu: () -> Unit,
    onMiniGameSuccess: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Now Playing: $language - Chapter $chapterId")
        Text("Dialogue Index: ${state.dialogueIndex}")
        Text("Mini-game active: ${state.isMiniGameActive}")
        Text("Paused: ${state.isPaused}")

        androidx.compose.material3.Button(onClick = onNextLineClicked) {
            Text("Next Line")
        }

        androidx.compose.material3.Button(onClick = onTogglePauseMenu) {
            Text("Toggle Pause")
        }

        if (state.isMiniGameActive) {
            androidx.compose.material3.Button(onClick = onMiniGameSuccess) {
                Text("Finish Mini-game")
            }
        }
    }
}

@Composable
fun SettingsScreen() {
    Text("Settings Page")
}