package com.example.lovebyte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.ui.unit.dp
import com.example.lovebyte.data.model.Chapter
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.ui.screens.ChapterCard
import com.example.lovebyte.ui.screens.TimelineScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimelineScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val deepPink = Color(0xFFFF85A1)
    private val inkBrown = Color(0xFF5D4037)
    private val pixelShape = CutCornerShape(8.dp)

    // ─── Header / Nav ──────────────────────────────────────────────────────────

    @Test
    fun timelineScreen_displaysTimelineHeader() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        composeTestRule.onNodeWithText("TIMELINE", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun timelineScreen_backButton_isDisplayed() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back to Character Selection")
            .assertIsDisplayed()
    }

    @Test
    fun timelineScreen_backButton_invokesCallback() {
        var backPressed = false

        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
                onChapterSelected = {},
                onBackPressed = { backPressed = true }
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Back to Character Selection")
            .performClick()

        assert(backPressed)
    }

    // ─── Language display ──────────────────────────────────────────────────────

    @Test
    fun timelineScreen_python_displaysDisplayName() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        composeTestRule.onNodeWithText("Python", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun timelineScreen_kotlin_displaysDisplayName() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.KOTLIN),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        composeTestRule.onNodeWithText("Kotlin", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun timelineScreen_noneLanguage_fallsBackToPython() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.NONE),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        // NONE falls back to PYTHON, so Python's display name should appear
        composeTestRule.onNodeWithText("Python", ignoreCase = true).assertIsDisplayed()
    }

    // ─── Progress display ──────────────────────────────────────────────────────

    @Test
    fun timelineScreen_showsCorrectProgressPercentage() {
        // Python has 12 chapters. currentChapter=4 → 3 completed → 3/12 = 25%
        val state = LoveByteState(
            currentLanguage = ProgrammingLanguage.PYTHON,
            progressMap = mapOf(ProgrammingLanguage.PYTHON to 4)
        )

        composeTestRule.setContent {
            TimelineScreen(
                state = state,
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        composeTestRule
            .onNodeWithText("Total Progress: ${state.progressPercentage}%", ignoreCase = true)
            .assertIsDisplayed()
    }

    @Test
    fun timelineScreen_zeroProgress_showsZeroPercent() {
        val state = LoveByteState(
            currentLanguage = ProgrammingLanguage.PYTHON,
            progressMap = mapOf(ProgrammingLanguage.PYTHON to 1) // chapter 1 = 0 completed
        )

        composeTestRule.setContent {
            TimelineScreen(
                state = state,
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        composeTestRule
            .onNodeWithText("Total Progress: 0%", ignoreCase = true)
            .assertIsDisplayed()
    }

    // ─── Section headers (sticky) ──────────────────────────────────────────────

    @Test
    fun timelineScreen_python_displaysAllSectionHeaders() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        val list = composeTestRule.onNodeWithTag("timeline_list")

        // First section is visible immediately
        composeTestRule.onNodeWithText("THE BASICS", ignoreCase = true).assertIsDisplayed()

        // Scroll the lazy list to each subsequent header
        list.performScrollToNode(hasText("CONTROL FLOW", ignoreCase = true))
        composeTestRule.onNodeWithText("CONTROL FLOW", ignoreCase = true).assertIsDisplayed()

        list.performScrollToNode(hasText("DATA STRUCTURES", ignoreCase = true))
        composeTestRule.onNodeWithText("DATA STRUCTURES", ignoreCase = true).assertIsDisplayed()

        list.performScrollToNode(hasText("MODULAR MAGIC", ignoreCase = true))
        composeTestRule.onNodeWithText("MODULAR MAGIC", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun timelineScreen_kotlin_displaysAllSectionHeaders() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.KOTLIN),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        val list = composeTestRule.onNodeWithTag("timeline_list")

        composeTestRule.onNodeWithText("FIRST STEPS", ignoreCase = true).assertIsDisplayed()

        list.performScrollToNode(hasText("FUNCTIONAL FUN", ignoreCase = true))
        composeTestRule.onNodeWithText("FUNCTIONAL FUN", ignoreCase = true).assertIsDisplayed()

        list.performScrollToNode(hasText("ANDROID POWER", ignoreCase = true))
        composeTestRule.onNodeWithText("ANDROID POWER", ignoreCase = true).assertIsDisplayed()
    }

    // ─── Chapter titles ────────────────────────────────────────────────────────

    @Test
    fun timelineScreen_python_displaysChapter1Title() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        composeTestRule.onNodeWithText("Print & Comments").assertIsDisplayed()
    }

    @Test
    fun timelineScreen_python_displaysChapter3Title() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        composeTestRule.onNodeWithText("Arithmetic Operators").assertIsDisplayed()
    }

    @Test
    fun timelineScreen_kotlin_displaysChapter1Title() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.KOTLIN),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        composeTestRule.onNodeWithText("Val vs Var").assertIsDisplayed()
    }

    // ─── Chapter unlock / lock logic ───────────────────────────────────────────

    @Test
    fun timelineScreen_currentChapter_isClickable() {
        var selectedChapter = -1
        // progressMap = 1 means chapter 1 is current, chapters 2+ are locked
        val state = LoveByteState(
            currentLanguage = ProgrammingLanguage.PYTHON,
            progressMap = mapOf(ProgrammingLanguage.PYTHON to 1)
        )

        composeTestRule.setContent {
            TimelineScreen(
                state = state,
                onChapterSelected = { selectedChapter = it },
                onBackPressed = {}
            )
        }

        composeTestRule.onNodeWithText("Print & Comments").performClick()
        assert(selectedChapter == 1)
    }

    @Test
    fun timelineScreen_completedChapter_isClickable() {
        var selectedChapter = -1
        // chapter 1 is completed when current = 2
        val state = LoveByteState(
            currentLanguage = ProgrammingLanguage.PYTHON,
            progressMap = mapOf(ProgrammingLanguage.PYTHON to 2)
        )

        composeTestRule.setContent {
            TimelineScreen(
                state = state,
                onChapterSelected = { selectedChapter = it },
                onBackPressed = {}
            )
        }

        composeTestRule.onNodeWithText("Print & Comments").performClick()
        assert(selectedChapter == 1)
    }

    @Test
    fun timelineScreen_lockedChapter_doesNotFireCallback() {
        var selectedChapter = -1
        // only chapter 1 is unlocked
        val state = LoveByteState(
            currentLanguage = ProgrammingLanguage.PYTHON,
            progressMap = mapOf(ProgrammingLanguage.PYTHON to 1)
        )

        composeTestRule.setContent {
            TimelineScreen(
                state = state,
                onChapterSelected = { selectedChapter = it },
                onBackPressed = {}
            )
        }

        // "If Statements" is chapter 4 — locked
        composeTestRule.onNodeWithText("If Statements").performClick()
        assert(selectedChapter == -1)
    }

    // ─── ChapterCard unit tests ────────────────────────────────────────────────

    @Test
    fun chapterCard_completed_showsStarIcon() {
        composeTestRule.setContent {
            ChapterCard(
                chapter = Chapter(id = 1, title = "Print & Comments", startNodeId = 101),
                languageName = "Python",
                isCompleted = true,
                isCurrent = false,
                isUnlocked = true,
                deepPink = deepPink,
                inkBrown = inkBrown,
                pixelRoundedShape = pixelShape,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("★").assertIsDisplayed()
    }

    @Test
    fun chapterCard_current_showsPlayIcon() {
        composeTestRule.setContent {
            ChapterCard(
                chapter = Chapter(id = 2, title = "Variables & Types", startNodeId = 201),
                languageName = "Python",
                isCompleted = false,
                isCurrent = true,
                isUnlocked = true,
                deepPink = deepPink,
                inkBrown = inkBrown,
                pixelRoundedShape = pixelShape,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("▶").assertIsDisplayed()
    }

    @Test
    fun chapterCard_locked_showsDiamondIcon() {
        composeTestRule.setContent {
            ChapterCard(
                chapter = Chapter(id = 4, title = "If Statements", startNodeId = 401),
                languageName = "Python",
                isCompleted = false,
                isCurrent = false,
                isUnlocked = false,
                deepPink = deepPink,
                inkBrown = inkBrown,
                pixelRoundedShape = pixelShape,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("◆").assertIsDisplayed()
    }

    @Test
    fun chapterCard_displaysChapterNumberAndTitle() {
        composeTestRule.setContent {
            ChapterCard(
                chapter = Chapter(id = 3, title = "Arithmetic Operators", startNodeId = 301),
                languageName = "Python",
                isCompleted = false,
                isCurrent = true,
                isUnlocked = true,
                deepPink = deepPink,
                inkBrown = inkBrown,
                pixelRoundedShape = pixelShape,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("CHAPTER 3", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Arithmetic Operators").assertIsDisplayed()
    }

    @Test
    fun chapterCard_unlocked_firesOnClick() {
        var clicked = false

        composeTestRule.setContent {
            ChapterCard(
                chapter = Chapter(id = 1, title = "Print & Comments", startNodeId = 101),
                languageName = "Python",
                isCompleted = false,
                isCurrent = true,
                isUnlocked = true,
                deepPink = deepPink,
                inkBrown = inkBrown,
                pixelRoundedShape = pixelShape,
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Print & Comments").performClick()
        assert(clicked)
    }

    @Test
    fun chapterCard_locked_isNotEnabled() {
        composeTestRule.setContent {
            ChapterCard(
                chapter = Chapter(id = 5, title = "Logical Operators", startNodeId = 501),
                languageName = "Python",
                isCompleted = false,
                isCurrent = false,
                isUnlocked = false,
                deepPink = deepPink,
                inkBrown = inkBrown,
                pixelRoundedShape = pixelShape,
                onClick = {}
            )
        }

        composeTestRule
            .onNodeWithText("Logical Operators")
            .assertIsNotEnabled()
    }

    @Test
    fun chapterCard_kotlinChapter_displaysCorrectly() {
        composeTestRule.setContent {
            ChapterCard(
                chapter = Chapter(id = 1, title = "Val vs Var", startNodeId = 2001),
                languageName = "Kotlin",
                isCompleted = false,
                isCurrent = true,
                isUnlocked = true,
                deepPink = deepPink,
                inkBrown = inkBrown,
                pixelRoundedShape = pixelShape,
                onClick = {}
            )
        }

        composeTestRule.onNodeWithText("Val vs Var").assertIsDisplayed()
        composeTestRule.onNodeWithText("CHAPTER 1", ignoreCase = true).assertIsDisplayed()
    }
}