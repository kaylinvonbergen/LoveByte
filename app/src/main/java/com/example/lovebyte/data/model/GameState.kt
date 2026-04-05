package com.example.lovebyte.data.model

enum class ProgrammingLanguage(
    val displayName: String,
    val totalChapters: Int,
    val description: String
) {
    PYTHON(
        displayName = "Python",
        totalChapters = 12,
        description = "A friendly but blunt snake who values 'readability' above all else. They hate unnecessary braces and will definitely judge your indentation."
    ),
    KOTLIN(
        displayName = "Kotlin",
        totalChapters = 10,
        description = "The modern star of the show. They are efficient, null-safe, and always trying to make things more concise. Very protective of their Android roots."
    ),
    NONE(
        displayName = "None",
        totalChapters = 0,
        description = ""
    )
}

data class LoveByteState(
    // System & Loading States
    val isLoading: Boolean = true,
    val errorMessage: String? = null,

    // Navigation & Context
    val currentLanguage: ProgrammingLanguage = ProgrammingLanguage.NONE,

    // progressMap stores the current chapter index for every language
    val progressMap: Map<ProgrammingLanguage, Int> = mapOf(
        ProgrammingLanguage.PYTHON to 1,
        ProgrammingLanguage.KOTLIN to 1
    ),

    val dialogueIndex: Int = 0,

    // UI Flags
    val isMiniGameActive: Boolean = false,
    val showResumeDialog: Boolean = false,
    val isPaused: Boolean = false,

    // External Data
    val weatherDescription: String = "Clear",
    val cityName: String = "Boston",
    val temperature: Double = 0.0,

    // Game Persistence
    val isChapterComplete: Boolean = false,
    val unlockedBadges: List<String> = emptyList()
) {
    // dynamic lookup: What chapter are we on for the selected language?
    val currentChapter: Int
        get() = progressMap[currentLanguage] ?: 1

    // progress fraction for LinearProgressIndicator
    val progressFraction: Float
        get() {
            val total = currentLanguage.totalChapters
            return if (total > 0) currentChapter.toFloat() / total.toFloat() else 0f
        }

    // 3. string for the UI text
    val progressPercentage: Int
        get() = (progressFraction * 100).toInt()
}