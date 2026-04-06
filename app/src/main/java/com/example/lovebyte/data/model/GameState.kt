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
    );

    val sections: List<Section>
        get() = when (this) {
            PYTHON -> listOf(
                Section("The Basics", listOf(
                    Chapter(id = 1, title = "Print & Comments", startNodeId = 101),
                    Chapter(id = 2, title = "Variables & Types", startNodeId = 201),
                    Chapter(id = 3, title = "Arithmetic Operators", startNodeId = 301)
                )),
                Section("Control Flow", listOf(
                    Chapter(id = 4, title = "If Statements", startNodeId = 401),
                    Chapter(id = 5, title = "Logical Operators", startNodeId = 501),
                    Chapter(id = 6, title = "The 'In' Keyword", startNodeId = 601)
                )),
                Section("Data Structures", listOf(
                    Chapter(id = 7, title = "Lists & Indexing", startNodeId = 701),
                    Chapter(id = 8, title = "Dictionaries", startNodeId = 801),
                    Chapter(id = 9, title = "Tuples & Sets", startNodeId = 901)
                )),
                Section("Modular Magic", listOf(
                    Chapter(id = 10, title = "Defining Functions", startNodeId = 1001),
                    Chapter(id = 11, title = "Importing Modules", startNodeId = 1101),
                    Chapter(id = 12, title = "The Final Project", startNodeId = 1201)
                ))
            )
            KOTLIN -> listOf(
                Section("First Steps", listOf(
                    Chapter(id = 1, title = "Val vs Var", startNodeId = 2001),
                    Chapter(id = 2, title = "Null Safety Basics", startNodeId = 2101),
                    Chapter(id = 3, title = "String Templates", startNodeId = 2201)
                )),
                Section("Functional Fun", listOf(
                    Chapter(id = 4, title = "Lambda Expressions", startNodeId = 2301),
                    Chapter(id = 5, title = "Higher-Order Functions", startNodeId = 2401),
                    Chapter(id = 6, title = "Extension Functions", startNodeId = 2501)
                )),
                Section("Android Power", listOf(
                    Chapter(id = 7, title = "State Management", startNodeId = 2601),
                    Chapter(id = 8, title = "Jetpack Compose Intro", startNodeId = 2701),
                    Chapter(id = 9, title = "Coroutines", startNodeId = 2801),
                    Chapter(id = 10, title = "Deployment", startNodeId = 2901)
                ))
            )
            NONE -> emptyList()
        }
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