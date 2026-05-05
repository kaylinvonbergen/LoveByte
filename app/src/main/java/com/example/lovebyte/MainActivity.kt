package com.example.lovebyte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.lovebyte.data.model.*
import com.example.lovebyte.ui.screens.*
import com.example.lovebyte.ui.theme.LoveByteTheme
import com.example.lovebyte.viewmodel.LoveByteViewModel
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { /* Logic for handling specific denials can go here */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Maintaining your Build Q check for activity recognition
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }

        setContent {
            LoveByteTheme {
                // NavController -- the "remote control" for the UI stuffs
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
                        // 1. Splash Screen with Sakura Burst
                        composable("splash") {
                            SplashScreen(state = state, onTimeout = {
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            })
                        }

                        // 2. Home Screen - Now with Tap-to-Start
                        composable("home") {
                            HomeScreen(
                                state = state,
                                // This now triggers when the user taps ANYWHERE on the screen
                                onContinueClicked = {
                                    if (state.currentLanguage == ProgrammingLanguage.NONE) {
                                        viewModel.onLanguageSelected(ProgrammingLanguage.PYTHON)
                                        viewModel.loadChapter(ProgrammingLanguage.PYTHON, 1)
                                        navController.navigate("chapter/PYTHON/1/false")
                                    } else {
                                        val currentLang = state.currentLanguage.name
                                        val chapterId = state.currentChapter
                                        navController.navigate("chapter/$currentLang/$chapterId/true")
                                    }
                                },
                                onSwapClicked = { navController.navigate("charselect") },
                                onSettingsClicked = { navController.navigate("settings") },
                                onOnboardingNext = { viewModel.nextOnboardingStep() },
                                onOnboardingPlacementComplete = { py, kt -> viewModel.applyOnboardingPlacement(py, kt) },
                                onOnboardingFinish = { viewModel.finishOnboarding() }
                            )
                        }

                        // 3. CharacterSelection
                        composable("charselect") {
                            CharSelectScreen(
                                state = state,
                                onCharacterSelected = { selectedLanguage ->
                                    viewModel.onLanguageSelected(selectedLanguage)
                                    navController.navigate("timeline/${selectedLanguage.name}")
                                },
                                onBackPressed = { navController.popBackStack() }
                            )
                        }

                        // 4. Timeline (Chapter Selection)
                        composable("timeline/{language}") { backStackEntry ->
                            val langName = backStackEntry.arguments?.getString("language") ?: "PYTHON"
                            val selectedLang = try { ProgrammingLanguage.valueOf(langName) } catch (e: Exception) { ProgrammingLanguage.PYTHON }

                            TimelineScreen(
                                state = state.copy(currentLanguage = selectedLang),
                                onChapterSelected = { chId ->
                                    viewModel.onChapterSelected(chId)
                                    navController.navigate("chapter/${selectedLang.name}/$chId/false")
                                },
                                onBackPressed = { navController.popBackStack() }
                            )
                        }

                        // 5. Chapter (The Game + Minigame)
                        composable("chapter/{language}/{chapterId}/{resume}") { backStackEntry ->
                            val resume = backStackEntry.arguments?.getString("resume")?.toBooleanStrictOrNull() ?: false
                            val chapterId = backStackEntry.arguments?.getString("chapterId")?.toIntOrNull() ?: 1
                            val langName = backStackEntry.arguments?.getString("language") ?: "PYTHON"
                            val selectedLang = try { ProgrammingLanguage.valueOf(langName) } catch (e: Exception) { ProgrammingLanguage.PYTHON }

                            LaunchedEffect(chapterId, langName, resume) {
                                if (resume) viewModel.resumeChapter(selectedLang, chapterId)
                                else viewModel.loadChapter(selectedLang, chapterId)
                            }

                            GameScreen(
                                state = state,
                                currentNode = viewModel.getCurrentNode(),
                                onNodeAdvanced = { nextId -> viewModel.advanceToNode(nextId) },
                                onChoiceSelected = { choice -> viewModel.handleChoiceSelected(choice) },
                                onMinigameResult = { success -> viewModel.handleMinigameResult(success) },
                                onBackPressed = {
                                    navController.popBackStack()
                                },
                                onNextChapter = {
                                    val nextCh = chapterId + 1
                                    navController.navigate("chapter/${selectedLang.name}/$nextCh/false")
                                },
                                onPythonEndingTriggered = {
                                    viewModel.routePythonEnding()
                                },
                                onChapterCompleted = { viewModel.markChapterComplete(selectedLang, chapterId) },
                            )
                        }

                        // 6. Settings
                        composable("settings") {
                            SettingsScreen(
                                state = state,
                                onPrivateModeChanged = { viewModel.setPrivateModeDefault(it) },
                                onReplayOnboarding = {
                                    viewModel.reopenOnboarding()
                                    navController.navigate("home")
                                },
                                onChangeProficiency = { pythonLevel, kotlinLevel ->
                                    viewModel.applyOnboardingPlacement(pythonLevel, kotlinLevel)
                                },
                                onBackClicked = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(state: LoveByteState, onTimeout: () -> Unit) {
    val deepPink = Color(0xFFE85D7A)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF5F7))
            .clickable { onTimeout() }
    ) {
        // 50 petals for high density
        repeat(50) { index ->
            FloatingPetal(index)
        }

        // keep the logo centered on top of the petals
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // main logo container
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CutCornerShape(16.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(4.dp, deepPink)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("<3", style = MaterialTheme.typography.displayMedium, color = deepPink)
                }
            }
            Spacer(Modifier.height(24.dp))

            // app title branding
            Text(
                text = "LoveByte",
                style = MaterialTheme.typography.headlineLarge,
                color = deepPink,
                letterSpacing = 4.sp
            )
            Spacer(Modifier.height(48.dp))

            // tell user to click to next
            Text(
                text = "TAP TO START",
                style = MaterialTheme.typography.labelLarge,
                color = deepPink.copy(alpha = 0.7f),
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun BoxScope.FloatingPetal(index: Int) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp // couldn't get it to work with the "more updated" one
    val screenHeight = configuration.screenHeightDp
    val transition = rememberInfiniteTransition(label = "petal")

    // random spawn position horizontal
    val startX = remember(index) { (-20..screenWidth).random().toFloat() }

    // start above visible screen
    val startY = remember(index) { (0..screenHeight).random().toFloat() * -2f }

    // control sway of petals
    val swayDuration = remember(index) { (2000..4000).random() }

    // loop the vertical falling animation infinitely
    val yPos by transition.animateFloat(
        initialValue = startY,
        targetValue = screenHeight.toFloat() + 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (6000..12000).random(), easing = LinearEasing)
        ), label = "y"
    )

    // drift the petals (mimic wind)
    val xSway by transition.animateFloat(
        initialValue = -40f,
        targetValue = 40f,
        animationSpec = infiniteRepeatable(
            animation = tween(swayDuration, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ), label = "x"
    )

    Box(
        Modifier
            .align(Alignment.TopStart) // start from top-left, not the center
            .offset(x = startX.dp + xSway.dp, y = yPos.dp)
            .rotate(yPos / 10f)
    ) {
        // simple oval petal
        androidx.compose.foundation.Canvas(modifier = Modifier.size(8.dp)) {
            drawOval(color = Color(0xFFFFB7C5).copy(alpha = 0.8f))
        }
    }
}

@Composable
fun SettingsScreen(
    state: LoveByteState,
    onPrivateModeChanged: (Boolean) -> Unit,
    onReplayOnboarding: () -> Unit,
    onChangeProficiency: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text("Current City: ${state.cityName}")
        Text("Weather: ${state.weatherDescription}")
    }
}