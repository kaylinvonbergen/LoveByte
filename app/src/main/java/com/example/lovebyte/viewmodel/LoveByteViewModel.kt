package com.example.lovebyte.viewmodel
//Holds app state and exposes functions the UI can call, like onNextLineClicked().
import com.example.lovebyte.data.model.DialogueChoice
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.repository.GameRepository
import com.example.lovebyte.data.model.ProgrammingLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.lovebyte.data.model.DialogueNode
// Swapped narrativeNodes for the master registry
import com.example.lovebyte.data.content.allNarrativeContent
import com.example.lovebyte.BuildConfig

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lovebyte.data.local.DatabaseProvider
import com.example.lovebyte.data.local.UserProgress
import com.example.lovebyte.data.repository.ProgressRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

import com.example.lovebyte.data.repository.WeatherRepository
import com.example.lovebyte.data.location.LocationHelper


import android.util.Log

class LoveByteViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherRepository = WeatherRepository(RetrofitProvider.weatherApi)
    private val locationHelper = LocationHelper(application)
    private val prefs = application.getSharedPreferences("lovebyte_prefs", Application.MODE_PRIVATE)

    private val _state = MutableStateFlow(
        LoveByteState(
            isLoading = true
        )
    )

    val state: StateFlow<LoveByteState> = _state.asStateFlow()

    private val database = DatabaseProvider.getDatabase(application)
    private val progressRepository = ProgressRepository(database.userProgressDao())

    init {
        loadAllSavedProgress()
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        val hasSeenOnboarding = prefs.getBoolean("has_seen_onboarding", false)

        if (!hasSeenOnboarding) {
            _state.value = _state.value.copy(
                shouldShowOnboarding = true,
                onboardingStep = 1
            )
        }
    }

    fun nextOnboardingStep() {
        val currentStep = _state.value.onboardingStep

        _state.value = _state.value.copy(
            onboardingStep = currentStep + 1
        )
    }

    fun finishOnboarding() {
        prefs.edit().putBoolean("has_seen_onboarding", true).apply()

        _state.value = _state.value.copy(
            shouldShowOnboarding = false,
            onboardingStep = 0
        )
    }

    fun reopenOnboarding() {
        _state.value = _state.value.copy(
            shouldShowOnboarding = true,
            onboardingStep = 1
        )
    }

    // Updates state when the user picks a language and loads any saved progress for it.
    fun onLanguageSelected(language: ProgrammingLanguage) {
        _state.value = _state.value.copy(
            currentLanguage = language,
            dialogueIndex = 0,
            isMiniGameActive = false,
            isChapterComplete = false,
            errorMessage = null
        )

        loadSavedProgressForLanguage(language)
    }

    // Starts a chapter by setting the current language, progress, and first dialogue node.
    fun loadChapter(language: ProgrammingLanguage, chapterId: Int) {
        val updatedProgressMap = _state.value.progressMap.toMutableMap()
        updatedProgressMap[language] = chapterId

        val startNode = (chapterId * 100) + 1

        _state.value = _state.value.copy(
            currentLanguage = language,
            progressMap = updatedProgressMap,
            dialogueIndex = startNode,
            isMiniGameActive = false,
            isPaused = false,
            isChapterComplete = false,
            errorMessage = null
        )

        saveCurrentProgress()
    }

    // Updates the selected chapter in state without fully starting/resuming it.
    fun onChapterSelected(chapterId: Int) {
        val currentState = _state.value
        val currentLanguage = currentState.currentLanguage

        if (currentLanguage == ProgrammingLanguage.NONE) return

        val updatedProgressMap = currentState.progressMap.toMutableMap()
        updatedProgressMap[currentLanguage] = chapterId

        _state.value = currentState.copy(
            progressMap = updatedProgressMap,
            dialogueIndex = 0,
            isMiniGameActive = false,
            isChapterComplete = false,
            errorMessage = null
        )
    }


    // Saves the user's current language, chapter, and dialogue position to the database.
    private fun saveCurrentProgress() {
        val currentState = _state.value
        val currentLanguage = currentState.currentLanguage

        if (currentLanguage == ProgrammingLanguage.NONE) return

        viewModelScope.launch {
            progressRepository.saveProgress(
                UserProgress(
                    language = currentLanguage.name,
                    chapterId = currentState.currentChapter,
                    dialogueIndex = currentState.dialogueIndex
                )
            )
        }
    }

    // Loads saved progress for all supported languages when the ViewModel is created.
    private fun loadAllSavedProgress() {
        viewModelScope.launch {
            try {
                val pythonProgress = progressRepository.getProgressForLanguageOnce(
                    ProgrammingLanguage.PYTHON.name
                )
                val kotlinProgress = progressRepository.getProgressForLanguageOnce(
                    ProgrammingLanguage.KOTLIN.name
                )

                val updatedProgressMap = _state.value.progressMap.toMutableMap()

                if (pythonProgress != null) {
                    updatedProgressMap[ProgrammingLanguage.PYTHON] = pythonProgress.chapterId
                }

                if (kotlinProgress != null) {
                    updatedProgressMap[ProgrammingLanguage.KOTLIN] = kotlinProgress.chapterId
                }

                val restoredLanguage = when {
                    pythonProgress != null && kotlinProgress == null -> ProgrammingLanguage.PYTHON
                    kotlinProgress != null && pythonProgress == null -> ProgrammingLanguage.KOTLIN
                    (pythonProgress?.chapterId ?: 1) >= (kotlinProgress?.chapterId ?: 1) &&
                            pythonProgress != null -> ProgrammingLanguage.PYTHON
                    kotlinProgress != null -> ProgrammingLanguage.KOTLIN
                    else -> ProgrammingLanguage.NONE
                }

                _state.value = _state.value.copy(
                    progressMap = updatedProgressMap,
                    currentLanguage = restoredLanguage,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load saved progress."
                )
            }
        }
    }

    // Loads saved progress for one specific language and restores that state if it exists.
    private fun loadSavedProgressForLanguage(language: ProgrammingLanguage) {
        if (language == ProgrammingLanguage.NONE) return

        viewModelScope.launch {
            val savedProgress = progressRepository
                .getProgressForLanguage(language.name)
                .firstOrNull()

            if (savedProgress != null) {
                val updatedProgressMap = _state.value.progressMap.toMutableMap()
                updatedProgressMap[language] = savedProgress.chapterId

                _state.value = _state.value.copy(
                    currentLanguage = language,
                    progressMap = updatedProgressMap,
                    dialogueIndex = savedProgress.dialogueIndex,
                    isMiniGameActive = false,
                    isPaused = false,
                    isChapterComplete = false,
                    errorMessage = null
                )
            }
        }
    }


    // Marks the current chapter complete and unlocks the next one if appropriate.
    private fun completeCurrentChapter() {
        val currentState = _state.value
        val currentLang = currentState.currentLanguage
        val currentChapter = currentState.currentChapter

        val updatedProgressMap = currentState.progressMap.toMutableMap()
        val savedProgress = updatedProgressMap[currentLang] ?: 1

        if (currentChapter >= savedProgress) {
            updatedProgressMap[currentLang] = currentChapter + 1
        }

        _state.value = currentState.copy(
            progressMap = updatedProgressMap,
            isChapterComplete = true,
            isMiniGameActive = false,
            isPaused = false
        )
    }

    // Public function that completes the chapter and saves that progress.
    fun markCurrentChapterComplete() {
        completeCurrentChapter()
        saveCurrentProgress()
    }

    // Clears the mini-game active state after a successful mini-game.
    fun onMiniGameSuccess() {
        _state.value = _state.value.copy(
            isMiniGameActive = false
        )
    }

    // Clears the mini-game and shows an error after a failed mini-game.
    fun onMiniGameFailed() {
        _state.value = _state.value.copy(
            isMiniGameActive = false,
            errorMessage = "Mini-game failed. Try again!"
        )
    }

    // Returns the current dialogue node based on the saved dialogue index AND language.
    fun getCurrentNode(): DialogueNode? {
        val currentLangName = _state.value.currentLanguage.name.uppercase()
        val nodesForLanguage = allNarrativeContent[currentLangName] ?: emptyMap()
        return nodesForLanguage[_state.value.dialogueIndex]
    }

    // Advances the story to a specific dialogue node and updates mini-game state if needed.
    fun advanceToNode(nextId: Int) {
        val currentLangName = _state.value.currentLanguage.name.uppercase()
        val nodesForLanguage = allNarrativeContent[currentLangName] ?: emptyMap()
        val nextNode = nodesForLanguage[nextId]

        _state.value = _state.value.copy(
            dialogueIndex = nextId,
            isMiniGameActive = nextNode?.triggerEvent != null,
            errorMessage = null
        )

        saveCurrentProgress()
    }

    // Handles a dialogue choice by moving to that choice's target node.
    fun handleChoiceSelected(choice: DialogueChoice) {
        val currentLangName = _state.value.currentLanguage.name.uppercase()
        val nodesForLanguage = allNarrativeContent[currentLangName] ?: emptyMap()
        val nextNode = nodesForLanguage[choice.targetNodeId]

        _state.value = _state.value.copy(
            dialogueIndex = choice.targetNodeId,
            isMiniGameActive = nextNode?.triggerEvent != null,
            errorMessage = null
        )

        saveCurrentProgress()
    }

    // Handles the result of a mini-game by sending the player to the success or failure node.
    fun handleMinigameResult(success: Boolean) {
        val targetNode = if (success) 109 else 110

        // Using advanceToNode ensures the language-specific map is checked
        advanceToNode(targetNode)
    }

    // Resumes a chapter from saved progress if the saved node belongs to that chapter.
    fun resumeChapter(language: ProgrammingLanguage, chapterId: Int) {
        viewModelScope.launch {
            val savedProgress = progressRepository.getProgressForLanguageOnce(language.name)
            val updatedProgressMap = _state.value.progressMap.toMutableMap().apply {
                put(language, chapterId)
            }

            val chapterPrefix = chapterId.toString()
            val savedNodeMatchesChapter =
                savedProgress != null &&
                        savedProgress.chapterId == chapterId &&
                        savedProgress.dialogueIndex.toString().startsWith(chapterPrefix)

            val restoredNode = if (savedNodeMatchesChapter) {
                savedProgress!!.dialogueIndex
            } else {
                (chapterId * 100) + 1
            }

            _state.value = _state.value.copy(
                currentLanguage = language,
                progressMap = updatedProgressMap,
                dialogueIndex = restoredNode,
                isMiniGameActive = false,
                isPaused = false,
                isChapterComplete = false,
                errorMessage = null
            )
        }
    }

    // Clears weather-related state when location access is denied or unavailable.
    fun setLocationDenied() {
        _state.value = _state.value.copy(
            weatherDescription = "",
            cityName = "",
            temperature = 0.0
        )
    }

    // Gets the user's location, fetches weather for that location, and updates state.
    fun updateWeatherFromLocation(context: android.content.Context) {
        viewModelScope.launch {
            Log.d("WEATHER_DEBUG", "updateWeatherFromLocation called")

            try {
                val locationHelper = LocationHelper(context)
                val locationResult = locationHelper.getCurrentLocationResult()

                if (locationResult == null) {
                    Log.d("WEATHER_DEBUG", "Location was null")
                    setLocationDenied()
                    return@launch
                }

                Log.d(
                    "WEATHER_DEBUG",
                    "Requesting weather for lat=${locationResult.latitude}, lon=${locationResult.longitude}"
                )
                Log.d("WEATHER_DEBUG", "locationResult = $locationResult")
                Log.d("WEATHER_DEBUG", "API key = '${BuildConfig.WEATHER_API_KEY}'")

                val weather = weatherRepository.getWeather(
                    lat = locationResult.latitude,
                    lon = locationResult.longitude,
                    apiKey = BuildConfig.WEATHER_API_KEY
                )

                Log.d("WEATHER_DEBUG", "Weather API success: $weather")

                val weatherInfo = weather.weather.firstOrNull()

                val adjective = if (weatherInfo != null) {
                    mapWeatherToAdjective(weatherInfo.main, weatherInfo.description)
                } else {
                    ""
                }

                _state.value = _state.value.copy(
                    cityName = locationResult.cityName ?: weather.name,
                    weatherDescription = adjective,
                    temperature = weather.main.temp,
                    errorMessage = null
                )

                Log.d("WEATHER_DEBUG", "State updated with weather")
            } catch (e: Exception) {
                Log.e("WEATHER_DEBUG", "Weather fetch failed", e)

                _state.value = _state.value.copy(
                    weatherDescription = "",
                    cityName = "",
                    temperature = 0.0,
                    errorMessage = "Failed to fetch weather: ${e.message}"
                )
            }
        }
    }

    // Converts raw weather API text into a simpler adjective for the UI.
    fun mapWeatherToAdjective(main: String, description: String): String {
        val desc = description.lowercase()
        val mainLower = main.lowercase()

        return when {
            "clear" in mainLower -> "sunny"
            "cloud" in mainLower -> "cloudy"
            "rain" in mainLower -> when {
                "light" in desc -> "lightly rainy"
                "heavy" in desc -> "heavily rainy"
                else -> "rainy"
            }
            "drizzle" in mainLower -> "drizzling"
            "thunderstorm" in mainLower -> "stormy"
            "snow" in mainLower -> "snowy"
            "mist" in mainLower ||
                    "fog" in mainLower ||
                    "haze" in mainLower -> "foggy"
            else -> description.lowercase()
        }
    }
}