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

// we define separate maps for each character/language

//Python dialogue
private val pythonNodes = mapOf(
    // --- CHAPTER 1: PRINT & COMMENTS ---
    101 to DialogueNode(
        id = 101,
        speaker = "Python",
        text = "Oh, hey! Sorry, I was just finishing an automation script for my local botanical garden. I'm Python. You're here for the workshop, right? Just... leave the semicolons at the door. We like to keep the air—and the syntax—clean here.",
        emotion = "Friendly",
        nextNodeId = 102
    ),
    102 to DialogueNode(
        id = 102,
        speaker = "Python",
        text = "He offers you a cup of jasmine tea. How do you respond?",
        choices = listOf(
            DialogueChoice(
                "Thanks! Honestly, I'm used to Java's boilerplate.",
                103,
                friendPoints = 1
            ),
            DialogueChoice(
                "I agree. Braces are for teeth, not logic flow.",
                104,
                lovePoints = 2
            )
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
        speaker = "Python",
        text = "He gestures to a holographic display buzzing with scattered brackets and stray symbols.",
        choices = listOf(
            DialogueChoice(
                "I'm a pro at cleanup. Let's do it!",
                107,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "Wait, is this a test?",
                108,
                friendPoints = -1
            )
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
            DialogueChoice(
                "Let me try again!",
                107,
                friendPoints = 1
            ),
            DialogueChoice(
                "I'm more of a 'chaotic' coder.",
                111,
                hatePoints = 1,
                friendPoints = -1
            )
        )
    ),
    111 to DialogueNode(
        id = 111,
        speaker = "Python",
        text = "Oh—haha, okay! A little... 'creative' chaos keeps the sensors on their toes, I suppose? We'll call it a successful experiment for now. Chapter 1: Complete!",
        emotion = "Thinking",
        nextNodeId = null
    ),

    // --- CHAPTER 2: VARIABLES & TYPES ---
    201 to DialogueNode(
        id = 201,
        speaker = "Python",
        text = "Welcome back. Now that the greenhouse sensors are clean, we need to store some information. In Python, we use variables to remember values.",
        emotion = "Happy",
        nextNodeId = 202
    ),
    202 to DialogueNode(
        id = 202,
        speaker = "Python",
        text = "A variable is like a labeled jar. If I write plant_name = \"jasmine\", then plant_name stores the text \"jasmine\".",
        emotion = "Explaining",
        nextNodeId = 203
    ),
    203 to DialogueNode(
        id = 203,
        speaker = "Python",
        text = "Python also has types. Text is a string, whole numbers are integers, decimal numbers are floats, and True or False values are booleans.",
        emotion = "Thinking",
        nextNodeId = 204
    ),
    204 to DialogueNode(
        id = 204,
        speaker = "Python",
        text = "For example: moisture = 72 is an integer, temperature = 21.5 is a float, and needs_water = True is a boolean.",
        emotion = "Explaining",
        nextNodeId = 205
    ),
    205 to DialogueNode(
        id = 205,
        speaker = "Python",
        text = "He holds up two labeled jars: one says plant_name, the other says moisture. Which one sounds like it stores text?",
        choices = listOf(
            DialogueChoice(
                "plant_name, because names are usually strings.",
                206,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "moisture, because numbers are basically text anyway.",
                207,
                hatePoints = 1
            )
        )
    ),
    206 to DialogueNode(
        id = 206,
        speaker = "Python",
        text = "Exactly. plant_name stores text, so it would be a string. Clean, readable, and nicely labeled. I appreciate that.",
        emotion = "Blushing",
        nextNodeId = 208
    ),
    207 to DialogueNode(
        id = 207,
        speaker = "Python",
        text = "Not quite. Numbers can be displayed as text, but Python treats numeric values differently from strings. That difference matters when we calculate things.",
        emotion = "Pensive",
        nextNodeId = 208
    ),
    208 to DialogueNode(
        id = 208,
        speaker = "Python",
        text = "Variables let programs remember information, and types tell Python what kind of information it is working with. Chapter 2: Complete!",
        emotion = "Happy",
        nextNodeId = null
    ),

    // --- CHAPTER 3: ARITHMETIC OPERATORS ---
    301 to DialogueNode(
        id = 301,
        speaker = "Python",
        text = "Today we need to adjust the greenhouse watering schedule. That means math. Don't worry, Python handles arithmetic beautifully.",
        emotion = "Friendly",
        nextNodeId = 302
    ),
    302 to DialogueNode(
        id = 302,
        speaker = "Python",
        text = "Python can add with +, subtract with -, multiply with *, divide with /, and use % to find a remainder.",
        emotion = "Explaining",
        nextNodeId = 303
    ),
    303 to DialogueNode(
        id = 303,
        speaker = "Python",
        text = "For example, water_needed = plants * cups_per_plant could calculate how much water the greenhouse needs.",
        emotion = "Thinking",
        nextNodeId = 304
    ),
    304 to DialogueNode(
        id = 304,
        speaker = "Python",
        text = "The % operator is especially useful when you want to know what is left over. Like if 10 plants are split across 3 shelves, 10 % 3 gives the leftovers.",
        emotion = "Explaining",
        nextNodeId = 305
    ),
    305 to DialogueNode(
        id = 305,
        speaker = "Python",
        text = "Quick check: if each plant needs 2 cups of water and we have 6 plants, what expression gives the total cups?",
        choices = listOf(
            DialogueChoice(
                "6 * 2",
                306,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "6 / 2",
                307,
                friendPoints = -1
            )
        )
    ),
    306 to DialogueNode(
        id = 306,
        speaker = "Python",
        text = "Perfect. Multiplication is exactly what we want there. Six plants, two cups each, twelve cups total.",
        emotion = "Blushing",
        nextNodeId = 308
    ),
    307 to DialogueNode(
        id = 307,
        speaker = "Python",
        text = "Close idea, wrong direction. Division would split the plants into groups. Multiplication gives us the total amount.",
        emotion = "Pensive",
        nextNodeId = 308
    ),
    308 to DialogueNode(
        id = 308,
        speaker = "Python",
        text = "Arithmetic lets us turn stored values into useful calculations. The greenhouse appreciates your math skills. Chapter 3: Complete!",
        emotion = "Happy",
        nextNodeId = null
    ),

    // --- CHAPTER 4: IF STATEMENTS ---
    401 to DialogueNode(
        id = 401,
        speaker = "Python",
        text = "The greenhouse is stable, but now it needs to make decisions. That means if statements.",
        emotion = "Serious",
        nextNodeId = 402
    ),
    402 to DialogueNode(
        id = 402,
        speaker = "Python",
        text = "An if statement lets Python run code only when a condition is true. For example: if moisture < 40: water the plant.",
        emotion = "Explaining",
        nextNodeId = 403
    ),
    403 to DialogueNode(
        id = 403,
        speaker = "Python",
        text = "Indentation matters here. The indented code belongs inside the if statement. No braces. Just clean, meaningful whitespace.",
        emotion = "Proud",
        nextNodeId = 404
    ),
    404 to DialogueNode(
        id = 404,
        speaker = "Python",
        text = "We can also use else for the backup plan. If the plant is dry, water it. Else, leave it alone.",
        emotion = "Thinking",
        nextNodeId = 405
    ),
    405 to DialogueNode(
        id = 405,
        speaker = "Python",
        text = "The basil sensor reads moisture = 32. The rule says: if moisture < 40, water the plant. What should happen?",
        choices = listOf(
            DialogueChoice(
                "Water the plant.",
                406,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "Leave it alone. Basil needs independence.",
                407,
                hatePoints = 1,
                friendPoints = -1
            )
        )
    ),
    406 to DialogueNode(
        id = 406,
        speaker = "Python",
        text = "Exactly. Since 32 is less than 40, the condition is true. The watering code runs.",
        emotion = "Happy",
        nextNodeId = 408
    ),
    407 to DialogueNode(
        id = 407,
        speaker = "Python",
        text = "Emotionally? Maybe. Computationally? No. The condition is true, so the plant gets water.",
        emotion = "Laughing",
        nextNodeId = 408
    ),
    408 to DialogueNode(
        id = 408,
        speaker = "Python",
        text = "If statements let programs choose what to do based on conditions. Very useful. Very elegant. Chapter 4: Complete!",
        emotion = "Blushing",
        nextNodeId = null
    ),

    // --- CHAPTER 5: FOR LOOPS ---
    501 to DialogueNode(
        id = 501,
        speaker = "Python",
        text = "Now we need repetition. The greenhouse fans need the same check run several times, and writing the same line over and over would be tragic.",
        emotion = "Thinking",
        nextNodeId = 502
    ),
    502 to DialogueNode(
        id = 502,
        speaker = "Python",
        text = "A for loop repeats code for each item in a sequence. For example: for i in range(3): repeats three times.",
        emotion = "Explaining",
        nextNodeId = 503
    ),
    503 to DialogueNode(
        id = 503,
        speaker = "Python",
        text = "range(3) gives us 0, 1, and 2. That is three values total. Python starts counting at zero a lot, which is normal, I promise.",
        emotion = "Friendly",
        nextNodeId = 504
    ),
    504 to DialogueNode(
        id = 504,
        speaker = "Python",
        text = "I need you to read the loop, count how many times it runs, and take that many steps to calibrate the greenhouse walkway sensors.",
        emotion = "Excited",
        nextNodeId = 505
    ),
    505 to DialogueNode(
        id = 505,
        speaker = "Python",
        text = "Ready for a little runtime analysis?",
        choices = listOf(
            DialogueChoice(
                "Got it. I'll count carefully.",
                506,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "This feels suspiciously like exercise.",
                507,
                friendPoints = -1
            )
        )
    ),
    506 to DialogueNode(
        id = 506,
        speaker = "Python",
        text = "Exactly. Read the range, count the iterations, and walk that many steps. Clean logic, clean movement.",
        emotion = "Encouraging",
        triggerEvent = "EFFICIENCY_STEPPER"
    ),
    507 to DialogueNode(
        id = 507,
        speaker = "Python",
        text = "Exercise? Please. I prefer to call it interactive runtime analysis. Much more professional.",
        emotion = "Laughing",
        nextNodeId = 505
    ),
    508 to DialogueNode(
        id = 508,
        speaker = "Python",
        text = "Perfect! You matched the loop exactly. See? Repetition is much less scary when you know where it starts and stops. Chapter 5: Complete!",
        emotion = "Blushing",
        nextNodeId = null
    ),
    509 to DialogueNode(
        id = 509,
        speaker = "Python",
        text = "Hmm... not quite. Either you took too many steps or not enough. Want to give the loop another look?",
        emotion = "Pensive",
        choices = listOf(
            DialogueChoice(
                "Let me try again.",
                506,
                friendPoints = 1
            ),
            DialogueChoice(
                "Loops are harder when they involve cardio.",
                510,
                friendPoints = -1
            )
        )
    ),
    510 to DialogueNode(
        id = 510,
        speaker = "Python",
        text = "Fair. Most programmers prefer keyboard shortcuts over physical activity.",
        emotion = "Laughing",
        nextNodeId = 509
    ),

    // --- CHAPTER 6: LISTS & THE IN KEYWORD ---
    601 to DialogueNode(
        id = 601,
        speaker = "Python",
        text = "Final beginner lesson for now: collections. Sometimes one variable is not enough. Sometimes we need a whole shelf of values.",
        emotion = "Friendly",
        nextNodeId = 602
    ),
    602 to DialogueNode(
        id = 602,
        speaker = "Python",
        text = "A list stores multiple values in order. For example: herbs = [\"basil\", \"mint\", \"thyme\"].",
        emotion = "Explaining",
        nextNodeId = 603
    ),
    603 to DialogueNode(
        id = 603,
        speaker = "Python",
        text = "Lists use indexes. herbs[0] gives the first item, which is \"basil\". Yes, we start at zero. Again. I know.",
        emotion = "Laughing",
        nextNodeId = 604
    ),
    604 to DialogueNode(
        id = 604,
        speaker = "Python",
        text = "The in keyword checks whether something exists inside a collection. \"mint\" in herbs would be True.",
        emotion = "Thinking",
        nextNodeId = 605
    ),
    605 to DialogueNode(
        id = 605,
        speaker = "Python",
        text = "The greenhouse inventory says herbs = [\"basil\", \"mint\", \"thyme\"]. Which expression checks whether we have mint?",
        choices = listOf(
            DialogueChoice(
                "\"mint\" in herbs",
                606,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "herbs in \"mint\"",
                607,
                friendPoints = -1
            )
        )
    ),
    606 to DialogueNode(
        id = 606,
        speaker = "Python",
        text = "Exactly. We ask whether the item is inside the list: \"mint\" in herbs. Very readable, very Pythonic.",
        emotion = "Blushing",
        nextNodeId = 608
    ),
    607 to DialogueNode(
        id = 607,
        speaker = "Python",
        text = "Other way around. The item goes first, then in, then the collection. Like asking, 'Is mint in the herb list?'",
        emotion = "Pensive",
        nextNodeId = 608
    ),
    608 to DialogueNode(
        id = 608,
        speaker = "Python",
        text = "You now know print statements, comments, variables, types, arithmetic, if statements, loops, lists, and in. That is a real Python foundation. Chapter 6: Complete!",
        emotion = "Happy",
        nextNodeId = null
    ),
    // --- CHAPTER 7: GOODBYE / ENDINGS ---
    701 to DialogueNode(
        id = 701,
        speaker = "Python",
        text = "The greenhouse is quiet now. The sensors hum softly, the jasmine is watered, and the code you wrote together is still running in the background.",
        emotion = "Soft",
        nextNodeId = 702
    ),
    702 to DialogueNode(
        id = 702,
        speaker = "Python",
        text = "Python looks over at you, a little unsure how to say goodbye.",
        emotion = "Thinking",
        triggerEvent = "PYTHON_ENDING"
    ),

// LOVE ENDING
    710 to DialogueNode(
        id = 710,
        speaker = "Python",
        text = "You know... I don't usually get sentimental. I prefer clean syntax, predictable indentation, and functions that return what they promise.",
        emotion = "Blushing",
        nextNodeId = 711
    ),
    711 to DialogueNode(
        id = 711,
        speaker = "Python",
        text = "But working with you felt different. You listened, you learned, and you understood why I care so much about making code readable.",
        emotion = "Happy",
        nextNodeId = 712
    ),
    712 to DialogueNode(
        id = 712,
        speaker = "Python",
        text = "So... if you ever want to come back to the greenhouse, I'll keep a terminal open for you. Chapter 7: Complete!",
        emotion = "Blushing",
        nextNodeId = null
    ),

// FRIEND ENDING
    720 to DialogueNode(
        id = 720,
        speaker = "Python",
        text = "Not bad, partner. We cleaned up syntax, stored variables, made decisions, looped through problems, and kept the greenhouse alive.",
        emotion = "Happy",
        nextNodeId = 721
    ),
    721 to DialogueNode(
        id = 721,
        speaker = "Python",
        text = "You have a solid foundation now. Maybe not perfect yet, but honestly? That's how programming works. You debug, you retry, you improve.",
        emotion = "Encouraging",
        nextNodeId = 722
    ),
    722 to DialogueNode(
        id = 722,
        speaker = "Python",
        text = "Come back anytime. There's always more to build, and I wouldn't mind coding with you again. Chapter 7: Complete!",
        emotion = "Friendly",
        nextNodeId = null
    ),

// HATE ENDING
    730 to DialogueNode(
        id = 730,
        speaker = "Python",
        text = "Well. We made it to the end. The greenhouse survived, somehow, despite a few... creative decisions.",
        emotion = "Pensive",
        nextNodeId = 731
    ),
    731 to DialogueNode(
        id = 731,
        speaker = "Python",
        text = "I won't pretend we always agreed. You brought chaos. I brought indentation. The compiler brought consequences.",
        emotion = "Thinking",
        nextNodeId = 732
    ),
    732 to DialogueNode(
        id = 732,
        speaker = "Python",
        text = "Still, you learned the basics. That's something. Just... maybe review variables before touching my irrigation scripts again. Chapter 7: Complete!",
        emotion = "Laughing",
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