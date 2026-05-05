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
                friendPoints = 2
            ),
            DialogueChoice(
                "I agree. Braces are for teeth, not logic flow.",
                104,
                lovePoints = 2
            ),
            DialogueChoice(
                "Semicolons are superior.",
                104,
                hatePoints = 2,
                lovePoints = -2,
                friendPoints = -2
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
                friendPoints = 2
            ),
            DialogueChoice(
                "Wait, is this a test?",
                108,
                friendPoints = -1
            ),
            DialogueChoice(
                choiceText = "Why am I doing your dirty work?",
                targetNodeId = 130,
                hatePoints = 5,
                friendPoints = -2
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
    130 to DialogueNode(
        id = 130,
        speaker = "Python",
        text = "Uhm.. aren't you here to learn?",
        emotion = "Serious",
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
        text = "Welcome back. The greenhouse sensors are clean now, but the data is just floating around. Tragic. We need variables.",
        emotion = "Happy",
        nextNodeId = 202
    ),
    202 to DialogueNode(
        id = 202,
        speaker = "Python",
        text = "A variable is a name that stores a value. Like plant_name = \"jasmine\" or moisture = 72.",
        emotion = "Explaining",
        nextNodeId = 203
    ),
    203 to DialogueNode(
        id = 203,
        speaker = "Python",
        text = "He pulls out three tiny labeled jars: plant_name, moisture, and needs_water.",
        emotion = "Friendly",
        nextNodeId = 204
    ),
    204 to DialogueNode(
        id = 204,
        speaker = "Python",
        text = "Which one sounds like it should store True or False?",
        choices = listOf(
            DialogueChoice(
                "needs_water?",
                205,
                lovePoints = 1,
                friendPoints = 2
            ),
            DialogueChoice(
                "plant_name?",
                206,
                friendPoints = 1
            ),
            DialogueChoice(
                "Why are you asking me this?",
                207,
                friendPoints = -2,
                hatePoints = 2
            )
        )
    ),
    205 to DialogueNode(
        id = 205,
        speaker = "Python",
        text = "Exactly. needs_water would probably be a boolean: True or False. Very clean reasoning.",
        emotion = "Blushing",
        nextNodeId = 208
    ),
    206 to DialogueNode(
        id = 206,
        speaker = "Python",
        text = "Emotionally, yes. Technically, plant_name is probably a string because it stores text. But I appreciate the empathy.",
        emotion = "Laughing",
        nextNodeId = 208
    ),
    207 to DialogueNode(
        id = 207,
        speaker = "Python",
        text = "That is... a bold philosophy of data. But no. moisture would usually be an int or float.",
        emotion = "Pensive",
        nextNodeId = 208
    ),
    208 to DialogueNode(
        id = 208,
        speaker = "Python",
        text = "Python has common types: strings for text, ints for whole numbers, floats for decimals, and booleans for True or False.",
        emotion = "Explaining",
        nextNodeId = 209
    ),
    209 to DialogueNode(
        id = 209,
        speaker = "Python",
        text = "Quick check. What type is temperature = 71.5?",
        choices = listOf(
            DialogueChoice(
                "Float, because it has a decimal.",
                210,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "Integer, because it is still a number.",
                211,
                friendPoints = -1
            ),
        )
    ),
    210 to DialogueNode(
        id = 210,
        speaker = "Python",
        text = "Perfect. A decimal number is a float. You are making this very easy to explain.",
        emotion = "Happy",
        nextNodeId = 213
    ),
    211 to DialogueNode(
        id = 211,
        speaker = "Python",
        text = "Close, but integers are whole numbers. Since 71.5 has a decimal, Python treats it as a float.",
        emotion = "Encouraging",
        nextNodeId = 213
    ),
    212 to DialogueNode(
        id = 212,
        speaker = "Python",
        text = "Only if it were written like \"71.5\". Without quotes, Python sees it as a number.",
        emotion = "Pensive",
        nextNodeId = 213
    ),
    213 to DialogueNode(
        id = 213,
        speaker = "Python",
        text = "He smiles and labels the greenhouse dashboard: plant_name, moisture, temperature, needs_water.",
        emotion = "Soft",
        nextNodeId = 214
    ),
    214 to DialogueNode(
        id = 214,
        speaker = "Python",
        text = "How do you feel about variables so far?",
        choices = listOf(
            DialogueChoice(
                "I think they are perfect!",
                215,
                lovePoints = 2
            ),
            DialogueChoice(
                "They are like little storage boxes. Makes sense.",
                216,
                friendPoints = 2
            ),
            DialogueChoice(
                "I will name every variable x and hope for the best.",
                217,
                friendPoints = -2,
                lovePoints = -2,
                hatePoints = 2
            )
        )
    ),
    215 to DialogueNode(
        id = 215,
        speaker = "Python",
        text = "Readable names? Careful. Say things like that and I might start thinking you understand me.",
        emotion = "Blushing",
        nextNodeId = 218
    ),
    216 to DialogueNode(
        id = 216,
        speaker = "Python",
        text = "Exactly. Little storage boxes with labels. Honestly, that is a very solid way to think about it.",
        emotion = "Happy",
        nextNodeId = 218
    ),
    217 to DialogueNode(
        id = 217,
        speaker = "Python",
        text = "I felt a disturbance in every codebase ever written. Please do not name everything x.",
        emotion = "Pensive",
        nextNodeId = 218
    ),
    218 to DialogueNode(
        id = 218,
        speaker = "Python",
        text = "Variables store values. Types describe what kind of values they are. Chapter 2: Complete!",
        emotion = "Happy",
        nextNodeId = null
    ),

// --- CHAPTER 3: ARITHMETIC OPERATORS ---
    301 to DialogueNode(
        id = 301,
        speaker = "Python",
        text = "Today, the greenhouse needs math. Nothing scary. Just arithmetic operators.",
        emotion = "Friendly",
        nextNodeId = 302
    ),
    302 to DialogueNode(
        id = 302,
        speaker = "Python",
        text = "Python uses + for addition, - for subtraction, * for multiplication, / for division, and % for remainders.",
        emotion = "Explaining",
        nextNodeId = 303
    ),
    303 to DialogueNode(
        id = 303,
        speaker = "System",
        text = "The greenhouse display flickers: 6 plants, 2 cups of water each.",
        emotion = "Neutral",
        nextNodeId = 304
    ),
    304 to DialogueNode(
        id = 304,
        speaker = "Python",
        text = "Which expression calculates the total cups of water?",
        choices = listOf(
            DialogueChoice(
                "6 * 2",
                305,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "6 / 2",
                307,
            )
        )
    ),
    305 to DialogueNode(
        id = 305,
        speaker = "Python",
        text = "Exactly. Six plants, and two cups each. Clean multiplication. Beautiful.",
        emotion = "Blushing",
        nextNodeId = 308
    ),
    306 to DialogueNode(
        id = 306,
        speaker = "Python",
        text = "That adds the two numbers, but we need repeated groups: 2 cups for each of 6 plants. Multiplication fits better.",
        emotion = "Encouraging",
        nextNodeId = 308
    ),
    307 to DialogueNode(
        id = 307,
        speaker = "Python",
        text = "That would split the plants into groups. The basil is now confused, but recoverable.",
        emotion = "Laughing",
        nextNodeId = 308
    ),
    308 to DialogueNode(
        id = 308,
        speaker = "Python",
        text = "Now for my favorite weird little operator: %. It gives the remainder after division.",
        emotion = "Thinking",
        nextNodeId = 309
    ),
    309 to DialogueNode(
        id = 309,
        speaker = "Python",
        text = "If 10 seed packets are split evenly across 3 shelves, 10 % 3 gives what is left over.",
        emotion = "Explaining",
        nextNodeId = 310
    ),
    310 to DialogueNode(
        id = 310,
        speaker = "Python",
        text = "So what does 10 % 3 equal?",
        choices = listOf(
            DialogueChoice(
                "1, because 9 fits evenly and 1 is left over.",
                311,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "3, because there are 3 shelves.",
                312,
                friendPoints = -1
            ),
            DialogueChoice(
                "10, because I refuse to divide the seeds.",
                313,
                hatePoints = 1
            )
        )
    ),
    311 to DialogueNode(
        id = 311,
        speaker = "Python",
        text = "Perfect. That is exactly how modulo works. I knew you had remainder energy.",
        emotion = "Happy",
        nextNodeId = 314
    ),
    312 to DialogueNode(
        id = 312,
        speaker = "Python",
        text = "Not quite. The 3 is what we divide by. The result of % is the leftover amount.",
        emotion = "Pensive",
        nextNodeId = 314
    ),
    313 to DialogueNode(
        id = 313,
        speaker = "Python",
        text = "The seeds appreciate your loyalty, but Python is still going to calculate the remainder.",
        emotion = "Laughing",
        nextNodeId = 314
    ),
    314 to DialogueNode(
        id = 314,
        speaker = "Python",
        text = "One more. If sunlight = 8 and shade = 3, what does sunlight - shade give us?",
        choices = listOf(
            DialogueChoice(
                "5, the difference between them.",
                315,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "11, because more light is better.",
                316,
                friendPoints = -1
            ),
            DialogueChoice(
                "I'm getting bored..",
                317,
                friendPoints = -5,
                lovePoints = -5,
                hatePoints = 10
            )
        )
    ),
    315 to DialogueNode(
        id = 315,
        speaker = "Python",
        text = "Exactly. Subtraction finds the difference. Very practical. Very elegant.",
        emotion = "Blushing",
        nextNodeId = 318
    ),
    316 to DialogueNode(
        id = 316,
        speaker = "Python",
        text = "That would be addition. Useful sometimes, but not when we are finding the difference.",
        emotion = "Encouraging",
        nextNodeId = 318
    ),
    317 to DialogueNode(
        id = 317,
        speaker = "Python",
        text = "The arithmetic answer is 5.",
        emotion = "Serious",
        nextNodeId = 318
    ),
    318 to DialogueNode(
        id = 318,
        speaker = "Python",
        text = "Arithmetic operators help programs calculate new values from stored ones. Chapter 3: Complete!",
        emotion = "Happy",
        nextNodeId = null
    ),

// --- CHAPTER 4: IF STATEMENTS ---
    401 to DialogueNode(
        id = 401,
        speaker = "Python",
        text = "The greenhouse can store data and calculate things now. Next, it needs to make decisions.",
        emotion = "Serious",
        nextNodeId = 402
    ),
    402 to DialogueNode(
        id = 402,
        speaker = "Python",
        text = "That means if statements. An if statement runs code only when a condition is true.",
        emotion = "Explaining",
        nextNodeId = 403
    ),
    403 to DialogueNode(
        id = 403,
        speaker = "Python",
        text = "Example: if moisture < 40: water_plant(). If the moisture is below 40, the plant gets water.",
        emotion = "Thinking",
        nextNodeId = 404
    ),
    404 to DialogueNode(
        id = 404,
        speaker = "Python",
        text = "Also, indentation matters. The indented lines belong inside the if statement. No braces. Just meaningful whitespace.",
        emotion = "Proud",
        nextNodeId = 405
    ),
    405 to DialogueNode(
        id = 405,
        speaker = "System",
        text = "A basil sensor flashes: moisture = 32.",
        emotion = "Neutral",
        nextNodeId = 406
    ),
    406 to DialogueNode(
        id = 406,
        speaker = "Python",
        text = "The rule is: if moisture < 40, water the plant. What should happen?",
        choices = listOf(
            DialogueChoice(
                "Water the plant.",
                407,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "Leave it alone. It needs to become stronger.",
                408,
                hatePoints = 1
            ),
            DialogueChoice(
                "Check whether 32 is less than 40 first.",
                409,
                friendPoints = 2
            )
        )
    ),
    407 to DialogueNode(
        id = 407,
        speaker = "Python",
        text = "Exactly. 32 is less than 40, so the condition is true and the watering code runs.",
        emotion = "Happy",
        nextNodeId = 410
    ),
    408 to DialogueNode(
        id = 408,
        speaker = "Python",
        text = "That is emotionally intense for basil. Computationally, the condition is true, so we water it.",
        emotion = "Pensive",
        nextNodeId = 410
    ),
    409 to DialogueNode(
        id = 409,
        speaker = "Python",
        text = "Good instinct. Conditions are all about checking whether something is true before acting.",
        emotion = "Encouraging",
        nextNodeId = 410
    ),
    410 to DialogueNode(
        id = 410,
        speaker = "Python",
        text = "We can also use else. If the plant is dry, water it. Else, leave it alone.",
        emotion = "Explaining",
        nextNodeId = 411
    ),
    411 to DialogueNode(
        id = 411,
        speaker = "System",
        text = "A mint sensor flashes: moisture = 57.",
        emotion = "Neutral",
        nextNodeId = 412
    ),
    412 to DialogueNode(
        id = 412,
        speaker = "Python",
        text = "Same rule: if moisture < 40, water the plant. Else, leave it alone. What happens to the mint?",
        choices = listOf(
            DialogueChoice(
                "Leave it alone because 57 is not less than 40.",
                413,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "Water it anyway. Mint deserves luxury.",
                414,
                friendPoints = 1,
                hatePoints = 1
            ),
            DialogueChoice(
                "The if statement breaks because 57 is too high.",
                415,
                hatePoints = 1
            )
        )
    ),
    413 to DialogueNode(
        id = 413,
        speaker = "Python",
        text = "Exactly. The condition is false, so the else path runs.",
        emotion = "Blushing",
        nextNodeId = 416
    ),
    414 to DialogueNode(
        id = 414,
        speaker = "Python",
        text = "A generous answer, but the else branch says to leave it alone. Mint can be dramatic without extra water.",
        emotion = "Laughing",
        nextNodeId = 416
    ),
    415 to DialogueNode(
        id = 415,
        speaker = "Python",
        text = "No breakage. The condition is just false, so Python skips the if body and uses else.",
        emotion = "Encouraging",
        nextNodeId = 416
    ),
    416 to DialogueNode(
        id = 416,
        speaker = "Python",
        text = "How do you want to write your greenhouse logic?",
        choices = listOf(
            DialogueChoice(
                "Clearly, so every condition is easy to understand.",
                417,
                lovePoints = 2
            ),
            DialogueChoice(
                "Carefully, because one wrong condition changes behavior.",
                418,
                friendPoints = 2
            ),
            DialogueChoice(
                "With vibes. The plants will know what I meant.",
                419,
                hatePoints = 2
            )
        )
    ),
    417 to DialogueNode(
        id = 417,
        speaker = "Python",
        text = "Readable conditional logic. You really do know how to make a language feel appreciated.",
        emotion = "Blushing",
        nextNodeId = 420
    ),
    418 to DialogueNode(
        id = 418,
        speaker = "Python",
        text = "Exactly. Conditions control behavior, so careful thinking matters.",
        emotion = "Happy",
        nextNodeId = 420
    ),
    419 to DialogueNode(
        id = 419,
        speaker = "Python",
        text = "The plants cannot parse vibes. I have checked.",
        emotion = "Laughing",
        nextNodeId = 420
    ),
    420 to DialogueNode(
        id = 420,
        speaker = "Python",
        text = "If statements let programs choose what to do based on conditions. Chapter 4: Complete!",
        emotion = "Happy",
        nextNodeId = null
    ),

// --- CHAPTER 5: FOR LOOPS / EFFICIENCY STEPPER ---
    501 to DialogueNode(
        id = 501,
        speaker = "Python",
        text = "Now we need repetition. The greenhouse fans need several checks, and I refuse to copy-paste the same line forever.",
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
        text = "range(3) gives 0, 1, and 2. That is three values total. Python starts at zero often. Please do not be alarmed.",
        emotion = "Friendly",
        nextNodeId = 504
    ),
    504 to DialogueNode(
        id = 504,
        speaker = "Python",
        text = "Why does range(3) run three times?",
        choices = listOf(
            DialogueChoice(
                "Because it gives three values: 0, 1, and 2.",
                505,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "Because it starts at 1 and ends at 3.",
                506,
                friendPoints = -1
            ),
            DialogueChoice(
                "Because Python likes being mysterious.",
                507,
                friendPoints = 1
            )
        )
    ),
    505 to DialogueNode(
        id = 505,
        speaker = "Python",
        text = "Exactly. The numbers are 0, 1, and 2. Three values, three repetitions.",
        emotion = "Happy",
        nextNodeId = 508
    ),
    506 to DialogueNode(
        id = 506,
        speaker = "Python",
        text = "Common guess, but not quite. range(3) starts at 0 and stops before 3.",
        emotion = "Encouraging",
        nextNodeId = 508
    ),
    507 to DialogueNode(
        id = 507,
        speaker = "Python",
        text = "I prefer 'elegant,' but I understand how it looks from the outside.",
        emotion = "Laughing",
        nextNodeId = 508
    ),
    508 to DialogueNode(
        id = 508,
        speaker = "Python",
        text = "The walkway sensors need calibration. You will read a loop, figure out how many times it runs, and take that many steps.",
        emotion = "Excited",
        nextNodeId = 509
    ),
    509 to DialogueNode(
        id = 509,
        speaker = "Python",
        text = "Ready for the Efficiency Stepper?",
        choices = listOf(
            DialogueChoice(
                "Yes. I will count the loop carefully.",
                510,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "I am ready, but I reserve the right to complain.",
                511,
                friendPoints = 1
            ),
            DialogueChoice(
                "I will simply guess and sprint.",
                512,
                hatePoints = 1
            )
        )
    ),
    510 to DialogueNode(
        id = 510,
        speaker = "Python",
        text = "Perfect. Count the iterations, then match them with your steps.",
        emotion = "Encouraging",
        triggerEvent = "EFFICIENCY_STEPPER"
    ),
    511 to DialogueNode(
        id = 511,
        speaker = "Python",
        text = "Fair. Complaining is allowed as long as the loop count is accurate.",
        emotion = "Laughing",
        triggerEvent = "EFFICIENCY_STEPPER"
    ),
    512 to DialogueNode(
        id = 512,
        speaker = "Python",
        text = "That is less 'runtime analysis' and more 'cardio chaos,' but let's see what happens.",
        emotion = "Pensive",
        triggerEvent = "EFFICIENCY_STEPPER"
    ),
    513 to DialogueNode(
        id = 513,
        speaker = "Python",
        text = "Perfect! You matched the loop exactly. Clean counting, clean movement, clean code.",
        emotion = "Blushing",
        nextNodeId = 515
    ),
    514 to DialogueNode(
        id = 514,
        speaker = "Python",
        text = "Not quite. Either the loop ran more or fewer times than your steps. Want to retry or move on?",
        emotion = "Pensive",
        choices = listOf(
            DialogueChoice(
                "Let me try again.",
                510,
                friendPoints = 1
            ),
            DialogueChoice(
                "Explain range one more time, then I will move on.",
                515,
                friendPoints = 1
            ),
            DialogueChoice(
                "Loops and legs should never mix.",
                516,
                hatePoints = 1
            )
        )
    ),
    515 to DialogueNode(
        id = 515,
        speaker = "Python",
        text = "For loops repeat work. range tells the loop how many values to move through. Chapter 5: Complete!",
        emotion = "Happy",
        nextNodeId = null
    ),
    516 to DialogueNode(
        id = 516,
        speaker = "Python",
        text = "Understandable. Most programmers do prefer seated algorithms. Chapter 5: Complete!",
        emotion = "Laughing",
        nextNodeId = null
    ),

// --- CHAPTER 6: KEEPING SECRETS / LIGHT SENSOR GAME ---
    601 to DialogueNode(
        id = 601,
        speaker = "Python",
        text = "There is one last greenhouse issue. Some data should not be visible all the time.",
        emotion = "Serious",
        nextNodeId = 602
    ),
    602 to DialogueNode(
        id = 602,
        speaker = "Python",
        text = "A good program knows when to reveal information and when to protect it. Today, we are keeping secrets.",
        emotion = "Thinking",
        nextNodeId = 603
    ),
    603 to DialogueNode(
        id = 603,
        speaker = "System",
        text = "A locked panel glows beside the jasmine plants. The words SECRET SENSOR MODE appear across the screen.",
        emotion = "Neutral",
        nextNodeId = 604
    ),
    604 to DialogueNode(
        id = 604,
        speaker = "Python",
        text = "The panel unlocks only when the light level changes. Covering the sensor creates a private little shadow.",
        emotion = "Explaining",
        nextNodeId = 605
    ),
    605 to DialogueNode(
        id = 605,
        speaker = "Python",
        text = "What do you think this mini-game is checking?",
        choices = listOf(
            DialogueChoice(
                "Whether the environment changes from light to dark.",
                606,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "Whether I can physically hide a secret.",
                607,
                friendPoints = 1
            ),
            DialogueChoice(
                "Whether the phone fears darkness.",
                608,
                hatePoints = 1
            )
        )
    ),
    606 to DialogueNode(
        id = 606,
        speaker = "Python",
        text = "Exactly. The program reacts to sensor input. Light changes become information the app can use.",
        emotion = "Happy",
        nextNodeId = 609
    ),
    607 to DialogueNode(
        id = 607,
        speaker = "Python",
        text = "Honestly? Kind of. But technically, we are reading the light sensor and responding to its values.",
        emotion = "Laughing",
        nextNodeId = 609
    ),
    608 to DialogueNode(
        id = 608,
        speaker = "Python",
        text = "The phone is very brave. The program is simply watching the light sensor.",
        emotion = "Pensive",
        nextNodeId = 609
    ),
    609 to DialogueNode(
        id = 609,
        speaker = "Python",
        text = "Ready to unlock the secret panel?",
        choices = listOf(
            DialogueChoice(
                "Yes. I will cover the sensor carefully.",
                610,
                lovePoints = 1,
                friendPoints = 1
            ),
            DialogueChoice(
                "Can I use Private Mode instead?",
                611,
                friendPoints = 1
            ),
            DialogueChoice(
                "I will stare directly into the sensor until it obeys.",
                611,
                hatePoints = 1
            )
        )
    ),
    610 to DialogueNode(
        id = 610,
        speaker = "Python",
        text = "Great. Watch the light level, create a shadow, and let the program detect the change.",
        emotion = "Encouraging",
        triggerEvent = "LIGHT_SENSITIVE_SECRET"
    ),
    611 to DialogueNode(
        id = 611,
        speaker = "Python",
        text = "That works too. A good app should offer an alternate path when sensors are not comfortable or practical.",
        emotion = "Friendly",
        triggerEvent = "LIGHT_SENSITIVE_SECRET"
    ),
    612 to DialogueNode(
        id = 612,
        speaker = "Python",
        text = "Unlocked! The panel slides open, revealing a tiny glowing note: readable code is a love language.",
        emotion = "Blushing",
        nextNodeId = 614
    ),
    613 to DialogueNode(
        id = 613,
        speaker = "Python",
        text = "The panel flickers but does not fully unlock. That's okay. Sensor input can be finicky, and retry paths matter.",
        emotion = "Encouraging",
        choices = listOf(
            DialogueChoice(
                "Let me try the sensor again.",
                610,
                friendPoints = 1
            ),
            DialogueChoice(
                "Let's move on. I understand the idea.",
                614,
                friendPoints = 1
            ),
            DialogueChoice(
                "The secret can stay secret.",
                615,
                hatePoints = 1
            )
        )
    ),
    614 to DialogueNode(
        id = 614,
        speaker = "Python",
        text = "Sensors let apps respond to the physical world. Light, motion, steps — all of it can become input. Chapter 6: Complete!",
        emotion = "Happy",
        nextNodeId = null
    ),
    615 to DialogueNode(
        id = 615,
        speaker = "Python",
        text = "Mysterious. Slightly inconvenient. But still educational. Chapter 6: Complete!",
        emotion = "Laughing",
        nextNodeId = null
    ),

// --- CHAPTER 7: ENDINGS ---
    701 to DialogueNode(
        id = 701,
        speaker = "System",
        text = "The greenhouse is calm. The sensors are clean, the variables are labeled, the plants are watered, and the secret panel is quiet again.",
        emotion = "Soft",
        nextNodeId = 702
    ),
    702 to DialogueNode(
        id = 702,
        speaker = "Python",
        text = "Well. That was the full beginner path. Print statements, variables, arithmetic, conditions, loops, and sensor input.",
        emotion = "Thinking",
        nextNodeId = 703
    ),
    703 to DialogueNode(
        id = 703,
        speaker = "Python",
        text = "Before you go... what did this feel like to you?",
        choices = listOf(
            DialogueChoice(
                "Like we really understood each other.",
                704,
                lovePoints = 2
            ),
            DialogueChoice(
                "Like a solid team project. I learned a lot.",
                705,
                friendPoints = 2
            ),
            DialogueChoice(
                "Like I survived a very dramatic greenhouse.",
                706,
                hatePoints = 5,
                friendPoints = 1
            )
        )
    ),
    704 to DialogueNode(
        id = 704,
        speaker = "Python",
        text = "Python looks away, suddenly very interested in the terminal prompt.",
        emotion = "Blushing",
        nextNodeId = 707
    ),
    705 to DialogueNode(
        id = 705,
        speaker = "Python",
        text = "He smiles. Not flashy, not dramatic. Just genuinely proud.",
        emotion = "Happy",
        nextNodeId = 707
    ),
    706 to DialogueNode(
        id = 706,
        speaker = "Python",
        text = "Honestly, fair. The greenhouse does have main-character energy.",
        emotion = "Laughing",
        nextNodeId = 707
    ),
    707 to DialogueNode(
        id = 707,
        speaker = "Python",
        text = "I suppose this is where the program decides what kind of ending we earned.",
        emotion = "Soft",
        triggerEvent = "PYTHON_ENDING"
    ),

// LOVE ENDING
    710 to DialogueNode(
        id = 710,
        speaker = "Python",
        text = "You know... I do not usually get sentimental. I prefer clean syntax, predictable indentation, and functions that return what they promise.",
        emotion = "Blushing",
        nextNodeId = 711
    ),
    711 to DialogueNode(
        id = 711,
        speaker = "Python",
        text = "But working with you felt different. You cared about the logic, the readability, and the little details.",
        emotion = "Soft",
        nextNodeId = 712
    ),
    712 to DialogueNode(
        id = 712,
        speaker = "Python",
        text = "You did not just learn my syntax. You understood why I care about making code feel clear.",
        emotion = "Happy",
        nextNodeId = 713
    ),
    713 to DialogueNode(
        id = 713,
        speaker = "Python",
        text = "So if you ever want to come back to the greenhouse, I will keep a terminal open for you. Chapter 7: Complete!",
        emotion = "Blushing",
        nextNodeId = null
    ),

// FRIEND ENDING
    720 to DialogueNode(
        id = 720,
        speaker = "Python",
        text = "Not bad, partner. We cleaned syntax, stored variables, calculated values, made decisions, looped through problems, and unlocked a sensor panel.",
        emotion = "Happy",
        nextNodeId = 721
    ),
    721 to DialogueNode(
        id = 721,
        speaker = "Python",
        text = "You have a real foundation now. Not because you were perfect, but because you kept thinking through the problems.",
        emotion = "Encouraging",
        nextNodeId = 722
    ),
    722 to DialogueNode(
        id = 722,
        speaker = "Python",
        text = "Come back anytime. There is always more to build, and I would be glad to code with you again. Chapter 7: Complete!",
        emotion = "Happy",
        nextNodeId = null
    ),

// HATE ENDING
    730 to DialogueNode(
        id = 730,
        speaker = "Python",
        text = "Well. We made it to the end. The greenhouse survived, despite several choices I would describe as... computationally adventurous.",
        emotion = "Pensive",
        nextNodeId = 731
    ),
    731 to DialogueNode(
        id = 731,
        speaker = "Python",
        text = "You brought chaos. I brought indentation. The plants brought silent judgment.",
        emotion = "Thinking",
        nextNodeId = 732
    ),
    732 to DialogueNode(
        id = 732,
        speaker = "Python",
        text = "Still, you learned the basics. Variables, operators, conditions, loops, and sensors. That counts.",
        emotion = "Encouraging",
        nextNodeId = 733
    ),
    733 to DialogueNode(
        id = 733,
        speaker = "Python",
        text = "Just... maybe review variable names before touching my irrigation scripts again. Chapter 7: Complete!",
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