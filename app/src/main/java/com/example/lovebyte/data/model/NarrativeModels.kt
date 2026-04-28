package com.example.lovebyte.data.model

data class Chapter(
    val id: Int,
    val title: String,
    val startNodeId: Int,
    val isLocked: Boolean = true,
    val difficulty: String = "Beginner",
    val description: String = "",
    val language: ProgrammingLanguage = ProgrammingLanguage.NONE,
    val progress: Float = 0.0f

)

data class Section(
    val title: String,
    val chapters: List<Chapter>
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
    val targetNodeId: Int,
    val lovePoints: Int = 0,
    val friendPoints: Int = 0,
    val hatePoints: Int = 0
)

data class SentimentScore(
    val love: Int = 0,
    val friend: Int = 0,
    val hate: Int = 0
)