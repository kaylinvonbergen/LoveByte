package com.example.lovebyte.data.model

enum class ProgrammingLanguage(val displayName: String) {
    PYTHON("Python"),
    KOTLIN("Kotlin"),
    NONE("None")
}

data class LoveByteState(
    // System & Loading States
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // Navigation & Context
    val currentLanguage: ProgrammingLanguage = ProgrammingLanguage.NONE,
    val currentChapter: Int = 1,
    val dialogueIndex: Int = 0,

    // UI Flags
    val isMiniGameActive: Boolean = false,
    val showResumeDialog: Boolean = false,
    val isPaused: Boolean = false,

    // External Data (Retrofit/Location)
    val weatherDescription: String = "Clear",
    val cityName: String = "Boston",
    val temperature: Double = 0.0,

    // Game Persistence (Room)
    val isChapterComplete: Boolean = false,
    val unlockedBadges: List<String> = emptyList()
)