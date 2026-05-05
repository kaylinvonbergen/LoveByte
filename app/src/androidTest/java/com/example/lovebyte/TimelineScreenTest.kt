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

    // UI constants used for consistent styling in ChapterCard tests
    private val deepPink = Color(0xFFFF85A1)
    private val inkBrown = Color(0xFF5D4037)
    private val pixelShape = CutCornerShape(8.dp)

    // HELPER FUNCTION
    // scrolls LazyColumn until a node with matching text is visible  sed because Timeline is a scrollable list
    private fun scrollTo(text: String) {
        composeTestRule
            .onNodeWithTag("timeline_list")
            .performScrollToNode(hasText(text))
    }

    // HEADER / NAVIGATION
    // verifies top-level UI elements (title + back button)
    @Test
    fun timelineScreen_displaysTimelineHeader() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        // ensure screen title is rendered
        composeTestRule.onNodeWithText("TIMELINE", ignoreCase = true)
            .assertIsDisplayed()
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

        // back navigation always visible
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

        // user presses back button
        composeTestRule
            .onNodeWithContentDescription("Back to Character Selection")
            .performClick()

        // ensure callback triggered
        assert(backPressed)
    }

// LANGUAGE DISPLAY
// ensures correct language name is shown in header area

    @Test
    fun timelineScreen_python_displaysDisplayName() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        composeTestRule.onNodeWithText("Python", ignoreCase = true)
            .assertIsDisplayed()
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

        composeTestRule.onNodeWithText("Kotlin", ignoreCase = true)
            .assertIsDisplayed()
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

        // default fallback lang should be Python
        composeTestRule.onNodeWithText("Python", ignoreCase = true)
            .assertIsDisplayed()
    }

    // PROGRESS INDICATOR
    // ensures user progress is correctly displayed
    @Test
    fun timelineScreen_showsCorrectProgressPercentage() {
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

        // progress percentage should match computed state value
        composeTestRule
            .onNodeWithText("Total Progress: ${state.progressPercentage}%", ignoreCase = true)
            .assertIsDisplayed()
    }

// SECTION HEADERS
// verifies grouped chapter sections render correctly in scroll list

    @Test
    fun timelineScreen_python_displaysAllSectionHeaders() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        // first section always visible
        composeTestRule.onNodeWithText("THE BASICS")
            .assertIsDisplayed()

        // scroll to middle
        scrollTo("CONTROL FLOW")
        composeTestRule.onNodeWithText("CONTROL FLOW")
            .assertIsDisplayed()

        // scroll to end
        scrollTo("END")
        composeTestRule.onNodeWithText("END")
            .assertIsDisplayed()

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

        composeTestRule.onNodeWithText("FIRST STEPS")
            .assertIsDisplayed()

        scrollTo("FUNCTIONAL FUN")
        composeTestRule.onNodeWithText("FUNCTIONAL FUN")
            .assertIsDisplayed()

        scrollTo("ANDROID POWER")
        composeTestRule.onNodeWithText("ANDROID POWER")
            .assertIsDisplayed()
    }

// CHAPTER VISIBILITY
// ensures specific chapters render correctly when scrolled

    @Test
    fun timelineScreen_python_displaysChapter1Title() {
        composeTestRule.setContent {
            TimelineScreen(
                state = LoveByteState(currentLanguage = ProgrammingLanguage.PYTHON),
                onChapterSelected = {},
                onBackPressed = {}
            )
        }

        scrollTo("Indentation")

        composeTestRule.onNodeWithText("Indentation")
            .assertIsDisplayed()
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

        scrollTo("Arithmetic Operators")

        composeTestRule.onNodeWithText("Arithmetic Operators")
            .assertIsDisplayed()
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

        scrollTo("Val vs Var")

        composeTestRule.onNodeWithText("Val vs Var")
            .assertIsDisplayed()
    }

    // INTERACTION LOGIC
    // ensures correct chapter selection behavior based on unlock state

    @Test
    fun timelineScreen_currentChapter_isClickable() {
        var selectedChapter = -1

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

        // only unlocked chapter clickable
        scrollTo("Indentation")
        composeTestRule.onNodeWithText("Indentation").performClick()

        assert(selectedChapter == 1)
    }

    @Test
    fun timelineScreen_lockedChapter_doesNotFireCallback() {
        var selectedChapter = -1

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

        // not clickable if not unlocked
        scrollTo("If Statements")
        composeTestRule.onNodeWithText("If Statements").performClick()

        assert(selectedChapter == -1)
    }

    // CHAPTER CARD UI STATES
    // verifies visual indicators for chapter states
    @Test
    fun chapterCard_completed_showsStarIcon() {
        composeTestRule.setContent {
            ChapterCard(
                chapter = Chapter(1, "Print & Comments", 101),
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

        // completed chapters use star icon
        composeTestRule.onNodeWithText("★").assertIsDisplayed()
    }

    @Test
    fun chapterCard_current_showsPlayIcon() {
        composeTestRule.setContent {
            ChapterCard(
                chapter = Chapter(2, "Variables & Types", 201),
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

        // current chapter uses play icon
        composeTestRule.onNodeWithText("▶").assertIsDisplayed()
    }

    @Test
    fun chapterCard_locked_showsDiamondIcon() {
        composeTestRule.setContent {
            ChapterCard(
                chapter = Chapter(4, "If Statements", 401),
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

        // locked chapters use diamond icon
        composeTestRule.onNodeWithText("◆").assertIsDisplayed()
    }
}