package com.example.lovebyte.data.model

data class SliderBlock(
    val id: Int,
    val code: String,
    val targetLevel: Int, // 0 = no indent, 1 = one tab, etc.
    var currentLevel: Int = 0
)

// The actual puzzle Python "wants" you to fix
val greenhousePuzzle = listOf(
    SliderBlock(0, "def check_moisture():", targetLevel = 0),
    SliderBlock(1, "if sensor.read() < 30:", targetLevel = 1),
    SliderBlock(2, "pump.start()", targetLevel = 2),
    SliderBlock(3, "print(\"Watering plants...\")", targetLevel = 2),
    SliderBlock(4, "return \"Success\"", targetLevel = 1)
)