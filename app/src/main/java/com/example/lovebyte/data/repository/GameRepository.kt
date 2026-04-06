package com.example.lovebyte.data.repository

import com.example.lovebyte.data.model.ProgrammingLanguage

object GameRepository {

    fun getDialogueLines(
        language: ProgrammingLanguage,
        chapterId: Int
    ): List<String> {
        return when (language) {
            ProgrammingLanguage.PYTHON -> when (chapterId) {
                1 -> listOf(
                    "Hey, I'm Python.",
                    "Let's learn print statements.",
                    "Use print() to display text.",
                    "Comments start with #.",
                    "Nice work!"
                )
                else -> emptyList()
            }

            ProgrammingLanguage.KOTLIN -> when (chapterId) {
                1 -> listOf(
                    "Hey, I'm Kotlin.",
                    "Let's talk about val and var.",
                    "val is immutable.",
                    "var can change.",
                    "You're getting it!"
                )
                else -> emptyList()
            }

            else -> emptyList()
        }
    }
}