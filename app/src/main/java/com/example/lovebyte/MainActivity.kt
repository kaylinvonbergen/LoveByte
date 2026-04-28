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

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val locationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val activityGranted =
                permissions[Manifest.permission.ACTIVITY_RECOGNITION] == true

            // Optional:
            // if (!locationGranted) viewModel.setLocationDenied()
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val permissionsToRequest = mutableListOf<String>()

        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (
            android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
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
                                    if (state.currentLanguage == ProgrammingLanguage.NONE) {
                                        // default start → Python Chapter 1
                                        viewModel.onLanguageSelected(ProgrammingLanguage.PYTHON)
                                        viewModel.loadChapter(ProgrammingLanguage.PYTHON, 1)

                                        navController.navigate("chapter/PYTHON/1/false")
                                    } else {
                                        val currentLang = state.currentLanguage.name
                                        val chapterId = state.currentChapter

                                        navController.navigate("chapter/$currentLang/$chapterId/true")
                                    }
                                },
//                                onStartAtChapter = { chapterId ->
//                                    viewModel.loadChapter(state.currentLanguage, chapterId)
//                                },
                                onSwapClicked = {
                                    navController.navigate("charselect")
                                },
                                onLocationPermissionGranted = { context ->
                                    viewModel.updateWeatherFromLocation(context)
                                },
                                onLocationPermissionDenied = {
                                    viewModel.setLocationDenied()
                                },
                                onOnboardingNext = {
                                    viewModel.nextOnboardingStep()
                                },
                                onOnboardingPlacementComplete = { pythonLevel, kotlinLevel ->
                                    viewModel.applyOnboardingPlacement(pythonLevel, kotlinLevel)
                                },
                                onOnboardingFinish = {
                                    viewModel.finishOnboarding()
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
                                    val nextCh = chapterId + 1
                                    val languageName = selectedLang.name

                                    navController.navigate("chapter/$languageName/$nextCh/false")
                                },
                                onChapterCompleted = {
                                    viewModel.markCurrentChapterComplete()
                                },
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
fun SettingsScreen(state: LoveByteState) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Current City: ${state.cityName}")
        Text("Weather: ${state.weatherDescription}")
    }
}