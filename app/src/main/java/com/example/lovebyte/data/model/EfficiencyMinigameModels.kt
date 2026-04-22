package com.example.lovebyte.data.model

data class EfficiencyLoopChallenge(
    val id: Int,
    val prompt: String,
    val codeLines: List<String>,
    val correctSteps: Int
)