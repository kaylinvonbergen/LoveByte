package com.example.lovebyte.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lovebyte.BuildConfig
import com.example.lovebyte.data.content.allNarrativeContent
import com.example.lovebyte.data.local.DatabaseProvider
import com.example.lovebyte.data.local.UserProgress
import com.example.lovebyte.data.location.LocationHelper
import com.example.lovebyte.data.model.DialogueChoice
import com.example.lovebyte.data.model.DialogueNode
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.data.model.SentimentScore
import com.example.lovebyte.data.repository.ProgressRepository
import com.example.lovebyte.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class LoveByteViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherRepository = WeatherRepository(RetrofitProvider.weatherApi)
    private val prefs = application.getSharedPreferences("lovebyte_prefs", Application.MODE_PRIVATE)

    private val _state = MutableStateFlow(
        LoveByteState(isLoading = true)
    )

    val state: StateFlow<LoveByteState> = _state.asStateFlow()

    private val database = DatabaseProvider.getDatabase(application)
    private val progressRepository = ProgressRepository(database.userProgressDao())

    init {
        loadAllSavedProgress()
        checkOnboardingStatus()
        loadSettings()
    }

    private fun loadSettings() {
        val privateModeDefault = prefs.getBoolean("private_mode_default", false)

        _state.value = _state.value.copy(
            privateModeDefault = privateModeDefault
        )
    }

    fun setPrivateModeDefault(enabled: Boolean) {
        prefs.edit().putBoolean("private_mode_default", enabled).apply()

        _state.value = _state.value.copy(
            privateModeDefault = enabled
        )
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

    fun openProficiencySettings() {
        _state.value = _state.value.copy(
            shouldShowOnboarding = true,
            onboardingStep = 2
        )
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
        val currentSavedProgress = updatedProgressMap[language] ?: 1
        updatedProgressMap[language] = maxOf(currentSavedProgress, chapterId)

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
        val currentSavedProgress = updatedProgressMap[currentLanguage] ?: 1
        updatedProgressMap[currentLanguage] = maxOf(currentSavedProgress, chapterId)

        _state.value = currentState.copy(
            progressMap = updatedProgressMap,
            dialogueIndex = 0,
            isMiniGameActive = false,
            isChapterComplete = false,
            errorMessage = null
        )
    }

    private fun saveCurrentProgress() {
        val currentState = _state.value
        val currentLanguage = currentState.currentLanguage

        if (currentLanguage == ProgrammingLanguage.NONE) return

        val sentiment = currentState.sentimentMap[currentLanguage] ?: SentimentScore()

        viewModelScope.launch {
            progressRepository.saveProgress(
                UserProgress(
                    language = currentLanguage.name,
                    chapterId = currentState.currentChapter,
                    dialogueIndex = currentState.dialogueIndex,
                    lovePoints = sentiment.love,
                    friendPoints = sentiment.friend,
                    hatePoints = sentiment.hate
                )
            )
        }
    }

    fun routePythonEnding() {
        val currentState = _state.value
        val score = currentState.sentimentMap[ProgrammingLanguage.PYTHON] ?: SentimentScore()

        val maxScore = maxOf(score.love, score.friend, score.hate)

        val possibleEndings = mutableListOf<Int>()

        if (score.love == maxScore) possibleEndings.add(710)
        if (score.friend == maxScore) possibleEndings.add(720)
        if (score.hate == maxScore) possibleEndings.add(730)

        val selectedEnding = possibleEndings.random()

        advanceToNode(selectedEnding)
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
                val updatedSentimentMap = _state.value.sentimentMap.toMutableMap()

                if (pythonProgress != null) {
                    updatedProgressMap[ProgrammingLanguage.PYTHON] = pythonProgress.chapterId
                    updatedSentimentMap[ProgrammingLanguage.PYTHON] = SentimentScore(
                        love = pythonProgress.lovePoints,
                        friend = pythonProgress.friendPoints,
                        hate = pythonProgress.hatePoints
                    )
                }

                if (kotlinProgress != null) {
                    updatedProgressMap[ProgrammingLanguage.KOTLIN] = kotlinProgress.chapterId
                    updatedSentimentMap[ProgrammingLanguage.KOTLIN] = SentimentScore(
                        love = kotlinProgress.lovePoints,
                        friend = kotlinProgress.friendPoints,
                        hate = kotlinProgress.hatePoints
                    )
                }

                val restoredLanguage = when {
                    pythonProgress != null && kotlinProgress == null -> ProgrammingLanguage.PYTHON
                    kotlinProgress != null && pythonProgress == null -> ProgrammingLanguage.KOTLIN
                    pythonProgress != null &&
                            (pythonProgress.chapterId >= (kotlinProgress?.chapterId ?: 1)) ->
                        ProgrammingLanguage.PYTHON
                    kotlinProgress != null -> ProgrammingLanguage.KOTLIN
                    else -> ProgrammingLanguage.NONE
                }

                _state.value = _state.value.copy(
                    progressMap = updatedProgressMap,
                    sentimentMap = updatedSentimentMap,
                    currentLanguage = restoredLanguage,
                    isLoading = false,
                    errorMessage = null
                )
            } catch (e: Exception) {
                Log.e("LOVE_BYTE_PROGRESS", "Failed to load saved progress", e)

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

                val updatedSentimentMap = _state.value.sentimentMap.toMutableMap()
                updatedSentimentMap[language] = SentimentScore(
                    love = savedProgress.lovePoints,
                    friend = savedProgress.friendPoints,
                    hate = savedProgress.hatePoints
                )

                _state.value = _state.value.copy(
                    currentLanguage = language,
                    progressMap = updatedProgressMap,
                    sentimentMap = updatedSentimentMap,
                    dialogueIndex = savedProgress.dialogueIndex,
                    isMiniGameActive = false,
                    isPaused = false,
                    isChapterComplete = false,
                    errorMessage = null
                )
            }
        }
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

    fun getCurrentNode(): DialogueNode? {
        val currentLangName = _state.value.currentLanguage.name.uppercase()
        val nodesForLanguage = allNarrativeContent[currentLangName] ?: emptyMap()

        return nodesForLanguage[_state.value.dialogueIndex]
    }

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

    fun handleChoiceSelected(choice: DialogueChoice) {
        val currentState = _state.value
        val currentLang = currentState.currentLanguage

        val currentScore = currentState.sentimentMap[currentLang] ?: SentimentScore()

        val updatedScore = currentScore.copy(
            love = (currentScore.love + choice.lovePoints).coerceIn(0, 50),
            friend = (currentScore.friend + choice.friendPoints).coerceIn(0, 50),
            hate = (currentScore.hate + choice.hatePoints).coerceIn(0, 50)
        )

        val updatedSentimentMap = currentState.sentimentMap.toMutableMap()
        updatedSentimentMap[currentLang] = updatedScore

        val currentLangName = currentLang.name.uppercase()
        val nodesForLanguage = allNarrativeContent[currentLangName] ?: emptyMap()
        val nextNode = nodesForLanguage[choice.targetNodeId]

        _state.value = currentState.copy(
            sentimentMap = updatedSentimentMap,
            dialogueIndex = choice.targetNodeId,
            isMiniGameActive = nextNode?.triggerEvent != null,
            errorMessage = null
        )

        saveCurrentProgress()
    }

    fun handleMinigameResult(success: Boolean) {
        val targetNode = if (success) 109 else 110
        advanceToNode(targetNode)
    }

    fun resumeChapter(language: ProgrammingLanguage, chapterId: Int) {
        viewModelScope.launch {
            val savedProgress = progressRepository.getProgressForLanguageOnce(language.name)

            val updatedProgressMap = _state.value.progressMap.toMutableMap().apply {
                val currentSavedProgress = this[language] ?: 1
                put(language, maxOf(currentSavedProgress, chapterId))
            }

            val updatedSentimentMap = _state.value.sentimentMap.toMutableMap()

            if (savedProgress != null) {
                updatedSentimentMap[language] = SentimentScore(
                    love = savedProgress.lovePoints,
                    friend = savedProgress.friendPoints,
                    hate = savedProgress.hatePoints
                )
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
                sentimentMap = updatedSentimentMap,
                dialogueIndex = restoredNode,
                isMiniGameActive = false,
                isPaused = false,
                isChapterComplete = false,
                errorMessage = null
            )
        }
    }

    fun applyOnboardingPlacement(pythonLevel: Int, kotlinLevel: Int) {
        val pythonStartingChapter = pythonLevel.coerceIn(1, 3)
        val kotlinStartingChapter = kotlinLevel.coerceIn(1, 3)

        val currentState = _state.value

        val updatedProgressMap = currentState.progressMap.toMutableMap()
        updatedProgressMap[ProgrammingLanguage.PYTHON] = pythonStartingChapter
        updatedProgressMap[ProgrammingLanguage.KOTLIN] = kotlinStartingChapter

        val pythonSentiment =
            currentState.sentimentMap[ProgrammingLanguage.PYTHON] ?: SentimentScore()
        val kotlinSentiment =
            currentState.sentimentMap[ProgrammingLanguage.KOTLIN] ?: SentimentScore()

        _state.value = currentState.copy(
            progressMap = updatedProgressMap,
            currentLanguage = ProgrammingLanguage.PYTHON,
            onboardingStep = 3
        )

        viewModelScope.launch {
            progressRepository.saveProgress(
                UserProgress(
                    language = ProgrammingLanguage.PYTHON.name,
                    chapterId = pythonStartingChapter,
                    dialogueIndex = pythonStartingChapter * 100 + 1,
                    lovePoints = pythonSentiment.love,
                    friendPoints = pythonSentiment.friend,
                    hatePoints = pythonSentiment.hate
                )
            )

            progressRepository.saveProgress(
                UserProgress(
                    language = ProgrammingLanguage.KOTLIN.name,
                    chapterId = kotlinStartingChapter,
                    dialogueIndex = kotlinStartingChapter * 100 + 1,
                    lovePoints = kotlinSentiment.love,
                    friendPoints = kotlinSentiment.friend,
                    hatePoints = kotlinSentiment.hate
                )
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

                val weather = weatherRepository.getWeather(
                    lat = locationResult.latitude,
                    lon = locationResult.longitude,
                    apiKey = BuildConfig.WEATHER_API_KEY
                )

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

    companion object {
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
}