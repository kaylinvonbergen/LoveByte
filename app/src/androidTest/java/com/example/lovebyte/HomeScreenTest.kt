package com.example.lovebyte

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.ui.screens.HomeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun onboarding_step1_displaysWelcomeMessage() {
        val onboardingState = LoveByteState(
            shouldShowOnboarding = true,
            onboardingStep = 1
        )

        composeTestRule.setContent {
            HomeScreen(
                state = onboardingState,
                onContinueClicked = {},
                onSwapClicked = {},
                onSettingsClicked = {},
                onOnboardingNext = {},
                onOnboardingPlacementComplete = { _, _ -> },
                onOnboardingFinish = {}
            )
        }

        // Verify Step 1 content
        composeTestRule.onNodeWithText("Welcome to LoveByte!", ignoreCase = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("Next", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun onboarding_step2_allowsProficiencySelection() {
        val onboardingState = LoveByteState(
            shouldShowOnboarding = true,
            onboardingStep = 2
        )

        composeTestRule.setContent {
            HomeScreen(
                state = onboardingState,
                onContinueClicked = {},
                onSwapClicked = {},
                onSettingsClicked = {},
                onOnboardingNext = {},
                onOnboardingPlacementComplete = { _, _ -> },
                onOnboardingFinish = {}
            )
        }

        // Targets the "1" specifically inside the Python Row
        composeTestRule
            .onNode(
                hasText("1") and hasAnyAncestor(hasTestTag("Python_Row")),
                useUnmergedTree = true
            )
            .assertIsDisplayed()

        // Targets the "1" specifically inside the Kotlin Row
        composeTestRule
            .onNode(
                hasText("1") and hasAnyAncestor(hasTestTag("Kotlin_Row")),
                useUnmergedTree = true
            )
            .assertIsDisplayed()
    }

    @Test
    fun mainDashboard_displaysCorrectSpriteAndChapter() {
        val activeState = LoveByteState(
            shouldShowOnboarding = false,
            currentLanguage = ProgrammingLanguage.PYTHON,
            progressMap = mapOf(ProgrammingLanguage.PYTHON to 3) // Chapter 3
        )

        composeTestRule.setContent {
            HomeScreen(
                state = activeState,
                onContinueClicked = {},
                onSwapClicked = {},
                onSettingsClicked = {},
                onOnboardingNext = {},
                onOnboardingPlacementComplete = { _, _ -> },
                onOnboardingFinish = {}
            )
        }

        // 1. Verify the Python Sprite is shown via Content Description
        composeTestRule.onNodeWithContentDescription("Python Sprite").assertIsDisplayed()

        // 2. Verify the specific chapter text is rendered correctly
        composeTestRule.onNodeWithText("PYTHON: CHAPTER 3", ignoreCase = true).assertIsDisplayed()

        // 3. Verify the "CONTINUE" button appears instead of "START"
        composeTestRule.onNodeWithText("CONTINUE").assertIsDisplayed()
    }

    @Test
    fun mainDashboard_noLanguageSelected_showsStartButton() {
        val newState = LoveByteState(
            shouldShowOnboarding = false,
            currentLanguage = ProgrammingLanguage.NONE
        )

        composeTestRule.setContent {
            HomeScreen(
                state = newState,
                onContinueClicked = {},
                onSwapClicked = {},
                onSettingsClicked = {},
                onOnboardingNext = {},
                onOnboardingPlacementComplete = { _, _ -> },
                onOnboardingFinish = {}
            )
        }

        // When no language is selected, it should prompt to "START"
        composeTestRule.onNodeWithText("START").assertIsDisplayed()
    }
}