package com.example.lovebyte.data.content

import com.example.lovebyte.data.model.DialogueChoice
import com.example.lovebyte.data.model.DialogueNode
import com.example.lovebyte.data.model.SliderBlock
import com.example.lovebyte.data.model.EfficiencyLoopChallenge

val pythonChapter1Blocks = listOf(
    SliderBlock(id = 1, code = "def water_plants():", targetLevel = 0),
    SliderBlock(id = 2, code = "    if soil_dry == True:", targetLevel = 1),
    SliderBlock(id = 3, code = "        pump.turn_on()", targetLevel = 2),
    SliderBlock(id = 4, code = "    return \"Done!\"", targetLevel = 1)
)

val pythonChapterxLoopChallenges =
    EfficiencyLoopChallenge(
        id = 0,
        prompt = "Count how many times this loop runs, then walk that many steps.",
        codeLines = listOf(
            "for i in range(3):",
            "    print(i)"
        ),
        correctSteps = 3
    )


val narrativeNodes = mapOf(
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
            DialogueChoice("I'm more of a 'chaotic' coder.", 109)
        )
    ),
    201 to DialogueNode(
        id = 201,
        speaker = "Python",
        text = "You did it! The greenhouse sensors are behaving again. Now that the plants are safe, I need to run a few maintenance checks. Luckily, Python loops make repeating things way easier.",
        emotion = "Happy",
        nextNodeId = 202
    ),
    202 to DialogueNode(
        id = 202,
        speaker = "Python",
        text = "A loop lets me repeat the same action multiple times without writing the same code over and over. Much cleaner, much less typing.",
        emotion = "Explaining",
        nextNodeId = 203
    ),
    203 to DialogueNode(
        id = 203,
        speaker = "System",
        text = "Python pulls up a maintenance script for the greenhouse fans.",
        nextNodeId = 204
    ),
    204 to DialogueNode(
        id = 204,
        speaker = "Python",
        text = "I need you to figure out how many times this loop will run. Then, take exactly that many steps so we can calibrate the greenhouse walkway sensors.",
        emotion = "Thinking",
        nextNodeId = 205
    ),
    205 to DialogueNode(
        id = 205,
        speaker = "System",
        text = "He points toward a glowing path through the greenhouse floor.",
        choices = listOf(
            DialogueChoice("Got it. I'll count carefully.", 206),
            DialogueChoice("This feels suspiciously like exercise.", 207)
        )
    ),
    206 to DialogueNode(
        id = 206,
        speaker = "Python",
        text = "Exactly! Just read the loop, count the iterations, and walk that many steps. Try not to overthink it.",
        emotion = "Encouraging",
        triggerEvent = "EFFICIENCY_STEPPER"
    ),
    207 to DialogueNode(
        id = 207,
        speaker = "Python",
        text = "Exercise? Please. I prefer to call it 'interactive runtime analysis.' Sounds way more professional, right?",
        emotion = "Laughing",
        nextNodeId = 205
    ),
    208 to DialogueNode(
        id = 208,
        speaker = "Python",
        text = "Perfect! You matched the loop exactly. See? Repetition isn't so scary when you know how many times it happens.",
        emotion = "Blushing",
        nextNodeId = null
    ),
    209 to DialogueNode(
        id = 209,
        speaker = "Python",
        text = "Hmm... not quite. Either you took too many steps or not enough. Want to give the loop another look?",
        emotion = "Pensive",
        choices = listOf(
            DialogueChoice("Let me try again.", 206),
            DialogueChoice("Loops are harder when they involve cardio.", 210)
        )
    ),
    210 to DialogueNode(
        id = 210,
        speaker = "Python",
        text = "Fair. Most programmers prefer keyboard shortcuts over physical activity.",
        emotion = "Laughing",
        nextNodeId = 209
    ),
//    201 to DialogueNode(
//        id = 201,
//        speaker = "Python",
//        text = "Welcome back! Now that the greenhouse is organized, let's talk about how I remember things. In my world, we use variables—think of them as labeled jars for your data.",
//        emotion = "Happy",
//        nextNodeId = 202
//    ),
//    202 to DialogueNode(
//        id = 202,
//        speaker = "Python",
//        text = "Try creating a variable for our jasmine tea temperature. Use: 'temp = 180'",
//        emotion = "Thinking",
//        nextNodeId = null
//    )
)