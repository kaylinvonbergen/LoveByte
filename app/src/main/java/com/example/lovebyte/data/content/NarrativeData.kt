package com.example.lovebyte.data.content

import com.example.lovebyte.data.model.DialogueChoice
import com.example.lovebyte.data.model.DialogueNode
import com.example.lovebyte.data.model.SliderBlock

val pythonChapter1Blocks = listOf(
    SliderBlock(id = 1, code = "def water_plants():", targetLevel = 0),
    SliderBlock(id = 2, code = "    if soil_dry == True:", targetLevel = 1),
    SliderBlock(id = 3, code = "        pump.turn_on()", targetLevel = 2),
    SliderBlock(id = 4, code = "    return \"Done!\"", targetLevel = 1)
)


// we define separate maps for each character/language

//Python dialogue
private val pythonNodes = mapOf(
    101 to DialogueNode(
        id = 101,
        speaker = "Python",
        text = "Oh, hey! Sorry, I was just finishing an automation script for my local botanical garden. I'm Python. You're here for the workshop, right? Just... leave the semicolons at the door. We like to keep the air—and the syntax—clean here.",
        emotion = "Friendly",
        nextNodeId = 102
    ),
    102 to DialogueNode(
        id = 102,
        speaker = "System",
        text = "He offers you a cup of jasmine tea. How do you respond?",
        choices = listOf(
            DialogueChoice("Thanks! Honestly, I'm used to Java's boilerplate.", 103),
            DialogueChoice("I agree. Braces are for teeth, not logic flow.", 104)
        )
    ),
    103 to DialogueNode(
        id = 103,
        speaker = "Python",
        text = "Java? Oof. That's a lot of typing just to say hello. I'm more of a 'get straight to the point' kind of guy, but I respect the hustle! Maybe I can show you how to simplify things.",
        emotion = "Encouraging",
        nextNodeId = 105
    ),
    104 to DialogueNode(
        id = 104,
        speaker = "Python",
        text = "Haha! Exactly! Why waste energy on curly braces when you can let the whitespace breathe? I think we're going to get along just fine.",
        emotion = "Happy",
        nextNodeId = 105
    ),
    105 to DialogueNode(
        id = 105,
        speaker = "Python",
        text = "Actually, since you're here... my moisture sensors in the greenhouse are sending back some really messy strings. It's a bit of a 'spaghetti code' situation.",
        emotion = "Thinking",
        nextNodeId = 106
    ),
    106 to DialogueNode(
        id = 106,
        speaker = "System",
        text = "He gestures to a holographic display buzzing with scattered brackets and stray symbols.",
        choices = listOf(
            DialogueChoice("I'm a pro at cleanup. Let's do it!", 107),
            DialogueChoice("Wait, is this a test?", 108)
        )
    ),
    107 to DialogueNode(
        id = 107,
        speaker = "Python",
        text = "That's the spirit! Just filter out the noise. If we keep the logic clean, the jasmine plants will stay hydrated. Ready?",
        emotion = "Excited",
        triggerEvent = "SYNTAX_DASH"
    ),
    108 to DialogueNode(
        id = 108,
        speaker = "Python",
        text = "A test? No, no! Think of it as... a collaborative debugging session. Low pressure, high reward! What do you say?",
        emotion = "Laughing",
        nextNodeId = 106
    ),
    109 to DialogueNode(
        id = 109,
        speaker = "Python",
        text = "Wow! Everything is aligned perfectly. You've got a real eye for structure. The greenhouse sensors are reading clear as day now. Chapter 1: Complete!",
        emotion = "Blushing",
        nextNodeId = null
    ),
    110 to DialogueNode(
        id = 110,
        speaker = "Python",
        text = "Ah, almost! A few of those blocks are a bit... out of place. Want to try one more time?",
        emotion = "Pensive",
        choices = listOf(
            DialogueChoice("Let me try again!", 107),
            DialogueChoice("I'm more of a 'chaotic' coder.", 111)
        )
    ),
    111 to DialogueNode(
        id = 111,
        speaker = "Python",
        text = "Oh—haha, okay! A little... 'creative' chaos keeps the sensors on their toes, I suppose? I can already hear the pumps acting a bit erratic, and the jasmine might end up a tad soggy, but... we'll call it a successful experiment for now. Chapter 1: Complete!",
        emotion = "Thinking",
        nextNodeId = null
    ),
    201 to DialogueNode(
        id = 201,
        speaker = "Python",
        text = "Welcome back! Now that the greenhouse is organized, let's talk about how I remember things. In my world, we use variables—think of them as labeled jars for your data.",
        emotion = "Happy",
        nextNodeId = 202
    ),
    202 to DialogueNode(
        id = 202,
        speaker = "Python",
        text = "Try creating a variable for our jasmine tea temperature. Use: 'temp = 180'",
        emotion = "Thinking",
        nextNodeId = null
    )
)

// Kotlin dialogue nodes
private val kotlinNodes = mapOf(
    101 to DialogueNode(
        id = 101,
        speaker = "Kotlin",
        text = "Oh, hi! I was just organizing some lambdas. I'm Kotlin! You're the one looking for the 'modern' experience, right?",
        emotion = "Happy",
        nextNodeId = null // TODO: Add Chapter 1 logic
    )
)

// the master registry that the LoveByteViewModel queries
val allNarrativeContent = mapOf(
    "PYTHON" to pythonNodes,
    "KOTLIN" to kotlinNodes
)