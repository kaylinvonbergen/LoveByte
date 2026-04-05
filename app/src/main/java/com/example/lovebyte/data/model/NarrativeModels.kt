package com.example.lovebyte.data.model

data class Chapter(
    val id: Int,
    val title: String,
    val description: String,
    val language: ProgrammingLanguage,
    val startNodeId: Int,
    val isLocked: Boolean = true,
    val difficulty: String = "Beginner",
    val progress: Float = 0.0f // useful for Home Screen Progress Bar!
)

data class DialogueNode(
    val id: Int,
    val speaker: String,
    val text: String,
    val emotion: String = "Neutral",
    val nextNodeId: Int? = null,
    val choices: List<DialogueChoice>? = null,
    val triggerEvent: String? = null // e.g., "START_MINIGAME"
)

data class DialogueChoice(
    val choiceText: String,
    val targetNodeId: Int
)