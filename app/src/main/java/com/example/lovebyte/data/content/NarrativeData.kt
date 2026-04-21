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
    // --- CHAPTER 1 ---
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

    // --- CHAPTER 2 ---
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
    ),

    // --- CHAPTER 3: ENVIRONMENTAL SECRETS ---
    301 to DialogueNode(
        id = 301,
        speaker = "Python",
        text = "You're getting comfortable with the syntax. But now we need to talk about the 'invisible' parts of a project. The stuff that keeps the garden running but shouldn't be touched by the public.",
        emotion = "Serious",
        nextNodeId = 302
    ),
    302 to DialogueNode(
        id = 302,
        speaker = "System",
        text = "He points to a line of code in the holographic display: 'API_KEY = \"sk-123456789\"'. It’s glowing a dangerous, bright red.",
        choices = listOf(
            DialogueChoice("Wait, isn't that a security risk?", 303),
            DialogueChoice("Is that how you connect to the sensors?", 304)
        )
    ),
    303 to DialogueNode(
        id = 303,
        speaker = "Python",
        text = "A massive one. Real code isn't meant to live in the shadows—logic should be transparent. But sensitive data? API keys, database passwords? Those must be hidden in places non-devs can't even see.",
        emotion = "Encouraging",
        nextNodeId = 305
    ),
    304 to DialogueNode(
        id = 304,
        speaker = "Python",
        text = "It is, but it's dangerous. Hard-coding a key like that is like leaving your house key in the lock. We NEVER put sensitive keys out in the public where any user—or bot—can scrape them.",
        emotion = "Thinking",
        nextNodeId = 305
    ),
    305 to DialogueNode(
        id = 305,
        speaker = "Python",
        text = "We use environment variables. They sit outside the script, in the OS layer. If we don't 'darken' the environment now, that key will leak into the logs. Cover the sensor—now!",
        emotion = "Excited",
        triggerEvent = "LIGHT_SENSITIVE_SECRET"
    ),
    // Success Node
    306 to DialogueNode(
        id = 306,
        speaker = "Python",
        text = "Perfectly masked. Now, instead of a raw key, the code just sees a reference. It's clean, it's secure, and it stays between us and the server. Chapter 3: Complete!",
        emotion = "Blushing",
        nextNodeId = null
    ),
    // Failure / Retry Node
    307 to DialogueNode(
        id = 307,
        speaker = "Python",
        text = "The light hit it! That key is compromised now. In the real world, we'd have to revoke that credential immediately. What's the plan for the next attempt?",
        emotion = "Pensive",
        choices = listOf(
            DialogueChoice("Let's try the shadow move again.", 305),
            DialogueChoice("I'll just use a .env file next time.", 308)
        )
    ),
    // Technical Knowledge Reward (Bypasses Minigame)
    308 to DialogueNode(
        id = 308,
        speaker = "Python",
        text = "Exactly. A .env file keeps the sensitive stuff out of version control and safely in the local environment. You've clearly got the right security mindset—let's move on. Chapter 3: Complete!",
        emotion = "Happy",
        nextNodeId = null // Marks chapter as finished
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