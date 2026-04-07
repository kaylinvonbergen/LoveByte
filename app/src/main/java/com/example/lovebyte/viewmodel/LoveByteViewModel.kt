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
import com.example.lovebyte.data.content.narrativeNodes
import com.example.lovebyte.BuildConfig

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lovebyte.data.local.DatabaseProvider
import com.example.lovebyte.data.local.UserProgress
import com.example.lovebyte.data.repository.ProgressRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

import com.example.lovebyte.data.repository.WeatherRepository
import com.example.lovebyte.data.location.LocationHelper

import android.util.Log

import android.content.Context
class LoveByteViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherRepository = WeatherRepository(RetrofitProvider.weatherApi)
    private val locationHelper = LocationHelper(application)


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
    }

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

    fun onNextLineClicked() {
        val currentState = _state.value

        if (currentState.currentLanguage == ProgrammingLanguage.NONE) return
        if (currentState.isMiniGameActive) return
        if (currentState.isPaused) return
        if (currentState.isChapterComplete) return

        val dialogueLines = GameRepository.getDialogueLines(
            currentState.currentLanguage,
            currentState.currentChapter
        )

        if (dialogueLines.isEmpty()) return

        val nextIndex = currentState.dialogueIndex + 1

        if (nextIndex >= dialogueLines.size) {
            completeCurrentChapter()
            return
        }

        val shouldStartMiniGame = nextIndex == 3

        _state.value = currentState.copy(
            dialogueIndex = nextIndex,
            isMiniGameActive = shouldStartMiniGame,
            errorMessage = null
        )
    }

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

    fun getCurrentDialogueLine(): String {
        val currentState = _state.value

        val dialogueLines = GameRepository.getDialogueLines(
            currentState.currentLanguage,
            currentState.currentChapter
        )

        return dialogueLines.getOrNull(currentState.dialogueIndex) ?: "End of chapter."
    }

    fun onChoiceSelected(choice: DialogueChoice) {
        _state.value = _state.value.copy(
            dialogueIndex = choice.targetNodeId,
            isMiniGameActive = false
        )
    }

    fun onRestartChapter() {
        _state.value = _state.value.copy(
            dialogueIndex = 0,
            isMiniGameActive = false,
            isPaused = false,
            isChapterComplete = false,
            errorMessage = null
        )

        saveCurrentProgress()
    }

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

    fun markCurrentChapterComplete() {
        completeCurrentChapter()
        saveCurrentProgress()
    }

    fun onMiniGameSuccess() {
        _state.value = _state.value.copy(
            isMiniGameActive = false
        )
    }

    fun onMiniGameFailed() {
        _state.value = _state.value.copy(
            isMiniGameActive = false,
            errorMessage = "Mini-game failed. Try again!"
        )
    }

    fun togglePauseMenu() {
        _state.value = _state.value.copy(
            isPaused = !_state.value.isPaused
        )
    }

    fun showResumeDialog(show: Boolean) {
        _state.value = _state.value.copy(
            showResumeDialog = show
        )
    }

    fun clearError() {
        _state.value = _state.value.copy(
            errorMessage = null
        )
    }

    fun getCurrentNode(): DialogueNode? {
        return narrativeNodes[_state.value.dialogueIndex]
    }

    fun advanceToNode(nextId: Int) {
        val nextNode = narrativeNodes[nextId]

        _state.value = _state.value.copy(
            dialogueIndex = nextId,
            isMiniGameActive = nextNode?.triggerEvent != null,
            errorMessage = null
        )

        saveCurrentProgress()
    }

    fun handleChoiceSelected(choice: DialogueChoice) {
        val nextNode = narrativeNodes[choice.targetNodeId]

        _state.value = _state.value.copy(
            dialogueIndex = choice.targetNodeId,
            isMiniGameActive = nextNode?.triggerEvent != null,
            errorMessage = null
        )

        saveCurrentProgress()
    }

    fun handleMinigameResult(success: Boolean) {
        _state.value = _state.value.copy(
            isMiniGameActive = false,
            dialogueIndex = if (success) 109 else 110
        )

        saveCurrentProgress()
    }

    fun goToNextChapter() {
        val currentState = _state.value
        val nextCh = currentState.currentChapter + 1
        val startNode = (nextCh * 100) + 1

        val updatedMap = currentState.progressMap.toMutableMap().apply {
            put(currentState.currentLanguage, nextCh)
        }

        _state.value = currentState.copy(
            progressMap = updatedMap,
            dialogueIndex = startNode,
            isMiniGameActive = false,
            isChapterComplete = false,
            errorMessage = null
        )

        saveCurrentProgress()
    }

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

    fun setLocationDenied() {
        _state.value = _state.value.copy(
            weatherDescription = "",
            cityName = "",
            temperature = 0.0
        )
    }
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

            else -> description.lowercase() // fallback
        }
    }
}

