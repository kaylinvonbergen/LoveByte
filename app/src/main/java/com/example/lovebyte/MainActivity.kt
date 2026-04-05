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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.lovebyte.ui.theme.LoveByteTheme

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument


import com.example.lovebyte.ui.screens.HomeScreen

import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.data.model.Chapter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoveByteTheme {


                // NavController -- the "remote control" for the UI stuffs
                // https://developer.android.com/develop/ui/compose/navigation
                val navController = rememberNavController()

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
                            val dummyState = LoveByteState(
                                weatherDescription = "Sunny",
                                cityName = "Boston",
                                currentLanguage = ProgrammingLanguage.PYTHON,
                                currentChapter = 3
                            )

                            HomeScreen(
                                state = dummyState,
                                onContinueClicked = {
                                    navController.navigate("chapter/${dummyState.currentLanguage.name}/3")
                                },
                                onSwapClicked = {
                                    navController.navigate("charselect")
                                }
                            )
                        }

                        // 3. CharacterSelection
                        composable("charselect") {
                           CharSelectScreen(onCharacterSelected = { lang: String ->
                                navController.navigate("timeline/$lang")
                            })
                        }

                        // 4. Timeline (Chapter Selection)
                        composable("timeline/{language}") { backStackEntry ->
                            val lang = backStackEntry.arguments?.getString("language") ?: "python"
                            TimelineScreen(language = lang, onChapterSelected = { chId: Int ->
                                navController.navigate("chapter/$lang/$chId")
                            })
                        }

                        // 5. Chapter (The Game + Minigame)
                        composable("chapter/{language}/{chapterId}") { backStackEntry ->
                            val lang = backStackEntry.arguments?.getString("language") ?: "python"
                            val chId = backStackEntry.arguments?.getString("chapterId")?.toInt() ?: 1
                            GameScreen(language = lang, chapterId = chId)
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
fun CharSelectScreen(onCharacterSelected: (String) -> Unit) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text("Select a Character", style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        androidx.compose.material3.Button(onClick = { onCharacterSelected("PYTHON") }) { Text("Python-kun") }
        androidx.compose.material3.Button(onClick = { onCharacterSelected("KOTLIN") }) { Text("Kotlin-chan") }
    }
}

@Composable
fun TimelineScreen(language: String, onChapterSelected: (Int) -> Unit) {
    Column {
        Text("Chapters for $language")
        androidx.compose.material3.Button(onClick = { onChapterSelected(1) }) { Text("Chapter 1") }
    }
}

@Composable
fun GameScreen(language: String, chapterId: Int) {
    Text("Now Playing: $language - Chapter $chapterId")
}

@Composable
fun SettingsScreen() {
    Text("Settings Page")
}