package com.example.lovebyte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lovebyte.data.model.*
import com.example.lovebyte.ui.screens.GameScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ─── Helpers ───────────────────────────────────────────────────────────────

    private fun baseState(
        language: ProgrammingLanguage = ProgrammingLanguage.PYTHON,
        isMiniGameActive: Boolean = false
    ) = LoveByteState(
        currentLanguage = language,
        isMiniGameActive = isMiniGameActive
    )

    private fun simpleNode(
        id: Int = 101,
        speaker: String = "Python",
        text: String = "Hello there!",
        emotion: String = "Friendly",
        nextNodeId: Int? = 102,
        choices: List<DialogueChoice>? = null,
        triggerEvent: String? = null
    ) = DialogueNode(
        id = id,
        speaker = speaker,
        text = text,
        emotion = emotion,
        nextNodeId = nextNodeId,
        choices = choices,
        triggerEvent = triggerEvent
    )

    // ─── Null node (error/safety state) ───────────────────────────────────────

    @Test
    fun gameScreen_nullNode_showsReturnButton() {
        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = null,
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule.onNodeWithText("RETURN TO TIMELINE", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun gameScreen_nullNode_returnButton_invokesCallback() {
        var backPressed = false

        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = null,
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = { backPressed = true },
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule.onNodeWithText("RETURN TO TIMELINE", ignoreCase = true).performClick()
        assert(backPressed)
    }

    // ─── Dialogue display ──────────────────────────────────────────────────────

    @Test
    fun gameScreen_displaysNodeSpeakerName() {
        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(speaker = "Python"),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule.onNodeWithText("Python").assertIsDisplayed()
    }

    @Test
    fun gameScreen_displaysNodeText() {
        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(text = "A friendly but blunt snake."),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule.onNodeWithText("A friendly but blunt snake.").assertIsDisplayed()
    }

    @Test
    fun gameScreen_displaysEmotionTag() {
        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(emotion = "Happy"),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        // Emotion is uppercased in the UI
        composeTestRule.onNodeWithText("HAPPY").assertIsDisplayed()
    }

    @Test
    fun gameScreen_displaysCharacterSprite() {
        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Character Sprite")
            .assertIsDisplayed()
    }

    // ─── Tap-to-advance (no choices) ──────────────────────────────────────────

    @Test
    fun gameScreen_noChoices_showsAdvanceIndicator() {
        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(choices = null, nextNodeId = 102),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule.onNodeWithText("▼").assertIsDisplayed()
    }

    @Test
    fun gameScreen_tapDialogueCard_advancesToNextNode() {
        var advancedTo = -1

        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(nextNodeId = 102),
                onNodeAdvanced = { advancedTo = it },
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        // Tap the dialogue text to advance
        composeTestRule.onNodeWithText("Hello there!").performClick()
        assert(advancedTo == 102)
    }

    @Test
    fun gameScreen_tapDialogueCard_nullNextNode_triggersChapterComplete() {
        var chapterCompleted = false

        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(nextNodeId = null, choices = null),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = { chapterCompleted = true }
            )
        }

        composeTestRule.onNodeWithText("Hello there!").performClick()
        assert(chapterCompleted)
    }

    @Test
    fun gameScreen_tapDialogueCard_nullNextNode_showsChapterCompleteDialog() {
        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(nextNodeId = null, choices = null),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule.onNodeWithText("Hello there!").performClick()
        composeTestRule.onNodeWithText("CHAPTER COMPLETE!", ignoreCase = true).assertIsDisplayed()
    }

    // ─── Chapter complete dialog ───────────────────────────────────────────────

    @Test
    fun chapterCompleteDialog_nextChapterButton_invokesCallback() {
        var nextChapterCalled = false

        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(nextNodeId = null, choices = null),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = { nextChapterCalled = true },
                onChapterCompleted = {}
            )
        }

        // Trigger dialog
        composeTestRule.onNodeWithText("Hello there!").performClick()
        composeTestRule.onNodeWithText("NEXT CHAPTER", ignoreCase = true).performClick()
        assert(nextChapterCalled)
    }

    @Test
    fun chapterCompleteDialog_chapterSelectButton_invokesBackCallback() {
        var backPressed = false

        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(nextNodeId = null, choices = null),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = { backPressed = true },
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule.onNodeWithText("Hello there!").performClick()
        composeTestRule.onNodeWithText("CHAPTER SELECT", ignoreCase = true).performClick()
        assert(backPressed)
    }

    @Test
    fun chapterCompleteDialog_displaysLanguageName() {
        composeTestRule.setContent {
            GameScreen(
                state = baseState(language = ProgrammingLanguage.PYTHON),
                currentNode = simpleNode(nextNodeId = null, choices = null),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule.onNodeWithText("Hello there!").performClick()
        // Dialog body mentions the language name
        composeTestRule
            .onAllNodesWithText("PYTHON", substring = true, ignoreCase = true)
            .assertCountEquals(2)
    }

    // ─── Choice nodes ──────────────────────────────────────────────────────────

    @Test
    fun gameScreen_withChoices_displaysAllChoiceButtons() {
        val choices = listOf(
            DialogueChoice("I love braces.", targetNodeId = 103, friendPoints = 1),
            DialogueChoice("Braces are for teeth.", targetNodeId = 104, lovePoints = 2)
        )

        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(choices = choices, nextNodeId = null),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule.onNodeWithText("I love braces.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Braces are for teeth.").assertIsDisplayed()
    }

    @Test
    fun gameScreen_withChoices_doesNotShowAdvanceIndicator() {
        val choices = listOf(
            DialogueChoice("Option A", targetNodeId = 103),
            DialogueChoice("Option B", targetNodeId = 104)
        )

        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(choices = choices),
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        // ▼ should not appear when there are choices to click
        composeTestRule.onNodeWithText("▼").assertDoesNotExist()
    }

    @Test
    fun gameScreen_selectingChoice_invokesCallback() {
        var selectedChoice: DialogueChoice? = null
        val choiceA = DialogueChoice("I love braces.", targetNodeId = 103, friendPoints = 1)
        val choiceB = DialogueChoice("Braces are for teeth.", targetNodeId = 104, lovePoints = 2)

        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(choices = listOf(choiceA, choiceB)),
                onNodeAdvanced = {},
                onChoiceSelected = { selectedChoice = it },
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule.onNodeWithText("Braces are for teeth.").performClick()
        assert(selectedChoice == choiceB)
    }

    @Test
    fun gameScreen_withChoices_tappingDialogueCardDoesNotAdvance() {
        var advancedTo = -1
        val choices = listOf(
            DialogueChoice("Option A", targetNodeId = 103)
        )

        composeTestRule.setContent {
            GameScreen(
                state = baseState(),
                currentNode = simpleNode(choices = choices, nextNodeId = 102),
                onNodeAdvanced = { advancedTo = it },
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        // Tapping the card body should NOT advance when choices are present
        composeTestRule.onNodeWithText("Hello there!").performClick()
        assert(advancedTo == -1)
    }

    // ─── Minigame routing ──────────────────────────────────────────────────────

    @Test
    fun gameScreen_syntaxDashTrigger_showsSyntaxMinigame() {
        val node = simpleNode(triggerEvent = "SYNTAX_DASH")

        composeTestRule.setContent {
            GameScreen(
                state = baseState(isMiniGameActive = true),
                currentNode = node,
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        // Dialogue card should NOT be visible — minigame takes over
        composeTestRule.onNodeWithText("Hello there!").assertDoesNotExist()
    }

    @Test
    fun gameScreen_miniGameNotActive_doesNotShowMinigame() {
        // Even if node has a triggerEvent, if isMiniGameActive is false the
        // dialogue view should render normally
        val node = simpleNode(triggerEvent = "SYNTAX_DASH")

        composeTestRule.setContent {
            GameScreen(
                state = baseState(isMiniGameActive = false),
                currentNode = node,
                onNodeAdvanced = {},
                onChoiceSelected = {},
                onMinigameResult = {},
                onBackPressed = {},
                onNextChapter = {},
                onChapterCompleted = {}
            )
        }

        composeTestRule.onNodeWithText("Hello there!").assertIsDisplayed()
    }
}